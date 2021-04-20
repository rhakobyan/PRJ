package prj.studentmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import prj.ast.JavaASTNode;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FeedbackModuleTest {

    @Autowired
    private FeedbackModule feedbackModule;


    @Test
    public void ifStatementFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode ifNode = new JavaASTNode("if", "");
        block.addChild(ifNode);

        String feedback =  feedbackModule.generateFeedback(ifNode);

        assertEquals("First, you need to write an <code>if</code> statement", feedback);
    }

    @Test
    public void elseBranchFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode elseNode = new JavaASTNode("else", "");
        block.addChild(elseNode);

        String feedback =  feedbackModule.generateFeedback(elseNode);

        assertEquals("First, you need to write the <code>else</code> branch", feedback);
    }

    @Test
    public void forLoopFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode forNode = new JavaASTNode("for", "");
        block.addChild(forNode);

        String feedback =  feedbackModule.generateFeedback(forNode);

        assertEquals("First, you need to write a <code>for</code> loop", feedback);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = 0; i < 5; ++i", "i = 5; i > 1; --i", ""})
    public void forControlFeedbackTest(String controlBody) {
        JavaASTNode forNode = new JavaASTNode("for", "");
        JavaASTNode forControlNode = new JavaASTNode("for-control", controlBody);
        forNode.addChild(forControlNode);

        String feedback =  feedbackModule.generateFeedback(forControlNode);

        assertEquals("Next, define the for loop by constructing the following statements: <code>" + controlBody + "</code>", feedback);
    }

    @Test
    public void whileLoopFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode whileNode = new JavaASTNode("while", "");
        block.addChild(whileNode);

        String feedback =  feedbackModule.generateFeedback(whileNode);

        assertEquals("First, you need to write a <code>while</code> loop", feedback);
    }

    @Test
    public void doWhileLoopFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode doWhileNode = new JavaASTNode("do-while", "");
        block.addChild(doWhileNode);

        String feedback =  feedbackModule.generateFeedback(doWhileNode);

        assertEquals("First, you need to write a <code>do-while</code> loop", feedback);
    }

    @Test
    public void switchStatementFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode switchNode = new JavaASTNode("switch", "");
        block.addChild(switchNode);

        String feedback =  feedbackModule.generateFeedback(switchNode);

        assertEquals("First, you need to write a <code>switch</code> statement", feedback);
    }

    @ParameterizedTest
    @CsvSource({"==, equal to", "!=, not equal to", ">, greater than", "<, smaller than", ">=, greater than or equal to", "<=, smaller than or equal to"})
    public void comparisonOperatorFeedbackTest(String operator, String comparisonText) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode comparisonOperatorNode = new JavaASTNode(operator, "");
        JavaASTNode i = new JavaASTNode("i", "i");
        JavaASTNode j = new JavaASTNode("j", "j");
        comparisonOperatorNode.addChild(i);
        comparisonOperatorNode.addChild(j);
        block.addChild(comparisonOperatorNode);

        String feedback =  feedbackModule.generateFeedback(comparisonOperatorNode);

        assertEquals("First, you need to use a <u>comparison</u> operator to check if <code>i</code> is " + comparisonText + " <code>j</code>", feedback);
    }

    @ParameterizedTest
    @CsvSource({"&&, true && false", "!, test", "||, i || j"})
    public void logicalOperatorFeedbackTest(String operator, String text) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode logicalOperatorNode = new JavaASTNode(operator, text);
        block.addChild(logicalOperatorNode);

        String feedback = feedbackModule.generateFeedback(logicalOperatorNode);

        assertEquals("First, you need to use the <code>" + operator + "</code> logical operator to check for the following statement: <code>" + text + "</code>", feedback);
    }

    @ParameterizedTest
    @CsvSource({"+, 5 + 10", "-, i - j", "*, 4 * test", "/, i / j", "%, i % 2"})
    public void arithmeticOperatorFeedbackTest(String operator, String text) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode arithmeticOperatorNode = new JavaASTNode(operator, text);
        block.addChild(arithmeticOperatorNode);

        String feedback = feedbackModule.generateFeedback(arithmeticOperatorNode);

        assertEquals("First, you need to use the <code>" + operator + "</code> arithmetic operator to write the following statement: <code>" + text + "</code>", feedback);
    }

    @ParameterizedTest
    @ValueSource(strings = {"dog.bark()", "human.arm.move()"})
    public void separatorOperatorFeedbackTest(String text) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode  separatorOperatorNode = new JavaASTNode(".", text);
        block.addChild(separatorOperatorNode);

        String feedback = feedbackModule.generateFeedback(separatorOperatorNode);

        assertEquals("First, you need to write the following statement: <code>" + text + "</code>", feedback);
    }

    @Test
    public void notSeniorSeparatorOperatorFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode  separatorOperatorNode = new JavaASTNode(".", "human.leg.move()");
        separatorOperatorNode.addChild(new JavaASTNode("human", ""));
        JavaASTNode notSeniorSeparator = new JavaASTNode(".", "leg.move()");
        separatorOperatorNode.addChild(notSeniorSeparator);
        notSeniorSeparator.addChild(new JavaASTNode("leg", ""));
        notSeniorSeparator.addChild(new JavaASTNode("method-call", "move()"));
        block.addChild(separatorOperatorNode);

        String feedback = feedbackModule.generateFeedback(notSeniorSeparator);

        assertEquals("First, you need to write the following statement: <code>human.leg.move()</code>", feedback);
    }

    @Test
    public void printSeparatorOperatorFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode printSeparatorOperatorNode = new JavaASTNode(".", "System.out.println(test)");
        printSeparatorOperatorNode.addChild( new JavaASTNode(".", "System.out"));
        JavaASTNode methodCall = new JavaASTNode("method-call", "println(test)");
        printSeparatorOperatorNode.addChild(methodCall);
        methodCall.addChild(new JavaASTNode("println", "println"));
        methodCall.addChild(new JavaASTNode("test", "test"));
        block.addChild(printSeparatorOperatorNode);

        String feedback = feedbackModule.generateFeedback(printSeparatorOperatorNode);

        assertEquals("First, you need to print out <code>test</code>", feedback);
    }

    @ParameterizedTest
    @CsvSource({"=, i = 5", "-=, j -= i", "*=, test *= 2", "/=, i /= j", "+=, i += 1"})
    public void assignmentFeedbackTest(String operator, String text) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode assignmentNode = new JavaASTNode(operator, text);
        block.addChild(assignmentNode);

        String feedback = feedbackModule.generateFeedback(assignmentNode);

        assertEquals("First, you need to use the assignment operator <code>" + operator + "</code> to define the following statement: <code>" + text + "</code>", feedback);
    }

    @Test
    public void primitiveTypeLocalVariableFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode localVarNode = new JavaASTNode("local-var", "int i = 10");
        localVarNode.addChild(new JavaASTNode("primitive-type", ""));
        localVarNode.getChildren().get(0).addChild(new JavaASTNode("int", "int"));
        localVarNode.addChild(new JavaASTNode("=", "i = 10"));
        localVarNode.getChildren().get(1).addChild(new JavaASTNode("i", "i"));
        localVarNode.getChildren().get(1).addChild(new JavaASTNode("10", "10"));

        block.addChild(localVarNode);

        String feedback = feedbackModule.generateFeedback(localVarNode);

        assertEquals("First, you need to declare a local variable of primitive type <code>int</code>", feedback);
    }

    @Test
    public void classTypeLocalVariableFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode localVarNode = new JavaASTNode("local-var", "Integer i = 10");
        localVarNode.addChild(new JavaASTNode("class-type", ""));
        localVarNode.getChildren().get(0).addChild(new JavaASTNode("Integer", "Integer"));
        localVarNode.addChild(new JavaASTNode("=", "i = 10"));
        localVarNode.getChildren().get(1).addChild(new JavaASTNode("i", "i"));
        localVarNode.getChildren().get(1).addChild(new JavaASTNode("10", "10"));

        block.addChild(localVarNode);

        String feedback = feedbackModule.generateFeedback(localVarNode);

        assertEquals("First, you need to declare a local variable of class type <code>Integer</code>", feedback);
    }

    @Test
    public void noTypeLocalVariableFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode localVarNode = new JavaASTNode("local-var", "");

        block.addChild(localVarNode);

        String feedback = feedbackModule.generateFeedback(localVarNode);

        assertEquals("First, you need to declare a local variable", feedback);
    }

    @Test
    public void prePostFixAloneFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode pre = new JavaASTNode("pre", "");
        JavaASTNode post = new JavaASTNode("post", "");

        block.addChild(pre);
        block.addChild(post);

        String feedback = feedbackModule.generateFeedback(pre);
        assertEquals("First, you need to use a pre-fixed operator", feedback);

        feedback = feedbackModule.generateFeedback(post);
        assertEquals("First, you need to use a post-fixed operator", feedback);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pre", "post"})
    public void prePostFixFeedbackTest(String name) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode(name, "");
        node.addChild(new JavaASTNode("++", "++"));
        node.addChild(new JavaASTNode("i", "i"));

        block.addChild(node);

        String feedback = feedbackModule.generateFeedback(node);
        assertEquals("First, you need to use the " + name + "-fixed operator <code>++</code> on the variable <code>i</code>", feedback);
    }

    @Test
    public void successFeedbackTest() {
        JavaASTNode success = new JavaASTNode("success-node", "");;

        String feedback = feedbackModule.generateFeedback(success);

        assertEquals("Next, try running your code!", feedback);
    }

    @Test
    public void methodCallAloneFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode methodCallNode = new JavaASTNode("method-call", "");

        block.addChild(methodCallNode);

        String feedback = feedbackModule.generateFeedback(methodCallNode);

        assertEquals("First, you need to call a method", feedback);
    }

    @Test
    public void methodCallTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode methodCallNode = new JavaASTNode("method-call", "");
        methodCallNode.addChild(new JavaASTNode("print", "print"));

        block.addChild(methodCallNode);

        String feedback = feedbackModule.generateFeedback(methodCallNode);

        assertEquals("First, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void extraFeedbackTest() {
        JavaASTNode extraNode = new JavaASTNode("extra-node", "");;

        String feedback = feedbackModule.generateFeedback(extraNode);

        assertEquals("Next, your code contains statements that are not necessary for the completion of this exercise", feedback);
    }

    @Test
    public void switchBlockFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode switchBlockNode = new JavaASTNode("switch-block", "");
        block.addChild(switchBlockNode);

        String feedback =  feedbackModule.generateFeedback(switchBlockNode);

        assertEquals("First, define the condition in the <code>switch</code> statement", feedback);
    }

    @Test
    public void methodCallNameTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode methodCallNode = new JavaASTNode("method-call", "");
        JavaASTNode methodCallNameNode = new JavaASTNode("print", "print");
        methodCallNode.addChild(methodCallNameNode);

        block.addChild(methodCallNode);

        String feedback = feedbackModule.generateFeedback(methodCallNameNode);

        assertEquals("Next, you should call the method <code>print</code>", feedback);
    }

    @Test
    public void otherNodesFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("i", "");

        block.addChild(node);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("First, specify <code>i</code>", feedback);
    }

    @ParameterizedTest
    @ValueSource(strings = {"if", "while"})
    public void insideIfWhileConditionFeedbackTest(String statement) {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode statementNode = new JavaASTNode(statement, "");
        JavaASTNode node = new JavaASTNode("==", "==");
        node.addChild(new JavaASTNode("i", "i"));
        node.addChild(new JavaASTNode("true", "true"));
        statementNode.addChild(node);

        block.addChild(statementNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the <code>" + statement + "</code> <u>condition</u>, you need to use a <u>comparison</u> operator to check if <code>i</code> is equal to <code>true</code>", feedback);
    }

    @Test
    public void insideIfStatementFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode statementNode = new JavaASTNode("if", "");
        JavaASTNode comparison = new JavaASTNode("==", "==");
        comparison.addChild(new JavaASTNode("i", "i"));
        comparison.addChild(new JavaASTNode("true", "true"));
        statementNode.addChild(comparison);
        JavaASTNode ifBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        ifBlock.addChild(node);
        statementNode.addChild(ifBlock);

        block.addChild(statementNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the <code>if</code> <u>statement</u>, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void insideWhileBodyFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode loopNode = new JavaASTNode("while", "");
        JavaASTNode comparison = new JavaASTNode("==", "==");
        comparison.addChild(new JavaASTNode("i", "i"));
        comparison.addChild(new JavaASTNode("true", "true"));
        loopNode.addChild(comparison);
        JavaASTNode loopBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        loopBlock.addChild(node);
        loopNode.addChild(loopBlock);

        block.addChild(loopNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the body of the <code>while</code> loop, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void insideForBodyFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode loopNode = new JavaASTNode("for", "");
        loopNode.addChild(new JavaASTNode("for-control", " i = 0; i < 5; i++;"));
        JavaASTNode loopBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        loopBlock.addChild(node);
        loopNode.addChild(loopBlock);

        block.addChild(loopNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the body of the <code>for</code> loop, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void insideDoWhileConditionFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode loopNode = new JavaASTNode("do-while", "");
        JavaASTNode loopBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        loopBlock.addChild(node);
        loopNode.addChild(loopBlock);
        JavaASTNode comparison = new JavaASTNode("==", "==");
        comparison.addChild(new JavaASTNode("i", "i"));
        comparison.addChild(new JavaASTNode("true", "true"));
        loopNode.addChild(comparison);

        block.addChild(loopNode);

        String feedback = feedbackModule.generateFeedback(comparison);

        assertEquals("Next, inside the <code>do-while</code> <u>condition</u>, you need to use a <u>comparison</u> operator to check if <code>i</code> is equal to <code>true</code>", feedback);
    }

    @Test
    public void insideDoWhileBodyFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode loopNode = new JavaASTNode("do-while", "");
        JavaASTNode loopBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        loopBlock.addChild(node);
        loopNode.addChild(loopBlock);

        block.addChild(loopNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the body of the <code>do-while</code> loop, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void insideElseBranchFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode elseNode = new JavaASTNode("else", "");
        JavaASTNode elseBlock = new JavaASTNode("block", "");
        JavaASTNode node = new JavaASTNode("method-call", "");
        node.addChild(new JavaASTNode("print", "print"));
        node.addChild(new JavaASTNode("test", "test"));
        elseBlock.addChild(node);
        elseNode.addChild(elseBlock);

        block.addChild(elseNode);

        String feedback = feedbackModule.generateFeedback(node);

        assertEquals("Next, inside the <code>else</code> branch, you need to call the method <code>print</code>", feedback);
    }

    @Test
    public void getAssignmentTypeFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode localVar = new JavaASTNode("local-var", "");
        JavaASTNode typeType = new JavaASTNode("primitive-type", "");
        JavaASTNode type = new JavaASTNode("String", "");
        typeType.addChild(type);
        localVar.addChild(typeType);

        block.addChild(localVar);

        String feedback = feedbackModule.generateFeedback(type);
        assertEquals("Your variable should be of type <code>String</code>", feedback);
        feedback = feedbackModule.generateFeedback(typeType);
        assertEquals("Your variable should be of type <code>String</code>", feedback);
    }

    @Test
    public void getAssignmentFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode assignment = new JavaASTNode("=", "");
        JavaASTNode variableName = new JavaASTNode("i", "");
        JavaASTNode value = new JavaASTNode("10", "10");
        assignment.addChild(variableName);
        assignment.addChild(value);

        block.addChild(assignment);

        String feedback = feedbackModule.generateFeedback(value);
        assertEquals("You should equate your variable <code>i</code> to <code>10</code>", feedback);
        feedback = feedbackModule.generateFeedback(variableName);
        assertEquals("You should name your variable <code>i</code>", feedback);
    }

    @Test
    public void methodCallArgumentFeedbackTest() {
        JavaASTNode block = new JavaASTNode("block", "");
        JavaASTNode methodCall = new JavaASTNode("method-call", "");
        JavaASTNode methodName = new JavaASTNode("print", "");
        JavaASTNode argument = new JavaASTNode("test", "test");
        methodCall.addChild(methodName);
        methodCall.addChild(argument);

        block.addChild(methodCall);

        String feedback = feedbackModule.generateFeedback(argument);
        assertEquals("You should call the method with the argument <code>test</code>", feedback);
    }
}