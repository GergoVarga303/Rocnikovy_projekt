package flows;

import graphs.Graph;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import sat.SatEncoder;
import java.util.stream.IntStream;

public class IsCritical {
    public static boolean is_A_flow_critical(Graph g, int k) {
        int n = g.getVertexCount();

        // Formulu vygenerujeme iba RAZ na začiatku
        SatEncoder encoder = new SatEncoder(g, k);
        var cnf = encoder.getCNF();
        int[] bVars = encoder.getVertexForceZeroVars();

        // --- TEST 1: Má pôvodný graf nenulový tok? (Sekvenčne na začiatku) ---
        ISolver baseSolver = SolverFactory.newDefault();
        baseSolver.newVar(cnf.getVariableCount());
        baseSolver.setExpectedNumberOfClauses(cnf.getClauseCount());
        try {
            for (int[] clause : cnf.getClauses()) {
                baseSolver.addClause(new VecInt(clause));
            }
            int[] baseAssumptions = new int[n];
            for (int v = 0; v < n; v++) baseAssumptions[v] = bVars[v];

            if (baseSolver.isSatisfiable(new VecInt(baseAssumptions))) {
                return false; // Graf už má tok, nie je kritický
            }
        } catch (Exception e) {
            return false;
        }

        // --- TEST 2: Paralelný inkrementálny cyklus pre všetky dvojice ---
        // IntStream.parallel() automaticky využije ForkJoinPool a zapojí všetky dostupné jadrá CPU.
        // allMatch funguje ako skratka (short-circuit) - ak jedno vlákno vráti false, celý stream ihneď končí.
        return IntStream.range(0, n - 1).parallel().allMatch(u -> {
            // Každé vlákno (každé 'u') dostane svoj vlastný nezávislý solver
            ISolver threadSolver = SolverFactory.newDefault();
            threadSolver.newVar(cnf.getVariableCount());
            threadSolver.setExpectedNumberOfClauses(cnf.getClauseCount());

            try {
                // Naplnenie solvera formulou pre toto vlákno
                for (int[] clause : cnf.getClauses()) {
                    threadSolver.addClause(new VecInt(clause));
                }

                // Lokálne pole assumptions pre toto vlákno
                int[] assumptions = new int[n];
                for (int x = 0; x < n; x++) {
                    assumptions[x] = bVars[x];
                }

                // Uvoľníme vrchol 'u' permanentne pre toto vlákno
                assumptions[u] = -bVars[u];

                // Prechádzame všetky prislúchajúce vrcholy 'v'
                for (int v = u + 1; v < n; v++) {
                    assumptions[v] = -bVars[v]; // Uvoľníme 'v'

                    if (!threadSolver.isSatisfiable(new VecInt(assumptions))) {
                        return false; // Našli sme protipríklad, toto vlákno vracia false
                    }

                    assumptions[v] = bVars[v]; // Vrátime 'v' späť do zafixovaného stavu
                }
            } catch (Exception e) {
                return false;
            }
            return true; // Všetky dvojice pre toto 'u' sú v poriadku
        });
    }
}