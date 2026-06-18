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
    private int[] vertexForceZeroVars;

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

        //vytvorenie premennych pre hrany
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

        int vertexCount = g.getVertexCount();
        vertexForceZeroVars = new int[vertexCount];

        //kodovanie zachovania toku vo vrcholoch
        for (int v = 0; v < vertexCount; v++) {
            List<Edge> incidentEdges = g.getEdgesFrom(v);
            int degree = incidentEdges.size();
            if (degree == 0) continue;

            int[][] sumVars = new int[degree][k];
            for (int i = 0; i < degree; i++) {
                for (int sum = 0; sum < k; sum++) sumVars[i][sum] = cnf.newVariable();
                exactlyOne(sumVars[i]);
            }

            Edge firstEdge = incidentEdges.get(0);
            int firstEIdx = edgeIndex.get(firstEdge);
            boolean firstIsInflow = (firstEdge.getTo() == v);
            for (int edgeVal = 1; edgeVal < k; edgeVal++) {
                int contribution = firstIsInflow ? edgeVal : Math.floorMod(-edgeVal, k);
                cnf.addClause(-vars[firstEIdx][edgeVal], sumVars[0][contribution % k]);
            }

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
            //vynutenie nuloveho sumarneho toku
            vertexForceZeroVars[v] = cnf.newVariable();
            cnf.addClause(-vertexForceZeroVars[v], sumVars[degree - 1][0]);
        }
    }

    //pomocna metoda pre klauzuly prave jedna
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
}