package autosnow;

import java.awt.BasicStroke;
import static java.awt.BasicStroke.CAP_SQUARE;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Grupp 2
 */
public class MapPanel extends JPanel {
  
    DataStore ds;
    private BufferedImage image;

    MapPanel(DataStore ds) {
        this.ds = ds;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
       
        //Deklarerar standardfärger 
       
        final Color DARK_COLOR = new Color(0, 0, 0);
        final Color RED_COLOR = new Color(255, 0, 0);
        final Color GREEN_COLOR = new Color(0,255,0);
        final Color YELLOW_COLOR = new Color(255,255,0);
        final Graphics2D g2 = (Graphics2D) g;
        final BasicStroke vanlig = new BasicStroke(2);
        final BasicStroke tjock = new BasicStroke(6);
        
        Font myFont = new Font("serif",Font.BOLD,200); 
      
        int x, y;
        int x1, y1;
        int x2, y2;
        double X1,X2,Y1,Y2;
        boolean nodeDrawn  = false;
        int xR,yR,wR,hR;
        final int circlesize = 10;
        final int ysize = 180;
        final int xsize = 300;
       
 

        if (ds.networkRead == true) { // Only try to plot if data has been properly read from file

            // Compute scale factor in order to keep the map in proportion when the window is resized
            int height = getHeight();
            int width = getWidth();
            double xscale = 1.0 * width / xsize;
            double yscale = 1.0 * height / ysize;
            

            g.setColor(DARK_COLOR);
            
            // Ritar ut noder som cirklar
           
               for (int i = 0; i < ds.nodeCounter; i++) 
               {   
                        x = (int) (ds.nodeXNew[i] * xscale);
                        y = (int) (ds.nodeYNew[i] * yscale);
                            
                        g.fillOval(x - (circlesize / 2), height - y - circlesize / 2, circlesize, circlesize);
                        g.drawString(""+(i+1), x+8, height-y-8); //Lägger till nodnummer vid cirkeln
                         
      
                        if(ds.nodeColor[i] == 1)
                        {
                            g.setColor(RED_COLOR);
                        }
                        else
                        {
                            g.setColor(DARK_COLOR);
                        }
      
                }

            // Ritar ut bågar
       
            for (int i = 0; i < ds.arcs; i++) {
                x1 = (int) (ds.nodeXNew[ds.arcStart[i] - 1] * xscale);
                y1 = (int) (ds.nodeYNew[ds.arcStart[i] - 1] * yscale);
                x2 = (int) (ds.nodeXNew[ds.arcEnd[i] - 1] * xscale);
                y2 = (int) (ds.nodeYNew[ds.arcEnd[i] - 1] * yscale);
                
                X1 = (ds.nodeXNew[ds.arcStart[i] - 1]);
                Y1 = (ds.nodeYNew[ds.arcStart[i] - 1]);
                X2 = (ds.nodeXNew[ds.arcEnd[i] - 1]);
                Y2 = (ds.nodeYNew[ds.arcEnd[i] - 1]);
                
                ds.xlength[i] = Math.abs((X2-X1));
                ds.ylength[i] = Math.abs((Y2-Y1));
               
                
              //Ritar ut olika färger på bågarna efter vilken prio de har
                if (ds.arcColor[i] == 1) {
                    g.setFont(myFont);
                  g.setColor(RED_COLOR);
                  g2.setStroke(tjock);
                }
                else if (ds.arcColor[i] == 0) {
                  g.setColor(DARK_COLOR); 
                   g2.setStroke(vanlig);                
                          }
                else if (ds.arcColor[i] == 2) {
                    g.setFont(myFont);
                    g.setColor(GREEN_COLOR);
                   g2.setStroke(tjock);
                  
                }
                else if (ds.arcColor[i] == 3) {
                    g.setColor(YELLOW_COLOR); 
                     g2.setStroke(tjock);
                  
                }
                 else if (ds.arcColor[i] == 4) {
                    g.setColor(DARK_COLOR);
                    g2.setStroke(vanlig);
                 
                }            
                g2.drawLine(x1, height- y1, x2, height - y2);  //Ritar upp linjen med givna färg och tjocklek      
            }

               x = (int) (ds.vehicleX*xscale);
               y = (int) (ds.vehicleY*yscale);
               Color color = new Color(111,165,79,120);
               hR = (int)(30*yscale);
               wR = (int)(30*xscale);
               
               //Ritar ut den bakgrund som visar vart vägen inte går.
               //Läser in från filen RectNodes som behöver ändras om vägnätet ändras
               for(int i = 0; i < ds.rectangleNodeCounter;i++)
               {
                    
                    xR = (int)(ds.nodeXNew[ds.rectangleNodes[i]-1]*xscale);
                    yR = (int)(ds.nodeYNew[ds.rectangleNodes[i]-1]*yscale);
                    //g.drawRect(xR, yR, wR, hR);
                    g.setColor(color);
                    g.fillRect(xR,yR,wR,hR);
                
                    
                   
                   
               }
               
               //Ritar ut ramen runt vägnätet
               g.fillRect(0, 0, 2000, (int)(15*yscale));
               g.fillRect(0, (int)(15*yscale), (int)(15*xscale), 2000);
               g.fillRect((int)(15*xscale), (int)(165*yscale), 2000, (int)(15*yscale));
               g.fillRect((int)(285*xscale), (int)(15*yscale), (int)(15*xscale), (int)(150*yscale));
               
                       
            
       
      
           
//Ändrar bilden som ritas upp som fordonet, går att rotera fordonet
        try
            {
                if(ds.carRight)
                {
                  image = ImageIO.read(getClass().getResourceAsStream("plogbilhöger.png"));  
                }
                if(ds.carLeft)
                {
                  image = ImageIO.read(getClass().getResourceAsStream("plogbilvänster.png"));  
                }
                if(ds.carUp)
                {
                  image = ImageIO.read(getClass().getResourceAsStream("plogbilupp.png"));  
                }
                if(ds.carDown)
                {
                  image = ImageIO.read(getClass().getResourceAsStream("plogbilner.png"));  
                }
                
                
                
            }catch(IOException e){
            e.printStackTrace();}
           //Ritar upp fordonet. 
           g.drawImage(image,x -((circlesize+70)/2),height -y -(circlesize+70)/2,circlesize+70,circlesize+70,null);
           
        }
    }

   
}

