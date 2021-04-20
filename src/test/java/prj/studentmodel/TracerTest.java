package prj.studentmodel;

import org.junit.jupiter.api.Test;
import prj.ast.JavaASTNode;
import prj.model.Problem;

import static org.junit.jupiter.api.Assertions.*;

class TracerTest {

    @Test
    public void generateASTTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = solutionTracer.generateAST("{int i = 5; System.out.println(i);}");

        assertEquals("block", node.getName());
        assertEquals(2, node.getChildren().size());
    }

    @Test
    public void compareEquivalentTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        // int i = 5; println(i);
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("local-var", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild.addChild(new JavaASTNode("=", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        solutionNode.addChild(new JavaASTNode("method-call", ""));
        solutionNode.getChildren().get(1).addChild(new JavaASTNode("println", ""));
        solutionNode.getChildren().get(1).addChild(new JavaASTNode("i", ""));

        // int i = 5; println(i);
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("local-var", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("primitive-type", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        problemChild.addChild(new JavaASTNode("=", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        problemNode.addChild(new JavaASTNode("method-call", ""));
        problemNode.getChildren().get(1).addChild(new JavaASTNode("println", ""));
        problemNode.getChildren().get(1).addChild(new JavaASTNode("i", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("success-node", result.getName());
    }

    @Test
    public void compareNotEquivalentTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        // int i = 5; println(i);
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("local-var", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild.addChild(new JavaASTNode("=", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        solutionNode.addChild(new JavaASTNode("method-call", ""));
        solutionNode.getChildren().get(1).addChild(new JavaASTNode("println", ""));
        solutionNode.getChildren().get(1).addChild(new JavaASTNode("i", ""));


        // println(i);
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("method-call", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("println", ""));
        problemChild.addChild(new JavaASTNode("i", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("local-var", result.getName());
    }

    @Test
    public void compareNormalisedEquivalentTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        // if (i == 5) println(i);
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("if", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("==", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("5", ""));
        solutionChild.addChild(new JavaASTNode("block", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("method-call", ""));
        solutionChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("println", ""));
        solutionChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("i", ""));

        // if (5 == i) println(i);
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("if", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("==", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("5", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("i", ""));
        problemChild.addChild(new JavaASTNode("block", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("method-call", ""));
        problemChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("println", ""));
        problemChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("i", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("success-node", result.getName());
    }

    @Test
    public void compareLogicalNormalisedEquivalentTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();

        // if (i > 5 && true) println(i);
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("if", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("&&", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode(">", ""));
        solutionChild.getChildren().get(0).getChildren().get(0).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(0).getChildren().get(0).addChild(new JavaASTNode("5", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("true", ""));
        solutionChild.addChild(new JavaASTNode("block", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("method-call", ""));
        solutionChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("println", ""));
        solutionChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("i", ""));

        // if (true && i > 5) println(i);
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("if", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("&&", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("true", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode(">", ""));
        problemChild.getChildren().get(0).getChildren().get(1).addChild(new JavaASTNode("i", ""));
        problemChild.getChildren().get(0).getChildren().get(1).addChild(new JavaASTNode("5", ""));
        problemChild.addChild(new JavaASTNode("block", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("method-call", ""));
        problemChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("println", ""));
        problemChild.getChildren().get(1).getChildren().get(0).addChild(new JavaASTNode("i", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("success-node", result.getName());
    }

    @Test
    public void compareArithmeticNormalisedEquivalentTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        // int i = 5 + 2;
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("local-var", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild.addChild(new JavaASTNode("=", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("+", ""));
        solutionChild.getChildren().get(1).getChildren().get(1).addChild(new JavaASTNode("5", ""));
        solutionChild.getChildren().get(1).getChildren().get(1).addChild(new JavaASTNode("2", ""));

        // int i = 2 + 5;
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("local-var", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("primitive-type", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        problemChild.addChild(new JavaASTNode("=", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("+", ""));
        problemChild.getChildren().get(1).getChildren().get(1).addChild(new JavaASTNode("2", ""));
        problemChild.getChildren().get(1).getChildren().get(1).addChild(new JavaASTNode("5", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("success-node", result.getName());
    }

    @Test
    public void compareExtraNodeTreesTest() {
        // int i = 5;
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("local-var", "");
        solutionNode.addChild(solutionChild);
        solutionChild.addChild(new JavaASTNode("primitive-type", ""));
        solutionChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        solutionChild.addChild(new JavaASTNode("=", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        solutionChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));

        // int i = 5; println(i);
        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("local-var", "");
        problemNode.addChild(problemChild);
        problemChild.addChild(new JavaASTNode("primitive-type", ""));
        problemChild.getChildren().get(0).addChild(new JavaASTNode("int", ""));
        problemChild.addChild(new JavaASTNode("=", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("i", ""));
        problemChild.getChildren().get(1).addChild(new JavaASTNode("5", ""));
        problemNode.addChild(new JavaASTNode("method-call", ""));
        problemNode.getChildren().get(1).addChild(new JavaASTNode("println", ""));
        problemNode.getChildren().get(1).addChild(new JavaASTNode("i", ""));

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("extra-node", result.getName());
    }

    @Test
    public void compareNullNodeTreesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode solutionNode = new JavaASTNode("block", "");
        JavaASTNode solutionChild = new JavaASTNode("null-node", "");
        solutionNode.addChild(solutionChild);

        JavaASTNode problemNode = new JavaASTNode("block", "");
        JavaASTNode problemChild = new JavaASTNode("local-var", "");
        problemNode.addChild(problemChild);

        JavaASTNode result = solutionTracer.compareTrees(solutionNode, problemNode);

        assertEquals("block", result.getName());
    }

    @Test
    public void addParenthesesTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        String result = solutionTracer.addParentheses("int i = 5;").replaceAll("\\s+","");

        assertEquals("{inti=5;}", result);
    }

    @Test
    public void normaliseTest() {
        SolutionTracer solutionTracer = new SolutionTracer();

        Problem problem = new Problem();
        problem.setSolutionStartIndex(1);
        problem.setSolutionEndLength(1);
        String code = "public void test() {\n int i = 5;\n}";
        String result = solutionTracer.normalise(problem, code).replaceAll("\\s+","");

        assertEquals("{inti=5;}", result);
    }

    @Test
    public void containsStringsTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = new JavaASTNode("\"test\"", "");

        assertTrue(solutionTracer.containsStrings(node));
    }

    @Test
    public void childContainsStringsTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = new JavaASTNode("parent", "");
        node.addChild(new JavaASTNode("\"child\"", ""));

        assertTrue(solutionTracer.containsStrings(node));
    }

    @Test
    public void doesNotContainStringsTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = new JavaASTNode("test", "");

        assertFalse(solutionTracer.containsStrings(node));
    }

    @Test
    public void successfulTraceTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = new JavaASTNode("success-node", "");

        assertTrue(solutionTracer.successfulTrace(node));
    }

    @Test
    public void unSuccessfulTraceTest() {
        SolutionTracer solutionTracer = new SolutionTracer();
        JavaASTNode node = new JavaASTNode("local-var", "");

        assertFalse(solutionTracer.successfulTrace(node));
    }

}