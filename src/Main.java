import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends Applet implements Runnable, KeyListener {

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
    boolean updating=false;

    public void init(){//STARTS THE PROGRAM
        this.resize(WIDTH, HEIGHT);
        this.addKeyListener(this);
        img=createImage(WIDTH,HEIGHT);
        gfx=img.getGraphics();
        DataProcessor dp=new DataProcessor();
        ch=new CourseHandler(dp.courses, WIDTH, HEIGHT);
        thread=new Thread(this);
        thread.start();
    }

    public void paint(Graphics g){
        //BACKGROUND
        gfx.setColor(background);//background
        gfx.fillRect(0,0,WIDTH,HEIGHT);//background size
        gfx.setColor(Color.BLACK);
        gfx.drawString("r="+repel,50,30);
        gfx.drawString("s="+speed,50,50);
        gfx.drawString("ly="+locky,50,70);
        ch.draw(gfx,WIDTH,HEIGHT);


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
        }
    }
    public void keyTyped(KeyEvent e) { }

}