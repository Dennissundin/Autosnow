package autosnow;
import java.io.*;
import java.util.Arrays;
import javax.microedition.io.*;
import javax.bluetooth.*;
public class BluetoothTransceiver implements Runnable{
    private ControlUI cui;

    //Variabeldeklaration
    PrintStream bluetooth_ut;
    BufferedReader bluetooth_in;
    private DataStore ds;
    boolean startReceiver = false;
    boolean sendDataAgain;
    boolean sendRF,gotOk;
    int bufferSize; //Bestämmer hur meddelande ut ska formateras
    String meddelande_ut_f;
    StreamConnection anslutning;
    int timer;
    int RFIDnow;
    int lastRFIDtag;
    String dummyError;
    String lastMessage; //Spara sista meddelandet skickat för att kunna skicka tillbaka ifall det blir något fel
    
    
    public BluetoothTransceiver(ControlUI cui, DataStore ds){
        //Init variabler
        sendDataAgain = false;
        this.cui = cui;
        this.ds = ds;
        bufferSize = 8; //8 = default
        meddelande_ut_f = "";
        timer =0;
        lastMessage = "";
        sendRF = false;
        gotOk = false;
       
    }
    
   @Override
    public void run() //Koden som körs när thread startar
    {
        while(true){ //Kör under tiden programmet är igång
            
            try {
                Thread.sleep(1000);
                if(cui.getButtonState()) //Försöker ansluta till robot när startknapp trycks
                {
                    
                    anslutning = (StreamConnection) Connector.open("btspp://201703226634:1");  //Upprättar en anslutning
                    bluetooth_ut = new PrintStream(anslutning.openOutputStream()); 
                    bluetooth_in = new BufferedReader(new InputStreamReader(anslutning.openInputStream()));
                    while(true) //När anslutning är upprättad körs denna loop tills 
                        //STOP skrivs i input || Stopp-knapp trycks || Anslutningen bryts
                    {
                        Thread.sleep(100); //Sleep som är till för både timer och prestanda
                        timer++;
                
                        
                     
                         if(bluetooth_in.ready()) //Läser bluetoothdata om robot skickat något
                         {
                              checkBluetoothIn(bluetooth_in.readLine()); //Funktion som kontrollerar indata
                         }
                           
                        if(timer >= 10000)//Kollar anslutningen var 20e sekund.
                        {
                            bluetooth_ut.print(formatString("?"));
                            cui.appendBluetoothStatus("Checking connection");
                            lastMessage = "?";
                            timer = 0;
                        }           
                        if(cui.pressedEnter)//Läser det som står i inputfönstret och skickar till robot
                            //då Enter trycks
                        {
                       
                            meddelande_ut_f = "";
                            String meddelande_ut = cui.getInput(); //Tar det som står i input-rutan
                            meddelande_ut_f = formatString(meddelande_ut); 
                            lastMessage = meddelande_ut_f; //Sparar meddelande
                        
                            if (meddelande_ut_f.equals("STOP"))
                            {
                                bluetooth_ut.println(formatString("S"));
                                break;
                            }
                            
                         bluetooth_ut.print(meddelande_ut_f); //Skickar meddelande till Robot  
                         cui.appendBluetoothStatus("Input: " + meddelande_ut);         
                         cui.pressedEnter = false;

                        }
                        
                        //Dessa är till för att kunna skicka kommandon
                        if(cui.pressedDown) //Manuell körning
                        {
                         bluetooth_ut.print(formatString("Rv"));
                       //  ds.calculateNewRoute = true;
                         cui.pressedDown = false;
                        }
                        if(cui.pressedUp)//Manuell körning
                        {
                          bluetooth_ut.print(formatString("R^"));
                    
                          cui.pressedUp = false;
                        }
                        if(cui.pressedLeft)//Manuell körning
                        {
                              bluetooth_ut.print(formatString("R<"));
                            
                          cui.pressedLeft = false;
                        }
                        if(cui.pressedRight)//Manuell körning
                        {
                          bluetooth_ut.print(formatString("R>"));   
                         
                          cui.pressedRight = false;
                        }
                       if(cui.pgup)
                        {
                            bluetooth_ut.print(formatString("S"));
                            cui.pgup = false;
                        }
                          if(cui.pgdown)
                        {
                            bluetooth_ut.print(formatString("Rf"));
                            cui.pgdown = false;
                        }

                         if(!cui.getButtonState())//Om stoppknapp trycks 
                         {
                             cui.changeColorBluetooth(0);
                             bluetooth_ut.print(formatString("S"));                          
                             break;
                         }
                   if(sendRF)
                   {
                    
                       while(!gotOk)
                       {
                         Thread.sleep(1000);
                         bluetooth_ut.print(formatString("Rf"));
                         Thread.sleep(1000);
                         cui.appendBluetoothStatus("Skickat Rf");
                         lastMessage = "Rf";
                         
                         if(bluetooth_in.ready()) //Läser bluetoothdata om robot skickat något
                         {
                              checkBluetoothIn(bluetooth_in.readLine()); //Funktion som kontrollerar indata
                         }
                           
                       }
                       sendRF = false;
                               
                      
                   }
                      
                       
                   
                    }
                    anslutning.close();
                    System.out.println("Disconnected");
                }
            } catch (Exception e)
            { 
                cui.changeColorBluetooth(2);
                cui.appendBluetoothStatus("Connection lost...");
                startReceiver = false;
                e.printStackTrace(); 
            }
        }
    }
    /**
     * Funktionen jämför msg enligt bluetoothprotokollet
     * @param msg Det meddelandet som ska undersökas
     */
    public void checkBluetoothIn(String msg)
    {
        
      
        if(msg.startsWith("*")) //Robot skickar * när den är ansluten
        {
            System.out.println("Skicka tillbaka *");
            bluetooth_ut.print("*\n");
       
        }
        else if(msg.startsWith("E5"))
        {
            cui.appendError("Hinder på vägen");
           
        }
        else if(msg.startsWith("i"))//Init meddelande
        {
         bufferSize = (int) (msg.charAt(1));//Ändrar bufferSize enligt msg
         cui.changeColorBluetooth(1); //Ändrar färg på BTknappen till grön
         cui.appendBluetoothStatus("Bluetooth connected");
         cui.appendBluetoothStatus("Skickat RF");
         sendRF = true;
         ds.startClock = true; //Körtiden börjar räknas
      
      
         
        }
        else if(msg.startsWith("E21"))
        {
             dummyError = String.valueOf(msg.charAt(3));
             if(dummyError.startsWith("P"))
             {
                 cui.appendError("Parity error on UART1");
             }
             else if(dummyError.startsWith("N"))
             {
                 cui.appendError("Noise error on UART1");
             }
             else if(dummyError.startsWith("F"))
             {
                 cui.appendError("Frame error on UART1");
             }
             else if(dummyError.startsWith("O"))
             {
                 cui.appendError("Overrun error on UART1");
             }
             else if(dummyError.startsWith("D"))
             {
                 cui.appendError("DMA transfer error on UART1");
             }
             

        }
        else if(msg.equals("E3."))
        {
            cui.appendError("Engine failure");
        } 
        else if(msg.startsWith("E4"))//Ifall vi skickar ett beslut som inte går att utföra
        {
            cui.appendError("Can not complete current workorder");
        }         
        else if(msg.equals("k"))
        {
            cui.appendBluetoothStatus("Connection is good");
            cui.changeColorBluetooth(1);
        }
        else if(msg.startsWith("OFF"))
        {
           System.out.println("OFF");
        }
        else if(msg.startsWith("+"))
        {
           cui.appendBluetoothStatus(" + skickar tbx :" + lastMessage);
           bluetooth_ut.print(formatString(lastMessage));
           System.out.println(lastMessage);
        }
        else if(msg.startsWith("A")) //Robot kör över en RFID tag och vill ha ett besult
        {
            RFIDnow = 0;
            RFIDnow = (int)(msg.charAt(1)-'0')*10 + (int)(msg.charAt(2)-'0'); //Läser in den int som är skickad
            cui.appendBluetoothStatus("RFIDnow = " + RFIDnow);
      
            
            if(lastRFIDtag != RFIDnow) //Ifall robot skickar samma tag flera gånger i rad när den står på en RFIDtag, körs den bara en gång
            {

                ds.bluetoothRFIDnode = 3;
                ds.checkDecision = true;


                 //  Kollar vilken nod som är motsvarande den som robot skickar

                int dummyNode = 0; //Håller det intressanta nodnummret.
                //Loopen kollar igenom alla RFID noder genom deras ID, RFIDnow skickas som ett ID
                //Sedan kollas detta mot RFIDNodNummer som innehåller alla RFID ID, När matchning hittas
                //sparas Nodnummret som finns i vägnätet i dummyNode
                for(int i = 0; i < ds.RFIDcounter;i++) 
                {

                    if(ds.RFIDNodNummer[i] == RFIDnow)
                    {
                        dummyNode = ds.RFIDtag[i];
                        cui.appendBluetoothStatus("Robot är i nod: " + ds.RFIDtag[i]);
                        break;
                    }

                }
                updatePosition(dummyNode); //Uppdaterar postioinen efter den nod robot precis kört över.

                //Tar fram ett beslut genom att söka igenom vilka noder som har ett beslut vid sig.
                //När det är hittat skickas det till robot
                
              
                
                    for(int i = 0; i < ds.decisionsMade;i++) 
                    {
                        if(dummyNode == ds.RFIDNodNrDecision[i])
                        {
                            //Kriteriet är att det ska vara den näst sista RFIDNoden i rutten
                            checkIfNewSearch(i); //Kollar om en ny optimeringsrutt behöver startas.
                            if(RFIDnow > 39)
                            {
                                bluetooth_ut.print(formatString(ds.RFIDdecision[i]));//Skickar beslut till robot
                                lastMessage = ds.RFIDdecision[i]; //Sparar beslutet ifall robot inte kan uppfatta det.
                                cui.appendBluetoothStatus("Besult: " + ds.RFIDdecision[i] + " skickat till robot"); 
                                
                            }
                            
                            
                            
                            ds.RFIDNodNrDecision[i] = -1;//Kört över noden och använt beslutet
                            
                            //För att inte använda samma beslut igen ifall man skulle köra över samma nod en gång till.
                            break; //När beslutet är skickat behöver ingen mer sökning göras
                        }
                    }
                    lastRFIDtag = RFIDnow;//Uppdaterar senaste RFIDtag
                    ds.passedRFIDnode = true;
                        
            }
        }
        else if(msg.startsWith("!"))//Svar på "?" 
        {
            cui.appendBluetoothStatus("Connection is good");
        }
        
        else if(msg.startsWith("ok")) //Robor har förstått meddelandet
        {      
           gotOk = true; 
           cui.appendBluetoothStatus("Från robot: " + msg);
        }
        else //Robot skickar något som inte är enligt protokoll
        {
            cui.appendBluetoothStatus("Ej inlagt Från robot: " + msg);
            System.out.println("ERROR: " + msg);
        }
    }
    /**
     * Formaterar strings som ska skickas enligt den bestämda bufferSize.
     * genom att lägga till mellanrum efter meddelandet
     * @param msg Det meddelande som ska formateras
     * @return Den formaterade strängen
     */
    public String formatString(String msg) 
    {
        String dummy = msg;
        for(int i = msg.length(); i < bufferSize-1;i++)
        {
            dummy = dummy + " "; 
        }
        dummy = dummy + "\n";
        
        return dummy;
    }
    /**
     * Uppdaterar och ritar upp ny postition för robot 
     * @param node noden robot ska flyttas till i anvgränssnittet
     */
    private void updatePosition(int node) 
    {
       for(int i = 0; i < ds.nodeCounter;i++)
       {
           if(node == ds.nodesNew[i])
           {
               ds.vehicleX = ds.nodeXNew[i];
               ds.vehicleY = ds.nodeYNew[i];
               cui.repaint();
               
               break;
           }
       }
    }
    /**
     * Kollar om en ny optimeringssökning ska köras. 
     * Kriteriet är att det ska finnas beslut i två RFIDtaggar framför den nuvarande.
     * Om den inte har något beslut startar optimeringsalgoritmen igen och gör en sökning
     * @param i index för den plats som ska undersökas
     */
    private void checkIfNewSearch(int i) 
         
    {
        if(ds.RFIDNodNrDecision[i+2] == 0) 
        {
          
            ds.calculateNewRoute = true;
        }
    
        
        
    }
   

  
}
   


