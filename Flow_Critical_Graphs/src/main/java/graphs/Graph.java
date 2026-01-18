package graphs;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int vertexCount;
    private List<List<Edge>> adj;
    private List<Edge> edges;

    public Graph(int vertexCount){
        adj = new ArrayList<>(vertexCount);
        this.vertexCount = vertexCount;
        for(int i = 0; i < vertexCount; i++){
            adj.add(new ArrayList<>());
        }
        this.edges = new ArrayList<>();
    }

    public void addEdge(int from, int to){
        Edge e = new Edge(from,to);
        adj.get(from).add(e);
        adj.get(to).add(e); //susedne hrany pre kazdy vrchol
        edges.add(e);
    }

    public List<Edge> getEdgesFrom(int vertex){
        return adj.get(vertex);
    }

    public List<Edge> getEdges(){
//        List<Edge> allEdges = new ArrayList<>();
//        for(List<Edge> list : adj){
//            allEdges.addAll(list);
//        }
//        return allEdges;
        return edges;
    }

    public int getVertexCount(){
        return vertexCount;
    }
    }
