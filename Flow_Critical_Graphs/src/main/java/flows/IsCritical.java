package flows;

import graphs.Graph;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import sat.SatEncoder;

public class IsCritical {
    public static boolean is_A_flow_critical(Graph g, int k) {
        int n = g.getVertexCount();

        // 1. Zakódujeme základný graf (všetky možnosti zlúčenia sú už vo formule pripravené)
        SatEncoder encoder = new SatEncoder(g, k);
        var cnf = encoder.getCNF();

        int[] bVars = encoder.getVertexForceZeroVars();
        int[][] mVars = encoder.getPairMergerVars();

        // 2. Inicializácia Sat4j solvera v pamäti
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(cnf.getVariableCount());
        solver.setExpectedNumberOfClauses(cnf.getClauseCount());

        try {
            for (int[] clause : cnf.getClauses()) {
                solver.addClause(new VecInt(clause));
            }

            // --- TEST 1: Má pôvodný graf nenulový tok? ---
            // Predpoklad: Všetky vrcholy sú vynútené na 0 (bVars sú TRUE) a žiadne zlúčenia nebežia (mVars sú FALSE)
            int[] baseAssumptions = new int[n + (n * (n - 1)) / 2];
            int idx = 0;
            for (int v = 0; v < n; v++) {
                baseAssumptions[idx++] = bVars[v]; // B_v = true
            }
            for (int u = 0; u < n - 1; u++) {
                for (int v = u + 1; v < n; v++) {
                    baseAssumptions[idx++] = -mVars[u][v]; // M_uv = false
                }
            }

            if (solver.isSatisfiable(new VecInt(baseAssumptions))) {
                return false; // Graf už má nenulový tok, nemôže byť kritický
            }

            // --- TEST 2: Inkrementálny cyklus pre všetky dvojice (u, v) ---
            for (int u = 0; u < n - 1; u++) {
                for (int v = u + 1; v < n; v++) {

                    // Skladáme predpoklady (assumptions) špecificky pre dvojicu [u, v]
                    int[] assumptions = new int[n + (n * (n - 1)) / 2];
                    int aIdx = 0;

                    // Všetky vrcholy okrem u a v musia mať balance 0
                    for (int x = 0; x < n; x++) {
                        if (x == u || x == v) {
                            assumptions[aIdx++] = -bVars[x]; // Uvoľníme Kirchhoffov zákon pre u a v
                        } else {
                            assumptions[aIdx++] = bVars[x];  // Ostatné vrcholy musia byť pevne 0
                        }
                    }

                    // Aktivujeme zlúčenie iba pre M_uv, ostatné vypneme
                    for (int x = 0; x < n - 1; x++) {
                        for (int y = x + 1; y < n; y++) {
                            if (x == u && y == v) {
                                assumptions[aIdx++] = mVars[x][y];  // M_uv = true (vynúti balance prepojenie)
                            } else {
                                assumptions[aIdx++] = -mVars[x][y]; // Ostatné zlúčenia = false
                            }
                        }
                    }

                    // Spustíme solver nad tými istými dátami, ale s novými predpokladmi
                    // Sat4j vďaka tomu kompletne zrecykluje všetko, čo sa doteraz naučil
                    if (!solver.isSatisfiable(new VecInt(assumptions))) {
                        return false; // Po identifikácii u a v graf nemá tok -> nie je kritický
                    }
                }
            }

        } catch (ContradictionException e) {
            // Ak nastane kontradikcia už pri plnení bázy, formula je triviálne UNSAT (graf nemá základný tok)
            // Čo je pre kritickosť v poriadku, musíme ale pokračovať na test dvojíc.
            return false;
        } catch (TimeoutException e) {
            throw new RuntimeException("SAT Solver timeouted", e);
        }

        return true; // Všetky dvojice po zlúčení mali SAT riešenie
    }
}