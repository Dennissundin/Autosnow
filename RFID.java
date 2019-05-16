package autosnow;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Grupp 2
 */
public class RFID implements Runnable {
    private ControlUI cui;
    private DataStore ds;
    private boolean finished = false;
    private boolean writeOnce;
    private String dummy;
    private int dummyInt;
    private FileWriter outputWriter;
    private File outputFile;
   
    
    public RFID(DataStore ds, ControlUI cui)
    {
        this.cui = cui;
        this.ds = ds;
        writeOnce = false;
      
       
    }
    @Override
    public void run()
    {
          while(!finished)
        {   
            try
            {
                Thread.sleep(1000);
                if(cui.changeRFID) //Om knappen trycks att ändra RFID
                {
                    int i = 0;
                    while(i<ds.RFIDcounter)//kollar alla RFIDnoder
                   {
                        Thread.sleep(100);
                        if(!writeOnce)
                        {
                            cui.appendStatus("RFID-node: " + ds.RFID[i]);
                            writeOnce = true;
                        }
                        if(cui.pressedEnter) //Väntar på input från användare
                        {
                            try
                            {
                                dummy = cui.getInput();
                                dummyInt = Integer.parseInt(dummy);
                                ds.RFIDtag[i] = dummyInt; //Sparar ner ID till en array
                                cui.pressedEnter = false;
                                writeOnce = false;
                                i++;   
                                

                            }catch(NumberFormatException e){cui.appendError("Could not convert.");}
                        }
                }
                cui.appendStatus("================Completed================");     
                skapaRFIDFil();
                finished = true;
              
             }
                
            }catch(InterruptedException e){cui.appendError("Something went wrong with RFID");}
            
       }
        
      
    }
    /**
     * Skapar en RFID fil som innehåller alla RFID IDs samt det nodnummer som finns i vägnätet.
     */
    public void skapaRFIDFil()
    {
        String dummy = "";
       
    	try {
		outputFile = new File("C:\\Users\\denni\\Desktop\\RFIDList.txt");	
		outputFile.createNewFile();
		outputWriter = new FileWriter(outputFile,false);
                for(int i = 0; i < ds.RFIDcounter; i++)
                {
                    dummy = dummy + ds.RFID[i] + " " +  ds.RFIDtag[i] +"\n"; //Laddar in RFID och skriver den till filen.
                }
		
		outputWriter.write(dummy);	
		
		outputWriter.close(); 
			
		}
		catch(IOException ierr)
		{
			System.out.println("Sorry I could not write to the file");
		}
		catch(NullPointerException ierr)
		{System.out.println("outputWriter has not yet been initialzied");}
			
           
    }
}
