package autosnow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**** @author Grupp 2*/

public class DataStore {/*** @paramargs the command line arguments*/
    //Variabeldeklaration
    private ArrayList<Roadtype> listOfRoads;
    private FileWriter outputWriter;
    private File outputFile;
    //Double []
    double[] nodeX;
    double[] nodeY;
    double[] nodeXNew;
    double[] nodeYNew;
    double[] arcPrio;
    //double
    double vehicleX; 
    double vehicleY;
    double collectedPrioPoints;
    double distanceTraveled;

         
     //int []
    int[] nodesNew;
    int[] nodesIn;
    int[] nodes;
    int[] arcEndAll,arcStartAll;
    int[] arcStart,arcEnd;
    double[] xlength;
    double[] ylength;
    int[] jpos;
    int[] RFID;
    int[] RFIDRoute;
    double[] RFIDx, RFIDy;
   
    int[] arcColor;
    int [] nodeColor;
    int[] pathofnodes;
    int[] rectangleNodes;
    //String []
    int[] RFIDtag;
    int[] RFIDNodNummer;
    int[] RFIDNodNrDecision;
    
    
    //int
    int counter;
    int arcs;
    int startnode;
    int stopnode;
    int startNode_maxLink;
    int endNode_maxLink;
    int startNode_optPlan;
    int nodeCounter;
    int pointsOptPlan;
    int vagkostnad;
    int howManyNodes;
    int endNode_Heurustic;
    int pathcounter;
    int RFIDcounter;
    int CurrentPathCounter;
    int rectangleNodeCounter;
    int bluetoothRFIDnode;
    String [] RFIDdecision;
    boolean checkDecision;
    int decisionsMade;
    
    //Booleans
    boolean networkRead;
    boolean updateUIflag;
    boolean startMaxLink;
    boolean startSearch;
    boolean checkIfPrioLinkLeft;
    boolean startOptPlan;
    boolean startNoPrioLinksLeft;
    boolean startMaxLink_Heuristic;
    boolean checkIfPrioOneLeft;
    boolean optPlanIsOn;
    boolean carDown, carUp,carLeft,carRight;
    boolean createRFIDroute;
    boolean heuristicNoPath;
    boolean calculateNewRoute;
    boolean passedRFIDnode;
    boolean robotGo;
    boolean startClock;
  
  

   
    
    
    
    //Constructor för datastore
    public DataStore(){
        
        //Initierar variablerna.
        listOfRoads = new ArrayList<Roadtype>();
        nodes = new int[1000];
        nodesIn = new int[1000];
        nodeX = new double[1000];
        nodeY = new double[1000];
        nodeXNew = new double[1000];
        nodeYNew = new double[1000];
        nodesNew = new int[1000];
        arcStartAll = new int[1000];
        arcEndAll = new int[1000];
        arcStart = new int[1000];
        arcEnd = new int[1000];
        RFID = new int[1000];
        RFIDx = new double[1000];
        RFIDy = new double[1000];
        nodeColor = new int [1000];
        RFIDtag = new int[1000];
        rectangleNodes = new int[1000];
        RFIDNodNummer = new int [1000];
        RFIDNodNrDecision = new int[1000];
        robotGo = false;
        
        arcs = 0;
        collectedPrioPoints = 0;
        startnode = 0;
        stopnode = 3;
        startNode_maxLink = 0;
        endNode_maxLink = 0;
        startNode_optPlan = 1; // DENNA FÖR ATT ÄNDRA STARATPOSITION
        pointsOptPlan = 0;
        vagkostnad = 0;
        RFIDcounter =0;
        CurrentPathCounter = 0;
        bluetoothRFIDnode = 0;
        checkDecision = false;
        decisionsMade = 0;
        passedRFIDnode = false;

        endNode_Heurustic = 0;
        arcPrio = new double[1000];
        arcColor = new int[1000];
        xlength = new double[1000];
        ylength = new double[1000];
        jpos = new int[1000];
        networkRead = false;
        updateUIflag = false;
        startMaxLink = false;
        startSearch = true;
        howManyNodes = 0;
        startOptPlan = true;
        distanceTraveled = 0;
        pathofnodes = new int[1000];
        pathcounter = 0;
     
        carUp = false;
        carDown = false;
        carLeft = false;
        carRight = true;
        createRFIDroute = false;
        heuristicNoPath = false;
        calculateNewRoute = false;
        startClock = false;
        RFIDdecision = new String[1000];
        
     
       
    }
    
    
    public void load(){
        String line;
        String lineRoad = null;
       
        //Sätter filnamnen för de olika filerna
        String nodesAndPos = "C:\\Users\\denni\\Desktop\\streetstest.txt"; //Innehåller positioner för de möjliga noderna.
        String typeOfRoad = "C:\\Users\\denni\\Desktop\\roadnames.txt";  //Innehåller vilken typ av väg det är. 
        setFileName("C:\\Users\\denni\\Desktop\\arcList.txt"); //Innehåller priovärdena för länkarna.
        
        try {
            File file = new File(nodesAndPos);
            File file2 = new File(typeOfRoad);
            
            Scanner scanner = new Scanner(file, "iso-8859-1");
            Scanner scanner2 = new Scanner(file2, "iso-8859-1");
            
            String[] sline;
            int k = 0;
            
            //Lägger in Nodes och Arcs för varje block
            for(int i = 0; i < 15; i++){
                   lineRoad = scanner2.nextLine();
                   
                for(int j = 0; j < 12; j++){
                    line = scanner.nextLine();
                 
                    sline = line.split(" ");
                    nodeX[k] = Double.parseDouble(sline[1].trim());
                    nodeY[k] = Double.parseDouble(sline[2].trim());
                    nodes[k] = Integer.parseInt(sline[0].trim());
                    nodesIn[j] = nodes[k]; //skickar in endast 12 noder till listOfRoads
                   
                    k++;
                }
              
                listOfRoads.add(new Roadtype(nodesIn, lineRoad));
                listOfRoads.get(i).calculateArcs(); //Skapar arcStart och arcEnd i roadType
            }
            counter = 0;
            //Lägger ihop alla arcStart och arcEnd i en array. 
            for(int i = 0 ; i < 15; i++)
            {
                 for(int j = 0; j < listOfRoads.get(i).howmanyArcs();j++) //Loopar till så många arcs som skapades i roadtypes.
                    {
                        arcStartAll[counter] = listOfRoads.get(i).arcStart[j];
                        arcEndAll[counter] = listOfRoads.get(i).arcEnd[j];                                   
                        counter++;
                        arcs++;
                       
                    }
            
              
            }
        arcStart = arcStartAll;
        arcEnd = arcEndAll;
        removeDuplicateNodes(); //Tar bort alla noder som ligger på varandra.
        changeNodes(); //Ändrar så att noden längst ner till vänster är nod nr 1.
        loadArcPrio(); //Laddar in arcprio från filen.
        paintPrio(); //Går igenom arcprio och ändrar arcColor, vilket ritar upp bågarna i färger efter deras prioritering.
        loadRFIDList(); //Laddar in RFID textfilen
        loadRectangleNodes(); //Laddar in alla de noder som det ska ritas en triangel av.
        //Om all kod lyckas köras. 
        vehicleX = nodeXNew[startNode_optPlan-1];
        vehicleY = nodeYNew[startNode_optPlan-1];
        networkRead = true;
        
        
    } catch (Exception e) {
            e.printStackTrace(); //catch för någon error som sker vid inläsning av filerna.
      }
        
    }
    public void skapaArcFil()
    {
        String dummy = "";
       
    	try {
			
		outputFile.createNewFile();
		outputWriter = new FileWriter(outputFile,false);
                for(int i = 0; i < counter; i++)
                {
                    dummy = dummy +  arcPrio[i] +" \n"; //Laddar in arcprio och skriver den till filen.
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
    public void setFileName(String filename)
	{
	
            outputFile = new File(filename);

	}
    public void changeNodes()
    {
        boolean dummy;
     
         nodeCounter = 0;
     
        for(int i = 0; i < 180;i++)
        {
            dummy = false;
            for(int j = 0; j < arcs;j++)
            {                  
                 if((nodes[i] == arcStartAll[j]) && !dummy)
                 {
                     nodesNew[nodeCounter] = nodeCounter+1;
                     nodeXNew[nodeCounter] = nodeX[i];
                     nodeYNew[nodeCounter] = nodeY[i]; 
                       
                     dummy = true;
                     searchForNode(i+1,nodeCounter+1);
                    
                     //System.out.println(nodesNew[nodeCounter] + " xpos: " + nodeXNew[nodeCounter] + " ypos: " + nodeYNew[nodeCounter]);
                     
                     for(int k =0; k < jcounter; k++){
                            
                            if(nodes[i] == jpos[k]){
                  
                            RFID[RFIDcounter] = nodesNew[nodeCounter];            //sätter postion samt vilken nod RFID ligger i 
                            RFIDx[RFIDcounter] =  nodeXNew[nodeCounter];
                            RFIDy[RFIDcounter] =  nodeYNew[nodeCounter];
                            RFIDcounter++;
                            nodeColor[nodesNew[nodeCounter]-2] = 1;
                          
                            
                           }
                       
                        
                        }
                            nodeCounter++;
                       
                 }
     
            }
                 
             
        }
  
        
                     }
        int jcounter =0;

   public void removeDuplicateNodes()
    {
        for(int i = 0; i < 180; i++)
            {
                for(int j = 0; j < 180;j++)
                {
                    if((nodeX[i] == nodeX[j]) && (i != j))
                    {
                        if((nodeY[i] == nodeY[j]))
                        {
                           searchForNode(nodes[i],nodes[j]);     
                           
                           
                           jpos[jcounter] =  nodes[j];
                           jcounter++;
                          
                           
                        }
                      
                    }
                }
            }
    }
    
    public void searchForNode(int oldNode, int newNode)
    {
        for(int i = 0; i < arcs; i ++)
        {
            if(arcStartAll[i] == oldNode)
            {
                arcStart[i] = newNode;
                
            }
            if(arcEndAll[i] == oldNode)
            {
                arcEnd[i] = newNode;

            }
            
            
           
        }
    }
    
    public void loadArcPrio(){
      try {
            File myFile = new File("C:\\Users\\denni\\Desktop\\arcList.txt");
            String arcPrioString = null;
            
            Scanner scanner = new Scanner(myFile, "iso-8859-1");
     
            
      
  
            

            for(int i = 0; i < arcs; i++){
                   arcPrioString = scanner.nextLine();
                   arcPrio[i] = Double.parseDouble(arcPrioString);
                   
                
            }
             
    } catch (Exception e) { 
            }
    }
       
    public void loadRFIDList(){
      try {
          String[] dummyLine;
          String line = "";
            File myFile = new File("C:\\Users\\denni\\Desktop\\RFIDList.txt");
            Scanner scanner = new Scanner(myFile, "iso-8859-1");
            for(int i = 0; i < RFIDcounter; i++){
               line = scanner.nextLine();
               dummyLine = line.split(" ");
               RFIDtag[i] = Integer.parseInt(dummyLine[0].trim());
               RFIDNodNummer[i] = Integer.parseInt(dummyLine[1].trim());
            }
             
    } catch (Exception e) { 
            }
    }
    
    public void paintPrio() 
    {
           for(int j = 0; j < arcs;j++)
               {
                   if(arcPrio[j] == 0)
                   {
                       arcColor[j] = 4;
                   }
                   if(arcPrio[j] == 1)
                   {
                       arcColor[j] = 0;
                   }
                    if(arcPrio[j] == 2)
                   {
                       arcColor[j] = 3;
                   }
                       if(arcPrio[j] == 4)
                   {
                       arcColor[j] = 1;
                   }
                 
               }
    }  
 
    private void loadRectangleNodes()
    {
        rectangleNodeCounter = 0;
        
              try {
            File myFile = new File("C:\\Users\\denni\\Desktop\\rectNodes.txt");
            Scanner scanner = new Scanner(myFile, "iso-8859-1");
            while(scanner.hasNextLine()){
               rectangleNodes[rectangleNodeCounter] = Integer.parseInt(scanner.nextLine());
               rectangleNodeCounter++;
            }
             
    } catch (Exception e) { e.printStackTrace();
            }
        
    }
}




