#include "I2Cdev.h"//add neccesary headfiles
#include "MPU6050.h"//add neccesary headfiles
#include <Wire.h>
#include <Streaming.h>
#include <string.h>
#include <math.h>
//====the offset of gyro===========
#define Gx_offset  -1.50
#define Gy_offset  0
#define Gz_offset  0.80
//====the offset of accelerator===========
#define Ax_offset -0.07
#define Ay_offset 0.02
#define Az_offset 0.14
//====================
MPU6050 accelgyro;

#define REG_ADDR_RESULT         0x00
#define REG_ADDR_CONFIG         0x02

#define ADDR_ADC121             0x58

int16_t ax,ay,az;//original data;
int16_t gx,gy,gz;//original data;
float Ax,Ay,Az;//Unit g(9.8m/s^2)
float Gx,Gy,Gz;//Unit ��/s
float loudness;

char buffer [1000];

int measurementMode = 0;

/*
 * Initialise the IMU
 */
void initialiseAccelerationAndGyro() {
   // Initializing IMU sensor
   accelgyro.initialize();
  
   // Testing connections with IMU
   Serial.println(accelgyro.testConnection() ? "IMU connection successful":"IMU connection failure");
}

/*
 * Initialise the loudness sensor
 */
void initialiseLoudnessSensor()
{
   Wire.beginTransmission(ADDR_ADC121);        // transmit to device
   Wire.write(REG_ADDR_CONFIG);                // Configuration Register
   Wire.write(0x20);
   Wire.endTransmission();  
}

/*
 * Update the acceleration and Orientation values
 */
void updateAccelerationAndGyro() {
   accelgyro.getMotion6(&ax,&ay,&az,&gx,&gy,&gz);//get the gyro and accelarator   
   
   Serial.println(ax);
   
   //==========accelerator================================
   Ax=ax;///16384.00;//to get data of unit(g)
   Ay=ay;///16384.00;//to get data of unit(g)
   Az=az;///16384.00;//to get data of unit(g)
   //===============gyro================================
   Gx=gx;///131.00;
   Gy=gy;///131.00;
   Gz=gz;///131.00;
}

/*
 * Update the loudness value
 */
void updateLoudness()
{
    int getData;
    Wire.beginTransmission(ADDR_ADC121);        // transmit to device
    Wire.write(REG_ADDR_RESULT);                // get reuslt
    Wire.endTransmission();

    Wire.requestFrom(ADDR_ADC121, 2);           // request 2byte from device
    delay(1);
    
    if(Wire.available()<=2)
    {
      getData = (Wire.read()&0x0f)<<8;
      getData |= Wire.read();
      loudness = getData;
    }
}

/*
 * Convert a double to a char array
 */
int dToBuffer(float value, char* buffer, int start)
{
  int origin = start;
  /*if (value >= 1000)
  {
    return -1;
  }*/
  
  int mode = 0; 
  long v = (long)(value * 1000LL);
  
  if (v < 0)
  {
    buffer[start++] = '-';
    v = abs(v);
  }
  
  int i = 10;
  while (i > 0)
  {
    long b = pow(10, i--);
    int _v = v/b;
    if (mode || _v > 0)
    { 
      buffer[start++] = (char)(_v+48);
      mode = 1;
    }
    v = v - _v*b;
    if (i == 2)
    {
      if (buffer[start-1] == '-')
      {
        buffer[start++] = '0';
      }
      else if (origin - start == 0)
      {
        buffer[start++] = '0';
      } 

      buffer[start++] = '.';
    }
  }
  
  if (buffer[start-1] == '.')
  {
    buffer[start++] = '0';  
  }
  
  buffer[start] = '\0';
  return start;
}

/*
 * Utility to copy arrays
 */
int copyOver(char origin[], char target[], unsigned int size, unsigned int start)
{
    int index = 0;
    while (index < size)
    {
        target[start + index] = origin[index];
        index++;
    }
    
    return start+index;
}

/*
 * Buffer the current sensor measurements
 */
int bufferSensorData(int start) 
{
  if (start < 0) 
  {
    return -1;  
  }
  
  char text[] = { '#', 'M', 'E', 'A', 'S', 'U', 'R', 'E', 'M', 'E', 'N', 'T', '|' };

  start = copyOver(text, buffer, 13, start);
  start = dToBuffer(Ax, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(Ay, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(Az, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(Gx, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(Gy, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(Gz, buffer, start);
  buffer[start++] = '|';
  start = dToBuffer(loudness, buffer, start);
  buffer[start++] = '#';
  buffer[start] = '\0';
  
  return start;
}

/*
 * Transmit the buffer to the base
 */
boolean bleTransmitBuffer() 
{
  if (Serial1)
  {
    Serial1.write(buffer);
    //Serial.println(buffer);
    Serial1.flush();
    return true;
  }
  else
  {
    return false;  
  }
}

/*
 * Initialise components and devices
 */
void setup() {
    // Console (remove when not used)
    Serial.begin(9600);
    
    // Connect to the BLE module
    Serial1.begin(38400);
   
    Wire.begin();
    
    initialiseAccelerationAndGyro();
    initialiseLoudnessSensor();
}

void loop() {
  while (true) 
  {
    if (Serial1.available())
    {
      measurementMode = Serial1.read();
    }
    
    //If greater than 0 => take measurement
    if (measurementMode > 48)
    {
      updateAccelerationAndGyro();
      updateLoudness();
      
      // If measurement mode is 2 or less, do not buffer
      if (measurementMode <= 50)
      {
        bufferSensorData(0);
        while(!bleTransmitBuffer());
        delay(50);
      }
      else
      {
        /*
        
          BUFFER MEASUREMENTS AND SEND WHEN BUFFER IS FULL
        
        */
        
        //change delay to whatever you need
        delay(50);
      }
    }
    
    // if measurement mode is 1 (49) => one measurement only
    if (measurementMode < 50)
    {
      measurementMode = 0;
    }
   
  }
}




