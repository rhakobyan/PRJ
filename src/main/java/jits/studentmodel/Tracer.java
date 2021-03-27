package jits.studentmodel;

import jits.antlr.AbstractTreeConstructor;
import jits.antlr.JavaASTNode;
import jits.antlr.JavaLexer;
import jits.antlr.JavaParser;
import jits.model.Problem;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Tracer {
    protected int nodesVisited;

    public abstract void initialise(Problem problem) throws IOException;
    public abstract JavaASTNode trace(Problem problem, String code);

    protected JavaASTNode generateAST(String solution) {
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(solution));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();
        return constructor.visit(tree);
    }

    protected JavaASTNode compareTrees (JavaASTNode modelSolutionNode, JavaASTNode currentSolutionNode) {
//        System.out.println("Comparing " + modelSolutionNode.getName() + " and " + currentSolutionNode.getName());
        if (!modelSolutionNode.getName().equals(currentSolutionNode.getName())) {
            if (modelSolutionNode.getName().equals("null-node"))
                return modelSolutionNode.getParent();

            return modelSolutionNode;
        }

        ++nodesVisited;
        List<JavaASTNode> modelSolutionChildren = modelSolutionNode.getChildren();
        List<JavaASTNode> currentSolutionChildren = currentSolutionNode.getChildren();

        if (((modelSolutionNode.getName().equals("+") && !containsStrings(modelSolutionNode)) || modelSolutionNode.getName().equals("*")
                || modelSolutionNode.getName().equals("==") || modelSolutionNode.getName().equals("!=") ||
                modelSolutionNode.getName().equals("&&") || modelSolutionNode.getName().equals("||"))
                && modelSolutionChildren.size() == 2 && currentSolutionChildren.size() == 2) {
            JavaASTNode firstCompare =
                    compareTrees(modelSolutionChildren.get(0), currentSolutionChildren.get(0));
            JavaASTNode secondCompare =
                    compareTrees(modelSolutionChildren.get(1), currentSolutionChildren.get(1));

            JavaASTNode altFirstCompare =
                    compareTrees(modelSolutionChildren.get(0), currentSolutionChildren.get(1));
            JavaASTNode altSecondCompare =
                    compareTrees(modelSolutionChildren.get(1), currentSolutionChildren.get(0));

            if ((firstCompare.getName().equals("success-node") &&
                    secondCompare.getName().equals("success-node")) ||
                    (altFirstCompare.getName().equals("success-node") &&
                            altSecondCompare.getName().equals("success-node")))
                return new JavaASTNode("success-node", "");
            else if (!firstCompare.getName().equals("success-node"))
                return firstCompare;
            else if (!secondCompare.getName().equals("success-node"))
                return secondCompare;
            else if (!altFirstCompare.getName().equals("success-node"))
                return altFirstCompare;
            else
                return altSecondCompare;
        }

        for (int i = 0; i < modelSolutionChildren.size(); ++i) {
            if (i >= currentSolutionChildren.size()) {
                return modelSolutionChildren.get(i);
            }

            JavaASTNode comparisonOutcome =
                    compareTrees(modelSolutionChildren.get(i), currentSolutionChildren.get(i));
            if (!comparisonOutcome.getName().equals("success-node"))
                return comparisonOutcome;
        }
        if (currentSolutionChildren.size() > modelSolutionChildren.size())
            return new JavaASTNode("extra-node", "");

        return new JavaASTNode("success-node", "");
    }

    protected String addParentheses(String code) {
        return "{ " + code + " }";
    }

    protected String normalise(Problem problem, String code) {
        String[] lines = code.split(System.getProperty("line.separator"));
        StringJoiner joiner = new StringJoiner("");
        joiner.add("{");
        for (int i = problem.getSolutionStartIndex(); i < lines.length - problem.getSolutionEndLength(); ++i) {
            joiner.add(lines[i]);
        }
        joiner.add("}");

        return joiner.toString();
    }

    protected boolean containsStrings (JavaASTNode node) {
        Pattern pattern = Pattern.compile("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"");
        Matcher matcher = pattern.matcher(node.getName());
        if (matcher.matches())
            return true;

        for (JavaASTNode child : node.getChildren()) {
            if (containsStrings(child))
                return true;
        }

        return false;
    }

    public boolean successfulTrace(JavaASTNode node) {
        return node.getName().equals("success-node");
    }
}
