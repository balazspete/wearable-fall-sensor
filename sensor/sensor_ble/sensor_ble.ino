#include "I2Cdev.h"//add neccesary headfiles
#include "MPU6050.h"//add neccesary headfiles
#include <Wire.h>
#include <Streaming.h>
#include <string.h>

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

void initialiseAccelerationAndGyro() {
   // Initializing IMU sensor
   accelgyro.initialize();
  
   // Testing connections with IMU
   Serial.println(accelgyro.testConnection() ? "IMU connection successful":"IMU connection failure");
}

void initialiseLoudnessSensor()
{
   Wire.beginTransmission(ADDR_ADC121);        // transmit to device
   Wire.write(REG_ADDR_CONFIG);                // Configuration Register
   Wire.write(0x20);
   Wire.endTransmission();  
}

void updateAccelerationAndGyro() {
   accelgyro.getMotion6(&ax,&ay,&az,&gx,&gy,&gz);//get the gyro and accelarator   
   //==========accelerator================================
   Ax=ax/16384.00;//to get data of unit(g)
   Ay=ay/16384.00;//to get data of unit(g)
   Az=az/16384.00;//to get data of unit(g)
   //===============gyro================================
   Gx=gx/131.00;
   Gy=gy/131.00;
   Gz=gz/131.00;
}

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

int dToBuffer(float value, char* buffer, int start)
{
  int origin = start;
  if (value >= 1000)
  {
    return -1;
  }
  
  int mode = 0; 
  long v = (long)(value * 1000LL);
  
  if (v < 0)
  {
    buffer[start++] = '-';
    v = abs(v);
  }
  
  int i = 6;
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
  
  buffer[start] = '\0';
  return start;
}

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

void writeToSerial(char text[], int length, float value)
{
    char buffer [length + 10];
    buffer[0] = *"#";
    copyOver(text, buffer, length, 1);
    int index = dToBuffer(value, buffer, length+1);
    buffer[index] = *"#";
    buffer[index+1] = '\0';
    
//    if (Serial.available())
//    {
//        Serial.write(buffer);
//        Serial.flush();  
//        Serial.println();
//    }
    
//    if (Serial1.available())
//    {
    if (Serial1) {
        Serial1.write(buffer);
        Serial1.flush();  
    }
}

void bleTransmitSensorData() 
{
    char text[] = { 'A', 'X', ':' };
    writeToSerial(text, 3, Ax);
    delay(5);
    
    text[1] = 'Y';
    writeToSerial(text, 3, Ay);
    delay(5);
    
    text[1] = 'Z';
    writeToSerial(text, 3, Az);
    delay(5);
    
    text[0] = 'G';
    text[1] = 'X';
    writeToSerial(text, 3, Gx);
    delay(5);
    
    text[1] = 'Y';
    writeToSerial(text, 3, Gy);
    delay(5);
    
    text[1] = 'Z';
    writeToSerial(text, 3, Gz);
    delay(5);
    
    text[0] = 'L';
    text[1] = 'O';
    writeToSerial(text, 3, loudness);
    delay(5);
}

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
  while(true) {
    updateAccelerationAndGyro();
    updateLoudness();
    //normalise()
    bleTransmitSensorData();
   
    delay(1000);
  }
}





