// Import javax.swing.*
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
class TechProject extends JFrame implements ActionListener
{
  //Instance variables/objects which all methods must be able to see
  public MazePanel maze=new MazePanel();
  static TechProject proj=new TechProject();
  JButton mazebutton=new JButton("Generate a new maze");
  JButton solvebutton=new JButton("Solve the Maze");
  JSlider width=new JSlider(5,200);
  JSlider height=new JSlider(5,200);
  JComboBox generatemethod;
  JComboBox solvemethod;
  JButton saveButton=new JButton("Save this maze");
  JCheckBox slow=new JCheckBox("Slow Mode");
  JButton clear=new JButton("Restore the maze");
  JButton help=new JButton("Help");
  public static void main(String[] args){
    int x=0;
  }
  public void clearMaze() {
    for (int i=0; i<maze.mazeArray.length; i++) {
      for (int a=0; a<maze.mazeArray[0].length; a++) {
        String s=maze.mazeArray[i][a];
        if (s.equals("x") || s.charAt(0)=='t' || s.charAt(0)=='f' || s.equals("*") || s.equals(":")) maze.mazeArray[i][a]=".";
      }
    }
  }
  public void actionPerformed(ActionEvent e) {
    maze.mode=-4;
    if (e.getSource()==clear) {
    clearMaze();
    repaint();
    } else {
    Object sor=e.getSource();
    if (sor==mazebutton) {
      mazebutton.setEnabled(false);
    solvebutton.setEnabled(false);
    clear.setEnabled(false);
      String item=(String) generatemethod.getSelectedItem();
      if (item.equals("Recursive Division")) maze.mode=MazePanel.GENDIV;
      if (item.equals("Depth-First")) maze.mode=MazePanel.GEN_DFS;
      if (item.equals("Prim Algorithm")) maze.mode=MazePanel.GEN_PRIM;
    }
    if (sor==solvebutton) {
      clearMaze();
      mazebutton.setEnabled(false);
    solvebutton.setEnabled(false);
    clear.setEnabled(false);
      String item=(String) solvemethod.getSelectedItem();
      if (item.equals("Dead end filling"))  maze.mode=MazePanel.DEAD_END;
      if (item.equals("Tramaux Algorithm")) maze.mode=MazePanel.TREM;
      if (item.equals("A# Search")) maze.mode=MazePanel.A_SHARP;
    }
    if (sor==saveButton) {
      maze.writeImage();
    }
    if (sor==slow) {
      maze.slowmo=slow.isSelected();
    }
    if (sor==help) new HelpPanel();
    maze.current=new Thread(maze);
    maze.current.start();
    }
  }
  public TechProject() {
    //Setting up the GUI
    super("Maze Generator/Solver");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JPanel bigpanel=new JPanel();
    BorderLayout lay=new BorderLayout();
    bigpanel.setLayout(lay);
    width.setPaintLabels(true);
    width.setPaintTicks(true);
    width.setMajorTickSpacing(65);
    height.setMajorTickSpacing(65);
    height.setPaintLabels(true);
    height.setPaintTicks(true);
    String[] generates={"Depth-First", "Prim Algorithm", "Recursive Division"};
    String[] solves={"Tramaux Algorithm", "A# Search", "Dead end filling"};
    generatemethod=new JComboBox(generates);
    solvemethod=new JComboBox(solves);
    mazebutton.addActionListener(this);
    solvebutton.addActionListener(this);
    clear.addActionListener(this);
    slow.addActionListener(this);
    saveButton.addActionListener(this);
    help.addActionListener(this);
    //Adding stuff to the bottom panel (all but the maze)
    JPanel bottom=new JPanel();
    bottom.setLayout(new GridLayout(2,7,10,10));
    bottom.add(new JLabel("Width:"));
    bottom.add(width);
    bottom.add(new JLabel("Generation Algorithm:"));
    bottom.add(generatemethod);
    bottom.add(mazebutton);
    bottom.add(clear);
    bottom.add(saveButton);
    bottom.add(new JLabel("Height:"));
    bottom.add(height);
    bottom.add(new JLabel("Solving Algorithm:"));
    bottom.add(solvemethod);
    bottom.add(solvebutton);
    bottom.add(slow);
    bottom.add(help);
    bigpanel.add(maze,BorderLayout.CENTER);
    bigpanel.add(bottom,BorderLayout.SOUTH);
    add(bigpanel);
    setSize(600,600);
    maze.generateDivision(11,11);
    setVisible(true);
  }
}
class MazePanel extends JPanel implements Runnable{
  public String[][] mazeArray;
  int cellWidth;
  int cellHeight;
  Thread current;
  final static byte GENDIV=0;
  final static byte DEAD_END=1;
  final static byte GEN_DFS=2;
  final static byte GEN_PRIM=3;
  final static byte TREM=4;
  final static byte A_SHARP=5;
  byte mode;
  boolean slowmo=false;
  /*
  .=open
  /=wall
  +=start
  -=end
  *=rigth path
  :=wrong path
  x=dead end filled
  f[int]=wrong A#
  t[int]=right/possible A#
  */
  public void run() {
    slowmo=TechProject.proj.slow.isSelected();
    if (mode==GENDIV) {
      int wid=TechProject.proj.width.getValue()*2+1;
      int hei=TechProject.proj.height.getValue()*2+1;
      generateDivision(hei,wid);
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    if (mode==DEAD_END) {
      deadEndSolve();
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    if (mode==GEN_DFS) {
      int wid=TechProject.proj.width.getValue()*2+1;
      int hei=TechProject.proj.height.getValue()*2+1;
      generateDFS(hei,wid);
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    if (mode==A_SHARP) {
      ASharp.ASharpSolve();
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    if (mode==GEN_PRIM) {
      int wid=TechProject.proj.width.getValue()*2+1;
      int hei=TechProject.proj.height.getValue()*2+1;
      genPrim(hei,wid);
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    if (mode==TREM) {
      tremSolve();
      repaint();
      TechProject.proj.mazebutton.setEnabled(true);
    TechProject.proj.solvebutton.setEnabled(true);
    TechProject.proj.clear.setEnabled(true);
    }
    
    repaint();
  }
  public MazePanel() {
    super();
    //this is for testing
    mazeArray= new String[][]{
    };
  }
  public void writeImage() {
    String loc=JOptionPane.showInputDialog(null, "Choose the file name of the image. The image will be saved in the save location as the .class file.");
    if (loc==null) return;
    BufferedImage img=new BufferedImage(mazeArray[0].length*20,mazeArray.length*20,BufferedImage.TYPE_INT_RGB);
    Graphics2D graf=img.createGraphics();
    graf.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    cellHeight=20;
    cellWidth=20;

    for (int row=0; row<mazeArray.length; row++) {
      for (int col=0; col<mazeArray[0].length; col++) {
        int x=col*cellWidth;
        int y=row*cellHeight;
        char p=mazeArray[row][col].charAt(0);
        switch (p) {
          case '.':
            graf.setColor(Color.WHITE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
          case '/':
            //wall
            graf.setColor(Color.BLACK);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
          case 'x':
            //dead end
            graf.setColor(Color.RED);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case 'f':
            //bad a# space
            graf.setColor(new Color(129,129,129));
            graf.fillRect(x,y,cellWidth,cellHeight);
            /* Text is ugly
            graf.setColor(Color.RED);
            FontMetrics met=getFontMetrics(font);
            int a=x + (cellWidth - met.stringWidth(mazeArray[row][col].substring(1,mazeArray[row][col].length())))/2;
            int b=y + (cellHeight/2);
            graf.drawString(mazeArray[row][col].substring(1,mazeArray[row][col].length()),a,b);
            */
            //show distance
            break;
            case ':':
            //wrong path
            graf.setColor(new Color(129,129,129));
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case 't':
            //possible A#
            graf.setColor(Color.BLUE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            /*
            graf.setColor(Color.RED);
            FontMetrics mat=getFontMetrics(font);
            int c=x + (cellWidth - mat.stringWidth(mazeArray[row][col].substring(1,mazeArray[row][col].length())))/2;
            int d=y + (cellHeight/2);
            graf.drawString(mazeArray[row][col].substring(1,mazeArray[row][col].length()),c,d);
            */
            break;
            case '*':
            //possible path
            graf.setColor(Color.BLUE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case '+':
            //start
            graf.setColor(Color.GREEN);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case '-':
            //end
            graf.setColor(Color.GREEN);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
        }
      }
      }
      graf.dispose();
      File out=new File(loc + ".jpg");
      try {
      ImageIO.write(img, "jpg", out);
      } catch (IOException e) {
        System.out.println("Error: " + e.toString());
      }
  }
  public void paintComponent(Graphics g) {
    Graphics2D graf=(Graphics2D) g;
    graf.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    Dimension rect=getSize();
    Font font=new Font("dfg",Font.PLAIN, 12);
    graf.setFont(font);
    cellHeight=rect.height/mazeArray.length;
    cellWidth=rect.width/mazeArray[0].length;
    graf.setColor(Color.BLACK);
   graf.fillRect(0,0,rect.width,rect.height);
    for (int row=0; row<mazeArray.length; row++) {
      for (int col=0; col<mazeArray[0].length; col++) {
        int x=col*cellWidth;
        int y=row*cellHeight;
        char p=mazeArray[row][col].charAt(0);
        switch (p) {
          case '.':
            graf.setColor(Color.WHITE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
          case '/':
            //wall
            graf.setColor(Color.BLACK);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
          case 'x':
            //dead end
            graf.setColor(Color.RED);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case 'f':
            //bad a# space
            graf.setColor(new Color(129,129,129));
            graf.fillRect(x,y,cellWidth,cellHeight);
            /* Text is ugly
            graf.setColor(Color.RED);
            FontMetrics met=getFontMetrics(font);
            int a=x + (cellWidth - met.stringWidth(mazeArray[row][col].substring(1,mazeArray[row][col].length())))/2;
            int b=y + (cellHeight/2);
            graf.drawString(mazeArray[row][col].substring(1,mazeArray[row][col].length()),a,b);
            */
            //show distance
            break;
            case ':':
            //wrong path
            graf.setColor(new Color(129,129,129));
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case 't':
            //possible A#
            graf.setColor(Color.BLUE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            /*
            graf.setColor(Color.RED);
            FontMetrics mat=getFontMetrics(font);
            int c=x + (cellWidth - mat.stringWidth(mazeArray[row][col].substring(1,mazeArray[row][col].length())))/2;
            int d=y + (cellHeight/2);
            graf.drawString(mazeArray[row][col].substring(1,mazeArray[row][col].length()),c,d);
            */
            break;
            case '*':
            //possible path
            graf.setColor(Color.BLUE);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case '+':
            //start
            graf.setColor(Color.GREEN);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
            case '-':
            //end
            graf.setColor(Color.GREEN);
            graf.fillRect(x,y,cellWidth,cellHeight);
            break;
        }
      }
  }
}
  void generateDivision(int rows,int cols) {
    mazeArray=new String[rows][cols];
    for (int row=0; row<rows; row++) {
      for (int col=0; col<cols; col++) {
        if (row==0 || col==0 || row==rows-1 || col==cols-1) {
          mazeArray[row][col]="/";
        } else {
          mazeArray[row][col]=".";
        }
      }
    }
    repaint();
    generate(rows-2,cols-2,1,1);
    repaint();
  }
public void generate(int rows, int cols,int rowDif, int colDif) {
    if (rows<=1 || cols<=1) {
      return;
    }
    int vertLine=0;
    int horLine=0;
      horLine=1 + 2*(int)(Math.random()*((rows-3)/2+1));
      for (int i=0; i<cols; i++) {
        mazeArray[horLine+rowDif][i+colDif]="/";
    }
      vertLine=1 + 2*(int)(Math.random()*((cols-3)/2+1));
      for (int i=0; i<rows; i++) {
        mazeArray[i+rowDif][vertLine+colDif]="/";
    }
    repaint();
    try {
    if (slowmo) Thread.sleep((30000/(mazeArray.length+mazeArray[0].length)));
    } catch (Exception e) {
    }
    boolean up=false;
    boolean down=false;
    boolean left=false;
    boolean right=false;
    int hGap=2*(int)(Math.random()*(cols+1)/2);
    mazeArray[horLine+rowDif][hGap+colDif]=".";
    if (hGap<vertLine) {
      left=true;
    } else {
      right=true;
    }
    int vGap=2*(int)(Math.random()*((rows+1)/2));
    mazeArray[vGap+rowDif][vertLine+colDif]=".";
    if (vGap<horLine) {
      up=true;
    } else {
      down=true;
    }
    ArrayList li=new ArrayList();
    if (!up) li.add("up");
    if (!down) li.add("down");
    if (!left) li.add("left");
    if (!right) li.add("right");
    Collections.shuffle(li);
    if (li.get(0).equals("up")) {
      mazeArray[(2*((int)Math.random()*((horLine+1)/2)))+rowDif][vertLine+colDif]=".";
    }
    if (li.get(0).equals("down")) {
      mazeArray[(horLine +1 + 2*((int)Math.random()*((rows-horLine)/2)))+rowDif][vertLine+colDif]=".";
    }
    if (li.get(0).equals("left")) {
      mazeArray[horLine+rowDif][(2*((int)Math.random()*((vertLine+1)/2)))+colDif]=".";
    }
    if (li.get(0).equals("right")) {
      mazeArray[horLine+rowDif][(vertLine  +1 +2*((int)Math.random()*((rows-vertLine)/2)))+colDif]=".";
    }
    repaint();
    try {
    if (slowmo) Thread.sleep((30000/(mazeArray.length+mazeArray[0].length)));
    } catch (Exception e) {
    }
    generate(horLine,vertLine,rowDif,colDif);
    repaint();
    generate(horLine,cols-vertLine-1,rowDif,colDif+vertLine+1);
    repaint();
    generate(rows-horLine-1,vertLine,rowDif+horLine+1,colDif);
    repaint();
    generate(rows-horLine-1,cols-vertLine-1,rowDif+horLine+1,colDif+vertLine+1);
    repaint();
    if (rows==mazeArray.length-2) {
      if (li.get(1).equals("up")) {
        mazeArray[1][cols]="-";
        mazeArray[1][1]="+";
      }else if (li.get(1).equals("left")) {
         mazeArray[1][1]="+";
         mazeArray[rows][1]="-";
      } else if (li.get(1).equals("right")){
        mazeArray[1][cols]="+";
        mazeArray[rows][cols]="-";
      } else {
        mazeArray[rows][1]="+";
        mazeArray[rows][cols]="-";
      }
    }
  }
  public void deadEndSolve() {
    boolean somethingChanged = true;
    ArrayList ops=new ArrayList(500);
    for (int i=0; i<mazeArray.length; i++) {
      for (int j=0; j<mazeArray[0].length; j++) {
        if (mazeArray[i][j].equals(".")) ops.add(new Point(i,j));
      }
    }
    while (somethingChanged) {
      somethingChanged=false;
      for (int i=0; i<ops.size(); i++) {
        int walls=0;
        Point p=(Point) ops.get(i);
        if (mazeArray[p.x+1][p.y].equals("/") || mazeArray[p.x+1][p.y].equals("x")) walls++;
        if (mazeArray[p.x-1][p.y].equals("/") || mazeArray[p.x-1][p.y].equals("x")) walls++;
        if (mazeArray[p.x][p.y+1].equals("/") || mazeArray[p.x][p.y+1].equals("x")) walls++;
        if (mazeArray[p.x][p.y-1].equals("/") || mazeArray[p.x][p.y-1].equals("x")) walls++;
        if (walls>=3) {
          mazeArray[p.x][p.y]="x";
          somethingChanged=true;
          ops.remove(p);
          repaint();
          if (slowmo) {
            try {
              Thread.sleep(400/mazeArray.length*mazeArray[0].length);
            } catch (Exception e) {
          }
          }
        }
      }
    }
  }
  public void generateDFS(int rows, int cols) {
    mazeArray = new String[rows][cols];
    for(int i = 0; i < mazeArray.length; i++){
        for(int j = 0; j < mazeArray[i].length; j++){
            mazeArray[i][j] = "/";
        }
    }
    mazeArray[1][1]=".";
    DFS(1,1);
    mazeArray[1][1]="+";
    mazeArray[rows-2][cols-2]="-";
    repaint();
}
public void DFS(int row, int col) {
    repaint();
    try {
    if (slowmo) Thread.sleep(4000/(mazeArray.length+mazeArray[0].length));
    } catch (Exception e) {
    }
    ArrayList li = new ArrayList();
    li.add((byte)2);
    li.add((byte)3);
    li.add((byte)0);
    li.add((byte)1);
    Collections.shuffle(li);
    for (int i=0; i<4; i++) {
    byte test =(byte) li.get(i);
    if (test==2) {
        if (col!=1 && mazeArray[row][col-2].equals("/")) {
            mazeArray[row][col-2]=".";
            mazeArray[row][col-1]=".";
            DFS(row,col-2);
        }
    }
    if (test==3) {
        if (col!=mazeArray[0].length-2 && mazeArray[row][col+2].equals("/")) {
            mazeArray[row][col+2]=".";
            mazeArray[row][col+1]=".";
            DFS(row,col+2);
        }
    }
    if (test==0) {
        if (row!=1 && mazeArray[row-2][col].equals("/")) {
            mazeArray[row-2][col]=".";
            mazeArray[row-1][col]=".";
            DFS(row-2,col);
        }
    }
    if (test==1) {
        if (row!=mazeArray.length-2 && mazeArray[row+2][col].equals("/")) {
            mazeArray[row+2][col]=".";
            mazeArray[row+1][col]=".";
            DFS(row+2,col);
        }
    }
}
}
public void genPrim(int rows, int cols) {
  mazeArray=new String[rows][cols];
  for (int i=0; i<rows; i++) {
    for (int j=0; j<cols; j++) {
      mazeArray[i][j]="/";
    }
  }
  ArrayList walls=new ArrayList(60);
  walls.add(new PrimWall(2,1,PrimWall.DOWN));
  mazeArray[1][1]=".";
  while (walls.size()>0) {
    int n=(int)Math.floor(Math.random()*(walls.size()-1));
    PrimWall w=(PrimWall) walls.get(n);
    int row=w.row+w.rowDif;
    int col=w.col+w.colDif;
    if (mazeArray[row][col].equals("/") && mazeArray[w.row][w.col].equals("/")) {
      mazeArray[row][col]=".";
      mazeArray[w.row][w.col]=".";
      if (mazeArray[row+1][col].equals("/") && row!=rows-2) walls.add(new PrimWall(row+1,col,PrimWall.DOWN));
      if (mazeArray[row][col+1].equals("/") && col!=cols-2) walls.add(new PrimWall(row,col+1,PrimWall.RIGHT));
      if (mazeArray[row-1][col].equals("/") && row!=1) walls.add(new PrimWall(row-1,col,PrimWall.UP));
      if (mazeArray[row][col-1].equals("/") && col!=1) walls.add(new PrimWall(row,col-1,PrimWall.LEFT));
      repaint();
      try {
    if (slowmo)Thread.sleep(4000/(mazeArray.length+mazeArray[0].length));
    } catch (Exception e) {
    }
    }
    walls.remove(w);
  }
  mazeArray[1][1]="+";
  mazeArray[rows-2][cols-2]="-";
  repaint();
}
  public boolean tremaux(int row, int col) {
    // *=current :=wrong
    boolean valid=false;
    mazeArray[row][col]="*";
    repaint();
  try {
  if(slowmo) Thread.sleep(3000/(mazeArray.length+mazeArray[0].length));
  } catch (Exception e) {
  }
    ArrayList li = new ArrayList();
    li.add((byte)2);
    li.add((byte)3);
    li.add((byte)0);
    li.add((byte)1);
    Collections.shuffle(li);
    for (int i=0; i<4; i++) {
    byte test =(byte) li.get(i);
    if (test==2) {
        if (col!=1 && mazeArray[row][col-2].equals(".") && mazeArray[row][col-1].equals(".")) {
            mazeArray[row][col-1]="*";
            if (tremaux(row,col-2)) {
              valid=true;
            } else {
              mazeArray[row][col-1]=":";
            }
        }
    }
    if (test==1) {
        if (col!=mazeArray[0].length-2 && mazeArray[row][col+2].equals(".") && mazeArray[row][col+1].equals(".")) {
            mazeArray[row][col+1]="*";
            if (tremaux(row,col+2)) {
              valid=true;
            } else {
              mazeArray[row][col+1]=":";
            }
        }
    }
    if (test==0) {
        if (row!=1 && mazeArray[row-2][col].equals(".") && mazeArray[row-1][col].equals(".")) {
            mazeArray[row-1][col]="*";
            if (tremaux(row-2,col)) {
              valid=true;
            } else {
              mazeArray[row-1][col]=":";
            }
        }
    }
    if (test==3) {
        if (row!=mazeArray.length-2 && mazeArray[row+2][col].equals(".") && mazeArray[row+1][col].equals(".")) {
            mazeArray[row+1][col]="*";
            if (tremaux(row+2,col)) {
              valid=true;
            } else {
              mazeArray[row+1][col]=":";
            }
        }
    }
    }
    if (col!=1 && mazeArray[row][col-2].equals("-") && mazeArray[row][col-1].equals(".")) {
      valid=true;
      mazeArray[row][col-1]="*";
    }
    if (col!=mazeArray[0].length-2 && mazeArray[row][col+2].equals("-") && mazeArray[row][col+1].equals(".")) {
      valid=true;
      mazeArray[row][col+1]="*";
    }
    if (row!=1 && mazeArray[row-2][col].equals("-") && mazeArray[row-1][col].equals(".")) {
      valid=true;
      mazeArray[row-1][col]="*";
    }
    if (row!=mazeArray.length-2 && mazeArray[row+2][col].equals("-") && mazeArray[row+1][col].equals(".")) {
      valid=true;
      mazeArray[row+1][col]="*";
    }
  mazeArray[row][col]=valid ? "*": ":";
  repaint();
  try {
  if (slowmo)Thread.sleep(3000/(mazeArray.length+mazeArray[0].length));
  } catch (Exception e) {
  }
  return valid;
  }
  public void tremSolve() {
    for (int i=0; i<mazeArray.length; i++) {
      for (int j=0; j<mazeArray[0].length; j++) {
        if (mazeArray[i][j].equals("+")) {
          tremaux(i,j);
          mazeArray[i][j]="+";
          repaint();
        }
      }
    }
  }
}
class ASharp {
  ArrayList children=new ArrayList();
  int row,col,dist;
  boolean valid=true;
  static boolean changed=true;
  static int distToStart=0;
  public ASharp(int x,int y, int fromStart) {
    row=x;
    col=y;
    dist=fromStart;
    valid=true;
  }
  public boolean step() {
    boolean isValid=false;
    if (children.size()==0) {
      if (col!=1 && TechProject.proj.maze.mazeArray[row][col-2].equals(".") && TechProject.proj.maze.mazeArray[row][col-1].equals(".")) {
        children.add(new ASharp(row,col-2,dist+2));
        isValid=true;
        changed=true;
        TechProject.proj.maze.mazeArray[row][col-2]="t" + (dist+2);
        TechProject.proj.maze.mazeArray[row][col-1]="t" + (dist+1);
      }
      if (col!=TechProject.proj.maze.mazeArray[0].length-2 && TechProject.proj.maze.mazeArray[row][col+2].equals(".") && TechProject.proj.maze.mazeArray[row][col+1].equals(".")) {
        children.add(new ASharp(row,col+2,dist+2));
        isValid=true;
        changed=true;
        TechProject.proj.maze.mazeArray[row][col+2]="t" + (dist+2);
        TechProject.proj.maze.mazeArray[row][col+1]="t" + (dist+1);
      }
      if (row!=1 && TechProject.proj.maze.mazeArray[row-2][col].equals(".") && TechProject.proj.maze.mazeArray[row-1][col].equals(".")) {
        children.add(new ASharp(row-2,col,dist+2));
        isValid=true;
        changed=true;
        TechProject.proj.maze.mazeArray[row-2][col]="t" + (dist+2);
        TechProject.proj.maze.mazeArray[row-1][col]="t" + (dist+1);
      }
      if (row!=TechProject.proj.maze.mazeArray.length-2 && TechProject.proj.maze.mazeArray[row+2][col].equals(".") && TechProject.proj.maze.mazeArray[row+1][col].equals(".")) {
        children.add(new ASharp(row+2,col,dist+2));
        isValid=true;
        changed=true;
        TechProject.proj.maze.mazeArray[row+2][col]="t" + (dist+2);
        TechProject.proj.maze.mazeArray[row+1][col]="t" + (dist+1);
      }
    } else {
    for (int i=0; i<children.size();i++) {
      if (((ASharp)children.get(i)).valid && ((ASharp)children.get(i)).step()) isValid=true;
    }
    }
    if ((col!=1 && TechProject.proj.maze.mazeArray[row][col-2].equals("-")) && (TechProject.proj.maze.mazeArray[row][col-1].equals(".") || TechProject.proj.maze.mazeArray[row][col-1].charAt(0)=='t')) {
      isValid=true;
      distToStart=Math.max(dist+2,distToStart);
      TechProject.proj.maze.mazeArray[row][col-1]="t" + dist+1;
    }
    if ((col!=TechProject.proj.maze.mazeArray[0].length-2 && TechProject.proj.maze.mazeArray[row][col+2].equals("-"))&& (TechProject.proj.maze.mazeArray[row][col+1].equals(".")  || TechProject.proj.maze.mazeArray[row][col+1].charAt(0)=='t')) {
      isValid=true;
      distToStart=Math.max(dist+2,distToStart);
      TechProject.proj.maze.mazeArray[row][col+1]="t" + dist+1;
    }
    if ((row!=1 && TechProject.proj.maze.mazeArray[row-2][col].equals("-"))&& (TechProject.proj.maze.mazeArray[row-1][col].equals(".") || TechProject.proj.maze.mazeArray[row-1][col].charAt(0)=='t')) {
      isValid=true;
      distToStart=Math.max(dist+2,distToStart);
      TechProject.proj.maze.mazeArray[row-1][col]="t" + dist+1;
    }
    if ((row!=TechProject.proj.maze.mazeArray.length-2 && TechProject.proj.maze.mazeArray[row+2][col].equals("-")) && (TechProject.proj.maze.mazeArray[row+1][col].equals(".") || TechProject.proj.maze.mazeArray[row+1][col].charAt(0)=='t')) {
      isValid=true;
      distToStart=Math.max(dist+2,distToStart);
      TechProject.proj.maze.mazeArray[row+1][col]="t" + dist+1;
    }
    if (isValid) {
      TechProject.proj.maze.mazeArray[row][col]="t" + dist;
    } else {
      TechProject.proj.maze.mazeArray[row][col]="f"+dist;
    }
    for (int i=0; i<children.size(); i++) {
      ASharp a=(ASharp)children.get(i);
      if (!a.valid) TechProject.proj.maze.mazeArray[(row+a.row)/2][(col+a.col)/2]="f" + dist+1;
    }
    TechProject.proj.maze.repaint();
    valid=isValid;
    return isValid;
  }
  static void ASharpSolve() {
    ASharp start=new ASharp(0,0,0);
    distToStart=0;
    changed=true;
    int startRow=0;
    int startCol=0;
    for (int i=0; i<TechProject.proj.maze.mazeArray.length; i++) {
      for (int j=0; j<TechProject.proj.maze.mazeArray[0].length; j++) {
        if (TechProject.proj.maze.mazeArray[i][j].equals("+")) {
          start=new ASharp(i,j,0);
          startRow=i;
          startCol=j;
        }
      }
    }
      while (ASharp.changed) {
        changed=false;
        start.step();
        int tChils=0;
        ASharp pos=new ASharp(0,0,0);
        for (int i=0; i<start.children.size(); i++) {
          if (((ASharp)start.children.get(i)).valid) {
            tChils++;
            pos=(ASharp) start.children.get(i);
          }
        }
        if (tChils==1) {
          start=pos;
        }
        TechProject.proj.maze.repaint();
        try {
        if (TechProject.proj.maze.slowmo) Thread.sleep(30000/(TechProject.proj.maze.mazeArray.length+TechProject.proj.maze.mazeArray[0].length));
      } catch (Exception e) {
      }
      }
      TechProject.proj.maze.mazeArray[startRow][startCol]="+";
      JOptionPane.showMessageDialog(null,"The end was reached in " + distToStart + " spaces.");
      TechProject.proj.maze.repaint();
    }
}
class PrimWall {
  int row=0;
  int col=0;
  public static final int UP=0;
  public static final int DOWN=1;
  public static final int LEFT=2;
  public static final int RIGHT=3;
  int direction=0;
  byte rowDif=0;
  byte colDif=0;
  public PrimWall(int x, int y, int direc) {
    row=x;
    col=y;
    direction=direc;
    if (direction==UP) rowDif=-1;
    if (direction==DOWN) rowDif=1;
    if (direction==LEFT) colDif=-1;
    if (direction==RIGHT) colDif=1;
  }
}
class HelpPanel extends JFrame {
  HelpPanel() {
    super("Help");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setSize(600,600);
    JPanel content=new JPanel();
    content.setLayout(new GridLayout(4,3,10,10));
    JLabel sliders=new JLabel("<html><p>Height and Width Sliders: Adjust the size of the maze</p></html>"); // Repeat the html stuff for all subsequent messages in HelpPanel
    JLabel slowMode=new JLabel("<html><p>Slow mode: Slow down the algorithms so they can be observed</p></html>");
    JLabel saveInfo=new JLabel("<html><p>Save Button: </p></html>");
    JLabel clearInfo=new JLabel("<html><p>Clear the Maze: </p></html>");
    JLabel genInfo=new JLabel("<html><p>Generate Button: </p></html>");
    JLabel solInfo=new JLabel("<html><p>Solve Button: </p></html>");
    JLabel trem=new JLabel("<html><p>Tremaux Solving: </p></html>");
    JLabel aSharp=new JLabel("<html><p>A# Solving: </p></html>");
    JLabel dead=new JLabel("<html><p>Dead end solving: </p></html>");
    JLabel prim=new JLabel("<html><p>Prim Generation: </p></html>");
    JLabel depth=new JLabel("<html><p>Depth Fist Generation: </p></html>");
    JLabel div=new JLabel("<html><p>Recursive Division Generation: </p></html>");
    content.add(sliders);
    content.add(genInfo);
    content.add(solInfo);
    content.add(slowMode);
    content.add(depth);
    content.add(trem);
    content.add(clearInfo);
    content.add(prim);
    content.add(aSharp);
    content.add(saveInfo);
    content.add(div);
    content.add(dead);
    add(content);
    setVisible(true);
  }
}
