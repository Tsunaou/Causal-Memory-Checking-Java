package History;

import Operation.OP_TYPE;
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
    String link; // TODO: link should not used
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

    public String getType() {
        return type;
    }

    public String getF() {
        return f;
    }

    @Deprecated
    public String getValue() {
        return value;
    }

    public int getK() {
        return k;
    }

    public int getV() {
        return v;
    }

    public int getProcess() {
        return process;
    }

    public long getTime() {
        return time;
    }

    public long getPosition() {
        return position;
    }

    public String getLink() {
        return link;
    }

    public int getIndex() {
        return index;
    }

    public int getConcurrency() {
        return concurrency;
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
