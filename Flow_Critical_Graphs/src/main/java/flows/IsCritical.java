package flows;

import graphs.Graph;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import sat.SatEncoder;
import java.util.stream.IntStream;
//CREDITS: https://www.sat4j.org/index.php

public class IsCritical {
    public static boolean is_A_flow_critical(Graph g, int k) {
        int n = g.getVertexCount();

        SatEncoder encoder = new SatEncoder(g, k);
        var cnf = encoder.getCNF();
        int[] bVars = encoder.getVertexForceZeroVars();

        //ma povodny nenulovy tok?
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
                return false; //graf uz ma tok, nie je kriticky
            }
        } catch (Exception e) {
            return false;
        }

        //vyuzivame viacere vlakna, ak na jednej vlakne dostaneme false, tak sme nasli protipriklad, ukoncime cele
        return IntStream.range(0, n - 1).parallel().allMatch(u -> {
            ISolver threadSolver = SolverFactory.newDefault();
            threadSolver.newVar(cnf.getVariableCount());
            threadSolver.setExpectedNumberOfClauses(cnf.getClauseCount());

            try {
                for (int[] clause : cnf.getClauses()) {
                    threadSolver.addClause(new VecInt(clause));
                }

                int[] assumptions = new int[n];
                for (int x = 0; x < n; x++) {
                    assumptions[x] = bVars[x];
                }

                assumptions[u] = -bVars[u];

                for (int v = u + 1; v < n; v++) {
                    assumptions[v] = -bVars[v];

                    if (!threadSolver.isSatisfiable(new VecInt(assumptions))) {
                        return false; //nasli sme protipriklad, nebude kriticky
                    }

                    assumptions[v] = bVars[v];
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        });
    }
}