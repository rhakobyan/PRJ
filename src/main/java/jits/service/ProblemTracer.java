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
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(normalisedCode));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParseTree tree = parser.block();
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();

        System.out.println("Model answer:");
//        solutionASTs.get(0).printTree();
        System.out.println("Current solution");
        JavaASTNode currentSolution = constructor.visit(tree);
//        currentSolution.printTree();

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

    public String getHint2(Problem problem, String code) {
        String normalisedCode = normalise(problem, code);
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(normalisedCode));
//        Java8Lexer java8Lexer = new Java8Lexer(CharStreams.fromString(normalisedCode));
//
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        Java8Parser parser = new Java8Parser(tokens);
//        ParseTree tree = parser.blockStatement();
//        System.out.println(tree.toStringTree(parser));
//        Java8ParserBaseVisitor<JavaAST.Node> visitor = new Java8ParserBaseVisitor<>();
//        JavaAST.Node ifStatement = visitor.visit(tree);
//        ifStatement.printContent();
//        System.out.println(tree.toStringTree(parser));
//        ParseTreeWalker walker = new ParseTreeWalker();
//        walker.walk(new TraceListener(), tree);
//        List<Token> tokens = new ArrayList<>();
//        for (Token token = lexer.nextToken(); token.getType() != Token.EOF; token = lexer.nextToken()) {
//            if (ignoreToken(token.getType()))
//                continue;
//            tokens.add(token);
////            System.out.println(token.getType());
////            System.out.println(token.getText());
////            System.out.println(token);
//        }
//        try {
//            int currentTokenIndex = 0;
//            File hints = new ClassPathResource("solutionTracers/hints.json").getFile();
//            FileInputStream fis = new FileInputStream(hints);
//            String hintsTxt = IOUtils.toString(fis, StandardCharsets.UTF_8);
//            JSONArray hintsArr = new JSONArray(hintsTxt);
//            for (int i = 0; i < hintsArr.length(); ++i) {
//                JSONObject node = hintsArr.getJSONObject(i);
//                if (tokens.size() - currentTokenIndex < node.getInt("tokensNum"))
//                    return node.getString("hint");
//                StringBuilder builder = new StringBuilder();
//
//                int c = 0;
//                while (c < node.getInt("tokensNum")) {
//                    if (!tokens.get(currentTokenIndex).getText().equals(node.getJSONArray("values").getString(c))) {
//                        System.out.println(tokens.get(currentTokenIndex).getText() + "vs" + node.getJSONArray("values").getString(c));
//                        return node.getString("hint");
//                    }
//                    ++currentTokenIndex;
//                    ++c;
//                }
//            }
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

        return "All seems well";
    }

    public void getTestResponse(Problem problem) {
        KieContainer kieContainer = beanFactory.getBean(KieContainer.class, "rules/rules.drl");
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(problem);
        kieSession.fireAllRules();
    }

    private String normalise(Problem problem, String code) {
        String[] lines = code.split(System.getProperty("line.separator"));
        StringJoiner joiner = new StringJoiner("");
        joiner.add("{");
        for (int i = 4; i < lines.length - 2; ++i) {
            joiner.add(lines[i]);
        }
        joiner.add("}");
        String main =  joiner.toString();
        //Pattern pattern = Pattern.compile("^if(\\s)*\\(.*\\)(\\s)*?!.*(;);");
        return main;
    }

    private boolean ignoreToken(int type) {
        if (type == JavaLexer.WS)
            return true;

        return false;
    }
}
