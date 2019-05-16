package autosnow;
import java.util.Arrays;
/**
 *
 * VehicleRead@authoCr denni
 */
public class ChangePrio implements Runnable {
    //Variabeldeklaration
    private ControlUI cui;
    private DataStore ds;
    private boolean finished = false;
    private boolean writeOnce;
    private double dummyDouble;
    private String dummy;
   
    
    public ChangePrio(DataStore ds, ControlUI cui)
    {
        //Init variabler
        this.cui = cui;
        this.ds = ds;
      
       
    }
    @Override
    public void run()
    {
          while(!finished) //Kör tills den är klar, klar är den när prio är ändrad.
        {   
            try
            {
                Thread.sleep(1000);
                if(cui.changePrioButton) //Om användaren väljer att ändra priovärden på bågarna
                  {
                int i = 0;
                while(i<ds.arcs) //Loopar igenom alla bågar.
                {
                    Thread.sleep(100);
                    if(!writeOnce)//För att inte mata ut appends, gör ingenting tils användare ändrat värde på bågen.
                    {
                        cui.appendStatus("Prio mellan nod: " + ds.arcStart[i] + " till nod: " + ds.arcEnd[i]);
                        writeOnce = true;
                        ds.arcColor[i] = 1; //Ändrar färg på den båge man redigerar till röd
                        cui.repaint();
                        
                    }
                    if(cui.pressedEnter) //Väntar på input
                    {
                        try
                        {
                            
                            dummy = cui.getInput();
                         
                            dummyDouble =  Double.parseDouble(dummy); //Konverterar input till double
                            ds.arcPrio[i] = dummyDouble;
                            cui.pressedEnter = false;
                            writeOnce = false;
                            ds.arcColor[i] = 2; //Ändrar färdiga bågar till grön
                            cui.repaint();
                              i++;   
                            
                        }catch(NumberFormatException e){cui.appendError("Could not convert String to Integer.");}
                    }
                         
                     
                   
            
                }
       
                Arrays.fill(ds.arcColor, 0); //Sätter färgen till vit för alla bågar
                ds.paintPrio(); //Ritar ut de nya priofärgerna för bågarna.
                ds.skapaArcFil(); //Sparar ner de värden i arcPrio filen
                cui.appendStatus("================Completed================");
                finished = true;
              
             }
                
            }catch(InterruptedException e){cui.appendError("Something went wrong while changing prio");}
            
       }
        
      
    }
}
