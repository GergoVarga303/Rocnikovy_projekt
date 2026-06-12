package graphs;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return from == edge.from && to == edge.to;
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to);
    }
}
