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

    public static boolean solveWithSat4j(CNFFormula cnf) {
        // Inicializácia defaultného solvera v RAM
        ISolver solver = SolverFactory.newDefault();
        solver.newVar(cnf.getVariableCount());
        solver.setExpectedNumberOfClauses(cnf.getClauseCount());

        try {
            // Postupne nahádžeme klauzuly priamo do pamäte solvera
            for (int[] clause : cnf.getClauses()) {
                solver.addClause(new VecInt(clause));
            }

            IProblem problem = solver;
            // Spustenie výpočtu bez akéhokoľvek zápisu na disk
            return problem.isSatisfiable();

        } catch (ContradictionException e) {
            // Knižnica vyhodí výnimku, ak pridáš klauzulu, ktorá je v priamom
            // rozpore s predošlými (formula je triviálne nesplniteľná -> UNSAT)
            return false;
        } catch (TimeoutException e) {
            throw new RuntimeException("SAT solver timeout: " + e.getMessage());
        }
    }
    /**
     * Spustí MiniSat nad danou formulou a vráti true, ak je SATISFIABLE.
     */
    public static boolean solveWithMiniSat(CNFFormula cnf) {
        try {
            // 1. Vytvorenie dočasných súborov, ktoré sa po skončení vymažú
            File inputCnf = File.createTempFile("formula_", ".cnf");
            File outputResult = File.createTempFile("result_", ".txt");

            inputCnf.deleteOnExit();
            outputResult.deleteOnExit();

            // 2. Zapísanie formuly do DIMACS formátu
            cnf.writeDimacs(inputCnf.getAbsolutePath());

            // 3. Príprava a spustenie príkazu (minisat vstup.cnf vystup.txt)
            ProcessBuilder pb = new ProcessBuilder("minisat", inputCnf.getAbsolutePath(), outputResult.getAbsolutePath());

            // Presmerujeme chybový výstup solvera, ak by sme ho chceli debugovať
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);

            Process process = pb.start();

            // Počkáme na dokončenie solvera.
            // MiniSat vracia exit code 10 pre SAT a 20 pre UNSAT.
            int exitCode = process.waitFor();

            // 4. Vyhodnotenie výsledku podľa exit kódu alebo obsahu súboru
            if (exitCode == 10) {
                return true;
            } else if (exitCode == 20) {
                return false;
            }

            // Ak MiniSat z nejakého dôvodu nevrátil štandardný exit kód, prečítame textový výstup
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