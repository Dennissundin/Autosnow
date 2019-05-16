/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autosnow;
import autosnow.DataStore;
import java.lang.Math;
import static java.lang.Math.abs;
        /**
 *
 * @author denni
 */
public class PositionVehicle implements Runnable {
    private int sleepTime;
    private ControlUI cui;
    private DataStore ds;
    boolean finished = false;
    double diffX =0;
    double diffY = 0;
    int counterY = 0;
    int i = 0;
    int howManyPassed = 0;

    
    
    public PositionVehicle(DataStore ds, ControlUI cui)
    {
        this.cui = cui;
        this.ds = ds;
        sleepTime =10;
       
    }
    @Override
    public void run()
    {   
      
       
        while(!finished){
        try
        {
           Thread.sleep(1000);
        
            if(ds.robotGo)
            {
                
               while(true)
               {
                   //Ser till att den inte uppdaterar framför en rfidnod den inte har passerat
                   if(ds.pathofnodes[i] == ds.RFIDNodNrDecision[howManyPassed])
                   {
                   
                       while(true)
                       {
                           Thread.sleep(1000);
                           if(ds.passedRFIDnode)
                           {
                               howManyPassed++;
                               i++;
                               ds.passedRFIDnode = false;
                               break;
                           }
                       }
                   }
                   System.out.println("uppdatera postion!!!!!!!");
                   //Uppdaterar position
                  
                   diffX = ds.nodeXNew[ds.pathofnodes[i+1]-1]-ds.nodeXNew[ds.pathofnodes[i]-1];
                   diffY = ds.nodeYNew[ds.pathofnodes[i+1]-1]-ds.nodeYNew[ds.pathofnodes[i]-1];
                   
                   if(diffX > 0 && diffY == 0) //Åker åt höger
                   {
                       ds.carDown = false;
                       ds.carLeft = false;
                       ds.carRight = true;
                       ds.carUp = false;
                       for(int k = 0; k < diffX;k++)
                       {
                           Thread.sleep(50);
                           ds.vehicleX++;
                           cui.repaint();
                       }
                   }
                   else if(diffX < 0 && diffY == 0) //Åker åt vänster
                   {
                       ds.carDown = false;
                       ds.carRight = false;
                       ds.carLeft = true;
                       ds.carUp = false;
                       for(int k = 0; k < abs(diffX);k++)
                       {
                           Thread.sleep(50);
                           ds.vehicleX--;
                           cui.repaint();
                       }
                   }
                   else if(diffY > 0 && diffX == 0) //Åker uppåt
                   {
                       ds.carDown = false;
                       ds.carLeft = false;
                       ds.carRight = false;
                       ds.carUp = true;
                       for(int k = 0; k < diffY;k++)
                       {
                           Thread.sleep(50);
                           ds.vehicleY++;
                           
                           cui.repaint();
                       }
                   }
                   else if(diffY < 0 && diffX == 0) //Åker neråt
                   {
                       ds.carDown = true;
                       ds.carRight = false;
                       ds.carLeft = false;
                       ds.carUp = false;
                       for(int k = 0; k < abs(diffY);k++)
                       {
                           Thread.sleep(50);
                           ds.vehicleY--;
                           
                           cui.repaint();
                       }
                   }

                   i++;
                   cui.repaint();
      
                   if(!cui.getButtonState())
                   {
                       break;
                   }
                   
               }
               
                        
          
               finished = true;                 
            }
          
          
            
            
        }catch(InterruptedException exception){
        }
        
        }
 
        
        
    }
}



