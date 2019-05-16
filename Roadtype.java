
package autosnow;

/**
 *  Roadtype tar in alla 12 noder med ett argument som är vägtyp. 
 * Därefter skapar den bågar med noderna efter vilket typ av väg det är.
 * Förklaring till att typer av vägar:
 * Turn1: Ner till vänster     Cross1: Vänster,Höger, Ner      Cross5: 4-Vägskorsning
 * Turn2: Ner till höger       Cross2: Upp,Höger,Ner           Straight1: Vänster till höger
 * Turn3: Upp till höger       Cross3: Vänster,Höger,Upp       Straight2: Upp till Ner
 * Turn4: Upp till vänster.    Cross4: Vänster,Upp,Ner 
 * @author Grupp 2
 */
public class Roadtype {
    final private String roadName;
    final private int node1, node2, node3, node4, node5, node6, 
                      node7, node8, node9, node10, node11, node12;
    int k = 0;
    public int[] arcStart, arcEnd;
    
    public Roadtype(int[] arg1, String arg2){ //Alla noder i arg1, arg2 är typ av väg.
        node1 = arg1[0];
        node2 = arg1[1];
        node3 = arg1[2];
        node4 = arg1[3];
        node5 = arg1[4];
        node6 = arg1[5];
        node7 = arg1[6];
        node8 = arg1[7];
        node9 = arg1[8];
        node10 = arg1[9];
        node11 = arg1[10];
        node12 = arg1[11];
        roadName = arg2;
       
    }
    /**
     * Skapar arcs mellan noder, beroende på vilket typ av vägtyp det är
     * Sparar även hur många arcs som skapas
     */
    public void calculateArcs()
    {
        k = 0;
        arcStart =  new int[1000];
        arcEnd =  new int[1000];
      
        if(roadName.equals("turn1")){
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
            
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
        }
        
        if(roadName.equals("turn2")){
           
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
        }
       
        if(roadName.equals("turn3")){
       
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
            
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
        }
        
        if(roadName.equals("turn4")){
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
        }
        
        if(roadName.equals("straight1")){
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
        }
        
        if(roadName.equals("straight2")){
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
        }
        
        if(roadName.equals("cross1")){
        
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
            
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
        }
        
        if(roadName.equals("cross2")){
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
            
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
        }
        
        if(roadName.equals("cross3")){
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
        }
        
        if(roadName.equals("cross4")){
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
        }
        
        if(roadName.equals("cross5"))
        {
            arcStart[k] = node2;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node9;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node12;
            k++;
            
            arcStart[k] = node11;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node1;
            k++;
            
            arcStart[k] = node3;
            arcEnd[k] = node4;
            k++;
            
            arcStart[k] = node4;
            arcEnd[k] = node5;
            k++;
            
            arcStart[k] = node9;
            arcEnd[k] = node8;
            k++;
            
            arcStart[k] = node8;
            arcEnd[k] = node7;
            k++;
            
            arcStart[k] = node5;
            arcEnd[k] = node6;
            k++;
            
            arcStart[k] = node10;
            arcEnd[k] = node9;
            k++;
           
        }
       
    }
    /**
     * 
     * @return Vägtypen för blocket
     */
    public String getroadName(){
        return roadName;
    }

    /** 
     * @return antalet arcs som har skapats för varje block
     */
    public int howmanyArcs()
    {
        return k;
    }

    
}
