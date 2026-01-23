import flows.AFlowCheck;
import flows.IsCritical;
import graphs.Graph;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AFlowCriticalGraphs {
    @Test
    public void two_flow(){
        Graph g = new Graph(2);
        g.addEdge(0,1);

        boolean res = IsCritical.is_A_flow_critical(g,2);

        assertTrue(res);
    }

    @Test
    public void three_flow(){
        Graph g = new Graph(4);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);

        boolean res = IsCritical.is_A_flow_critical(g,3);

        assertTrue(res);
    }


    @Test
    public void petersen(){
        Graph petersen = new Graph(10);

        petersen.addEdge(0, 1);
        petersen.addEdge(1, 2);
        petersen.addEdge(2, 3);
        petersen.addEdge(3, 4);
        petersen.addEdge(4, 0);

        petersen.addEdge(5, 7);
        petersen.addEdge(7, 9);
        petersen.addEdge(9, 6);
        petersen.addEdge(6, 8);
        petersen.addEdge(8, 5);

        petersen.addEdge(0, 5);
        petersen.addEdge(1, 6);
        petersen.addEdge(2, 7);
        petersen.addEdge(3, 8);
        petersen.addEdge(4, 9);

        boolean res = IsCritical.is_A_flow_critical(petersen,4);
        assertTrue(res);
    }

    //blanusove snarky podla testov nie su Z3 kriticke, ale algoritmus to vyhodnoti rychlo aj napriek tomu, ze tam
    //mame 27 hran a 18 vrcholov

    @Test
    public void blanusa1(){
        Graph b1 = new Graph(18);
        int[][] b1Edges = {
                {0, 1}, {0, 4}, {0, 5},
                {1, 2}, {1, 7},
                {2, 3}, {2, 8},
                {3, 4}, {3, 9},
                {4, 6},
                {5, 10}, {5, 14},
                {6, 13}, {6, 17},
                {7, 11}, {7, 12},
                {8, 12}, {8, 15},
                {9, 13}, {9, 16},
                {10, 11}, {10, 15},
                {11, 16},
                {12, 17},
                {13, 14},
                {14, 16},
                {15, 17}
        };

        for (int[] edge : b1Edges) {
            b1.addEdge(edge[0], edge[1]);
        }

        boolean hasNonzeroFlow = new AFlowCheck(b1,3).hasNonZeroFlow();
        boolean res = IsCritical.is_A_flow_critical(b1,3);
        assertFalse(hasNonzeroFlow);
        assertFalse(res);
    }

    @Test
    public void blanusa2(){
        Graph b2 = new Graph(18);

        int[][] edges = {
                {0,1}, {1,2}, {2,3}, {3,4}, {4,0},
                {0,5}, {1,6}, {2,7}, {3,8}, {4,9},
                {5,7}, {7,9}, {9,6}, {6,8},
                {10,11}, {11,12}, {12,13}, {13,14}, {14,10},
                {10,15}, {11,16}, {12,17}, {13,5}, {14,8},

                {15,16}, {16,17}, {17,15},
                {15,7}, {16,9}, {17,6}
        };
        for (int[] e : edges) b2.addEdge(e[0], e[1]);

        boolean hasNonzeroFlow = new AFlowCheck(b2,3).hasNonZeroFlow();
        boolean res = IsCritical.is_A_flow_critical(b2,3);
        assertFalse(hasNonzeroFlow);
        assertFalse(res);
    }

    //iny snark, ten je uz Z_4 tokovo kriticky
    @Test
    public void flower(){
        int n = 5;
        Graph j5 = new Graph(4 * n);

        for (int i = 0; i < n; i++) {
            int center = 4 * i;
            int b = 4 * i + 1;
            int c = 4 * i + 2;
            int d = 4 * i + 3;

            j5.addEdge(center, b);
            j5.addEdge(center, c);
            j5.addEdge(center, d);

            int nextB = (4 * (i + 1) + 1) % 20;
            int nextC = (4 * (i + 1) + 2) % 20;
            int nextD = (4 * (i + 1) + 3) % 20;

            j5.addEdge(b, nextB);
            j5.addEdge(c, nextD);
            j5.addEdge(d, nextC);
        }

        boolean hasNonzeroFlow = new AFlowCheck(j5,4).hasNonZeroFlow();
        boolean res = IsCritical.is_A_flow_critical(j5,4);
        assertFalse(hasNonzeroFlow);
        assertTrue(res);
    }
}
