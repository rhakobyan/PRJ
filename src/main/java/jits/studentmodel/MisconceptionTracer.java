package jits.studentmodel;

import jits.antlr.AbstractTreeConstructor;
import jits.antlr.JavaASTNode;
import jits.antlr.JavaLexer;

import jits.antlr.JavaParser;

import jits.model.Misconception;
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
public class MisconceptionTracer extends Tracer{

    private List<JavaASTNode> misconceptionASTs;
    private List<Misconception> misconceptions;
    private Misconception equatedMisconception;

    private int nodesVisited;
    private int bestSolutionIndex;

    public void initialise(Problem problem) {
        misconceptionASTs = new ArrayList<>();
        misconceptions = problem.getMisconceptions();

        for (Misconception misconception: misconceptions)
            misconceptionASTs.add(generateAST(addParentheses(misconception.getCode())));
    }

    public Pair<JavaASTNode, JavaASTNode> trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaASTNode currentSolution = generateAST(normalisedCode);

        for (int i = 0; i < misconceptionASTs.size(); ++i) {
            nodesVisited = 1;
            Pair<JavaASTNode, JavaASTNode> comparison = compareTrees(misconceptionASTs.get(i), currentSolution);
            if (comparison.getFirstElement().getName().equals("success-node")) {
                equatedMisconception = misconceptions.get(i);
                return comparison;
            }
        }
        return new Pair<>(new JavaASTNode("null-node", "null-node"), new JavaASTNode("null-node", "null-node"));
    }

    public boolean successfulTrace(Pair<JavaASTNode, JavaASTNode> nodePair) {
        return nodePair.getFirstElement().getName().equals("success-node");
    }

    public String getMisconception() {
        if (equatedMisconception != null)
            return equatedMisconception.getHint();

        return "Internal Server error";
    }

}

