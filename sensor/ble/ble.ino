
void setup() {
    // Console
    Serial.begin(9600);
    
    // Connect to the BLE module
    Serial1.begin(38400);
}

void loop()
{
  while(true) {
    // If there's something from the console
    if(Serial.available())
    {
      // Write to BLE whatever has been inputted from the console
      Serial1.write(Serial.read());
    }
    
    // If there's something from BLE
    if(Serial1.available())
    {
      // Write to serial wheverer has been received through the BLE connection
      Serial.write(Serial1.read());
    }
  }
}
