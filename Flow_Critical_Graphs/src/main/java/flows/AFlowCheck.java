package flows;

import graphs.Edge;
import graphs.Graph;

import java.util.Comparator;
import java.util.List;

public class AFlowCheck {

    private Graph g;
    private int k;

    private List<Edge> edges;
    private int[] balance;

    public AFlowCheck(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.edges = g.getEdges();
        this.balance = new int[g.getVertexCount()];

        //hrany su zoradene podla stupna koncovych vrcholov vzostupne(preto reversed), takze earlyModulo ukonci
        //nevhodne vetvy skorej
        edges.sort(Comparator.comparingInt(
                e -> g.getEdgesFrom(e.getFrom()).size() + g.getEdgesFrom(e.getTo()).size()
        ));
        edges = edges.reversed();

    }

    public boolean hasNonZeroFlow() {
        return backtrack(0);
    }

    private boolean backtrack(int i) {

        if (i == edges.size()) {
            for (int v = 0; v < g.getVertexCount(); v++) {
                if (Math.floorMod(balance[v], k) != 0)
                    return false;
            }
            return true;
        }

        Edge e = edges.get(i);

        for (int val = 1; val < k; val++) {

            balance[e.getFrom()] -= val;
            balance[e.getTo()] += val;

            if (earlyModulo(i + 1)) {
                if (backtrack(i + 1)) {
                    return true;
                }
            }

            balance[e.getFrom()] += val;
            balance[e.getTo()] -= val;
        }
        return false;
    }

    private boolean earlyModulo(int i) {
        for (int v = 0; v < g.getVertexCount(); v++) {  //pre kazdy vrchol
            boolean hasEdge = false;

            for (int j = i; j < edges.size(); j++) {    //pre kazdu zatial vynechanu hranu
                Edge e = edges.get(j);
                if (g.getEdgesFrom(v).contains(e)){     //este existuje hrana ktora moze zmenit balance pre v
                    hasEdge = true;
                    break;
                }
            }

            if (!hasEdge && Math.floorMod(balance[v], k) != 0)  //ak uz neexistuje hrana kt moze zmenit balance a zaroven balance nie je 0,
                return false;                                        //tak ukoncime vetvu, neide do backtrack
        }
        return true;
    }

}