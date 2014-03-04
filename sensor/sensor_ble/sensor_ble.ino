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

void bleTransmitSensorData() {
   char out[100];
   
   // xxxx.xxx|xxxx.xxx|xxxx.xxx|xxxx.xxx|xxxx.xxx|xxxx.xxx|xxxx.xxx"%4.3f|%4.3f|%4.3f|%4.3f|%4.3f|%4.3f|%4.3f"
   sprintf(out, "%s", Ax);
   Serial.println(out);
   
   if (Serial1.available()) {
      Serial1.write(out);
   }
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
   updateAccelerationAndGyro();
   updateLoudness();
   bleTransmitSensorData();
   
   delay(1000);
}





