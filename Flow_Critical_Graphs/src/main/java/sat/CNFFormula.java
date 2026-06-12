package sat;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CNFFormula {
    private final List<int[]> clauses = new ArrayList<>();
    private int variableCount = 0;

    public int newVariable() {
        variableCount++;
        return variableCount;
    }

    public void addClause(int... literals) {
        clauses.add(literals);
    }

    public int getVariableCount() {
        return variableCount;
    }

    public int getClauseCount() {
        return clauses.size();
    }

    public List<int[]> getClauses() {
        return clauses;
    }

    public void writeDimacs(String filename) throws IOException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename))) {
            out.println("p cnf " + variableCount + " " + clauses.size());

            for (int[] clause : clauses) {
                for (int lit : clause) {
                    out.print(lit + " ");
                }
                out.println("0");
            }
        }
    }
}