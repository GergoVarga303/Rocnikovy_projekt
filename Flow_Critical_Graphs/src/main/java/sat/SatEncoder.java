package sat;

import graphs.Edge;
import graphs.Graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SatEncoder {
    private Graph g;
    private int k;
    private int[][] vars;
    private CNFFormula cnf;
    private Map<Edge, Integer> edgeIndex;

    public SatEncoder(Graph g, int k) {
        this.g = g;
        this.k = k;

        cnf = new CNFFormula();

        edgeIndex = new HashMap<>();
        for (int i = 0; i < g.getEdges().size(); i++) {
            edgeIndex.put(g.getEdges().get(i), i);
        }

        int edgeCount = g.getEdges().size();
        vars = new int[edgeCount][k];

        // 1. Premenné pre hrany: vars[e][val] znamená, že hrana e má hodnotu val (1 až k-1)
        for (int e = 0; e < edgeCount; e++) {
            for (int i = 1; i < k; i++) {
                vars[e][i] = cnf.newVariable();
            }
        }

        // Podmienky: Každá hrana musí mať priradenú práve jednu nenulovú hodnotu z grupy
        for (int e = 0; e < edgeCount; e++) {
            // Aspoň jedna hodnota z 1..k-1 (nikde nulový tok)
            int[] atLeastOne = new int[k - 1];
            for (int val = 1; val < k; val++) {
                atLeastOne[val - 1] = vars[e][val];
            }
            cnf.addClause(atLeastOne);

            // Najviac jedna hodnota
            for (int a = 1; a < k; a++) {
                for (int b = a + 1; b < k; b++) {
                    cnf.addClause(-vars[e][a], -vars[e][b]);
                }
            }
        }

        // 2. Spustenie optimalizovaného kódovania Kirchhoffovho zákona
        encodeBalanceConstraintsLinear();
    }

    /**
     * Getter pre získanie vygenerovanej formuly (rieši tvoju otázku getCNF)
     */
    public CNFFormula getCNF() {
        return cnf;
    }

    public void writeDimacs(String filename) throws IOException {
        cnf.writeDimacs(filename);
    }

    /**
     * Lineárne zakódovanie toku vo vrcholoch pomocou sekvenčného sumátora.
     * Zložitosť klesá z (k-1)^Δ na polynomiálnu Δ * k^2.
     */
    private void encodeBalanceConstraintsLinear() {
        for (int v = 0; v < g.getVertexCount(); v++) {
            List<Edge> incidentEdges = g.getEdgesFrom(v);
            if (incidentEdges.isEmpty()) continue;

            int degree = incidentEdges.size();

            // sumVars[i][val] hovorí, že po spracovaní i-tej incidentnej hrany je priebežný súčet v Z_k rovný 'val' (0 až k-1)
            int[][] sumVars = new int[degree][k];
            for (int i = 0; i < degree; i++) {
                for (int val = 0; val < k; val++) {
                    sumVars[i][val] = cnf.newVariable();
                }
                // Priebežný súčet musí v každom kroku nadobúdať práve jednu hodnotu z 0..k-1
                exactlyOne(sumVars[i]);
            }

            // --- BÁZA: Inicializácia pre prvú hranu ---
            Edge firstEdge = incidentEdges.get(0);
            int firstEdgeIdx = edgeIndex.get(firstEdge);
            boolean isFirstInflow = (firstEdge.getTo() == v);

            for (int val = 1; val < k; val++) {
                // Ak hrana z vrcholu vychádza, jej príspevok odčítame (zmena znamienka v Z_k)
                int effectiveVal = isFirstInflow ? val : Math.floorMod(-val, k);

                // Ak má prvá hrana hodnotu 'val', prvý priebežný súčet musí byť 'effectiveVal'
                // Implikácia: vars[firstEdgeIdx][val] => sumVars[0][effectiveVal]
                // V CNF: ¬vars[firstEdgeIdx][val] ∨ sumVars[0][effectiveVal]
                cnf.addClause(-vars[firstEdgeIdx][val], sumVars[0][effectiveVal]);
            }

            // --- KROK: Sekvenčné prichytávanie ďalších hrán ---
            for (int i = 1; i < degree; i++) {
                Edge e = incidentEdges.get(i);
                int eIdx = edgeIndex.get(e);
                boolean isInflow = (e.getTo() == v);

                for (int prevSum = 0; prevSum < k; prevSum++) {
                    for (int edgeVal = 1; edgeVal < k; edgeVal++) {
                        int contribution = isInflow ? edgeVal : Math.floorMod(-edgeVal, k);
                        int newSum = (prevSum + contribution) % k;

                        // Implikácia: (predošlý súčet bol prevSum AND hrana má hodnotu edgeVal) => nový súčet je newSum
                        // V CNF: ¬sumVars[i-1][prevSum] ∨ ¬vars[eIdx][edgeVal] ∨ sumVars[i][newSum]
                        cnf.addClause(-sumVars[i-1][prevSum], -vars[eIdx][edgeVal], sumVars[i][newSum]);
                    }
                }
            }

            // --- ZÁVER: Výsledný Kirchhoffov zákon ---
            // Po sčítaní všetkých incidentných hrán musí byť finálna suma na pozícii (degree - 1) rovná 0 mod k
            cnf.addClause(sumVars[degree - 1][0]);
        }
    }

    /**
     * Pomocná metóda, ktorá vynúti, aby z poľa premenných platila práve jedna (Exactly One)
     */
    private void exactlyOne(int[] varsSubset) {
        // Aspoň jedna premenná musí byť pravdivá
        cnf.addClause(varsSubset);

        // Najviac jedna premenná môže byť pravdivá
        for (int i = 0; i < varsSubset.length; i++) {
            for (int j = i + 1; j < varsSubset.length; j++) {
                cnf.addClause(-varsSubset[i], -varsSubset[j]);
            }
        }
    }
}