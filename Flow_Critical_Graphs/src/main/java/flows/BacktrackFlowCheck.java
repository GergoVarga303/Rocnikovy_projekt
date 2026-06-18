package flows;

import graphs.Edge;
import graphs.Graph;
import graphs.IdentifyVertices;

import java.util.Comparator;
import java.util.List;

public class BacktrackFlowCheck {

    private Graph g;
    private int k;

    private List<Edge> edges;
    private int[] balance;
    private int[] remainingDegree;

    public BacktrackFlowCheck(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.edges = g.getEdges();
        this.balance = new int[g.getVertexCount()];
        this.remainingDegree = new int[g.getVertexCount()];

        for (int v = 0; v < g.getVertexCount(); v++){
            remainingDegree[v] = g.getEdgesFrom(v).size();
        }

        //hrany su zoradene podla stupna koncovych vrcholov,
        //takze najprv spracuje najzlozitejsie vetvy a ukonci ich hned, ak su nevhodne
        //(po testovani som zistil, ze reversed bol rychlejsi)
        edges.sort(Comparator.comparingInt(
                e -> Math.min( g.getEdgesFrom(e.getFrom()).size(), g.getEdgesFrom(e.getTo()).size())
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

        remainingDegree[u]--;   //vybrali sme hranu, s ktorou budeme pracovat
        remainingDegree[v]--;

        int forcedValue = -1;

        if (remainingDegree[v] == 0) {  //ak tato hrana bola posledna nespracovana
            forcedValue = Math.floorMod(balance[v], k);     //tak priradime hodnotu balance, aby sa to vyrovnalo na 0
        }

        if (remainingDegree[u] == 0) {
            int needed = Math.floorMod(-balance[u], k);

            //ak hrana bola posledna pre obidve hrany, a maju iny balance, tak sa to uz neda vyrovnat
            if (forcedValue != -1 && needed != forcedValue) {
                remainingDegree[u]++;
                remainingDegree[v]++;
                return false;
            }
            forcedValue = needed;
        }

        if (forcedValue != -1){     //ak niektora hrana mala poslednu hranu
            if (forcedValue > 0 && forcedValue < k){
                if (applyAndContinue(i, e, forcedValue)) return true;
            }
        }
        else {
            for (int val = 1; val < k; val++) {     //inak klasicky backtrack pre vsetky hodnoty z grupy
                if (applyAndContinue(i, e, val)) return true;
            }
        }

        remainingDegree[u]++;
        remainingDegree[v]++;
        return false;
    }

    //vypocitame balance, ideme dalej v backtrack
    private boolean applyAndContinue(int i, Edge e, int val) {
        balance[e.getFrom()] -= val;
        balance[e.getTo()] += val;

        if (backtrack(i + 1)) return true;

        balance[e.getFrom()] += val;
        balance[e.getTo()] -= val;
        return false;
    }

    public static class IsCritical {
        public static boolean is_A_flow_critical(Graph g, int k){
            if(new BacktrackFlowCheck(g,k).hasNonZeroFlow()){
                return false;
            }

            for(int u = 0; u < g.getVertexCount()-1; u++){
                for(int v = u+1; v < g.getVertexCount(); v++){
                    Graph g2 = IdentifyVertices.identifyVertices(g,u,v);
                    if (!(new BacktrackFlowCheck(g2,k).hasNonZeroFlow())){
                        return false; //"po identifikovani lubovolnych dvoch vrcholov graf nikde nulovy A-tok mat bude"
                    }
                }
            }
            return true;
        }
    }
}