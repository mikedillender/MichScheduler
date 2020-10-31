import com.sun.javafx.geom.Vec2f;

import java.awt.*;
import java.util.ArrayList;

public class Course implements Constants {
    public String cs;
    public int lvl;
    public String[] lines;
    public String name;
    public String credits;
    public int index;
    public String prs;

    ArrayList<int[]> pigments;

    public int maxUpLine=0;
    public int maxDownLine=0;
    int wid;
    int hei;

    int[] reqby;
    int[] req;

    public int numlinked;
    Vec2f cen;

    Vec2f p;
    Vec2f v;
    public Color c;
    int code;
    public float r;
    String eeCore="200 215 216 230 280 300 301";
    String[] types= {"200 215 216 230 280 300 301",
            "417 458", //BE
            "311 312 320 413 414 421 423 425 427 428 429",//CSS
            "351 442 444 452 453 455 460 461 464",//Comm & Controls
            "270 370 373 376 445 470 473 475 477 478",
            "330 334 411 430 434 438", // E&M
            "418 419 463"// Power
             };
    Color[] colors= {Color.WHITE,new Color(224, 51, 53),Color.GREEN,Color.YELLOW,new Color(82, 92, 170),
            new Color(233,126,217),new Color(45, 47, 80)};

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
        code=Integer.parseInt(name.substring(5));
        credits=lines[1].substring(lines[1].lastIndexOf("("));
        lines[1]=lines[1].substring(0,lines[1].lastIndexOf("("));
        ArrayList<String> reqstt=getStatements(lines[1]);
        for (int i=0; i<reqstt.size(); i++){
            if (reqstt.get(i).contains(" C ")||reqstt.get(i).length()<5){
                reqstt.remove(i);
                i--;
            }
        }
        pigments=new ArrayList<>();
        parseStatements(reqstt);
        /*for (String s:reqstt){
            System.out.println(s);
        }*/
        //System.out.println("statements = "+reqstt.size());  // EECS
    }
    float minOr=(float)(0);
    float maxOr=(float)(Math.PI*2);
    public void setInitPos(int w,int h,int i,int numinlayer){

        wid=w;
        hei=h;
        cen=new Vec2f(wid/2f,hei/2f);
        c=new Color(Integer.parseInt(name.substring(5,6))*28,Integer.parseInt(name.substring(6,7))*28,Integer.parseInt(name.substring(7,8))*28);
        //p=new Vec2f((code-((code/100)*100)+10)/120f*w,(maxUpLine+1f)*h/9f);
        //or=(code-((code/100)*100)+10)/100f*6.28f;
        or=((i+1f)/((float)numinlayer+2))*3.14f*2;
        if (code==281){or=(float)(Math.PI*7.5/8f)*2;}
        if (code==270){or=(float)(Math.PI*4/8f)*2;}
        if (code==215){or=(float)(Math.PI*1.5f/4f)*2;}
        if (code==216){or=(float)(Math.PI*3.8/8f)*2;}
        if (code==330){or=(float)(Math.PI*1/6.5f)*2/2;}
        if (code==501){or=(float)(Math.PI*1/2f)*2/2;}
        //r=((lvl)*h/9f)/2f;
        r=(float)(((maxUpLine+.8f)*h/8f))/2f;
        p=new Vec2f((float)Math.cos(or)*r+(cen.x),(float)Math.sin(or)*r+(cen.y));
        v=new Vec2f(0,0);
    }

    public void render(Graphics g){
        g.setColor(c);
        g.fillOval((int)p.x-5,(int)p.y-5,10,10);
        g.setColor(Color.BLACK);
        g.drawString(code+"",(int)p.x-5,(int)p.y-5);
        int num=0;
        for (int i=0; i<types.length; i++){
            if (types[i].contains(""+code)){
                /*if (num==0){
                    g.setColor(Color.WHITE);
                    g.fillOval((int)p.x-4,(int)p.y-4,8,8);
                }*/
                //g.setColor(colors[i]);
                //g.fillOval((int)p.x-3+num,(int)p.y-3+num,6-(num*2),6-(num*2));
            }
        }
    }

    public void updateCirc(float dt,ArrayList<Course> cs,ArrayList<Integer>[] ups,ArrayList<Integer>[] lvls, boolean repel){
        if (code==281||code==215||code==330||code==270||code==501||code==216){return;}
        float vdx=0;
        for (int i:reqby){
            float dor=getOrDif(or,cs.get(i).or,false);
            vdx+=((Math.abs(dor)>50f/(6*r))?1:-1)*(6.28*r*dor)*.1f/(Math.abs(r-cs.get(i).r)+r/lvl);
        }
        for (int i:req){
            float dor=getOrDif(or,cs.get(i).or,false);
            vdx+=((Math.abs(dor)>50f/(6*r))?1:-1)*(6.28*r*dor)*.1f/(Math.abs(r-cs.get(i).r)+r/lvl);
        }
        if (repel) {
            for (int i : ups[maxUpLine]) {
            //for (int i : lvls[lvl]) {
                float dor=getOrDif(or,cs.get(i).or,true);
                if (i == index) {
                    continue;
                }
                vdx +=(dor>0?-1:1)*(500f) / (float) Math.pow(Math.abs(dor*6.28*r) + 1f, 1.5);
            }
        }
        v.x+=vdx*dt;
        if (v.x>3.14/40){v.x=3.14f/40f;}
        if (v.x<-3.14/40){v.x=-3.14f/40f;}

        //v.x=vdx;
        v.x*=.95f;
        or=or+(v.x*dt);
        //float or1=or+(v.x*dt);
        //if (or1>minOr&&or1<maxOr){or=or1;v.x=0;v.y=0;}
        float cr=r*(float)( (2/(Math.sqrt(Math.pow(Math.cos(or),2)+Math.pow(2*Math.sin(or),2))))  );
        p.x=(float)Math.cos(or)*cr+(cen.x);
        p.y=(float)Math.sin(or)*cr+(cen.y);

    }

    private float getOrDif(float firstAngle,float secondAngle,boolean ar) {
        if (ar) {
        double difference = secondAngle - firstAngle;
        while (difference < -3.1415) difference += 6.283;
        while (difference > 3.1415) difference -= 6.283;
        return (float)difference;
        } else {
            return secondAngle - firstAngle;
        }
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

    public void updateFree(float dt,ArrayList<Course> cs,ArrayList<Course> render,ArrayList<Integer>[] ups, boolean repel){
        Vec2f a=new Vec2f();
        Vec2f cen=new Vec2f(wid/2f,hei/10f);
        float cd=cen.distance(p);
        float cdx=p.x-cen.x;
        float cdy=p.y-cen.y;
        float cp1x=cen.x+((cdx*2*r)/cd);
        float cp1y=cen.y+((cdy*2*r)/cd);
        a.x+=(.03f)*(cp1x-p.x);
        a.y+=(.03f)*(cp1y-p.y);


        for (int i:reqby){
            float d=cs.get(i).p.distance(p);
            float dx=p.x-cs.get(i).p.x;
            float dy=p.y-cs.get(i).p.y;
            int tlinked=numlinked+cs.get(i).numlinked;
            float linkrad=120+(5*(tlinked));
            float p1x=cs.get(i).p.x+((dx*linkrad)/d);
            float p1y=cs.get(i).p.y+((dy*linkrad)/d);
            a.x+=(.04f/tlinked)*(p1x-p.x);
            a.y+=(.04f/tlinked)*(p1y-p.y);
            //50^2=x^
        }
        for (int i:req){
            float d=cs.get(i).p.distance(p);
            float dx=p.x-cs.get(i).p.x;
            float dy=p.y-cs.get(i).p.y;
            int tlinked=numlinked+cs.get(i).numlinked;
            float linkrad=120+(5*(tlinked));
            float p1x=cs.get(i).p.x+((dx*linkrad)/d);
            float p1y=cs.get(i).p.y+((dy*linkrad)/d);
            a.x+=(.04f/tlinked)*(p1x-p.x);
            a.y+=(.04f/tlinked)*(p1y-p.y);
        }
        if (repel) {
            for (Course c: render){
                if (c.index==index){continue;}
                float d=c.p.distance(p);
                a.x+=((c.p.x>p.x)?-1:1)*(40f)/(d*d+20);
                a.y+=((c.p.y>p.y)?-1:1)*(40f)/(d*d+20);
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
            for (int i=0; i<exclude.length()/3; i++){
                String nm1=exclude.substring(i*3,i*3+3);
                System.out.println(nm1);
                s=s.replaceAll(nm1,"");
            }
            //if (s.contains(""))
        }
        this.prs=prs;
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
        numlinked=reqby.length+req.length;

    }

    public ArrayList<String> getDesc(){
        int maxl=70;
        ArrayList<String> ls=new ArrayList<>();
        for (String s:lines){
            if (s.length()<maxl){
                ls.add(s);
            }else {
                for (int i=0;i<s.length();i+=maxl){
                    int l=(i+maxl<s.length())?i+maxl:s.length();
                    ls.add(s.substring(i,l));
                }
            }
        }
        return ls;
    }
}
