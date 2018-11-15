
int ledPin = 11;
// Example 4 - Receive a number as text and convert it to an int

const byte numChars = 6;
char receivedChars[numChars];   // an array to store the received data

boolean newData = false;

double dataNumber = 0;             // new for this version

/*void setup()
{
  Serial.begin(9600);
  // Aguarda 1 seg antes de acessar as informações do sensor
  delay(1000);

}*/

/*void loop()
{

  if(Serial.available()){
    char rc = Serial.read();
    Serial.println(rc);
  }


  // Mostra os valores lidos, na serial
  

  // Nao diminuir muito o valor abaixo
  // O ideal e a leitura a cada 2 segundos
  delay(2000);
}*/



void setup()
{
  Serial.begin(9600);
  // Aguarda 1 seg antes de acessar as informações do sensor
  pinMode(ledPin, OUTPUT);
  digitalWrite(ledPin, HIGH);
}
void loop()
{

    static byte ndx = 0;
    char rc;
    newData = false;
    char where;
    short ponto = 0;

    
    delay(5);


    if(Serial.available() > 0){
      rc = Serial.read();

      if(rc != '0' && rc != '1')
        return;
    
      //Salva o número
      while(isnumberorpoint(rc) && ndx < 5){
        receivedChars[ndx] = rc;
        ndx++;
        rc = Serial.read();
        if(ponto == 0 && rc != '.')
          return;
        ponto = 1;
      }

      if(ndx == 5){
        receivedChars[ndx] = '\0'; // terminate the string
        ndx = 0;
      }
      else
        return;
          
      if(iscommand(rc)){
        //Salvar o comando
        where = rc;
        newData = true;
      }
      else
      return;
      Serial.print(receivedChars);
      Serial.print(" ");
      Serial.println(where);
      showNewNumber();
    }
    
}

void showNewNumber() {

    if (newData == true) {
        dataNumber = 0;             // new for this version
        dataNumber = atof(receivedChars);   // new for this version   
        
        int value = (int) (dataNumber * 255);

        newData = false;

        if(value >= 0 || value < 256)
        analogWrite(ledPin, value);
    }
}

bool iscommand(char c){
  if(c == 'u' || c == 'd' || c == 'l' || c == 'r'){
    return true;
  }
  return false;
}

bool isnumberorpoint(char c){
  if((c >= '0' && c <= '9') || c == '.')
      return true;
  return false;
}


 

   
