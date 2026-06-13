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

    /**
     * Generuje Flower Snark J_n.
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

            // Vnútorná hviezda (stred modulu)
            g.addEdge(a, b);
            g.addEdge(a, c);
            g.addEdge(a, d);

            // Spojenia na nasledujúci modul
            int next = (i + 1) % n;
            int nextB = 4 * next + 1;
            int nextC = 4 * next + 2;
            int nextD = 4 * next + 3;

            if (i == n - 1) {
                // Prekríženie (twist) na konci kvetu
                g.addEdge(b, nextC);
                g.addEdge(c, nextB);
                g.addEdge(d, nextD);
            } else {
                // Priame prepojenie medzi modulmi
                g.addEdge(b, nextB);
                g.addEdge(c, nextC);
                g.addEdge(d, nextD);
            }
        }
        return g;
    }

    /**
     * Generuje Zovšeobecnený Petersenov graf G(n, k).
     * Má 2 * n vrcholov. Pozostáva z vonkajšieho n-uholníka a vnútorného hviezdicového n-uholníka.
     */
    private Graph createGeneralizedPetersen(int n, int k) {
        Graph g = new Graph(2 * n);
        for (int i = 0; i < n; i++) {
            // Vonkajší kruh
            g.addEdge(i, (i + 1) % n);
            // Špice (spojenie vonkajšieho a vnútorného kruhu)
            g.addEdge(i, n + i);
            // Vnútorný kruh s krokom k
            g.addEdge(n + i, n + (i + k) % n);
        }
        return g;
    }

    @Test
    public void brutal_flowerSnark_100_vertices() {
        // n = 25 znamená 4 * 25 = 100 vrcholov a 150 hrán.
        // Počet dvojíc na kontrolu: (100 * 99) / 2 = 4 950 podgrafov!
        Graph j25 = createFlowerSnark(25);

        System.out.println(">>> ŠTART: Flower Snark J_25 (100 vrcholov, k=4) <<<");
        long start = System.currentTimeMillis();

        // Flower Snarky sú 4-tokovo kritické -> metóda MUSÍ prejsť všetkých 4950 kombinácií
        // a pre každú úspešne nájsť tok (SAT), takže finálny výsledok je true.
        boolean res = IsCritical.is_A_flow_critical(j25, 4);

        long end = System.currentTimeMillis();
        System.out.println(">>> KONIEC: J_25 vyriešený za: " + (end - start) + " ms. Výsledok: " + res + " <<<");

        assertTrue(res);
    }


    @Test
    public void brutal_petersen_short_circuit_120_vertices() {
        // Zovšeobecnený Petersen G(60, 2) má 2 * 60 = 120 vrcholov a 180 hrán.
        // Tento test overuje schopnosť algoritmu rýchlo "skratovať" (short-circuit).
        // Ak graf sám o sebe MÁ nenulový k-tok, SAT solver to zistí hneď v 1. kroku
        // a nespúšťa žiadne cykly. Malo by to zbehnúť do pár milisekúnd.
        Graph petersenBig = createGeneralizedPetersen(60, 2);

        System.out.println(">>> ŠTART: Petersen G(60,2) (120 vrcholov, k=4) <<<");
        long start = System.currentTimeMillis();

        boolean res = IsCritical.is_A_flow_critical(petersenBig, 4);

        long end = System.currentTimeMillis();
        System.out.println(">>> KONIEC: G(60,2) vyriešený za: " + (end - start) + " ms. Výsledok: " + res + " <<<");

        // G(60,2) nie je kritický (má 4-tok sám o sebe), očakávame false
        assertFalse(res);
    }

    @Test
    public void brutal_petersen_subgraph_fail_100_vertices() {
        // G(50, 3) má 100 vrcholov.
        // Tento test simuluje situáciu, kedy pôvodný graf síce nemá tok (prejde úvodným filtrom),
        // ale hneď pri niektorej z prvých dvojíc vrcholov po identifikácii zlyhá (nenájde sa tok -> UNSAT podgraf).
        // Tým pádom hneď vyskočí z cyklu von.
        Graph petersenBig = createGeneralizedPetersen(50, 3);

        System.out.println(">>> ŠTART: Petersen G(50,3) (100 vrcholov, k=3) <<<");
        long start = System.currentTimeMillis();

        boolean res = IsCritical.is_A_flow_critical(petersenBig, 3);

        long end = System.currentTimeMillis();
        System.out.println(">>> KONIEC: G(50,3) vyriešený za: " + (end - start) + " ms. Výsledok: " + res + " <<<");

        assertFalse(res);
    }
}
