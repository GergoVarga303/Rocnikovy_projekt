package flows;

import graphs.Edge;
import graphs.Graph;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MilpFlowCheck {
    static {
        Loader.loadNativeLibraries();
    }

    public static boolean is_A_flow_critical(Graph g, int k) {
        int n = g.getVertexCount();

        if (solveFlow(g, k, -1, -1)) {
            return false;
        }

        return IntStream.range(0, n - 1).parallel().allMatch(u -> {
            for (int v = u + 1; v < n; v++) {
                if (!solveFlow(g, k, u, v)) {
                    return false;
                }
            }
            return true;
        });
    }

    private static boolean solveFlow(Graph g, int k, int idU, int idV) {
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            throw new RuntimeException("SCIP solver sa nepodarilo inicializovať.");
        }

        List<Edge> edges = g.getEdges();
        int m = edges.size();
        int n = g.getVertexCount();

        Map<Edge, Integer> edgeIndexMap = new HashMap<>();
        for (int i = 0; i < m; i++) {
            edgeIndexMap.put(edges.get(i), i);
        }

        MPVariable[] x = new MPVariable[m];
        for (int e = 0; e < m; e++) {
            x[e] = solver.makeIntVar(1, k - 1, "x_" + e);
        }

        MPVariable[] y = new MPVariable[n];
        for (int v = 0; v < n; v++) {
            if (v == idU || v == idV) continue;
            int degree = g.getEdgesFrom(v).size();
            y[v] = solver.makeIntVar(-degree, degree, "y_" + v);
        }

        MPVariable y_uv = null;
        if (idU != -1 && idV != -1) {
            int combinedDegree = g.getEdgesFrom(idU).size() + g.getEdgesFrom(idV).size();
            y_uv = solver.makeIntVar(-combinedDegree, combinedDegree, "y_" + idU + "_" + idV);
        }

        for (int v = 0; v < n; v++) {
            if (v == idU || v == idV) continue;

            MPConstraint ct = solver.makeConstraint(0, 0, "kirchhoff_" + v);
            ct.setCoefficient(y[v], -k);
            addBalanceToConstraint(g, v, edgeIndexMap, x, ct);
        }

        if (idU != -1 && idV != -1) {
            MPConstraint ct = solver.makeConstraint(0, 0, "kirchhoff_" + idU + "_" + idV);
            ct.setCoefficient(y_uv, -k);
            addBalanceToConstraint(g, idU, edgeIndexMap, x, ct);
            addBalanceToConstraint(g, idV, edgeIndexMap, x, ct);
        }

        MPSolver.ResultStatus resultStatus = solver.solve();

        return resultStatus == MPSolver.ResultStatus.FEASIBLE ||
                resultStatus == MPSolver.ResultStatus.OPTIMAL;
    }

    private static void addBalanceToConstraint(Graph g, int v, Map<Edge, Integer> edgeIndexMap, MPVariable[] x, MPConstraint ct) {
        for (Edge edge : g.getEdgesFrom(v)) {
            Integer eIdx = edgeIndexMap.get(edge);
            if (eIdx == null) continue;

            if (edge.getFrom() == v) {
                ct.setCoefficient(x[eIdx], ct.getCoefficient(x[eIdx]) + 1.0);
            }
            if (edge.getTo() == v) {
                ct.setCoefficient(x[eIdx], ct.getCoefficient(x[eIdx]) - 1.0);
            }
        }
    }
}