import com.sun.javafx.geom.Vec2f;

import java.awt.*;
import java.util.ArrayList;

public class Course {
    public String cs;
    public int lvl;
    public String[] lines;
    public String name;
    public String credits;
    public int index;
    public String prs;

    public int maxUpLine=0;
    public int maxDownLine=0;
    int wid;
    int hei;

    int[] reqby;
    int[] req;

    Vec2f p;
    Vec2f v;
    public Color c;
    int code;
    public float r;

    public float or;

    public Course(String cs,int lvl, int in){
        this.index=in;
        this.cs=cs+"";
        this.lvl=lvl;
        lines=new String[3];
        lines[0]=cs.substring(0,cs.indexOf("\n"));
        cs=cs.substring(lines[0].length()+1);
        lines[1]=(cs.contains("credits"))?cs.substring(0,cs.indexOf("credits)")+8):((cs.contains("credit"))?cs.substring(0,cs.indexOf("credit)")+7):cs.substring(0,cs.indexOf("\n")));
        lines[1]=lines[1].replaceAll("“C”","C");
        lines[1]=lines[1].replaceAll("Enforced","");
        lines[1]=lines[1].replaceAll(" enforced","");
        lines[1]=lines[1].replaceAll(" advised","");
        lines[1]=lines[1].replaceAll("Min",". Min");
        lines[1]=lines[1].replaceAll("\n","");
        cs=cs.substring(cs.indexOf("\n"));
        lines[2]=cs;

        //System.out.println("");
        //System.out.println(lines[0] + " | "+lines[1]);

        name=lines[0].substring(0,8);
        credits=lines[1].substring(lines[1].lastIndexOf("("));
        lines[1]=lines[1].substring(0,lines[1].lastIndexOf("("));
        ArrayList<String> reqstt=getStatements(lines[1]);
        for (int i=0; i<reqstt.size(); i++){
            if (reqstt.get(i).contains(" C ")||reqstt.get(i).length()<5){
                reqstt.remove(i);
                i--;
            }
        }
        parseStatements(reqstt);
        /*for (String s:reqstt){
            System.out.println(s);
        }*/
        //System.out.println("statements = "+reqstt.size());  // EECS
    }

    public void setInitPos(int w,int h,int i,int numinlayer){
        code=Integer.parseInt(name.substring(5));
        c=new Color(Integer.parseInt(name.substring(5,6))*28,Integer.parseInt(name.substring(6,7))*28,Integer.parseInt(name.substring(7,8))*28);
        //p=new Vec2f((code-((code/100)*100)+10)/120f*w,(maxUpLine+1f)*h/9f);
        //or=(code-((code/100)*100)+10)/100f*6.28f;
        or=(i/(float)numinlayer)*6.28f;
        r=((lvl)*h/9f)/2f;
        //r=((maxUpLine+1f)*h/9f)/2f;
        wid=w;
        hei=h;
        p=new Vec2f((float)Math.cos(or)*r+(wid/2),(float)Math.sin(or)*r+(hei/2));
        v=new Vec2f(0,0);
    }

    public void render(Graphics g){
        g.setColor(c);
        g.fillOval((int)p.x-5,(int)p.y-5,10,10);
        g.setColor(Color.BLACK);
        g.drawString(code+"",(int)p.x-5,(int)p.y-5);
    }

    public void updateCirc(float dt,ArrayList<Course> cs,ArrayList<Integer>[] ups,ArrayList<Integer>[] lvls, boolean repel){
        float vdx=0;
        for (int i:reqby){
            float dor=getOrDif(or,cs.get(i).or);
            vdx+=((Math.abs(dor)>50f/(6*r))?1:-1)*(6.28*r*dor)*.1f/(Math.abs(r-cs.get(i).r)+r/lvl);
        }
        for (int i:req){
            float dor=getOrDif(or,cs.get(i).or);
            vdx+=((Math.abs(dor)>50f/(6*r))?1:-1)*(6.28*r*dor)*.1f/(Math.abs(r-cs.get(i).r)+r/lvl);
        }
        if (repel) {
            //for (int i : ups[maxUpLine]) {
            for (int i : lvls[lvl]) {
                float dor=getOrDif(or,cs.get(i).or);
                if (i == index) {
                    continue;
                }
                vdx +=(dor>0?-1:1)*(400f) / (float) Math.pow(Math.abs(dor*6.28*r) + 1f, 1.5);
            }
        }
        v.x+=vdx*dt;
        if (v.x>3.14/10){v.x=.31415f;}
        //v.x=vdx;
        v.x*=.95f;
        or=or+(v.x*dt);
        p.x=(float)Math.cos(or)*r+(wid/2);
        p.y=(float)Math.sin(or)*r+(hei/2);

    }

    private float getOrDif(float firstAngle,float secondAngle){
        double difference = secondAngle - firstAngle;
        while (difference < -3.1415) difference += 6.283;
        while (difference > 3.1415) difference -= 6.283;
        return (float)difference;
    }

    public void update(float dt,ArrayList<Course> cs,ArrayList<Integer>[] ups, boolean repel){
        float vdx=0;
        for (int i:reqby){
            vdx+=(cs.get(i).p.x-p.x)/Math.abs(cs.get(i).p.y-p.y);
        }
        for (int i:req){
            vdx+=(cs.get(i).p.x-p.x)/Math.abs(cs.get(i).p.y-p.y);
        }
        if (repel) {
            for (int i : ups[maxUpLine]) {
                if (i == index) {
                    continue;
                }
                vdx +=((cs.get(i).p.x>p.x)?-1:1)*(100f) / (float) Math.pow(Math.abs(cs.get(i).p.x - p.x) + 1f, 1.5);
            }
        }
        v.x+=vdx;
        v.x*=.95f;
        float x1= p.x+(dt*(v.x+dt*vdx));
        if (x1>wid/12&&x1<wid-wid/12){
            p.x=x1;
        }
    }

    public void updateFree(float dt,ArrayList<Course> cs,ArrayList<Integer>[] ups, boolean repel){
        Vec2f a=new Vec2f();
        for (int i:reqby){
            float d=cs.get(i).p.distance(p)-30;
            a.x+=((d>50)?1:-1)*((cs.get(i).p.x>p.x)?1:-1)*(.03)*(d);
            a.y+=((d>50)?1:-1)*((cs.get(i).p.y>p.y)?1:-1)*(.03)*(d);
        }
        for (int i:req){
            float d=cs.get(i).p.distance(p)-30;
            a.x+=((d>50)?1:-1)*((cs.get(i).p.x>p.x)?1:-1)*(.03)*(d);
            a.y+=((d>50)?1:-1)*((cs.get(i).p.y>p.y)?1:-1)*(.03)*(d);
        }
        if (repel) {
            for (Course c: cs){
                if (c.index==index){continue;}
                float d=c.p.distance(p);
                a.x+=((c.p.x>p.x)?-1:1)*(.5f)/(d*d+1);
                a.y+=((c.p.y>p.y)?-1:1)*(.5f)/(d*d+1);
            }
        }
        v.x+=dt*a.x;
        v.y+=dt*a.y;
        v.x*=.99f;
        v.y*=.99f;
        Vec2f p1= new Vec2f(p.x+(dt*(v.x)),p.y+(dt*(v.y)));
        if (p1.x>wid/12&&p1.x<wid-wid/12){
            p.x=p1.x;
            v.x=0;
        }if (p1.y>hei/12&&p1.y<hei-hei/12){
            p.y=p1.y;
            v.y=0;
        }
    }



    private void parseStatements(ArrayList<String> st){
        String prs="none";
        for (String s:st){
            if (s.substring(0,1).equalsIgnoreCase(" ")){s=s.substring(1);}
            if (s.substring(0,6).equalsIgnoreCase("Prereq")){
                prs=s.substring(14);
            }
        }
        this.prs=prs;
        //System.out.println(prs);
    }

    private ArrayList<String> getStatements(String s){
        ArrayList<String> stt=new ArrayList<>();
        while (s.contains(".")){
            int i=s.indexOf(".");
            stt.add(s.substring(0,i));
            s=s.substring(s.indexOf(".")+1);
        }
        if (s.length()>5){stt.add(s);}

        return stt;
    }

    public boolean isRelated(String course){
        return lines[1].contains(course);
    }

    public void setReqbyReq( ArrayList<Course>[] reqby1, ArrayList<Course>[] req1,int i){
        req=new int[req1[i].size()];
        reqby=new int[reqby1[i].size()];
        int j=0;
        if (req1[i].size()>0) {
            for (Course c : req1[i]) {
                req[j] = c.index;
                j++;
            }
        }
        if (reqby1[i].size()>0) {
            j = 0;
            for (Course c : reqby1[i]) {
                reqby[j] = c.index;
                j++;
            }
        }

    }
}
