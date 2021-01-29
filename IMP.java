package CSCI442.Lab00;/*
 *Hunter Lloyd
 * Copy write.......I wrote, ask permission if you want to use it outside of class.
 */

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.util.prefs.Preferences;

class IMP implements MouseListener, ChangeListener {
   JFrame frame, secondFrame;
   JPanel mp;
   JButton start;
   JScrollPane scroll;
   JMenuItem openItem, exitItem, resetItem;
   Toolkit toolkit;
   File pic;
   ImageIcon img;
   int colorX, colorY, totalPixels, redLow, redHigh, greenLow,greenHigh,blueLow,blueHigh;
   int [] pixels;
   int [] results;
   int[] origResults;
   int[] red, blue, green;
   MyPanel redPanel;
   MyPanel greenPanel;
   MyPanel bluePanel;
   //Instance Fields you will be using below
   
   //This will be your height and width of your 2d array
   int height=0, width=0, originalHeight = 0, originalWidth = 0;
   
   //your 2D array of pixels
   int[][] picture;

    /* 
     * In the Constructor I set up the GUI, the frame the menus. The open pull down
     * menu is how you will open an image to manipulate. 
     */
   IMP() {
      toolkit = Toolkit.getDefaultToolkit();
      frame = new JFrame("Image Processing");
      JMenuBar bar = new JMenuBar();
      JMenu file = new JMenu("File");
      JMenu functions = getFunctions();
      frame.addWindowListener(new WindowAdapter(){
            @Override
              public void windowClosing(WindowEvent ev){quit();}
            });
      openItem = new JMenuItem("Open");
      openItem.addActionListener(new ActionListener(){
            @Override
          public void actionPerformed(ActionEvent evt){ handleOpen(); }
           });
      resetItem = new JMenuItem("Reset");
      resetItem.addActionListener(new ActionListener(){
            @Override
          public void actionPerformed(ActionEvent evt){ reset(); }
           });     
      exitItem = new JMenuItem("Exit");
      exitItem.addActionListener(new ActionListener(){
            @Override
          public void actionPerformed(ActionEvent evt){ quit(); }
           });
      file.add(openItem);
      file.add(resetItem);
      file.add(exitItem);
      bar.add(file);
      bar.add(functions);
      frame.setSize(600, 600);
      mp = new JPanel();
      mp.setBackground(new Color(0, 0, 0));
      scroll = new JScrollPane(mp);
      frame.getContentPane().add(scroll, BorderLayout.CENTER);
      JPanel butPanel = new JPanel();
      butPanel.setBackground(Color.black);
      start = new JButton("start");
      start.setEnabled(false);
      start.addActionListener(new ActionListener(){
            @Override
          public void actionPerformed(ActionEvent evt){
                redPanel.drawHistogram();
                bluePanel.drawHistogram();
                greenPanel.drawHistogram();}
           });
      butPanel.add(start);
      frame.getContentPane().add(butPanel, BorderLayout.SOUTH);
      frame.setJMenuBar(bar);
      frame.setVisible(true);      
   }
   
   /* 
    * This method creates the pull down menu and sets up listeners to selection of the menu choices. If the listeners are activated they call the methods
    * for handling the choice, fun1, fun2, fun3, fun4, etc. etc. 
    */
   
  private JMenu getFunctions()
  {
     JMenu fun = new JMenu("Functions");
     
     JMenuItem firstItem = new JMenuItem("MyExample");
     JMenuItem rotateItem = new JMenuItem("Rotate 90");
     JMenuItem grayScaleItem = new JMenuItem("Apply Grayscale");
     JMenuItem blurItem = new JMenuItem("Blur");
     JMenuItem EdgeDetectionItem = new JMenuItem("Edge Detection");
     JMenuItem histogramItem = new JMenuItem("Histogram");
     JMenuItem equalizeItem = new JMenuItem("Equalize Image");
     JMenuItem trackColor = new JMenuItem("Track Colors");

    
     firstItem.addActionListener(new ActionListener(){
            @Override
          public void actionPerformed(ActionEvent evt){fun1();}
           });
     rotateItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {rotateImage();}
     });
     grayScaleItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {grayScale();}
     });
     blurItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {blurImage(); }
     });
     EdgeDetectionItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {edgeDetection(); }
     });
     histogramItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {makeHistogram();
         }
     });
     equalizeItem.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {equalizeImage();
         }
     });
     trackColor.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {trackColor();
         }
     });

      fun.add(firstItem);
      fun.add(rotateItem);
      fun.add(grayScaleItem);
      fun.add(blurItem);
      fun.add(EdgeDetectionItem);
      fun.add(histogramItem);
      fun.add(equalizeItem);
      fun.add(trackColor);
     
      return fun;
  }
  
  /*
   * This method handles opening an image file, breaking down the picture to a one-dimensional array and then drawing the image on the frame. 
   * You don't need to worry about this method. 
   */
    private void handleOpen()
  {  
     img = new ImageIcon();
     JFileChooser chooser = new JFileChooser();
     Preferences pref = Preferences.userNodeForPackage(IMP.class);
     String path = pref.get("DEFAULT_PATH", "");

     chooser.setCurrentDirectory(new File(path));
     int option = chooser.showOpenDialog(frame);
     
     if(option == JFileChooser.APPROVE_OPTION) {
        pic = chooser.getSelectedFile();
        pref.put("DEFAULT_PATH", pic.getAbsolutePath());
       img = new ImageIcon(pic.getPath());
      }
     width = img.getIconWidth();
     height = img.getIconHeight();
     originalWidth = width;
     originalHeight = height;

     JLabel label = new JLabel(img);
     label.addMouseListener(this);
     pixels = new int[width*height];
     results = new int[width*height];
     origResults = new int[width*height];

     Image image = img.getImage();
        
     PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width );
     try{
         pg.grabPixels();
     }catch(InterruptedException e)
       {
          System.err.println("Interrupted waiting for pixels");
          return;
       }
     for(int i = 0; i<width*height; i++)
        results[i] = pixels[i];
     origResults = results.clone();
     turnTwoDimensional();
     mp.removeAll();
     mp.add(label);
     
     mp.revalidate();
  }
  
  /*
   * The libraries in Java give a one dimensional array of RGB values for an image, I thought a 2-Dimensional array would be more useful to you
   * So this method changes the one dimensional array to a two-dimensional. 
   */
  private void turnTwoDimensional()
  {
     picture = new int[height][width];
     //originalPicture = new int[height][width];
     for(int i=0; i<height; i++)
       for(int j=0; j<width; j++)
          picture[i][j] = pixels[i*width+j];
     //originalPicture = picture.clone();
  }
  /*
   *  This method takes the picture back to the original picture
   */
  private void reset() {
      width = originalWidth;
      height = originalHeight;
      for(int i = 0; i<width*height; i++)
             pixels[i] = results[i];
      turnTwoDimensional();
      Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width));

      JLabel label2 = new JLabel(new ImageIcon(img2));    
       mp.removeAll();
       mp.add(label2);
       mp.repaint();
       mp.revalidate(); 
    }
  /*
   * This method is called to redraw the screen with the new image. 
   */
  private void resetPicture() {
      for(int i=0; i<height; i++)
           for(int j=0; j<width; j++)
               pixels[i*width+j] = picture[i][j];
      Image img2 = toolkit.createImage(new MemoryImageSource(width, height, pixels, 0, width)); 

      JLabel label2 = new JLabel(new ImageIcon(img2));    
      mp.removeAll();
      mp.add(label2);
      mp.repaint();
      mp.revalidate();
  }

    /*
     * This method takes a single integer value and breaks it down doing bit manipulation to 4 individual int values for A, R, G, and B values
     */
  private int [] getPixelArray(int pixel)
  {
      int[] temp = new int[4];
      temp[0] = (pixel >> 24) & 0xff;
      temp[1]   = (pixel >> 16) & 0xff;
      temp[2] = (pixel >>  8) & 0xff;
      temp[3]  = (pixel      ) & 0xff;
      return temp;
    }
    /*
     * This method takes an array of size 4 and combines the first 8 bits of each to create one integer. 
     */
  private int getPixels(int rgb[])
  {
         int alpha = 0;
         int rgba = (rgb[0] << 24) | (rgb[1] <<16) | (rgb[2] << 8) | rgb[3];
        return rgba;
  }
  
  public void getValue()
  {
      int pix = picture[colorY][colorX];
      int[] temp = getPixelArray(pix);
      System.out.println("Color value " + temp[0] + " " + temp[1] + " "+ temp[2] + " " + temp[3]);
    }
   /*
    * Example function that just removes all red values from the picture. 
    * Each pixel value in picture[i][j] holds an integer value. You need to send that pixel to getPixelArray the method which will return a 4 element array 
    * that holds A,R,G,B values. Ignore [0], that's the Alpha channel which is transparency, we won't be using that, but you can on your own.
    * getPixelArray will breaks down your single int to 4 ints so you can manipulate the values for each level of R, G, B. 
    * After you make changes and do your calculations to your pixel values the getPixels method will put the 4 values in your ARGB array back into a single
    * integer value so you can give it back to the program and display the new picture. 
    */
  private void fun1() {
      int i = 0, j = 0;
      for(i=0; i<height; i++)
       for(j=0; j<width; j++)
       {   
          int[] rgbArray = new int[4];
         
          //get three ints for R, G and B
          rgbArray = getPixelArray(picture[i][j]);
          rgbArray[1] = 0;
          //take three ints for R, G, B and put them back into a single int
           picture[i][j] = getPixels(rgbArray);
        } 
     resetPicture();
  }

    // rotate image
    private void rotateImage() {
      int[][] rotatedPicture = new int[width][height];

      for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
             rotatedPicture[j][(height-1) - i] = picture[i][j];
         }
      }
      picture = rotatedPicture;

      int rotatedHeight = rotatedPicture.length;
      int rotatedWidth = rotatedPicture[0].length;
      height = rotatedHeight;
      width = rotatedWidth;
      resetPicture();
    }

    public void grayScale() {
        for(int i=0; i<height; i++)
            for(int j=0; j<width; j++)
            {
                int[] rgbArray = new int[4];

                //get three ints for R, G and B
                rgbArray = getPixelArray(picture[i][j]);

                int red = (int) (rgbArray[1] * 0.21);
                int green = (int) (rgbArray[2] * 0.72);
                int blue = (int) (rgbArray[3] * 0.07);

                rgbArray[1] = red + green + blue;
                rgbArray[2] = red + green + blue;
                rgbArray[3] = red + green + blue;
                //take three ints for R, G, B and put them back into a single int
                picture[i][j] = getPixels(rgbArray);
            }
        resetPicture();
    }

    public void blurImage() {
      grayScale();
      int[][] blurredPicture = new int[height][width];

      for (int i = 1; i < height - 1; i++) {
        for (int j = 1; j < width - 1; j++) {
            int[] rgbArray = new int[4];
            int avg = AverageOf(picture,i,j);
            rgbArray[0] = 255;
            rgbArray[1] = avg;
            rgbArray[2] = avg;
            rgbArray[3] = avg;

            blurredPicture[i][j] = getPixels(rgbArray);
        }
      }
      picture = blurredPicture;
      resetPicture();
  }

    public int AverageOf(int[][] image, int row, int column) {
        /*
         *  A B C D E
         *  F G H I J
         *  K L ! M N
         *  O P Q R S
         *  T U V W X
         *
         * 5x5 key
         * A -2 -2         I -1 +1         P +1 -1         X +2 +2
         * B -2 -1         J -1 +2         Q +1  0
         * C -2 0          K 0  -2         R +1 +1
         * D -2 +1         L 0  -1         S +1 +2
         * E -2 +2         ! 0   0         T +2 -2
         * F -1 -2         M 0  +1         U +2 -1
         * G -1 -1         N 0  +2         V +2  0
         * H -1 0          O +1 -2         W +2 +1
         * */
        return (getPixelArray(image[row-1][column - 1])[1] +  // G
                getPixelArray(image[row - 1][column])[1] + // H
                getPixelArray(image[row - 1][column + 1])[1] + // I
                getPixelArray(image[row][column-1])[1] + // L
                getPixelArray(image[row][column])[1] + // !
                getPixelArray(image[row][column + 1])[1] + // M
                getPixelArray(image[row + 1][column - 1])[1] + // P
                getPixelArray(image[row + 1][column])[1] + // Q
                getPixelArray(image[row + 1][column + 1])[1] // R
        )/9;
    }

    public void edgeDetection() {
        // *  A B C D E
        // *  F G H I J
        // *  K L ! M N
        // *  O P Q R S
        // *  T U V W X
        // *
        // * 5x5 key
        // * A -2 -2         I -1 +1         P +1 -1         X +2 +2
        // * B -2 -1         J -1 +2         Q +1  0
        // * C -2 0          K 0  -2         R +1 +1
        // * D -2 +1         L 0  -1         S +1 +2
        // * E -2 +2         ! 0   0         T +2 -2
        // * F -1 -2         M 0  +1         U +2 -1
        // * G -1 -1         N 0  +2         V +2  0
        // * H -1 0          O +1 -2         W +2 +1
        // *
      grayScale();
      int[][] tmpEdged = new int[height][width];
      for (int i = 2; i < height-2; i++) {
          for (int j = 2; j < width-2; j++) {
              int[] rgbArray = new int[4];
              int surrounds = 0;
              // --------------------------------------------- row 1
              int A = getPixelArray(picture[i-2][j-2])[1];
              int B = getPixelArray(picture[i-2][j-1])[1];
              int C = getPixelArray(picture[i-2][j])[1];
              int D = getPixelArray(picture[i-2][j+1])[1];
              int E = getPixelArray(picture[i-2][j+2])[1];
              // --------------------------------------------- row 2
              int F = getPixelArray(picture[i-1][j-2])[1];
              int G = getPixelArray(picture[i-1][j-1])[1];
              int H = getPixelArray(picture[i-1][j])[1];
              int I = getPixelArray(picture[i-1][j+1])[1];
              int J = getPixelArray(picture[i-1][j+2])[1];
              // --------------------------------------------- row 3
              int K = getPixelArray(picture[i][j-2])[1];
              int L = getPixelArray(picture[i][j-1])[1];
              int Centre = getPixelArray(picture[i][j])[1];
              int M = getPixelArray(picture[i][j+1])[1];
              int N = getPixelArray(picture[i][j+2])[1];
              // --------------------------------------------- row 4
              int O = getPixelArray(picture[i+1][j-2])[1];
              int P = getPixelArray(picture[i+1][j-1])[1];
              int Q = getPixelArray(picture[i+1][j])[1];
              int R = getPixelArray(picture[i+1][j+1])[1];
              int S = getPixelArray(picture[i+1][j+2])[1];
              // --------------------------------------------- row 5
              int T = getPixelArray(picture[i+2][j-2])[1];
              int U = getPixelArray(picture[i+2][j-1])[1];
              int V = getPixelArray(picture[i+2][j])[1];
              int W = getPixelArray(picture[i+2][j+1])[1];
              int X = getPixelArray(picture[i+2][j+2])[1];

              int[][] fiveByFiveMask = {
                      {-1,-1,-1,-1,-1},
                      {-1, 0, 0, 0,-1},
                      {-1, 0, 16,0,-1},
                      {-1, 0, 0, 0,-1},
                      {-1,-1,-1,-1,-1}};

              int[][] imageBorderForEdge = {{A,B,C,D,E},{F,G,H,I,J},{K,L,Centre,M,N},{O,P,Q,R,S},{T,U,V,W,X}};

              for (int a = 0; a < 5; a++) {
                  for (int b = 0; b < 5; b++) {
                      imageBorderForEdge[a][b] *= fiveByFiveMask[a][b];
                      surrounds += imageBorderForEdge[a][b];
                  }
              }

              rgbArray[0] = 255;
              if (surrounds > 100) {
                  rgbArray[1] = 255;
                  rgbArray[2] = 255;
                  rgbArray[3] = 255;
              } else {
                  rgbArray[1] = 0;
                  rgbArray[2] = 0;
                  rgbArray[3] = 0;
              }
              tmpEdged[i][j] = getPixels(rgbArray);
          }
      }
      picture = tmpEdged;
      resetPicture();
    }

    public void makeHistogram() {
        red = new int[256];
        green = new int[256];
        blue = new int[256];
        totalPixels = width * height;
        int rc, gc, bc;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int[] rgbArray;

                // get three ints for R, G and B
                rgbArray = getPixelArray(picture[i][j]);

                // for each pixel, put in respective int from rgbArray to color array
                rc = rgbArray[1];
                red[rc]++;
                gc = rgbArray[2];
                green[gc]++;
                bc = rgbArray[3];
                blue[bc]++;
            }
        }
      // second frame to house container panel
      secondFrame = new JFrame("Histograms");
      secondFrame.setLayout(new BorderLayout());
      secondFrame.setSize(1200,600);
      secondFrame.setLocation(600,0);

      // container panel to house 3 individual histogram panels
      JPanel container = new JPanel(new BorderLayout());
      Border blackLine = BorderFactory.createLineBorder(Color.BLACK);

      // individual histogram panels being passed respective color value arrays
      redPanel = new MyPanel(red);
      greenPanel = new MyPanel(blue);
      bluePanel = new MyPanel(green);

      redPanel.setPreferredSize(new Dimension(400,600));
      greenPanel.setPreferredSize(new Dimension(400,600));
      bluePanel.setPreferredSize(new Dimension(400, 600));

      redPanel.setBorder(blackLine);
      greenPanel.setBorder(blackLine);
      bluePanel.setBorder(blackLine);

      JLabel redLabel = new JLabel("Red");
      JLabel greenLabel = new JLabel("Green");
      JLabel blueLabel = new JLabel("Blue");

      redPanel.add(redLabel);
      greenPanel.add(greenLabel);
      bluePanel.add(blueLabel);

      container.add(redPanel, BorderLayout.LINE_START);
      container.add(greenPanel, BorderLayout.CENTER);
      container.add(bluePanel, BorderLayout.LINE_END);

      secondFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      secondFrame.add(container, BorderLayout.CENTER);
      secondFrame.repaint();
      secondFrame.setVisible(true);
      start.setEnabled(true);
    }

    public void equalizeImage() {
        /*
         * put image array into histogram array
         * apply algorithm to histogram array to make equalized array
         * make picture be represent equalized array using make2d()?
         * resetPicture();
         * */
        makeHistogram();
        double dR, nR, dG, nG, dB, nB;
        int rFinal, gFinal, bFinal,
                redMinCDF = getArrayMin(red), redMaxCDF = 8000,
                greenMinCDF = getArrayMin(green), greenMaxCDF = 8000,
                bluMinCDF = getArrayMin(blue), bluMaxCDF = 8000;
        int[] redFinalArray = new int[256];
        int[] greenFinalArray = new int[256];
        int[] bluFinalArray = new int[256];
        int[] tmp = new int[4];
        int[] rgbArray = new int[4];

        /*
         * function for finding minCDF, and calculation equalization params
         * https://en.wikipedia.org/wiki/Histogram_equalization
         * */

        for (int i = 0; i < 256; i++) {
            redMaxCDF += red[i];
            greenMaxCDF += green[i];
            bluMaxCDF += blue[i];

            // respective denominators and numerators for wikipedia equation and h(v)
            dR = height * width - redMinCDF;
            nR = redMaxCDF - redMinCDF;
            rFinal = (int) (nR / dR) * 255;
            redFinalArray[i] = rFinal;

            dG = height * width - greenMinCDF;
            nG = greenMaxCDF - greenMinCDF;
            gFinal = (int) (nG / dG) * 255;
            greenFinalArray[i] = gFinal;

            dB = height * width - bluMinCDF;
            nB = bluMaxCDF - greenMinCDF;
            bFinal = (int) (nB / dB) * 255;
            bluFinalArray[i] = bFinal;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                rgbArray = getPixelArray(picture[i][j]);
                tmp[0]=255;
                tmp[1]=redFinalArray[rgbArray[1]];
                tmp[2]=bluFinalArray[rgbArray[2]];
                tmp[3]=greenFinalArray[rgbArray[3]];
                picture[i][j] = getPixels(tmp);
            }
        }
        resetPicture();
    }

  public void trackColor() {

      redLow = greenLow = blueLow = 180;
      redHigh = greenHigh = blueHigh = 255;

      JPanel sliderPanel = new JPanel(new GridLayout(3,2));
      JSlider rLSlider = new JSlider(0,255);
      JSlider rHSlider = new JSlider(0,255);
      JSlider gLSlider = new JSlider(0,255);
      JSlider gHSlider = new JSlider(0,255);
      JSlider bLSlider = new JSlider(0,255);
      JSlider bHSlider = new JSlider(0,255);

      rLSlider.setName("redLow");
      rHSlider.setName("redHigh");
      gLSlider.setName("greenLow");
      gHSlider.setName("greenHigh");
      bLSlider.setName("blueLow");
      bHSlider.setName("blueHigh");

      rLSlider.setMajorTickSpacing(50);
      rHSlider.setMajorTickSpacing(50);
      gLSlider.setMajorTickSpacing(50);
      gHSlider.setMajorTickSpacing(50);
      bLSlider.setMajorTickSpacing(50);
      bHSlider.setMajorTickSpacing(50);

      rLSlider.setPaintTicks(true);
      rHSlider.setPaintTicks(true);
      gLSlider.setPaintTicks(true);
      gHSlider.setPaintTicks(true);
      bLSlider.setPaintTicks(true);
      bHSlider.setPaintTicks(true);

      rLSlider.setPaintLabels(true);
      rHSlider.setPaintLabels(true);
      gLSlider.setPaintLabels(true);
      gHSlider.setPaintLabels(true);
      bLSlider.setPaintLabels(true);
      bHSlider.setPaintLabels(true);

      rLSlider.addChangeListener(this);
      rHSlider.addChangeListener(this);
      gLSlider.addChangeListener(this);
      gHSlider.addChangeListener(this);
      bLSlider.addChangeListener(this);
      bHSlider.addChangeListener(this);

      sliderPanel.add(rLSlider);
      sliderPanel.add(rHSlider);
      sliderPanel.add(gLSlider);
      sliderPanel.add(gHSlider);
      sliderPanel.add(bLSlider);
      sliderPanel.add(bHSlider);

      int result = JOptionPane.showConfirmDialog(null,sliderPanel,"Color Search",
      JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

      System.out.println(result);

      for (int i = 0; i < height; i ++) {
          for (int j = 0; j < width; j++) {
              int[] rgbArray = new int[4];
              rgbArray = getPixelArray(picture[i][j]);
              boolean colorMatch = false;

              //Compare RGB values with chain of ifs
              if (rgbArray[1] >= redLow && rgbArray[1] <= redHigh) {    // within red
                  if (rgbArray[2] >= greenLow && rgbArray[2] <= greenHigh) { // within green
                      if (rgbArray[3] >= blueLow && rgbArray[3] <= blueHigh) {
                          colorMatch = true;
                          rgbArray[1] = 255;
                          rgbArray[2] = 255;
                          rgbArray[3] = 255;
                      }
                  }
              }
              if (!colorMatch) {
                  rgbArray[1] = 0;
                  rgbArray[2] = 0;
                  rgbArray[3] = 0;
              }
              picture[i][j] = getPixels(rgbArray);
          }
          resetPicture();
      }
      resetPicture();
  }
  public int getArrayMin(int[] arrayToSearch) {
      int min = 0;
      for (int i = 0; i < 256; i++) {
          if (arrayToSearch[i] != 0 && arrayToSearch[i] < min) {
              min = arrayToSearch[i];
          }
      }
      return min;
    }

  private void quit()
  {  
     System.exit(0);
  }
    @Override
   public void mouseEntered(MouseEvent m){}
    @Override
   public void mouseExited(MouseEvent m){}
    @Override
   public void mouseClicked(MouseEvent m){
        colorX = m.getX();
        colorY = m.getY();
        System.out.println(colorX + "  " + colorY);
        getValue();
        start.setEnabled(true);
    }
    @Override
   public void mousePressed(MouseEvent m){}
    @Override
   public void mouseReleased(MouseEvent m){}
   public void stateChanged(ChangeEvent changeEvent) {
      JSlider source = (JSlider)changeEvent.getSource();
      if (!source.getValueIsAdjusting()) {
          switch (source.getName()) {
              case "redLow":
                  redLow = source.getValue();
                  break;
              case "redHigh":
                  redHigh = source.getValue();
                  break;
              case "greenLow":
                  greenLow = source.getValue();
                  break;
              case "greenHigh":
                  greenHigh = source.getValue();
                  break;
              case "blueLow":
                  blueLow = source.getValue();
                  break;
              case "blueHigh":
                  blueHigh = source.getValue();
                  break;
          }
      }
   }
   
   public static void main(String [] args)
   {
      IMP imp = new IMP();
   }
 
}