package graphs;

public class Edge {
    private int from;
    private int to;

    Edge(int from, int to){
        this.from = from;
        this.to = to;
    }

    public int getFrom(){
        return from;
    }
    public int getTo(){
        return to;
    }

    @Override
    public String toString() {
        return "[" + from + "," + to + "]";
    }
}
