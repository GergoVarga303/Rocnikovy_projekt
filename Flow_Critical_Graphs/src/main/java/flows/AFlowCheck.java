package flows;

import graphs.Edge;
import graphs.Graph;


public class AFlowCheck {
    private Graph g;
    private int k;   //modulo pre Zk
    private int edgeCount;
    private int[] balance;   //ci hodnota vchadzajucich = hodnota vychadzajucich

    public AFlowCheck(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.edgeCount = g.getAllEdges().size();
        balance = new int[g.getVertexCount()];
    }

    public boolean hasNonZeroFlow(){
        return backtrack(0);
    }

    private boolean backtrack(int i){
        if (i == edgeCount){
            for (int v = 0; v < g.getVertexCount(); v++) {
                if (Math.floorMod(balance[v],k)!= 0)  //mat. korektne modulo, zanechava znamienko hodnoty k
                    return false;
            }
            return true;
        }

        Edge e = g.getAllEdges().get(i);

        for(int val = 1; val < k; val++){
            balance[e.getFrom()] -= val;
            balance[e.getTo()] += val;


            if (backtrack(i + 1)) {
                return true;
            }


            balance[e.getFrom()] += val;
            balance[e.getTo()] -= val;

        }
        return false;
    }

}
