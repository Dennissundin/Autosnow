/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autosnow;

import java.util.*;

/**
 *
 * @author Grupp 2
 */

public class OptPlan implements Runnable{
   private List <Vertex> nodes;
   private List <Edge> edges;
   private DataStore ds;
  
   
   private double [] weight;
   private int noConnectedLinks_inMaxLink,startNodeMaxLink,endNodeMaxLink, startNodeMaxLink_dummy, prioWeight;
   private double linkPrio_dummy, prio_dummy,maxLinkPrio,length_VS_prio,prioBestLink,weightFactor;
   private ControlUI cui;
   private double d, weightedDistance;
  

public OptPlan(DataStore ds,ControlUI cui){
     this.ds = ds;
     this.cui = cui;

     
     // ########### BYT VIKTER ############
     weightFactor = 10;          // ökas denna ökar prioritetsvärden gentemot avstånd
     prioWeight = 20;
     // ###################################
}
 
@Override
 
public void run(){
    //antal sökningar
    int k =1;
    int no_searches = 15;
    while(true){
        try{
            Thread.sleep(500);
            if(cui.getButtonState()){
                
                while(k <= no_searches){
                    
                    Thread.sleep(500);              
                    for (int j = 0; j < ds.arcs; j++){
                        if(ds.arcPrio[j] > 1){
                            ds.checkIfPrioLinkLeft = true;
                            if(ds.startOptPlan== true){
                                ds.optPlanIsOn = true;
                               
                                createPlan();
                                cui.repaint();
                            }
                        }                        
                    }
                    while(true)
                    {
                        Thread.sleep(500);
                        if(ds.startOptPlan ==true)
                        {
                            break;
                        }
                    }                 
                        if(ds.checkIfPrioLinkLeft == false){
                            System.out.println("                                                                                                                no prio paths left  ");
                            ds.optPlanIsOn = false;
                            ds.createRFIDroute = true;
                            ds.startOptPlan = false;
                            ds.startNoPrioLinksLeft = true;
                               
                            break;
                        } 
                   
                    //Här är en sökning gjord
                    ds.createRFIDroute = true;
                    while(true)
                    {
                        Thread.sleep(500);
                        try
                        {
                                
                                if(ds.calculateNewRoute)//Ska breaka när robot är i sista RFID noden i en path
                                {
                                    ds.calculateNewRoute = false;
                                    break;
                                    
                                }
                   
                                
                              
                            
                        }catch(NullPointerException e)
                    {System.out.println("Something went wrong in optplan");}
                    }
                    
                    k++;
                  
                }
                break;
            }
        }
        catch(InterruptedException exception){
        }
    }
}
    
public void createPlan(){
    
    nodes = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>(); 
    
    prio_dummy =0;
    startNodeMaxLink_dummy = 0;
    maxLinkPrio = 10000;
    endNodeMaxLink =0;
    startNodeMaxLink =0;
    linkPrio_dummy = 0;
    d = 100000;
   
    weight = new double[1000];
    ds.checkIfPrioLinkLeft = false;
      

    if(ds.startOptPlan == true){
        for(int i =0; i < ds.nodeCounter; i++){
            Vertex location = new Vertex(""+(i+1), "nod# " + (i+1)); 
            nodes.add(location);
        }
       
        for(int i = 0; i < ds.arcs; i++){   // viktar prio så inte bara kortast möjliga avstånd körs utan även tar hänsyn till priopoäng.
            weight[i] = ds.arcPrio[i]*weightFactor;    // högre weightfaktor ger lägre weight
            Edge lane = new Edge(""+(i+1), nodes.get(ds.arcStart[i] - 1), nodes.get(ds.arcEnd[i]-1), ((ds.xlength[i] +ds.ylength[i]) -weight[i])); // sista argumentet är vikt + avstånd för länk. Dijkstra minimerar sista argumentet.

         edges.add(lane);
        }
        
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm djikstra = new DijkstraAlgorithm(graph); 
         
        for(int i =0; i < ds.arcs; i++){           // räknar ut vilken rad( båge ) som har högsta prio 
            if (2 <= ds.arcPrio[i] && ds.arcPrio[i] > 1){  

                linkPrio_dummy = ds.arcPrio[i];   //placeholder prio för en sammansatt prioriterad länk
                noConnectedLinks_inMaxLink = 1;     // sätter antalet sammansatta länkar till 1.
                startNodeMaxLink_dummy = ds.arcStart[i]; // placeholder startpunkt för den sammansatta prioriterade länken
                
                
                for(int j = 0; j < ds.arcs; j++){
                    if(startNodeMaxLink_dummy == ds.arcEnd[j]){     
                        if(ds.arcPrio[j] > 1 ){   // kollar ifall den sammansatta prioriterade länken är längre
                            linkPrio_dummy = linkPrio_dummy + ds.arcPrio[j];  // ökar den totala prion
                            noConnectedLinks_inMaxLink = noConnectedLinks_inMaxLink + 1;  // ökar antalet sammansatta länkar                      
                            startNodeMaxLink_dummy  = ds.arcStart[j];               // flyttar startpunkten för sammansatt priolänk
                            j= -1;          // sätter j = -1 så nya startpunkten jämförs med alla  
                        }
                    }
                }
                // skickar var vi är så vi kan räkna ut distanser till olika siktpunkter
                djikstra.findDistances(nodes.get(ds.startNode_optPlan-1)); 
                
                // får tillbaka viktade distanser till olika siktpunkter för att räkna kostnad att köra till dessa. 
                weightedDistance = djikstra.getDistanceToTarget(nodes.get(startNodeMaxLink_dummy-1)); 
                
                // hittar bästa prioriterade länken genom att ta kostnad att köra till länken - prion för prioriterade länken. 
                length_VS_prio = weightedDistance - linkPrio_dummy*prioWeight;   // räknar ut vart den ska sikta genom

              // Välj siktpunkt efter minsta värde från olika priobågar. Med värde menas en kostnad att köra på väg till noden man siktar mot. Sedan dras prion för länken man siktar bort* en vikt bort från kostanden för att hitta en bra båge att köra.
                
                if(maxLinkPrio > length_VS_prio){ 
                    maxLinkPrio = length_VS_prio;
                    prioBestLink = linkPrio_dummy;                  // sätter prio på bästa länken till nytt värde
                    startNodeMaxLink = startNodeMaxLink_dummy;   // sätter faktiskt startnod för prioriterad länk
                    endNodeMaxLink = ds.arcEnd[i];              // sätter faktiskt slutnod för prioriterad länk
                    d = weightedDistance;                       
                }
            } 
        }
        
                                                                           
        ds.startNode_maxLink = startNodeMaxLink; // ger startNode_maxLink i datastore ett värde så class maxlink kan använda denna.
                     
 
   
        
        
        if(ds.startNode_optPlan == ds.startNode_maxLink){ // ifall prio i början
            ds.endNode_maxLink = endNodeMaxLink; // ger endNode_maxLink i datastore ett värde så class maxlink kan använd
            ds.startOptPlan = false;           // pausar Optplan
            ds.startMaxLink = true;            // startar MaxLink
        }
        
        else{
        djikstra.execute(nodes.get(ds.startNode_optPlan-1));  // hämtar startnod från datastore, samt skickar till dijkstras
        LinkedList<Vertex> path = djikstra.getPath(nodes.get(ds.startNode_maxLink-1)); // skickar in startpunkt för maxLink och får tillbaka optimal väg dit
         ds.CurrentPathCounter = 0;
        for(int i = 0; i < path.size(); i++){
            
            ds.pathofnodes[ds.pathcounter] = Integer.valueOf(path.get(i).getId());
            ds.pathcounter++;
            ds.CurrentPathCounter++;
            
        }
            
        

   
                   
        //länkar i billigaste väg
         
        for (int i =0; i < path.size()-1; i++){
            for (int j =0; j< ds.arcs; j++){
                if (ds.arcStart[j] == Integer.parseInt(path.get(i).getId()) && ds.arcEnd[j] == Integer.parseInt(path.get(i+1).getId())){        
                    if(ds.arcPrio[j] >= 1){
                        ds.collectedPrioPoints = ds.collectedPrioPoints +  ds.arcPrio[j]; // räkna poäng
                        ds.arcPrio[j] = 0.1;   // om väg blivit plogad sätt dess prio till 0.1
                    }
                    ds.arcColor[j] = 1;       // rita ut i annan färg
                    ds.distanceTraveled =  ds.distanceTraveled + ds.xlength[j] + ds.ylength[j]; // beräkna längd för körd sträcka
                    cui.repaint();
                }
            }
        }
  
        for (int j =0; j < path.size()-1; j++){  //ifall en prioriterad väg blivit plogad, kolla detta och beräkna ny prioLink.
            for (int i = 0; i < ds.arcs; i ++ ){
                if (ds.arcStart[i] == Integer.parseInt(path.get(j).getId()) && ds.arcEnd[i] == Integer.parseInt(path.get(j+1).getId())){
                    if(Integer.parseInt(path.get(j+1).getId()) == endNodeMaxLink){
                        if(ds.arcPrio[i]==0.1){  // kollar om sista länken i maxLink är plogad
                            endNodeMaxLink = ds.arcStart[i];    // sätter slutNod i maxLink till startNod av sista länken för att inte köra där det plogats
                            i = -1;      // sätter i = -1 för att jämföra nya slutnoden i maxlink med alla.
                        }
                    }
                }
            }
        } 
        ds.endNode_maxLink = endNodeMaxLink; // ger endNode_maxLink i datastore ett värde så class maxlink kan använda
        ds.startOptPlan = false;           // pausar Optplan
        ds.startMaxLink = true;            // startar MaxLink
    }
}
cui.appendPoints(String.valueOf(ds.collectedPrioPoints));
cui.appendDistance(String.valueOf(ds.distanceTraveled));    

}     
}
         
     



    
