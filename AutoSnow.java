package autosnow;



/**
 *
 * @author Grupp 2
 */
public class AutoSnow {

    DataStore ds;
    ControlUI cui;
    ChangePrio pri;
    Timer tim;
    BluetoothTransceiver blue; 
    CreateDecision cd;
    RFID rfid;
    noPrioLinksLeft np;
    MaxLink ml;
    OptPlan op;
 
    
    
    AutoSnow(){

        /*
         * Initialize the DataStore call where all "global" data will be stored
         */
        ds = new DataStore();
        
        /*
         * This sets the file path and read network text file.
        Adjust for your needs.
         */
       
        ds.load();

        /*
         * Initialize and show the GUI. 
        The constructor gets access to the DataStore
         */
        cui = new ControlUI(ds);
        cui.setVisible(true);
               

        pri = new ChangePrio(ds,cui);
        Thread t3 = new Thread(pri); 
        t3.start();
        
        op = new OptPlan(ds,cui);
        Thread t4 = new Thread(op);
        t4.start();
        
        ml = new MaxLink(ds,cui);
        Thread t5 = new Thread(ml);
        t5.start();
        
        np = new noPrioLinksLeft(ds,cui);
        Thread t6 = new Thread(np);
        t6.start();
        
        blue = new BluetoothTransceiver(cui,ds);
        Thread t7 = new Thread(blue);
       t7.start();
        
        cd = new CreateDecision(ds,cui);
        Thread t8 = new Thread(cd);
        t8.start();

     
        tim = new Timer(cui,ds);
        Thread t9 = new Thread(tim);
        t9.start();
        
        rfid = new RFID(ds,cui);
        Thread t10 = new Thread(rfid);
        t10.start();
  

 
    }
    
    
    public static void main(String[] args) {

        AutoSnow x = new AutoSnow();
    }
}
