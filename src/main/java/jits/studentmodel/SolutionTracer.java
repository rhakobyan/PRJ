package jits.studentmodel;

import jits.antlr.AbstractTreeConstructor;
import jits.antlr.JavaASTNode;
import jits.antlr.JavaLexer;

import jits.antlr.JavaParser;

import jits.model.Problem;
import jits.model.Solution;
import jits.util.FileIO;
import jits.util.Pair;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.stereotype.Service;

import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SolutionTracer extends Tracer{

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

    public Pair<JavaASTNode, JavaASTNode> trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaASTNode currentSolution = generateAST(normalisedCode);
        Pair<JavaASTNode, JavaASTNode> bestComparison = null;
        int maxNodesVisited = 0;
        bestSolutionIndex = 0;

        for (int i = 0; i < solutionASTs.size(); ++i) {
            nodesVisited = 1;
            Pair<JavaASTNode, JavaASTNode> comparison = compareTrees(solutionASTs.get(i), currentSolution);
            if (comparison.getFirstElement().getName().equals("success-node")) {
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

    public boolean successfulTrace(Pair<JavaASTNode, JavaASTNode> nodePair) {
        return nodePair.getFirstElement().getName().equals("success-node");
    }

}

