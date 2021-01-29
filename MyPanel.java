package CSCI442.Lab00;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

public class MyPanel extends JPanel
{
 
    int startX, flag, startY, endX, endY;
    int [] colorForHistogram;
    BufferedImage grid;
    Graphics2D gc;

	public MyPanel(int[] colorToBeGraphed)
	{
	   colorForHistogram = colorToBeGraphed;
	   startX = startY = 0;
       endX = endY = 100;
 	}

     public void clear()
    {
       grid = null;
       repaint();
    }
    public void paintComponent(Graphics g)
    {  
         super.paintComponent(g);
         Graphics2D g2 = (Graphics2D)g;
         if(grid == null){
            int w = this.getWidth();
            int h = this.getHeight();
            grid = (BufferedImage)(this.createImage(w,h));
            gc = grid.createGraphics();

         }
         g2.drawImage(grid, null, 0, 0);
     }
    public void spreadDataABit() {
	    for (int i = 0; i < 255; i++) {
	        this.colorForHistogram[i] = colorForHistogram[i]/5;
        }
    }

    public void drawHistogram() {
	    spreadDataABit();
	    for (int i = 0; i < colorForHistogram.length; i++) {
            gc.drawLine(i, this.getHeight(), i, this.getHeight()  - colorForHistogram[i]);
	        repaint();
        }
    }
}
