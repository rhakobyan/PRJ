package prj.studentmodel;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import prj.ast.JavaASTNode;
import prj.model.Misconception;
import prj.model.Problem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MisconceptionTracerTest {

    @Test
    public void successfulMisconceptionTraceTest() {
        List<JavaASTNode> misconceptionASTs = new ArrayList<>();
        MisconceptionTracer tracer = new MisconceptionTracer();

        // int i = 5;
        JavaASTNode misconceptionNode = new JavaASTNode("block", "");
        JavaASTNode misconceptionChild = new JavaASTNode("local-var", "");
        misconceptionNode.addChild(misconceptionChild);
        misconceptionChild.addChild(new JavaASTNode("primitive-type", ""));
        misconceptionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        misconceptionChild.addChild(new JavaASTNode("=", ""));
        misconceptionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        misconceptionChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));

        misconceptionASTs.add(misconceptionNode);

        ReflectionTestUtils.setField(tracer, "misconceptionASTs", misconceptionASTs);

        Misconception misconception = new Misconception();
        List<Misconception> misconceptions = new ArrayList<>();
        misconceptions.add(misconception);

        ReflectionTestUtils.setField(tracer, "misconceptions", misconceptions);

        Problem problem = new Problem();
        problem.setSolutionStartIndex(1);
        problem.setSolutionEndLength(1);

        String code = "public void test() {\nint i = 5;\n}";

        JavaASTNode comparison = tracer.trace(problem, code);

        assertEquals("success-node",comparison.getName());
    }

    @Test
    public void unSuccessfulMisconceptionTraceTest() {
        List<JavaASTNode> misconceptionASTs = new ArrayList<>();
        MisconceptionTracer tracer = new MisconceptionTracer();

        // int i = 5;
        JavaASTNode misconceptionNode = new JavaASTNode("block", "");
        JavaASTNode misconceptionChild = new JavaASTNode("local-var", "");
        misconceptionNode.addChild(misconceptionChild);
        misconceptionChild.addChild(new JavaASTNode("primitive-type", ""));
        misconceptionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        misconceptionChild.addChild(new JavaASTNode("=", ""));
        misconceptionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        misconceptionChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));

        misconceptionASTs.add(misconceptionNode);

        ReflectionTestUtils.setField(tracer, "misconceptionASTs", misconceptionASTs);

        Misconception misconception = new Misconception();
        List<Misconception> misconceptions = new ArrayList<>();
        misconceptions.add(misconception);

        ReflectionTestUtils.setField(tracer, "misconceptions", misconceptions);

        Problem problem = new Problem();
        problem.setSolutionStartIndex(1);
        problem.setSolutionEndLength(1);

        String code = "public void test() {\ni = 5;\n}";

        JavaASTNode comparison = tracer.trace(problem, code);

        assertEquals("null-node",comparison.getName());
    }

    @Test
    public void getNullMisconceptionTest() {
        MisconceptionTracer tracer = new MisconceptionTracer();
        ReflectionTestUtils.setField(tracer, "equatedMisconception", null);

        assertEquals(tracer.getMisconception(), "Internal Server Error");
    }

    @Test
    public void getMisconceptionTest() {
        MisconceptionTracer tracer = new MisconceptionTracer();
        Misconception misconception = new Misconception();
        misconception.setHint("hint");
        ReflectionTestUtils.setField(tracer, "equatedMisconception", misconception);

        assertEquals(tracer.getMisconception(), "hint");
    }
}
