package graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    private int vertexCount;
    private List<List<Edge>> adj;

    public Graph(int vertexCount){
        adj = new ArrayList<>(vertexCount);
        this.vertexCount = vertexCount;
        for(int i = 0; i < vertexCount; i++){
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to){
        Edge e = new Edge(from,to);
        adj.get(from).add(e);
    }

    public List<Edge> getEdgesFrom(int vertex){
        return adj.get(vertex);
    }

    public List<Edge> getAllEdges(){
        List<Edge> allEdges = new ArrayList<>();
        for(List<Edge> list : adj){
            allEdges.addAll(list);
        }
        return allEdges;
    }

    public int getVertexCount(){
        return vertexCount;
    }
    }
