package sat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class SatSolverWrapper {

    /**
     * Vyuziva open source kniznicu Sat4j, funguje na viacerych platformoch, nezavisle od OS, vsetko prebehne v Jave
     */
    public static boolean solveWithSat4j(CNFFormula cnf) {
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(cnf.getVariableCount());
        solver.setExpectedNumberOfClauses(cnf.getClauseCount());

        try {
            for (int[] clause : cnf.getClauses()) {
                solver.addClause(new VecInt(clause));
            }

            IProblem problem = solver;
            return problem.isSatisfiable();

        } catch (ContradictionException e) {
            return false;
        } catch (TimeoutException e) {
            throw new RuntimeException("SAT solver timeout: " + e.getMessage());
        }
    }
    /**
     * Spusti MiniSat, funguje iba na Linux
     */
    public static boolean solveWithMiniSat(CNFFormula cnf) {
        try {
            File inputCnf = File.createTempFile("formula_", ".cnf");
            File outputResult = File.createTempFile("result_", ".txt");

            inputCnf.deleteOnExit();
            outputResult.deleteOnExit();

            cnf.writeDimacs(inputCnf.getAbsolutePath());

            ProcessBuilder pb = new ProcessBuilder("minisat", inputCnf.getAbsolutePath(), outputResult.getAbsolutePath());

            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = pb.start();

            int exitCode = process.waitFor();

            if (exitCode == 10) {
                return true;
            } else if (exitCode == 20) {
                return false;
            }

            return parseOutput(outputResult);

        } catch (IOException | InterruptedException e) {
            System.err.println("Chyba pri spúšťaní MiniSat-u: " + e.getMessage());
            throw new RuntimeException("Chyba pri spúšťaní MiniSat-u: " + e.getMessage());
        }
    }

    private static boolean parseOutput(File resultFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(resultFile))) {
            String line = br.readLine();
            if (line != null && line.trim().equalsIgnoreCase("SAT")) {
                return true;
            }
        }
        return false;
    }
}