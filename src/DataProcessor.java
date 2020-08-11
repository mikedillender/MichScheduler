import java.util.ArrayList;

public class DataProcessor {
    public ArrayList<Course> courses;
    public DataProcessor(){
        processRaw();

    }
    public void processRaw(){
        RawData rd=new RawData();
        String[] lvls=rd.site;
        int maxtier=6;
        if (maxtier>9){maxtier=9;}
        ArrayList[] classes=new ArrayList[9];
        for (int i=0;i<9; i++){
            classes[i]=new ArrayList();
        }
        courses=new ArrayList<>();
        int[] linecounts=new int[10];
        for (int i=0; i<maxtier; i++){
            while (lvls[i].length()>0){
                int next=lvls[i].indexOf("\n\n");
                if (next==-1){next=lvls[i].length();}
                String newc=(lvls[i].substring(0,next));
                classes[i].add(newc);
                courses.add(new Course(newc,i+1, courses.size()));
                lvls[i]=(next+2<lvls[i].length())?lvls[i].substring(next+2,lvls[i].length()):"";


            }
        }


    }
}
