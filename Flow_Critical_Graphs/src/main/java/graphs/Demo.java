package graphs;

//“Orientation independence. Modify a nowhere-zero flow φ on a graph G by choosing an edge e, reversing it, and then replacing φ(e) with −φ(e). After this adjustment, φ is still a nowhere-zero flow. … Thus the existence of a nowhere-zero M-flow … is independent of the orientation of the graph.”
public class Demo {
    public static void main(String[] args) {
        Graph g = new Graph(4);
        g.addEdge(0,2);
        g.addEdge(1,3);
        g.addEdge(0,1);

        System.out.println(g.getAllEdges());
        Graph h = IdentifyVertices.identifyVertices(g,0,1);
        System.out.println(h.getAllEdges());
    }
}
