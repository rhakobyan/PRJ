package prj.studentmodel;

import prj.antlr.JavaASTNode;
import prj.model.Misconception;
import prj.model.Problem;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MisconceptionTracer extends Tracer {

    private List<JavaASTNode> misconceptionASTs;
    private List<Misconception> misconceptions;
    private Misconception equatedMisconception;

    public void initialise(Problem problem) {
        misconceptionASTs = new ArrayList<>();
        misconceptions = problem.getMisconceptions();

        for (Misconception misconception: misconceptions)
            misconceptionASTs.add(generateAST(addParentheses(misconception.getCode())));
    }

    public JavaASTNode trace(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaASTNode currentSolution = generateAST(normalisedCode);

        for (int i = 0; i < misconceptionASTs.size(); ++i) {
            nodesVisited = 1;
            JavaASTNode comparison = compareTrees(misconceptionASTs.get(i), currentSolution);
            if (comparison.getName().equals("success-node")) {
                equatedMisconception = misconceptions.get(i);
                return comparison;
            }
        }
        return new JavaASTNode("null-node", "null-node");
    }

    public String getMisconception() {
        if (equatedMisconception != null)
            return equatedMisconception.getHint();

        return "Internal Server error";
    }

}

