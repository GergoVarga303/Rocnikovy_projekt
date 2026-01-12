package graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private int vertexCount;
    private List<Edge> edgeList;

    public Graph(int vertexCount){
        this.vertexCount = vertexCount;
        this.edgeList = new ArrayList<>();
    }

    public void addEdge(int from, int to){
        Edge e = new Edge(from,to);
        edgeList.add(e);
    }

    public List<Edge> getEdges(){
        return edgeList;
    }

    public int getVertexCount(){
        return vertexCount;
    }
    }
