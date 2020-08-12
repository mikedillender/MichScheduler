import java.awt.*;
import java.util.ArrayList;

public class CourseHandler {
    ArrayList<Course> courses;
    ArrayList<Course>[] reqby;
    ArrayList<Course>[] req;
    ArrayList<Integer>[] downs=new ArrayList[10];
    ArrayList<Integer>[] ups=new ArrayList[10];
    ArrayList<Integer>[] lvls=new ArrayList[9];
    ArrayList<Course> rendering;


    public CourseHandler(ArrayList<Course> courses,int w, int h){
        this.courses=courses;
        /*int[] reqby=new int[courses.size()];
        for (int i=0; i<courses.size();i++){
            for (Course c:courses){
                if (c.isRelated(courses.get(i).name)){
                    reqby[i]++;
                }
            }
        }
        for (int i=0; i<reqby.length; i++){
            if (reqby[i]==0){continue;}
            System.out.println(courses.get(i).name+" - "+reqby[i]);
        }*/
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
            if (c.maxUpLine!=0||c.maxDownLine!=0){
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
}
