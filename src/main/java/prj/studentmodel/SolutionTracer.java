package prj.studentmodel;

import prj.ast.JavaASTNode;
import prj.model.Problem;
import prj.model.Solution;
import prj.util.FileIO;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * The SolutionTracer inside the student model is used for tracing the user solution for a problem with
 * that problem's model solutions and returning a hint associated with the point a model solution and the user code diverge.
 */
@Service
public class SolutionTracer extends Tracer {

    private List<JavaASTNode> solutionASTs;
    private List<Solution> solutions;

    // The index of solution in solutionASTs which matches a given student attempt the most
    private int bestSolutionIndex;

    /*
     * Initialise Abstract Syntax Trees for every solution in the provided problem, by calling the generateAST method.
     * @param problem The problem file for whose solutions to initialise ASTs
     * @throws IOException if the provided solution file is not found
     */
    public void initialise(Problem problem) throws IOException {
        solutionASTs = new ArrayList<>();
        solutions = problem.getSolutions();

        // For all the model solutions in the problem, build an abstract syntax tree and store it in solutionASTs list
        for (Solution solution: solutions) {
            String solutionText = FileIO.resourceFileToString(solution.getSolutionFile());
            solutionASTs.add(generateAST(addParentheses(solutionText)));
        }
    }

    /*
     * Trace the user code with the problem's model solutions and then
     * obtain the solution text from the solutions list with at the index of bestSolutionIndex.
     * @param problem The problem the student is trying to solve.
     * @param code The code the student has written so far to solve the problem.
     * @return The model solution of @param problem that matches the user code the most.
     * @throws IOException if the requested solution file does not exist.
     */
    public String getSolution(Problem problem, String code) throws IOException {
        trace(problem, code);
        if (bestSolutionIndex < solutions.size())
            return FileIO.resourceFileToString(solutions.get(bestSolutionIndex).getSolutionFile());

        return "";
    }

    /*
     * This method traces the user code with the solutions in solutionASTs.
     * It uses the compareTrees method of the Tracer class to compare two ASTs.
     * This methods obtains the index model solution that matches the user code the most and saves it be used later.
     * @param problem The problem which the student is solving.
     * @param code The code that the user has written so far to solve the problem.
     * @return The JavaASTNode of the best matching model solution where it and the student code diverge.
     * If a matching model solution is found, then it means that the student has correctly solved the problem so
     * a symbolic success-node is returned.
     */
    public JavaASTNode trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaASTNode currentSolution = generateAST(normalisedCode);
        JavaASTNode bestComparison = null;
        int maxNodesVisited = 0;
        bestSolutionIndex = 0;

        /* For the Abstract Syntax Trees in solutionASTs, compare them with the attempt provided
        by the student. If the solution successfully matches the code provided by the user, then return this.
        In all other cases, count the number of nodes visited during compareTrees method, and set
        the best solution to the one that has visited the most methods.*/

        for (int i = 0; i < solutionASTs.size(); ++i) {
            nodesVisited = 1;
            JavaASTNode comparison = compareTrees(solutionASTs.get(i), currentSolution);
            if (comparison.getName().equals("success-node")) {
                bestSolutionIndex = i;
                return comparison;
            }

            if (nodesVisited > maxNodesVisited) {
                maxNodesVisited = nodesVisited;
                bestComparison = comparison;
                bestSolutionIndex = i;
            }
        }
        return bestComparison;
    }

}

