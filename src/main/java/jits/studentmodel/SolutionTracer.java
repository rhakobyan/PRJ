package jits.studentmodel;

import jits.antlr.JavaASTNode;
import jits.model.Problem;
import jits.model.Solution;
import jits.util.FileIO;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolutionTracer extends Tracer {

    private List<JavaASTNode> solutionASTs;
    private List<Solution> solutions;

    private int bestSolutionIndex;

    public void initialise(Problem problem) throws IOException {
        solutionASTs = new ArrayList<>();
        solutions = problem.getSolutions();

        for (Solution solution: solutions) {
            String solutionText = FileIO.resourceFileToString(solution.getSolutionFile());
            solutionASTs.add(generateAST(addParentheses(solutionText)));
        }
    }

    public String getSolution(Problem problem, String code) throws IOException{
        trace(problem, code);
        if (bestSolutionIndex < solutions.size())
            return FileIO.resourceFileToString(solutions.get(bestSolutionIndex).getSolutionFile());

        return "";
    }

    public JavaASTNode trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaASTNode currentSolution = generateAST(normalisedCode);
        JavaASTNode bestComparison = null;
        int maxNodesVisited = 0;
        bestSolutionIndex = 0;

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

