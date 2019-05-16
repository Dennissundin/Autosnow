/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autosnow;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author vikto
 */
public class noPrioLinksLeft implements Runnable{
    
    private List <Vertex> nodes;
    private List <Edge> edges;
    private DataStore ds;
    private ControlUI cui;
    private boolean firstTime,breakLoop;
    private int[] connectedArray;
    private double prio_dummy,prioBestHeuristicLink,linkPrio_dummy, weightFactor, HeuristicPrio,length_VS_prio,d_dummy, d;
    private double [] weight;
    private boolean firstTimeHeu;
    
    private int prioWeight,startNodeHeuristic_dummy, endNodeHeuristic ,noOfConnectedLinks;
    
    public noPrioLinksLeft(DataStore ds, ControlUI cui){
    this.ds = ds;
    this.cui = cui;
     // ########### BYT VIKTER ############
     weightFactor = 10;          // ökas denna ökar prioritetsvärden gentemot avstånd
     prioWeight = 20;
     firstTimeHeu = false;
     // ###################################
        
    }
 @Override
 
public void run(){
    try{
        int k =1;
        while(k <= 3){   
        Thread.sleep(500);

            if(ds.startNoPrioLinksLeft == true){
                       while(true)
                      {
                        if(firstTimeHeu)
                        {
                            break;
                        }
                   
                        Thread.sleep(500);
                        try
                        {
                            if(ds.calculateNewRoute)
                            {
                             
                                    ds.calculateNewRoute = false;
                                    firstTimeHeu = true;
                                    break;
          
                            }
                        }catch(NullPointerException e)
                        {System.out.println("");}
                    }
                
                for (int j = 0; j < ds.arcs; j++){
                    if(ds.arcPrio[j] == 1){
                        
                        ds.checkIfPrioOneLeft = true;
                        createPlan();
                        break;
                    }
                }
                
  
               
           if(ds.startNoPrioLinksLeft == true)
           {
                if(ds.checkIfPrioOneLeft == false){
                    cui.appendStatus("========Route done========");
              
                    break;
                }
            }
           while(true)
                {
                    Thread.sleep(500);
                    if(ds.startNoPrioLinksLeft == true)
                    {
                        break;
                    }
                }
            
       
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
            }
            
        }
        
        k++;
    } 
    catch(InterruptedException exception){
    }
}
public void createPlan(){
    nodes = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>();
    
    prio_dummy = 0;
    linkPrio_dummy = 0;
    HeuristicPrio =100000;
    endNodeHeuristic =0;
    ds.checkIfPrioOneLeft = false;
    weight = new double[1000];
    breakLoop = false;
    connectedArray = new int[1000];
            
            
    if(ds.startNoPrioLinksLeft == true){  
        for (int i =0; i < ds.nodeCounter; i++){
            Vertex location = new Vertex(""+(i+1), "nod# " + (i+1)); // sätter noder
            nodes.add(location);
        }
        for(int i = 0; i < ds.arcs; i++){   // viktar prio så inte bara kortast möjliga avstånd körs utan även tar hänsyn till priopoäng.
            weight[i] = ds.arcPrio[i]*weightFactor;    // högre weightfaktor ger lägre weight
            Edge lane = new Edge(""+(i+1), nodes.get(ds.arcStart[i] - 1), nodes.get(ds.arcEnd[i]-1), ((ds.xlength[i] +ds.ylength[i]) - weight[i])); // sista argumentet är vikt + avstånd för länk. Dijkstra minimerar sista argumentet.

            edges.add(lane);  
        }
        
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm djikstra = new DijkstraAlgorithm(graph); 
        
        for(int i = 0; i < ds.arcs; i++){           // räknar ut vilken rad( båge ) som har högsta prio 
            if (prio_dummy <= ds.arcPrio[i] && ds.arcPrio [i] > 0.5){      
                firstTime = true;
                noOfConnectedLinks = 0;
                prio_dummy = ds.arcPrio[i];
                linkPrio_dummy = ds.arcPrio[i];   //placeholder prio för en sammansatt prioriterad länk
                startNodeHeuristic_dummy = ds.arcStart[i]; // placeholder startpunkt för den sammansatta prioriterade länken

          for(int j = 0; j < ds.arcs; j++){
                    if(startNodeHeuristic_dummy == ds.arcEnd[j]){     
                        if(ds.arcPrio[j] == 1 ){   // kollar ifall den sammansatta prioriterade länken är längre                       
                            linkPrio_dummy = linkPrio_dummy + ds.arcPrio[j];  // ökar den totala prion
                            startNodeHeuristic_dummy  = ds.arcStart[j];               // flyttar startpunkten för sammansatt priolänk
                            

                            if(firstTime == true){                                     
                                connectedArray[noOfConnectedLinks] = ds.arcEnd[j]; 
                             
                                    firstTime = false;                                
                            }
                            if( firstTime == false){                                   
                                for(int k =0; k < connectedArray.length; k++){      
                                    if(startNodeHeuristic_dummy == connectedArray[k]){       
                                        startNodeHeuristic_dummy = ds.arcStart[j];
                                    
                                        breakLoop = true;
                                        break;                                       
                                    }
                                }
                            }

                            noOfConnectedLinks = noOfConnectedLinks  +1;                                                  
                            j= -1;          // sätter j = -1 så nya startpunkten jämförs med alla       
                        }
                    }
                    if(breakLoop)
                    {
                        break;
                    }
                }                            

   
                djikstra.findDistances(nodes.get(ds.endNode_maxLink-1)); 
                d_dummy = djikstra.getDistanceToTarget(nodes.get(startNodeHeuristic_dummy-1)); 
                length_VS_prio = d_dummy-linkPrio_dummy*prioWeight;

                if(HeuristicPrio > length_VS_prio){
                    HeuristicPrio = length_VS_prio;
                    prioBestHeuristicLink = linkPrio_dummy;
                     ds.startNode_maxLink = startNodeHeuristic_dummy;   // sätter faktiskt startnod för prioriterad länk
                    endNodeHeuristic = ds.arcEnd[i];           // sätter faktiskt slutnod för prioriterad länk
                    d = d_dummy;
                }
            }
        }
                                       
   
        if(ds.endNode_maxLink ==  ds.startNode_maxLink){ // om man står i punkten man siktar mot
            ds.endNode_maxLink = endNodeHeuristic; // ger endNode_maxLink i datastore ett värde så class maxlink kan använda
            ds.startNoPrioLinksLeft = false;           // pausar Heurustic
            ds.startMaxLink = true;
            ds.heuristicNoPath = true;
        }
        else{
           
            djikstra.execute(nodes.get(ds.endNode_maxLink-1)); //dit föregående slutade
            LinkedList<Vertex> path = djikstra.getPath(nodes.get( ds.startNode_maxLink-1)); //dit prio länk börjar
            ds.CurrentPathCounter = 0;
            for(int i = 0; i < path.size(); i++){
                ds.pathofnodes[ds.pathcounter] = Integer.valueOf(path.get(i).getId());
                ds.pathcounter++;
                ds.CurrentPathCounter++;           
            }
      
            for(int i =0; i < path.size()-1; i++){
                for (int j =0; j < ds.arcs; j++){          
                    if (ds.arcStart[j] == Integer.parseInt(path.get(i).getId()) && ds.arcEnd[j] == Integer.parseInt(path.get(i+1).getId())){
                        if(ds.arcPrio[j] >= 1){
                            ds.collectedPrioPoints = ds.collectedPrioPoints +   ds.arcPrio[j]; // räkna poäng
                            ds.arcPrio[j] = 0.1;   // om väg blivit plogad sätt dess prio till 0.1
                        }
                        ds.arcColor[j] = 3;         // rita ut i annan färg                  
                        ds.distanceTraveled =   ds.distanceTraveled +  ds.xlength[j] + ds.ylength[j]; // beräkna längd för körd sträcka
                    } 
                }
            }
             
        for (int j =0; j < path.size()-1; j++){  //ifall en prioriterad väg blivit plogad, kolla detta och beräkna ny prioLink.
            for (int i = 0; i < ds.arcs; i ++ ){
                if (ds.arcStart[i] == Integer.parseInt(path.get(j).getId()) && ds.arcEnd[i] == Integer.parseInt(path.get(j+1).getId())){
                    if(Integer.parseInt(path.get(j+1).getId()) == endNodeHeuristic){
                        if(ds.arcPrio[i]==0.1){  // kollar om sista länken i maxLink är plogad
                            endNodeHeuristic = ds.arcStart[i];    // sätter slutNod i maxLink till startNod av sista länken för att inte köra där det plogats                    
                            i = -1;      // sätter i = -1 för att jämföra nya slutnoden i maxlink med alla.
                        }
                    }
                }
            }
        } 
        ds.endNode_maxLink = endNodeHeuristic; // ger endNode_maxLink i datastore ett värde så class maxlink kan använda
        ds.startNoPrioLinksLeft = false;           // pausar Heurustic
        ds.startMaxLink = true;
        ds.heuristicNoPath = false;
        }
    }
    cui.appendPoints(String.valueOf(ds.collectedPrioPoints));
    cui.appendDistance(String.valueOf(ds.distanceTraveled));

}
}
     



    

    
    

