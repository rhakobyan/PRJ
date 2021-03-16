package jits.service;

import jits.antlr.AbstractTreeConstructor;
import jits.antlr.JavaASTNode;
import jits.antlr.JavaLexer;

import jits.antlr.JavaParser;

import jits.model.Problem;
import jits.model.Solution;
import jits.util.FileReader;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.antlr.v4.runtime.CharStreams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
public class ProblemTracer {
    @Autowired
    private BeanFactory beanFactory;

    List<JavaASTNode> solutionASTs;

    public void initialise(Problem problem) throws IOException {
        solutionASTs = new ArrayList<>();
        List<Solution> solutions = problem.getSolutions();
        for (Solution solution: solutions) {
            String solutionText = FileReader.resourceFileToString(solution.getSolutionFile());
            solutionASTs.add(generateAST(solutionText));
        }
    }

    private JavaASTNode generateAST(String solution) {
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(solution));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();
       return constructor.visit(tree);
    }

    public  String tempHint(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(normalisedCode));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();
        constructor.visit(tree);
        return "a";
    }

    public String getHint(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        System.out.println("normalisedCode");
        System.out.println(normalisedCode);
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(normalisedCode));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();

        JavaASTNode currentSolution = constructor.visit(tree);

        JavaASTNode comparison = compareTrees(solutionASTs.get(0), currentSolution);
        if (comparison.getName().equals("success-node"))
            return "All seems well";

        return FeedbackModule.generateFeedback(comparison);
    }

    private JavaASTNode compareTrees (JavaASTNode modelSolutionNode, JavaASTNode currentSolutionNode) {
        System.out.println("Comparing " + modelSolutionNode.getName() + " and " + currentSolutionNode.getName());
         if (!modelSolutionNode.getName().equals(currentSolutionNode.getName())) {
             if (modelSolutionNode.getName().equals("null-node"))
                 return modelSolutionNode.getParent();

             return modelSolutionNode;
         }

        List<JavaASTNode> modelSolutionChildren = modelSolutionNode.getChildren();
        List<JavaASTNode> currentSolutionChildren = currentSolutionNode.getChildren();
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

    public void getTestResponse(Problem problem) {
        KieContainer kieContainer = beanFactory.getBean(KieContainer.class, "rules/rules.drl");
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(problem);
        kieSession.fireAllRules();
    }

    private String normalise(Problem problem, String code) {
        System.out.println(problem.getSolutionStartIndex());
        System.out.println(problem.getSolutionEndLength());
        String[] lines = code.split(System.getProperty("line.separator"));
        StringJoiner joiner = new StringJoiner("");
        joiner.add("{");
        for (int i = problem.getSolutionStartIndex(); i < lines.length - problem.getSolutionEndLength(); ++i) {
            joiner.add(lines[i]);
        }
        joiner.add("}");

        return joiner.toString();
    }

}
