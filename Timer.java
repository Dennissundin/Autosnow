package autosnow;

/**
 * Detta är en thread som räknar tiden från och med vi startar körningen.
 * @author Grupp 2
 */
public class Timer implements Runnable{
    ControlUI cui;
    DataStore ds;
    int seconds = 0;
    int minutes = 0;
    boolean finished = false;
    public boolean tenSeconds = false;
    public Timer(ControlUI cui,DataStore ds)
    {
      
        this.cui = cui;
        this.ds = ds;
    }
     @Override
    public void run() //Thread för timer över körtid.
    {
        while(!finished)
        {
            
        try
        {
            Thread.sleep(1000);
              if(ds.startClock)
         {
            for(;;)
            {
              
              seconds++;
    
                if(seconds > 60)
                {
                    seconds = 0;
                    minutes++;
                }
                
                
                Thread.sleep(1000);
                if(!cui.getButtonState())
                {
                    finished = true;
                    break;
                }
                
             
                cui.appendTimer(minutes + ":" + seconds);
                
            }
         }
        }catch(InterruptedException e)
        {cui.appendError("Something went wrong with the timer");}
        
        }
      
        
    }
    
    
}

