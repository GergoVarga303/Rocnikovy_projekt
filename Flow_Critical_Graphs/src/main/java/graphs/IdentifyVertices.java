package graphs;

public class IdentifyVertices{
    public static Graph identifyVertices(Graph g, int u, int v){
        Graph newG = new Graph(g.getVertexCount()-1);
        int[] map = new int[g.getVertexCount()];

        int count = 0;
        for (int i = 0; i < g.getVertexCount(); i++) {
            if (i == v) continue;     //v identifikujeme s u
            map[i] = count++;
        }
        map[v] = map[u];

        for(Edge edge : g.getEdges()){
            int a = map[edge.getFrom()];
            int b = map[edge.getTo()];

            if (a != b) {  //slucka nema vplyv na tok(vychadza -> vchadza rovnaka hodnota)
                newG.addEdge(a,b);
            }
        }
        return newG;
    }
}
