package prj.studentmodel;

import prj.ast.AbstractTreeConstructor;
import prj.ast.JavaASTNode;
import prj.antlr.JavaLexer;
import prj.antlr.JavaParser;
import prj.model.Problem;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * The abstract Tracer of the student model implements methods for various model tracing techniques.
 * It also declares abstract methods that its subclasses must implement.
 */
public abstract class Tracer {
    // The number of nodes that were visted during compareTrees()
    protected int nodesVisited;

    public abstract void initialise(Problem problem) throws IOException;
    public abstract JavaASTNode trace(Problem problem, String code);

    /*
     * Generate an Abstract Syntax tree for a given code string.
     * @param code The program for which to generate the AST.
     * @return the root node of the generated Abstract Syntax Tree.
     */
    protected JavaASTNode generateAST(String code) {
        // Lex the program
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(code));
        // Tokenize the program
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Parse the program
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();

        // Generate the AST, and return the root node
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();
        return constructor.visit(tree);
    }

    /*
     * This method compares two Abstract Syntax Trees from their root JavaASTNode.
     * The trees are compared using pre-order traversal.
     * Firstly the nodes themselves are compared and then their respective children are recursively traversed.
     * This method also counts the number of nodes that have been visited during the traversal.
     * This is later used by solutionTracer for obtaining the model solution that matches the student code the most.
     * @param modelNode A model solution node if called from the SolutionTracer,
     * and a misconception node if called from the MisconceptionTracer.
     * @param currentSolutionNode AST node of the user written code.
     * @return the node in @param modelNode at which the two trees diverge.
     * A symbolic success-node is returned if the trees are complete match each other.
     * A symbolic extra-node is returned if both the trees have been successfully traversed until the end
     * of the @param modelNode, but the @param currentSolutionNode has further children.
     */
    protected JavaASTNode compareTrees (JavaASTNode modelNode, JavaASTNode currentSolutionNode) {
        if (!modelNode.getName().equals(currentSolutionNode.getName())) {
            // If the node is a null node then return its parent
            if (modelNode.getName().equals("null-node"))
                return modelNode.getParent();

            return modelNode;
        }

        ++nodesVisited;
        List<JavaASTNode> modelChildren = modelNode.getChildren();
        List<JavaASTNode> currentSolutionChildren = currentSolutionNode.getChildren();

        /* If the nodes are +, ==, !=, &&, ||, compare them both in the normal order and in reverse order
        This is a normalisation technique, as changing the places of the LHS and RHS of these operators
        does not affect the logic of the program.
        For example, var1 + var2 is the same as var2 + var1
        && is an exception, since in Java the order technically matters. However, since the programs
        taught in this ITS are simple, this can be ignored. */

        if (((modelNode.getName().equals("+") && !containsStrings(modelNode)) || modelNode.getName().equals("*")
                || modelNode.getName().equals("==") || modelNode.getName().equals("!=") ||
                modelNode.getName().equals("&&") || modelNode.getName().equals("||"))
                && modelChildren.size() == 2 && currentSolutionChildren.size() == 2) {
            JavaASTNode firstCompare =
                    compareTrees(modelChildren.get(0), currentSolutionChildren.get(0));
            JavaASTNode secondCompare =
                    compareTrees(modelChildren.get(1), currentSolutionChildren.get(1));

            JavaASTNode altFirstCompare =
                    compareTrees(modelChildren.get(0), currentSolutionChildren.get(1));
            JavaASTNode altSecondCompare =
                    compareTrees(modelChildren.get(1), currentSolutionChildren.get(0));

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

        // Compare all their children and return the divergence point if the result
        // of the comparison is not a success-node
        for (int i = 0; i < modelChildren.size(); ++i) {
            if (i >= currentSolutionChildren.size()) {
                return modelChildren.get(i);
            }

            JavaASTNode comparisonOutcome =
                    compareTrees(modelChildren.get(i), currentSolutionChildren.get(i));
            if (!comparisonOutcome.getName().equals("success-node"))
                return comparisonOutcome;
        }
        // The currentSolutionNode has more children than the modelNode
        if (currentSolutionChildren.size() > modelChildren.size())
            return new JavaASTNode("extra-node", "");

        // Everything has passed, return success-node, that is, the two trees are equal
        return new JavaASTNode("success-node", "");
    }

    /*
     * Utility method for adding parentheses to a string code.
     */
    protected String addParentheses(String code) {
        return "{ " + code + " }";
    }

    /*
     * This method determines the part of a problem object for which an AST should be built
     * extracts that part and adds parentheses before and after that part.
     * @param problem The problem file for which to build an AST.
     * @param code The code-solution for the given problem file.
     * @return the substring of the solution which needs to be parsed enclosed in parentheses.
     */
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

    /*
     * Using regular expressions, check if a given node or its children contain a string.
     * @param node The node for which to check if it or its children contain a string.
     * @return True if the node or its children contain a string and false otherwise.
     */
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

    /*
     * Check if the node is a success node.
     */
    public boolean successfulTrace(JavaASTNode node) {
        return node.getName().equals("success-node");
    }
}
