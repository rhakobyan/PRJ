package prj.studentmodel;

import prj.ast.JavaASTNode;
import prj.model.Misconception;
import prj.model.Problem;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/*
 * The MisconceptionTracer inside the student model is used for tracing the user solution for a problem with
 * that problem's misconceptions and returning a specific misconception hint.
 */
@Service
public class MisconceptionTracer extends Tracer {

    private List<JavaASTNode> misconceptionASTs;
    private List<Misconception> misconceptions;
    // The misconception that the student has produced
    private Misconception equatedMisconception;

    /*
     * Initialise Abstract Syntax Trees for every misconception in the provided problem, by calling the generateAST method.
     * @param problem The problem file for whose misconceptions to initialise ASTs
     */
    public void initialise(Problem problem) {
        misconceptionASTs = new ArrayList<>();
        misconceptions = problem.getMisconceptions();

        for (Misconception misconception: misconceptions)
            misconceptionASTs.add(generateAST(addParentheses(misconception.getCode())));
    }

    /*
     * This method traces the user code with the misconceptions in misconceptionASTs.
     * It uses the compareTrees method of the Tracer class to compare two ASTs.
     * If a matching misconception is found, then it is saved to the equatedMisconception to be retrieved later.
     * @param problem The problem which the student is solving.
     * @param code The code that the user has written so far to solve the problem.
     * @return The JavaASTNode of one of the comparison where a misconception was found.
     * If no misconception was found, return a symbolic null-node to signify this.
     */
    public JavaASTNode trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        // Generate an AST for the user code
        JavaASTNode currentSolution = generateAST(normalisedCode);

        // Iterate through the misconceptions and compare them with currentSolution
        for (int i = 0; i < misconceptionASTs.size(); ++i) {
            nodesVisited = 1;
            JavaASTNode comparison = compareTrees(misconceptionASTs.get(i), currentSolution);
            // If a misconception was found save it in equatedMisconception and stop the tracing
            // as there is no need to compare further trees
            if (comparison.getName().equals("success-node")) {
                equatedMisconception = misconceptions.get(i);
                return comparison;
            }
        }
        // No misconception was found, so return a symbolic null-node
        return new JavaASTNode("null-node", "null-node");
    }

    /*
     * @return the equatedMisconception fields.
     * If it is null return "Internal Server Error" message.
     */
    public String getMisconception() {
        if (equatedMisconception != null)
            return equatedMisconception.getHint();

        return "Internal Server Error";
    }

}

