package History;

import org.apache.commons.lang3.StringUtils;


public class HistoryItem {

    String type;
    String f;
    String value;
    int k;
    int v;
    int process;
    long time;
    long position;
    String link; // TODO: link应该不会被用到
    int index;
    int concurrency;

    public HistoryItem(String type, String f, String value, int process, long time, long position, String link, int index, int concurrency) {
        this.type = type;
        this.f = f;
        this.value = value;
        this.process = process % concurrency;
        this.time = time;
        this.position = position;
        this.link = link;
        this.index = index;
        this.concurrency = concurrency;

        String[]kv = StringUtils.strip(value,"[]").split(" ");
        this.k = Integer.parseInt(kv[0]);
        if(kv[1].equals("nil")){
            this.v = -1;
        }else{
            this.v = Integer.parseInt(kv[1]);
        }

    }


    @Override
    public String toString() {
        String vi = "nil";
        if(v != -1){
            vi = String.valueOf(v);
        }
        return ":type " + type +
                ", :f " + f +
                ", :value [" + k + " " + vi + "]"+
                ", :process " + process +
                ", :time " + time +
                ", :position " + position +
                ", :link " + link +
                ", :index " + index;
    }



    public static void main(String[] args) {

    }
}
