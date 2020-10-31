import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Applet implements Runnable, KeyListener, MouseListener {

    //BASIC VARIABLES
    private final int WIDTH=1800, HEIGHT=900;

    //GRAPHICS OBJECTS
    private Thread thread;
    Graphics gfx;
    Image img;

    //COLORS
    Color background=new Color(255, 255, 255);
    Color gridColor=new Color(150, 150,150);

    boolean repel=false;
    boolean locky=true;
    CourseHandler ch;
    float speed=.02f;
    boolean updating=true;
    Course clicked=null;

    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        DataProcessor dp=new DataProcessor();
        ch=new CourseHandler(dp.courses, WIDTH, HEIGHT);

        for (int i=0;i<100;i++){
            ch.update(.5f, repel, locky);
        }
        addMouseListener(this);
        thread=new Thread(this);
        thread.start();
    }

    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(background);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        gfx.setColor(Color.BLACK);
        gfx.drawString("r="+repel,50,30);
        gfx.drawString("s="+((int)(speed*1000)/1000f),50,50);
        gfx.drawString("ly="+locky,50,70);
        ch.draw(gfx,WIDTH,HEIGHT);
        if (clicked!=null) {
            gfx.setFont(gfx.getFont().deriveFont(20f));
            int x1=120;
            int y1=30;
            for (String s:clicked.getDesc()){
                gfx.drawString(s,x1,y1);
                y1+=24;
            }
            gfx.setFont(gfx.getFont().deriveFont(12f));
        }

        //RENDER FOREGROUND


        //FINAL
        g.drawImage(img,0,0,this);
    }

    public void update(Graphics g){ //REDRAWS FRAME
        paint(g);
    }

    public void run() { for (;;){//CALLS UPDATES AND REFRESHES THE GAME

        //UPDATES
        if (updating) {
            ch.update(speed, repel, locky);
        }
        repaint();//UPDATES FRAME
        try{ Thread.sleep(15); } //ADDS TIME BETWEEN FRAMES (FPS)
        catch (InterruptedException e) { e.printStackTrace();System.out.println("GAME FAILED TO RUN"); }//TELLS USER IF GAME CRASHES AND WHY
    } }


    //INPUT
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            speed*=.9f;
        } else if (e.getKeyCode() == KeyEvent.VK_UP) {
            speed*=1.1f;
        }
    }
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            updating=!updating;
        } if (e.getKeyCode() == KeyEvent.VK_L) {
            locky=!locky;
        } if (e.getKeyCode() == KeyEvent.VK_R) {
            repel=!repel;
        }if (e.getKeyCode() == KeyEvent.VK_P) {
            exportImg();
        }
    }
    public void keyTyped(KeyEvent e) { }


    public void exportImg(){
        String export="C:\\Users\\Mike\\Documents\\GitHub\\MichScheduler\\img.png";

        RenderedImage rendImage = toBufferedImage(img);
        File file = new File(export);
        try {
            ImageIO.write(rendImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);

    }

    public BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) { return (BufferedImage) img; }
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //System.out.println(e.getX()+", "+e.getY());
        clicked=ch.getCourseAt(e.getX(),e.getY());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}