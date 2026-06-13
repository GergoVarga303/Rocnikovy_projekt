package sat;

import graphs.Edge;
import graphs.Graph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SatEncoder {
    private Graph g;
    private int k;
    private int[][] vars;
    private CNFFormula cnf;
    private Map<Edge, Integer> edgeIndex;

    // Tieto mapovania budeme potrebovať v IsCritical
    private int[] vertexForceZeroVars; // Premenné B_x
    private int[][] pairMergerVars;     // Premenné M_uv


    public SatEncoder(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.cnf = new CNFFormula();

        edgeIndex = new HashMap<>();
        for (int i = 0; i < g.getEdges().size(); i++) {
            edgeIndex.put(g.getEdges().get(i), i);
        }

        int edgeCount = g.getEdges().size();
        vars = new int[edgeCount][k];

        for (int e = 0; e < edgeCount; e++) {
            for (int i = 1; i < k; i++) {
                vars[e][i] = cnf.newVariable();
            }
        }

        for (int e = 0; e < edgeCount; e++) {
            int[] edgeVars = new int[k - 1];
            for (int i = 1; i < k; i++) edgeVars[i - 1] = vars[e][i];
            exactlyOne(edgeVars);
        }

        // Uložíme si koncové balance premenné pre každý vrchol
        int vertexCount = g.getVertexCount();
        int[][] vertexBalanceVars = new int[vertexCount][k];
        vertexForceZeroVars = new int[vertexCount];
        pairMergerVars = new int[vertexCount][vertexCount];

        // --- Generovanie balance podmienok (Kirchhoff) ---
        for (int v = 0; v < vertexCount; v++) {
            List<Edge> incidentEdges = g.getEdgesFrom(v);
            int degree = incidentEdges.size();

            if (degree == 0) continue;

            int[][] sumVars = new int[degree][k];
            for (int i = 0; i < degree; i++) {
                for (int sum = 0; sum < k; sum++) {
                    sumVars[i][sum] = cnf.newVariable();
                }
                exactlyOne(sumVars[i]);
            }

            // Inicializácia pre prvú hranu
            Edge firstEdge = incidentEdges.get(0);
            int firstEIdx = edgeIndex.get(firstEdge);
            boolean firstIsInflow = (firstEdge.getTo() == v);
            for (int edgeVal = 1; edgeVal < k; edgeVal++) {
                int contribution = firstIsInflow ? edgeVal : Math.floorMod(-edgeVal, k);
                cnf.addClause(-vars[firstEIdx][edgeVal], sumVars[0][contribution % k]);
            }

            // Sekvenčný sčítač pre ostatné hrany
            for (int i = 1; i < degree; i++) {
                Edge e = incidentEdges.get(i);
                int eIdx = edgeIndex.get(e);
                boolean isInflow = (e.getTo() == v);

                for (int prevSum = 0; prevSum < k; prevSum++) {
                    for (int edgeVal = 1; edgeVal < k; edgeVal++) {
                        int contribution = isInflow ? edgeVal : Math.floorMod(-edgeVal, k);
                        int newSum = (prevSum + contribution) % k;
                        cnf.addClause(-sumVars[i - 1][prevSum], -vars[eIdx][edgeVal], sumVars[i][newSum]);
                    }
                }
            }

            // Zafixujeme, ktoré premenné držia finálny balance vrcholu v
            for (int val = 0; val < k; val++) {
                vertexBalanceVars[v][val] = sumVars[degree - 1][val];
            }

            // VYLEPŠENIE: Namiesto cnf.addClause(sumVars[degree-1][0]) to naviažeme na aktivačnú premennú B_v
            vertexForceZeroVars[v] = cnf.newVariable();
            // Implikácia: B_v => (Finálny Balance == 0)  -->  CNF: ¬B_v ∨ vertexBalanceVars[v][0]
            cnf.addClause(-vertexForceZeroVars[v], vertexBalanceVars[v][0]);
        }

        // --- Pridanie premenných pre zlúčenie dvojíc (M_uv) ---
        for (int u = 0; u < vertexCount - 1; u++) {
            for (int v = u + 1; v < vertexCount; v++) {
                int mergerVar = cnf.newVariable(); // M_uv
                pairMergerVars[u][v] = mergerVar;

                // Ak sú u a v zlúčené (M_uv je TRUE), potom Balance_v musieť byť rovný -Balance_u mod k
                for (int val = 0; val < k; val++) {
                    int targetVal = Math.floorMod(-val, k);
                    // Implikácia: M_uv => (Balance_u[val] => Balance_v[targetVal])
                    // CNF: ¬M_uv ∨ ¬Balance_u[val] ∨ Balance_v[targetVal]
                    cnf.addClause(-mergerVar, -vertexBalanceVars[u][val], vertexBalanceVars[v][targetVal]);
                }
            }
        }
    }

    private void exactlyOne(int[] varsSubset) {
        cnf.addClause(varsSubset);
        for (int i = 0; i < varsSubset.length; i++) {
            for (int j = i + 1; j < varsSubset.length; j++) {
                cnf.addClause(-varsSubset[i], -varsSubset[j]);
            }
        }
    }

    public CNFFormula getCNF() { return cnf; }
    public int[] getVertexForceZeroVars() { return vertexForceZeroVars; }
    public int[][] getPairMergerVars() { return pairMergerVars; }
}