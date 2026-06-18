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

        boolean res = IsCritical.is_A_flow_critical(b1,3);
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

        boolean res = IsCritical.is_A_flow_critical(b2,3);
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

        boolean res = IsCritical.is_A_flow_critical(j5,4);
        assertTrue(res);
    }

    /**
     * generuje glower snark J_n.
     * Počet vrcholov je 4 * n. Každý vrchol má stupeň 3 (kubický graf).
     * Pre nepárne n >= 3 nemá nikde-nulový 4-tok a je tokovo Z_4-kritický.
     */
    private Graph createFlowerSnark(int n) {
        if (n % 2 == 0) {
            throw new IllegalArgumentException("Parameter n pre Flower Snark musí byť nepárny!");
        }

        Graph g = new Graph(4 * n);
        for (int i = 0; i < n; i++) {
            int a = 4 * i;
            int b = 4 * i + 1;
            int c = 4 * i + 2;
            int d = 4 * i + 3;

            g.addEdge(a, b);
            g.addEdge(a, c);
            g.addEdge(a, d);

            int next = (i + 1) % n;
            int nextB = 4 * next + 1;
            int nextC = 4 * next + 2;
            int nextD = 4 * next + 3;

            if (i == n - 1) {
                g.addEdge(b, nextC);
                g.addEdge(c, nextB);
                g.addEdge(d, nextD);
            } else {
                g.addEdge(b, nextB);
                g.addEdge(c, nextC);
                g.addEdge(d, nextD);
            }
        }
        return g;
    }

    /**
     * generuje zovseobecneny Petersenov graf G(n, k).
     * Má 2 * n vrcholov. Pozostáva z vonkajšieho n-uholníka a vnútorného hviezdicového n-uholníka.
     */
    private Graph createGeneralizedPetersen(int n, int k) {
        Graph g = new Graph(2 * n);
        for (int i = 0; i < n; i++) {
            g.addEdge(i, (i + 1) % n);
            g.addEdge(i, n + i);
            g.addEdge(n + i, n + (i + k) % n);
        }
        return g;
    }

    @Test
    public void brutal_flowerSnark_100_vertices() {
        //n = 25 znamena 4 * 25 = 100 vrcholov a 150 hrán.
        //pocet dvojic na kontrolu: (100 * 99) / 2 = 4 950 podgrafov
        Graph j25 = createFlowerSnark(25);

        boolean res = IsCritical.is_A_flow_critical(j25, 4);

        assertTrue(res);
    }


    @Test
    public void brutal_petersen_short_circuit_120_vertices() {
        Graph petersenBig = createGeneralizedPetersen(60, 2);

        boolean res = IsCritical.is_A_flow_critical(petersenBig, 4);

        // G(60,2) ma 4 tok, teda false
        assertFalse(res);
    }

    @Test
    public void brutal_petersen_subgraph_fail_100_vertices() {
        Graph petersenBig = createGeneralizedPetersen(50, 3);

        boolean res = IsCritical.is_A_flow_critical(petersenBig, 3);

        assertFalse(res);
    }

    @Test
    public void petersenBig() {
        //zovseobecneny Petersenov graf G(47, 2) -> 2 * 47 = 94 vrcholov a 141 hrán.
        int n = 47;
        Graph petersenBig = new Graph(2 * n);

        for (int i = 0; i < n; i++) {
            petersenBig.addEdge(i, (i + 1) % n);
            petersenBig.addEdge(i, i + n);
            petersenBig.addEdge(i + n, ((i + 2) % n) + n);
        }

        boolean res = IsCritical.is_A_flow_critical(petersenBig, 4);

        assertFalse(res);
    }

    @Test
    public void flowerBigJ25() {
        int n = 25;
        Graph j25 = new Graph(4 * n);

        for (int i = 0; i < n; i++) {
            int center = 4 * i;
            int b = 4 * i + 1;
            int c = 4 * i + 2;
            int d = 4 * i + 3;

            j25.addEdge(center, b);
            j25.addEdge(center, c);
            j25.addEdge(center, d);

            int nextB = (4 * (i + 1) + 1) % 100;
            int nextC = (4 * (i + 1) + 2) % 100;
            int nextD = (4 * (i + 1) + 3) % 100;

            j25.addEdge(b, nextB);
            j25.addEdge(c, nextD); //twist
            j25.addEdge(d, nextC);
        }

        boolean res = IsCritical.is_A_flow_critical(j25, 4);

        assertTrue(res);
    }

    @Test
    public void moebiusLadderM50() {
        // Möbiusov rebríček s 100 vrcholmi (cyklus prepojený protiľahlými priečkami)
        int n = 100;
        Graph moebius = new Graph(n);

        for (int i = 0; i < n; i++) {
            moebius.addEdge(i, (i + 1) % n);
        }
        for (int i = 0; i < n / 2; i++) {
            moebius.addEdge(i, i + (n / 2));
        }

        boolean res = IsCritical.is_A_flow_critical(moebius, 3);

        assertFalse(res);
    }

    @Test
    public void generalizedPetersenG50_2() {
        int n = 50;
        Graph g50 = new Graph(2 * n);

        for (int i = 0; i < n; i++) {
            g50.addEdge(i, (i + 1) % n);
            g50.addEdge(i, i + n);
            g50.addEdge(i + n, ((i + 2) % n) + n);
        }

        boolean res = IsCritical.is_A_flow_critical(g50, 3);

        assertFalse(res);
    }

    @Test
    public void prismGraphY50() {
        // Prizmatický graf (rebrík spojený do kruhu bez pretočenia) -> 100 vrcholov.
        int n = 50;
        Graph prism = new Graph(2 * n);

        for (int i = 0; i < n; i++) {
            prism.addEdge(i, (i + 1) % n);
            prism.addEdge(i + n, ((i + 1) % n) + n);
            prism.addEdge(i, i + n);
        }

        boolean res = IsCritical.is_A_flow_critical(prism, 3);

        assertFalse(res);
    }
}
