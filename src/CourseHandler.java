import java.awt.*;
import java.util.ArrayList;

public class CourseHandler implements Constants {
    ArrayList<Course> courses;
    ArrayList<Course>[] reqby;
    ArrayList<Course>[] req;
    ArrayList<Integer>[] downs=new ArrayList[10];
    ArrayList<Integer>[] ups=new ArrayList[10];
    ArrayList<Integer>[] lvls=new ArrayList[9];
    ArrayList<Course> rendering;


    public CourseHandler(ArrayList<Course> courses,int w, int h){
        this.courses=courses;
        ArrayList<Integer> noreq=new ArrayList<>();
        for (int i=0; i<courses.size(); i++){
            if (courses.get(i).prs.contains("None")||courses.get(i).prs.contains("none")){
                noreq.add(i);
                //System.out.println(courses.get(i).name);
            }
        }
        reqby=new ArrayList [courses.size()];
        req=new ArrayList [courses.size()];
        for (int i=0; i<courses.size();i++) {
            req[i]=new ArrayList<>();
        }
        for (int i=0; i<courses.size();i++){
            reqby[i]=new ArrayList<>();
            Course ci=courses.get(i);
            for (Course c:courses){
                if (c.prs.contains(ci.name)){
                    reqby[i].add(c);
                    req[c.index].add(ci);
                }
            }
        }


        boolean[] shown=new boolean[courses.size()];
        for (int i=0; i<courses.size();i++){
            if (courses.get(i).prs.contains("None")||courses.get(i).prs.contains("none")||!courses.get(i).prs.contains("EECS")){
                listreqby(i,0);

            }
        }
        for (int i=0; i<10;i++) {
            if (i<9){
                lvls[i]=new ArrayList<>();
            }
            downs[i]=new ArrayList<>();
            ups[i]=new ArrayList<>();
        }
        rendering=new ArrayList<>();

        for (Course c: courses){
            if ((c.maxUpLine!=0||c.maxDownLine!=0)&&!exclude.contains(Integer.toString(c.code))){
                rendering.add(c);
            }
        }
        for (Course c: rendering){
            lvls[c.lvl].add(c.index);
        }
        for (Course c:rendering){
            downs[c.maxDownLine].add(c.index);
            ups[c.maxUpLine].add(c.index);
        }
        for (int i=0; i<ups.length; i++) {
            int j=0;
            for (int c: ups[i]) {
                courses.get(c).setInitPos(w, h,j,ups[i].size());
                courses.get(c).setReqbyReq(reqby, req, c);
                j++;
            }

        }
        addPigments();

            //System.out.println("no prereqs = "+noreq.size());
    }

    public void draw(Graphics g, int w, int h){
        for (Course c: rendering){
            for (int c1:c.reqby){
                g.setColor(c.c);
                g.drawLine((int)c.p.x,(int)c.p.y,(int)courses.get(c1).p.x,(int)courses.get(c1).p.y);
            }
        }
        for (Course c: rendering){
            c.render(g);
        }
    }


    public void addPigments(){
        //spreadFrom(getCofCode(430),230,20,215,1,new ArrayList<Course>());
        spreadFrom(getCofCode(230),230,20,215,1,new ArrayList<Course>());
        spreadFrom(getCofCode(281),150,150,245,1,new ArrayList<Course>());
        spreadFrom(getCofCode(270),230,80,60,1,new ArrayList<Course>());
        spreadFrom(getCofCode(691),230,80,60,2,new ArrayList<Course>());
        spreadFrom(getCofCode(301),200,200,15,1,new ArrayList<Course>());
        spreadFrom(getCofCode(311),40,230,45,1,new ArrayList<Course>());
        spreadFrom(getCofCode(216),40,230,45,2,new ArrayList<Course>());
        for (Course c:courses){
            if (c.pigments.size()>1){
                float[] col=new float[]{0,0,0,0};
                for (int[] p:c.pigments){
                    for (int i=0;i<3;i++){
                        col[i]+=((float)p[i])/(float)Math.pow(p[3],2);
                    }
                    col[3]+=(1f/(float)Math.pow(p[3],2));

                }
                for (int i=0;i<3;i++) { col[i]/=col[3];}
                c.c=new Color((int)col[0],(int)col[1],(int)col[2]);
            }
        }
    }

    public Course getCofCode(int code){
        for (Course c:courses){
            if (c.code==code){
                return c;
            }
        }
        return null;
    }

    public void spreadFrom(Course c, int r, int g, int b, int i, ArrayList<Course> prev){
        if(exclude.contains(Integer.toString(c.code))){return;}
        if (c==null){return;}
        int[] pigs=new int[]{r,g,b,i};
        c.pigments.add(pigs);
        prev.add(c);
        ArrayList<Integer> connected=new ArrayList<>();
        for (int in:c.reqby){ connected.add(in);}
        for (int in:c.req){connected.add(in);}
        for (int cin:connected){
            if (prev.contains(courses.get(cin))){continue;}
            spreadFrom(courses.get(cin),r,g,b,i+1,prev);
            //System.out.println("Spread from "+c.name+" to "+courses.get(cin).name);
        }
    }

    public void update(float t, boolean repel,boolean ly){
        for (Course c: rendering) {
            if (ly) {
                //c.update(t, courses, ups, repel);
                c.updateCirc(t, courses, ups,lvls, repel);
            }else {
                c.updateFree(t,courses,rendering,ups,repel);
            }
        }
    }

    public int listreqby(int in, int x){
        int d=x;
        if (x>courses.get(in).maxUpLine){courses.get(in).maxUpLine=x;}
        System.out.println();
        for (int i=0; i<x;i++){
            System.out.print("\t");
        }
        if (x!=0){ System.out.print(""); }
        System.out.print(courses.get(in).lines[0]);
        for (Course c:reqby[in]){
            int d1=listreqby(courses.indexOf(c),x+1);
            if (d1>d){d=d1;}
        }
        if (d>courses.get(in).maxDownLine){courses.get(in).maxDownLine=d-x;}
        return d;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public Course getCourseAt(int x, int y){
        Course closest=null;
        float dist=100000;
        for (Course c:courses){
            if (c.p==null){continue;}
            float d=(float)(Math.pow(c.p.x-x,2)+Math.pow(c.p.y-y,2));
            if (d<dist){
                dist=d;
                closest=c;
            }
        }
        return closest;
    }
}
