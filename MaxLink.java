/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autosnow;

//              denna class ska köra en båge



import java.util.*;

/**
 *
 * @author vikto
 */

public class MaxLink implements Runnable{
    private List <Vertex> nodes;
    private List <Edge> edges;
    private DataStore ds;
    private ControlUI cui;
    private int [] weight;
    private int weightFactor;

  
    public MaxLink(DataStore ds,ControlUI cui){
        this.ds = ds;
        this.cui = cui;
        
    }
    
 @Override
 
public void run(){
    try{
        int k =1;
        while(k <= 3){      
            Thread.sleep(500);
            if(ds.startMaxLink== true){
                createPlan();
                cui.repaint();
               
            } 
        }
        k++;
    } 
    catch(InterruptedException exception){
    }
}
public void createPlan(){
    // denna class kör en sammansatt prioriterad länk som skapats i optplan. Den hämtar sin start och slutpunkt från datastore.
    nodes = new ArrayList<Vertex>();
    edges = new ArrayList<Edge>();  
    weightFactor = 2000;          // ökas denna ökar prioritetsvärden gentemot avstånd för den ska köra enligt prion
    weight = new int[1000];
    if(ds.startMaxLink == true ){      
                     

        for (int i =0; i < ds.nodeCounter; i++){
            Vertex location = new Vertex(""+(i+1), "nod# " + (i+1)); 
            nodes.add(location);
        }
   
        for (int i = 0; i < ds.arcs; i++){
          weight[i] = (int) -ds.arcPrio[i]*weightFactor;    // högre weightfaktor ger lägre weight

            Edge lane = new Edge(""+(i+1), nodes.get(ds.arcStart[i] - 1), nodes.get(ds.arcEnd[i]-1),(weight[i]-1) +1); 
            edges.add(lane);            // vikten här är 1 eftersom maxlink ska köra en prioriterade båge som är hittad, längd spelar ingen roll.
        }
         
        Graph graph = new Graph(nodes, edges);
        DijkstraAlgorithm djikstra = new DijkstraAlgorithm(graph); 
        ds.startNode_optPlan = ds.endNode_maxLink;  // sätter OptPlans nya startpunkt till slutpunkten i MaxLink 
       // ds.startNode_Heurustic = ds.endNode_maxLink;
      
        djikstra.execute(nodes.get(ds.startNode_maxLink -1));
        LinkedList<Vertex> path = djikstra.getPath(nodes.get(ds.endNode_maxLink -1));
        if(ds.heuristicNoPath)
        {
            ds.CurrentPathCounter = 0;
        }
        for(int i = 0; i < path.size(); i++){
            ds.pathofnodes[ds.pathcounter] = Integer.valueOf(path.get(i).getId());
            ds.pathcounter++;
            ds.CurrentPathCounter++;
        }
        
        
        
        // länkar i sammansatt priolänk 
        for (int i =0; i < path.size()-1; i++){
            
            for (int j =0; j< ds.arcs; j++){
            
                if (ds.arcStart[j] == Integer.parseInt(path.get(i).getId()) && ds.arcEnd[j] == Integer.parseInt(path.get(i+1).getId())){
                              
                    if(ds.arcPrio[j] >= 1){
                        ds.collectedPrioPoints = ds.collectedPrioPoints +   ds.arcPrio[j]; // räkna poäng
                        ds.arcPrio[j] = 0.1;   // om väg blivit plogad sätt dess prio till 0.1
                    }
                    ds.arcColor[j] = 2;         // rita ut i annan färg
                    if(ds.checkIfPrioLinkLeft == false){
                        ds.arcColor[j] = 3;         // rita ut i annan färg

                    }
                    ds.distanceTraveled =   ds.distanceTraveled +  ds.xlength[j] + ds.ylength[j]; // beräkna längd för körd sträcka
                } 
            }
        }            
        ds.startMaxLink = false;    // pausar MaxLink
      
        
        if(ds.optPlanIsOn == true){
                    ds.startOptPlan = true;   // startar Optplan

        }
        else{
           ds.startNoPrioLinksLeft =true;
        }
   
    }
    cui.appendPoints(String.valueOf(ds.collectedPrioPoints));
    cui.appendDistance(String.valueOf(ds.distanceTraveled));
  


}
}
     



    
