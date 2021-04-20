package prj.studentmodel;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import prj.ast.JavaASTNode;
import prj.model.Problem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTracerTest {

    @Test
    public void successfulTraceTest() {
        List<JavaASTNode> solutionASTs = new ArrayList<>();
        SolutionTracer tracer = new SolutionTracer();

        // int i = 5;
        JavaASTNode solutionNode1 = new JavaASTNode("block", "");
        JavaASTNode solutionChild1 = new JavaASTNode("local-var", "");
        solutionNode1.addChild(solutionChild1);
        solutionChild1.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild1.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild1.addChild(new JavaASTNode("=", ""));
        solutionChild1.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild1.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        // int i = 25;
        JavaASTNode solutionNode2 = new JavaASTNode("block", "");
        JavaASTNode solutionChild2 = new JavaASTNode("local-var", "");
        solutionNode2.addChild(solutionChild2);
        solutionChild2.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild2.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild2.addChild(new JavaASTNode("=", ""));
        solutionChild2.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild2.getChildren().get(1).addChild(new JavaASTNode("25", ""));
        solutionASTs.add(solutionNode1);
        solutionASTs.add(solutionNode2);

        ReflectionTestUtils.setField(tracer, "solutionASTs", solutionASTs);

        Problem problem = new Problem();
        problem.setSolutionStartIndex(1);
        problem.setSolutionEndLength(1);

        String code1 = "public void test() {\n int i = 5;\n}";
        String code2 = "public void test() {\n int i = 25;\n}";

        JavaASTNode comparison1 = tracer.trace(problem, code1);
        JavaASTNode comparison2 = tracer.trace(problem, code2);

        assertEquals("success-node",comparison1.getName());
        assertEquals("success-node",comparison2.getName());
    }

    @Test
    public void unSuccessfulTraceTest() {
        List<JavaASTNode> solutionASTs = new ArrayList<>();
        SolutionTracer tracer = new SolutionTracer();

        // int i = 5;
        JavaASTNode solutionNode1 = new JavaASTNode("block", "");
        JavaASTNode solutionChild1 = new JavaASTNode("local-var", "");
        solutionNode1.addChild(solutionChild1);
        solutionChild1.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild1.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild1.addChild(new JavaASTNode("=", ""));
        solutionChild1.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild1.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        // int i = 25;
        JavaASTNode solutionNode2 = new JavaASTNode("block", "");
        JavaASTNode solutionChild2 = new JavaASTNode("local-var", "");
        solutionNode2.addChild(solutionChild2);
        solutionChild2.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild2.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild2.addChild(new JavaASTNode("=", ""));
        solutionChild2.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild2.getChildren().get(1).addChild(new JavaASTNode("25", ""));
        solutionASTs.add(solutionNode1);
        solutionASTs.add(solutionNode2);

        ReflectionTestUtils.setField(tracer, "solutionASTs", solutionASTs);

        Problem problem = new Problem();
        problem.setSolutionStartIndex(1);
        problem.setSolutionEndLength(1);

        String code = "public void test() {\n i = 5;\n}";

        JavaASTNode comparison = tracer.trace(problem, code);

        assertEquals("local-var",comparison.getName());
    }
}