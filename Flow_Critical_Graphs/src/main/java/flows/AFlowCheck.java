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
    private int[] remainingDegree;

    public AFlowCheck(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.edges = g.getEdges();
        this.balance = new int[g.getVertexCount()];
        this.remainingDegree = new int[g.getVertexCount()];

        for (int v = 0; v < g.getVertexCount(); v++){
            remainingDegree[v] = g.getEdgesFrom(v).size();
        }

        //hrany zoradime podla stupna koncovych vrcholov,
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
        int v = e.getFrom();
        int u = e.getTo();

        remainingDegree[u]--;
        remainingDegree[v]--;

        int forcedValue = -1;

        if (remainingDegree[v] == 0) {
            forcedValue = Math.floorMod(balance[v], k);
        }

        if (remainingDegree[u] == 0) {
            int needed = Math.floorMod(-balance[u], k);

            if (forcedValue != -1 && needed != forcedValue) {
                remainingDegree[u]++;
                remainingDegree[v]++;
                return false;
            }
            forcedValue = needed;
        }

        if (forcedValue != -1){
            if (forcedValue > 0 && forcedValue < k){
                if (applyAndContinue(i, e, forcedValue)) return true;
            }
        }
        else {
            for (int val = 1; val < k; val++) {
                if (applyAndContinue(i, e, val)) return true;
            }
        }

        remainingDegree[u]++;
        remainingDegree[v]++;
        return false;
    }

    private boolean applyAndContinue(int i, Edge e, int val) {
        balance[e.getFrom()] -= val;
        balance[e.getTo()] += val;

        if (backtrack(i + 1)) return true;

        balance[e.getFrom()] += val;
        balance[e.getTo()] -= val;
        return false;
    }
}