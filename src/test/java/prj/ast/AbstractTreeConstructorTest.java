package prj.ast;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import prj.antlr.JavaLexer;
import prj.antlr.JavaParser;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AbstractTreeConstructorTest {

    private AbstractTreeConstructor constructor;

    @BeforeEach
    public void setUp() {
        constructor = new AbstractTreeConstructor();
    }

    private JavaParser parseProgram(String code) {
        JavaLexer lexer = new JavaLexer(CharStreams.fromString(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        return new JavaParser(tokens);
    }

    @Test
    public void visitBlockWithNoChildrenTest() {
        JavaParser parser = parseProgram("{}");
        JavaParser.BlockContext blockContext = parser.block();
        JavaASTNode blockNode = constructor.visitBlock(blockContext);

        assertEquals("block", blockNode.getName());
        assertEquals(blockNode.getChildren().size(), 0);
    }

    @Test
    public void visitBlockWithChildrenTest() {
        JavaParser parser = parseProgram("{int x = 0;}");
        JavaParser.BlockContext blockContext = parser.block();
        JavaASTNode blockNode = constructor.visitBlock(blockContext);

        assertEquals("block", blockNode.getName());
        assertEquals(blockNode.getChildren().size(), 1);
    }

    @Test
    public void visitBlockStatementTest() {
        JavaParser parser = parseProgram("{}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode blockNode = constructor.visitStatement(statementContext);

        assertEquals("block", blockNode.getName());
    }

    @Test
    public void visitBlockIfStatementTest() {
        JavaParser parser = parseProgram("if (true) {i = 1;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode ifNode = constructor.visitStatement(statementContext);

        assertEquals("if", ifNode.getName());
        assertEquals(ifNode.getChildren().size(), 2);
        assertEquals(ifNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitSingleLineIfStatementTest() {
        JavaParser parser = parseProgram("if (true) i = 1;");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode ifNode = constructor.visitStatement(statementContext);

        assertEquals("if", ifNode.getName());
        assertEquals(ifNode.getChildren().size(), 2);
        // Should still add a block
        assertEquals(ifNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitIfStatementWithAnElseBranchTest() {
        JavaParser parser = parseProgram("if (true) {i = 1;} else {i = 2;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode ifNode = constructor.visitStatement(statementContext);

        assertEquals("if", ifNode.getName());
        assertEquals(ifNode.getChildren().size(), 3);
        assertEquals(ifNode.getChildren().get(1).getName(), "block");
        assertEquals(ifNode.getChildren().get(2).getName(), "else");
    }

    @Test
    public void visitIfStatementWithAnElseBranchWithoutBlockTest() {
        JavaParser parser = parseProgram("if (true) i = 1; else i = 2;");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode ifNode = constructor.visitStatement(statementContext);

        assertEquals("if", ifNode.getName());
        assertEquals(ifNode.getChildren().size(), 3);
        assertEquals(ifNode.getChildren().get(1).getName(), "block");
        assertEquals(ifNode.getChildren().get(2).getName(), "else");
    }

    @Test
    public void visitBlockForStatementTest() {
        JavaParser parser = parseProgram("for (int i = 0; i < 5; i++) {j = i + j;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode forNode = constructor.visitStatement(statementContext);

        assertEquals("for", forNode.getName());
        assertEquals(forNode.getChildren().size(), 2);
        assertEquals(forNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitSingleLineForStatementTest() {
        JavaParser parser = parseProgram("for (int i = 0; i < 5; i++) j = i + j;");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode forNode = constructor.visitStatement(statementContext);

        assertEquals("for", forNode.getName());
        assertEquals(forNode.getChildren().size(), 2);
        assertEquals(forNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visiForStatementWithNoControlTest() {
        JavaParser parser = parseProgram("for{ j = i + j;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode forNode = constructor.visitStatement(statementContext);

        assertEquals("for", forNode.getName());
        assertEquals(forNode.getChildren().size(), 0);
    }

    @Test
    public void visiEmptyForStatementTest() {
        JavaParser parser = parseProgram("for");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode forNode = constructor.visitStatement(statementContext);

        assertEquals("for", forNode.getName());
        assertEquals(forNode.getChildren().size(), 0);
    }

    @Test
    public void visiForStatementWithNoBodyTest() {
        // Even if no body, should still create a block
        JavaParser parser = parseProgram("for (int i = 0; i < 5; ++i)");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode forNode = constructor.visitStatement(statementContext);

        assertEquals("for", forNode.getName());
        assertEquals(forNode.getChildren().size(), 2);
        assertEquals(forNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitBlockDoWhileStatementTest() {
        JavaParser parser = parseProgram("do {i = i + 1;} while (true);");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode doWhileNode = constructor.visitStatement(statementContext);

        assertEquals("do-while", doWhileNode.getName());
        assertEquals(doWhileNode.getChildren().size(), 2);
        assertEquals(doWhileNode.getChildren().get(0).getName(), "block");
    }

    @Test
    public void visitSingleLineDoWhileStatementTest() {
        JavaParser parser = parseProgram("do i = i + 1; while(true);");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode doWhileNode = constructor.visitStatement(statementContext);

        assertEquals("do-while", doWhileNode.getName());
        assertEquals(doWhileNode.getChildren().size(), 2);
        assertEquals(doWhileNode.getChildren().get(0).getName(), "block");
    }

    @Test
    public void visitDoWhileStatementNoWhilePartTest() {
        JavaParser parser = parseProgram("do {i = i + 1;};");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode doWhileNode = constructor.visitStatement(statementContext);

        assertEquals("do-while", doWhileNode.getName());
        assertEquals(doWhileNode.getChildren().size(), 1);
    }

    @Test
    public void visitBlockWhileStatementTest() {
        JavaParser parser = parseProgram("while (true) {i = i + 1;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode whileNode = constructor.visitStatement(statementContext);

        assertEquals("while", whileNode.getName());
        assertEquals(whileNode.getChildren().size(), 2);
        assertEquals(whileNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitSingleLineWhileStatementTest() {
        JavaParser parser = parseProgram("while(true) i = i + 1;");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode whileNode = constructor.visitStatement(statementContext);

        assertEquals("while", whileNode.getName());
        assertEquals(whileNode.getChildren().size(), 2);
        assertEquals(whileNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitEmptyWhileStatementTest() {
        JavaParser parser = parseProgram("while");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode whileNode = constructor.visitStatement(statementContext);

        assertEquals("while", whileNode.getName());
        assertEquals(whileNode.getChildren().size(), 2);
        assertEquals(whileNode.getChildren().get(1).getName(), "block");
    }

    @Test
    public void visitSwitchStatementTest() {
        JavaParser parser = parseProgram("switch (i) {case 1: i = 2;break; case 2: i = 3;break;}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode switchNode = constructor.visitStatement(statementContext);

        assertEquals("switch", switchNode.getName());
        assertEquals(switchNode.getChildren().size(), 2);
        assertEquals(switchNode.getChildren().get(1).getName(), "switch-block");
    }

    @Test
    public void visitEmptySwitchStatementTest() {
        JavaParser parser = parseProgram("switch");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode switchNode = constructor.visitStatement(statementContext);

        assertEquals("switch", switchNode.getName());
        assertEquals(switchNode.getChildren().size(), 0);
    }

    @Test
    public void visitSwitchStatementWithOnlyLabelTest() {
        JavaParser parser = parseProgram("switch (i) {case 1:}");
        JavaParser.StatementContext statementContext = parser.statement();
        JavaASTNode switchNode = constructor.visitStatement(statementContext);

        assertEquals("switch", switchNode.getName());
        assertEquals(switchNode.getChildren().size(), 1);
    }

    @Test
    public void visitParExpressionTest() {
        JavaParser parser = parseProgram("(true)");
        JavaParser.ParExpressionContext parExpressionContext = parser.parExpression();
        JavaASTNode parExpression = constructor.visitParExpression(parExpressionContext);

        assertEquals("true", parExpression.getName());
        assertEquals(parExpression.getChildren().size(), 0);
    }

    @Test
    public void visitEmptyParExpressionTest() {
        JavaParser parser = parseProgram("");
        JavaParser.ParExpressionContext parExpressionContext = parser.parExpression();
        JavaASTNode parExpression = constructor.visitParExpression(parExpressionContext);

        assertEquals("", parExpression.getName());
        assertEquals(parExpression.getChildren().size(), 0);
    }

    @Test
    public void visitPrimaryExpressionTest() {
        JavaParser parser = parseProgram("true");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals("true", expression.getName());
        assertEquals(expression.getChildren().size(), 0);
    }

    @Test
    public void visitBooleanIdentifierSeparatorExpressionTest() {
        JavaParser parser = parseProgram("human.name");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals(".", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "human");
        assertEquals(expression.getChildren().get(1).getName(), "name");
    }

    @Test
    public void visitBooleanMethodCallSeparatorExpressionTest() {
        JavaParser parser = parseProgram("human.speak()");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals(".", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "human");
        assertEquals(expression.getChildren().get(1).getName(), "method-call");
    }

    @Test
    public void visitEmptyBooleanSeparatorExpressionTest() {
        JavaParser parser = parseProgram(".");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals(".", expression.getName());
        assertEquals(expression.getChildren().size(), 0);
    }

    @Test
    public void visitMethodCallExpressionTest() {
        JavaParser parser = parseProgram("print()");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals("method-call", expression.getName());
    }

    @Test
    public void visitPostfixExpressionTest() {
        JavaParser parser = parseProgram("i++");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals("post", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "++");
        assertEquals(expression.getChildren().get(1).getName(), "i");
    }

    @Test
    public void visitPrefixExpressionTest() {
        JavaParser parser = parseProgram("--i");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals("pre", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "--");
        assertEquals(expression.getChildren().get(1).getName(), "i");
    }

    @Test
    public void visitPrefixExpressionWithOnlyOperatorTest() {
        JavaParser parser = parseProgram("++");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals("pre", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "++");
        assertEquals(expression.getChildren().get(1).getName(), "");
    }

    @Test
    public void visitNormalBooleanExpressionTest() {
        JavaParser parser = parseProgram("age >= 20");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        assertEquals(">=", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "age");
        assertEquals(expression.getChildren().get(1).getName(), "20");
    }

    @Test
    public void visitReversedBooleanExpressionTest() {
        JavaParser parser = parseProgram("age <= 20");
        JavaParser.ExpressionContext expressionContext = parser.expression();
        JavaASTNode expression = constructor.visitExpression(expressionContext);

        // Operator reversed
        assertEquals(">=", expression.getName());
        assertEquals(expression.getChildren().size(), 2);
        assertEquals(expression.getChildren().get(0).getName(), "20");
        assertEquals(expression.getChildren().get(1).getName(), "age");
    }

    @Test
    public void visitIdentifierPrimaryTest() {
        JavaParser parser = parseProgram("age");
        JavaParser.PrimaryContext primaryContext = parser.primary();
        JavaASTNode primary = constructor.visitPrimary(primaryContext);

        assertEquals("age", primary.getName());
        assertEquals(primary.getChildren().size(), 0);
    }

    @Test
    public void visitLiteralPrimaryTest() {
        JavaParser parser = parseProgram("2");
        JavaParser.PrimaryContext primaryContext = parser.primary();
        JavaASTNode primary = constructor.visitPrimary(primaryContext);

        assertEquals("2", primary.getName());
        assertEquals(primary.getChildren().size(), 0);
    }

    @Test
    public void visitThisPrimaryTest() {
        JavaParser parser = parseProgram("this");
        JavaParser.PrimaryContext primaryContext = parser.primary();
        JavaASTNode primary = constructor.visitPrimary(primaryContext);

        assertEquals("this", primary.getName());
        assertEquals(primary.getChildren().size(), 0);
    }

    @Test
    public void visitSuperPrimaryTest() {
        JavaParser parser = parseProgram("super");
        JavaParser.PrimaryContext primaryContext = parser.primary();
        JavaASTNode primary = constructor.visitPrimary(primaryContext);

        assertEquals("super", primary.getName());
        assertEquals(primary.getChildren().size(), 0);
    }

    @Test
    public void visitParenthesisExpressionPrimaryTest() {
        JavaParser parser = parseProgram("(i++)");
        JavaParser.PrimaryContext primaryContext = parser.primary();
        JavaASTNode primary = constructor.visitPrimary(primaryContext);

        assertEquals("post", primary.getName());
        assertEquals(primary.getChildren().size(), 2);
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"test\"", "'a'", "true", "null", "1", "25", "0.500", "0b0010"})
    public void visitLiteralsTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.LiteralContext literalContext = parser.literal();
        JavaASTNode literal = constructor.visitLiteral(literalContext);

        assertEquals(parameter, literal.getName());
        assertEquals(parameter, literal.getText());
        assertEquals(literal.getChildren().size(), 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "0x100", "0b1100"})
    public void visitIntegerLiteralsTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.IntegerLiteralContext literalContext = parser.integerLiteral();
        JavaASTNode literal = constructor.visitIntegerLiteral(literalContext);

        assertEquals(parameter, literal.getName());
        assertEquals(parameter, literal.getText());
        assertEquals(literal.getChildren().size(), 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.1", "100.4", "0x1.4p3"})
    public void visitFloatLiteralsTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.FloatLiteralContext literalContext = parser.floatLiteral();
        JavaASTNode literal = constructor.visitFloatLiteral(literalContext);

        assertEquals(parameter, literal.getName());
        assertEquals(parameter, literal.getText());
        assertEquals(literal.getChildren().size(), 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"int i = 10;", "float i = 10.5;", "double i = 5.5;", "boolean i = true;", "char i = 'i';", "long i = 100000000;"})
    public void visitPrimitiveTypeLocalVariableDeclarationTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.LocalVariableDeclarationContext localVariableDeclarationContext = parser.localVariableDeclaration();
        JavaASTNode localVariable = constructor.visitLocalVariableDeclaration(localVariableDeclarationContext);

        assertEquals("local-var", localVariable.getName());
        assertEquals(localVariable.getChildren().size(), 2);
        assertEquals(localVariable.getChildren().get(0).getName(), "primitive-type");
        assertEquals(localVariable.getChildren().get(1).getName(), "=");
    }

    @ParameterizedTest
    @ValueSource(strings = {"String i = \"Hello\";", "Object obj = i;"})
    public void visitClassTypeLocalVariableDeclarationTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.LocalVariableDeclarationContext localVariableDeclarationContext = parser.localVariableDeclaration();
        JavaASTNode localVariable = constructor.visitLocalVariableDeclaration(localVariableDeclarationContext);

        assertEquals("local-var", localVariable.getName());
        assertEquals(localVariable.getChildren().size(), 2);
        assertEquals(localVariable.getChildren().get(0).getName(), "class-type");
        assertEquals(localVariable.getChildren().get(1).getName(), "=");
    }

    @ParameterizedTest
    @ValueSource(strings = {"int", "float", "double", "boolean", "char", "long"})
    public void visitPrimitiveTypeTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.PrimitiveTypeContext primitiveTypeContext = parser.primitiveType();
        JavaASTNode primitiveType = constructor.visitPrimitiveType(primitiveTypeContext);

        assertEquals("primitive-type", primitiveType.getName());
        assertEquals(primitiveType.getChildren().size(), 1);
        assertEquals(primitiveType.getChildren().get(0).getName(), parameter);
    }

    @ParameterizedTest
    @ValueSource(strings = {"String", "Object", "Animal", "Human", "Parser.Context"})
    public void visitClassTypeTest(String parameter) {
        JavaParser parser = parseProgram(parameter);
        JavaParser.ClassOrInterfaceTypeContext classOrInterfaceTypeContext = parser.classOrInterfaceType();
        JavaASTNode classType = constructor.visitClassOrInterfaceType(classOrInterfaceTypeContext);

        assertEquals("class-type", classType.getName());
        assertEquals(classType.getChildren().size(), 1);
        assertEquals(classType.getChildren().get(0).getName(), parameter);
    }

    @Test
    public void visitVariableDeclaratorTest() {
        JavaParser parser = parseProgram("a = 5;");
        JavaParser.VariableDeclaratorContext variableDeclaratorContext = parser.variableDeclarator();
        JavaASTNode variableDeclarator = constructor.visitVariableDeclarator(variableDeclaratorContext);

        assertEquals("=", variableDeclarator.getName());
        assertEquals(variableDeclarator.getChildren().size(), 2);
        assertEquals(variableDeclarator.getChildren().get(0).getName(), "a");
        assertEquals(variableDeclarator.getChildren().get(1).getName(), "5");
    }

    @Test
    public void visitVariableDeclaratorWithoutAssignmentTest() {
        JavaParser parser = parseProgram("a;");
        JavaParser.VariableDeclaratorContext variableDeclaratorContext = parser.variableDeclarator();
        JavaASTNode variableDeclarator = constructor.visitVariableDeclarator(variableDeclaratorContext);

        assertEquals("=", variableDeclarator.getName());
        assertEquals(variableDeclarator.getChildren().size(), 1);
        assertEquals(variableDeclarator.getChildren().get(0).getName(), "a");
    }

    @Test
    public void visitVariableDeclaratorIdTest() {
        JavaParser parser = parseProgram("i");
        JavaParser.VariableDeclaratorIdContext variableDeclaratorIdContext = parser.variableDeclaratorId();
        JavaASTNode variableDeclaratorId = constructor.visitVariableDeclaratorId(variableDeclaratorIdContext);

        assertEquals("i", variableDeclaratorId.getName());
        assertEquals(variableDeclaratorId.getChildren().size(), 0);
    }

    @Test
    public void visitArrayVariableDeclaratorIdTest() {
        JavaParser parser = parseProgram("i[]");
        JavaParser.VariableDeclaratorIdContext variableDeclaratorIdContext = parser.variableDeclaratorId();
        JavaASTNode variableDeclaratorId = constructor.visitVariableDeclaratorId(variableDeclaratorIdContext);

        assertEquals("i[]", variableDeclaratorId.getName());
        assertEquals(variableDeclaratorId.getChildren().size(), 0);
    }

    @Test
    public void visitMethodCallTest() {
        JavaParser parser = parseProgram("print()");
        JavaParser.MethodCallContext methodCallContext = parser.methodCall();
        JavaASTNode methodCall = constructor.visitMethodCall(methodCallContext);

        assertEquals("method-call", methodCall.getName());
        assertEquals(methodCall.getChildren().size(), 1);
        assertEquals(methodCall.getChildren().get(0).getName(), "print");
    }

    @Test
    public void visitMethodCallWithParameterTest() {
        JavaParser parser = parseProgram("print(i)");
        JavaParser.MethodCallContext methodCallContext = parser.methodCall();
        JavaASTNode methodCall = constructor.visitMethodCall(methodCallContext);

        assertEquals("method-call", methodCall.getName());
        assertEquals(methodCall.getChildren().size(), 2);
        assertEquals(methodCall.getChildren().get(0).getName(), "print");
        assertEquals(methodCall.getChildren().get(1).getName(), "i");
    }

    @Test
    public void visitExpressionListTest() {
        JavaParser parser = parseProgram("i, j, k");
        JavaParser.ExpressionListContext expressionListContext = parser.expressionList();
        JavaASTNode expressionList = constructor.visitExpressionList(expressionListContext);

        assertEquals("i", expressionList.getName());
        assertEquals(expressionList.getChildren().size(), 0);
    }

    @Test
    public void visitExpressionListWithASingleElementTest() {
        JavaParser parser = parseProgram("j");
        JavaParser.ExpressionListContext expressionListContext = parser.expressionList();
        JavaASTNode expressionList = constructor.visitExpressionList(expressionListContext);

        assertEquals("j", expressionList.getName());
        assertEquals(expressionList.getChildren().size(), 0);
    }

    @Test
    public void visitForControlTest() {
        JavaParser parser = parseProgram("int i = 0; i < 5; i++");
        JavaParser.ForControlContext forControlContext = parser.forControl();
        JavaASTNode forControl = constructor.visitForControl(forControlContext);

        assertEquals("for-control", forControl.getName());
        assertEquals(forControl.getChildren().size(), 3);
        assertEquals(forControl.getChildren().get(0).getName(), "local-var");
        // Will get reversed
        assertEquals(forControl.getChildren().get(1).getName(), ">");
        assertEquals(forControl.getChildren().get(2).getName(), "post");
    }

    @Test
    public void visitEmptyForControlTest() {
        JavaParser parser = parseProgram("; ;");
        JavaParser.ForControlContext forControlContext = parser.forControl();
        JavaASTNode forControl = constructor.visitForControl(forControlContext);

        assertEquals("for-control", forControl.getName());
        assertEquals(forControl.getChildren().size(), 0);
    }

    @Test
    public void visitIncompleteForControlTest() {
        JavaParser parser = parseProgram("; i < 5;");
        JavaParser.ForControlContext forControlContext = parser.forControl();
        JavaASTNode forControl = constructor.visitForControl(forControlContext);

        assertEquals("for-control", forControl.getName());
        assertEquals(forControl.getChildren().size(), 1);
        // Will get reversed
        assertEquals(forControl.getChildren().get(0).getName(), ">");
    }

    @Test
    public void visitSwitchBlockStatementGroupTest() {
        JavaParser parser = parseProgram("case 1: i = 5;");
        JavaParser.SwitchBlockStatementGroupContext switchBlockStatementGroupContext = parser.switchBlockStatementGroup();
        JavaASTNode switchBlock = constructor.visitSwitchBlockStatementGroup(switchBlockStatementGroupContext);

        assertEquals("switch-block", switchBlock.getName());
        assertEquals(switchBlock.getChildren().size(), 2);
        assertEquals(switchBlock.getChildren().get(0).getName(), "1");
        assertEquals(switchBlock.getChildren().get(1).getName(), "=");
    }

    @Test
    public void visitSwitchLabelTest() {
        JavaParser parser = parseProgram("case 1:");
        JavaParser.SwitchLabelContext switchLabelContext = parser.switchLabel();
        JavaASTNode switchLabel = constructor.visitSwitchLabel(switchLabelContext);

        assertEquals("1", switchLabel.getName());
        assertEquals(switchLabel.getChildren().size(), 0);
    }

    @Test
    public void visitDefaultSwitchLabelTest() {
        JavaParser parser = parseProgram("default:");
        JavaParser.SwitchLabelContext switchLabelContext = parser.switchLabel();
        JavaASTNode switchLabel = constructor.visitSwitchLabel(switchLabelContext);

        assertEquals("default", switchLabel.getName());
        assertEquals(switchLabel.getChildren().size(), 0);
    }
}