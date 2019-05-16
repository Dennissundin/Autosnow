/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package autosnow;


 /**
 *
 * @author Grupp 2
 */
public class CreateDecision implements Runnable {
   
    private ControlUI cui;
    private DataStore ds;  

    
    int RFIDcounter;
    int[] pathofnodesNew;
    int realpathcounter;
    int realCurrentPathCounter;
    int currentRFIDcounter;
    

    
    public CreateDecision(DataStore ds, ControlUI cui)
    {
        this.cui = cui;
        this.ds = ds;
        ds.RFIDRoute = new int[1000];
        pathofnodesNew = new int[1000];
        RFIDcounter = 0;
        realpathcounter = 0;
      
   
    
       
    }
    @Override
    public void run()
    {   
      while(true)
      {
          try
          {
             Thread.sleep(500);
             if(ds.createRFIDroute)
             {
             //   realpathcounter = 0;
                realCurrentPathCounter = 0;
         
                for(int i = ds.pathcounter-ds.CurrentPathCounter; i < ds.pathcounter;i++) //Att man har en CurrentPathCounter och en TotalPathCounter, så att hela rutten inte behöver räknas om.
                {//Man kan skriva for-loopen for(int i=TotalPathcounter-CurrentPathCounter; i < TotalPathCounter; i++)
                   if(i == 0)
                   {
                        pathofnodesNew[realpathcounter] = ds.pathofnodes[i]; //Nya rutten utan extranoder.
                        realpathcounter++; //Rutten där samma noder inte räknas två gånger när optplan går till maxlink ;)
                        realCurrentPathCounter++; //En counter för varje sökning.   
                   }
                   else
                   {
                      
                            if(ds.pathofnodes[i-1] != ds.pathofnodes[i])
                            {
                              pathofnodesNew[realpathcounter] = ds.pathofnodes[i]; //Nya rutten utan extranoder.
                              realpathcounter++; //Rutten där samma noder inte räknas två gånger när optplan går till maxlink ;)
                              realCurrentPathCounter++; //En counter för varje sökning.   
                            }
                 
                       
                    }
                }
                
             
                 currentRFIDcounter = 0; //Counter för hur många RFID noder som ligger i n sökning.
                
                for(int i = realpathcounter-realCurrentPathCounter; i < realpathcounter;i++)
                {
                    for(int j = 0; j < ds.RFIDcounter;j++)
                    {

                       if((pathofnodesNew[i]) == ds.RFID[j] ) //Om RFID nod finns i path läggs den in i en RFIDRoute som bara består av alla RFID noder.
                       { 
                       ds.RFIDRoute[RFIDcounter] = ds.RFID[j];      
                       RFIDcounter++;
                       currentRFIDcounter++;
                       }
                    }
                }
               
     
                cui.appendStatus("=====RFID Noder i rutten=====");
                //Skriver ut all RFIDnoder som ligger i den rutten som körs i statusrutan
             
               

                //Skapar en array med de besulut som finns i rutten, läggs till i
                //ds.RFIDdecision samt det nodNummer som tillhör beslutet i RFIDNodNrDecision
            for(int i = ds.decisionsMade; i < RFIDcounter;i++)
            {
                if(!checkIfLast(ds.RFIDRoute[i]))
                {
                    ds.RFIDdecision[i] = checkDecision(ds.RFIDRoute[i]);
                    ds.decisionsMade++;
                    ds.RFIDNodNrDecision[i] = ds.RFIDRoute[i];
                    cui.appendStatus("RFIDNod: " + ds.RFIDRoute[i]);
                    
                    ds.RFIDRoute[i] = 0;
                }
                else
                {
                    break;
                }
                
            }

           
        
           ds.createRFIDroute = false;
                
                 }
         
          
          }
          catch(InterruptedException e){e.printStackTrace();}
          if(cui.stopProgram())
          {
              break;
          }
          
      }
       
    }
    /**
     * Funktionen tar in en RFID nod som ligger i rutten. Jämför sedan med den RFID noden som ligger efter för att bestämma hur 
     * Roboten ska ta sig dit, 
     * @param RFIDCurrent Den RFID nod som undersöks
     * @return Beslut att köra
     */
    public String checkDecision(int RFIDCurrent)
    {
       
 
        for(int i = 0; i < RFIDcounter-1;i++) 
        {
            if(RFIDCurrent == ds.RFIDRoute[i])
            {
                if(checkUturn(ds.RFIDRoute[i],ds.RFIDRoute[i+1]))
                {
                    return "Tu";
                    
                }
                else if(checkLeftTurn(ds.RFIDRoute[i],ds.RFIDRoute[i+1]))
                {
                     return "T<";
                     
                }
                else if(checkRightTurn(ds.RFIDRoute[i],ds.RFIDRoute[i+1]))
                {
                    return "T>";
                     
                }
                else
                {
                    return "T^";
                     
                }
                        
            }
        }
        return "";

    }
    /**
     * 
     * @param RFIDCurrent RFIDnod som undersöks
     * @return true om RFIDCurrent är sista nod i rutten
     */
    public boolean checkIfLast(int RFIDCurrent)
    {
        for(int i = 0; i < RFIDcounter;i++)
        {
            if(RFIDCurrent == ds.RFIDRoute[i])
            {
                if(ds.RFIDRoute[i+1] == 0)
                {
                    return true;
                }
               
            }
        }
        return false;
    }
    /**
     * Kollar om beslutet är att ta en usväng
     * @param RFIDCurrent RFIDnod som undersöks i rutten
     * @param RFIDNext Nästa RFIDnod i rutten
     * @return true om det är Usväng
     */
    private boolean checkUturn(int RFIDCurrent, int RFIDNext)
    {
        
       double extraY = ds.nodeYNew[RFIDCurrent-1] + 30;
       double mindreY =ds.nodeYNew[RFIDCurrent-1] - 30;
       double extraX =ds.nodeXNew[RFIDCurrent-1] + 30;
       double mindreX =ds.nodeXNew[RFIDCurrent-1] - 30;

        if(ds.nodeXNew[RFIDNext-1] == ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] == (extraY ))
        {
          return true;  
        }
        else if(ds.nodeXNew[RFIDNext-1] == ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] == (mindreY))
        {
         return true;   
        }
        else if(ds.nodeXNew[RFIDNext-1] == extraX && ds.nodeYNew[RFIDNext-1] == (ds.nodeYNew[RFIDCurrent-1] ))
        {
            return true;
        }
        else if(ds.nodeXNew[RFIDNext-1] == mindreX && ds.nodeYNew[RFIDNext-1] == (ds.nodeYNew[RFIDCurrent-1] ))
        {
            return true;
        }
        
        return false;
    }
     /**
     * Kollar om beslutet är att ta en vänstersväng
     * @param RFIDCurrent RFIDnod som undersöks i rutten
     * @param RFIDNext Nästa RFIDnod i rutten
     * @return true om det är vänstersväng
     */
    private boolean checkLeftTurn(int RFIDCurrent, int RFIDNext)
    {
        int nodeAfterCurrent = 0;
        for (int i = 0; i< realpathcounter; i++) {
          if (pathofnodesNew[i] == RFIDCurrent){
          nodeAfterCurrent = pathofnodesNew[i+1];

            break;
        }
        }

        if(ds.nodeXNew[RFIDNext-1] > ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] > ds.nodeYNew[RFIDCurrent-1] && ds.nodeXNew[nodeAfterCurrent-1] != ds.nodeXNew[RFIDCurrent -1] )//Den man länken man är på ändras först i x-led
        {
          return true;  
        }
        else if((ds.nodeXNew[RFIDNext-1] > ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] < ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeYNew[nodeAfterCurrent-1] != ds.nodeYNew[RFIDCurrent -1])) 
        {
         return true;   
        }
        else if((ds.nodeXNew[RFIDNext-1] < ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] > ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeYNew[nodeAfterCurrent-1] != ds.nodeYNew[RFIDCurrent -1]))
        {
         return true;
        }
        else if((ds.nodeXNew[RFIDNext-1] < ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] < ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeXNew[nodeAfterCurrent-1] != ds.nodeXNew[RFIDCurrent -1]))
        {
            return true;
        }

        return false;
    }
     /**
     * Kollar om beslutet är att ta en högersväng
     * @param RFIDCurrent RFIDnod som undersöks i rutten
     * @param RFIDNext Nästa RFIDnod i rutten
     * @return true om det är högersväng
     */
     private boolean checkRightTurn(int RFIDCurrent, int RFIDNext)
    {
        int nodeAfterCurrent = 0;
        for (int i = 0; i< realpathcounter; i++) {
            if (pathofnodesNew[i] == RFIDCurrent){
                nodeAfterCurrent = pathofnodesNew[i+1];
      
                break;
            }
        }
       
        if(ds.nodeXNew[RFIDNext-1] > ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] > ds.nodeYNew[RFIDCurrent-1] && ds.nodeYNew[nodeAfterCurrent-1] != ds.nodeYNew[RFIDCurrent -1] )//Den man länken man är på ändras först i x-led
        {
          return true;  
        }
        else if((ds.nodeXNew[RFIDNext-1] > ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] < ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeXNew[nodeAfterCurrent-1] != ds.nodeXNew[RFIDCurrent -1])) 
        {
         return true;   
        }
        else if((ds.nodeXNew[RFIDNext-1] < ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] > ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeXNew[nodeAfterCurrent-1] != ds.nodeXNew[RFIDCurrent -1]))
        {
         return true;
        }
        else if((ds.nodeXNew[RFIDNext-1] < ds.nodeXNew[RFIDCurrent-1] && ds.nodeYNew[RFIDNext-1] < ds.nodeYNew[RFIDCurrent-1] &&  ds.nodeYNew[nodeAfterCurrent-1] != ds.nodeYNew[RFIDCurrent -1]))
        {
         return true;
        }

        return false;
    }    
}


