package prj.ast;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import prj.antlr.JavaParser;
import prj.antlr.JavaParserBaseVisitor;

/*
 * The AbstractTreeConstructor class is used for traversing a Parse Tree and constructing
 * an abstract syntax tree from it.
 * The visitor pattern, extended from JavaParserBaseVisitor, is used for the traversal.
 * The Abstract Syntax Tree is represented using JavaASTNode object.
 */
public class AbstractTreeConstructor extends JavaParserBaseVisitor<JavaASTNode> {

    /*
     * This methods visits a block and the expressions inside of it.
     * Blocks in Java are represented using the {, } symbols.
     * The visited block is added to the Abstract Syntax Tree as a node.
     */
    @Override
    public JavaASTNode visitBlock(JavaParser.BlockContext ctx) {
        // Create a new block AST node
        JavaASTNode block = new JavaASTNode("block",ctx.getText());

        // Visit all the expressions inside the block and as them as children nodes to the block
        for (int i = 0; i < ctx.blockStatement().size(); ++i)
            block.addChild(visit(ctx.blockStatement(i)));

        return block;
    }

    /*
     * This method visits statements and adds them to the Abstract Syntax Tree.
     * Depending on the type of the node, this method will create an appropriate JavaAST node
     * and add the different parts that make up the statement as children nodes.
     */
    @Override
    public JavaASTNode visitStatement(JavaParser.StatementContext ctx) {
        // The below if-else block checks the type of statement that has been passed and constructs the
        // nodes as appropriate for each kind of statement
        if (ctx.blockLabel != null) {
            return visit(ctx.blockLabel);
        }
        // If the statement is an if statement
        else if (ctx.IF() != null) {
            // Create a node for an if statement
            JavaASTNode ifStatement = new JavaASTNode("if",getText(ctx));
            // Visit the expression in the parenthesis of the if statement and add it as a child to the if node
            ifStatement.addChild(visit(ctx.parExpression()));

            // If the statement part is not empty, add the statement as a child to the if statement
            if (ctx.statement().size() > 0) {
                // In order to normalise all codes, to make comparisons easier
                // ensure that all the if statements are inside a block ({})
                if (ctx.statement(0).blockLabel == null)
                    ifStatement.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    ifStatement.addChild(visit(ctx.statement(0)));
            }

            // If the if statement has an else branch present
            if (ctx.ELSE() != null && ctx.statement().size() > 1) {
                // Create a node for an else statement
                JavaASTNode elseBlock = new JavaASTNode("else", getText(ctx));
                // In order to normalise all codes, to make comparisons easier
                // force all the else statements inside a block ({})
                if (ctx.statement(1).blockLabel == null)
                    elseBlock.addChild(addBlockToStatement(ctx.getText(), ctx.statement(1)));
                else
                    elseBlock.addChild(visit(ctx.statement(1)));

                ifStatement.addChild(elseBlock);
            }

            return ifStatement;
        } else if (ctx.FOR() != null) {
            // If the statement is a for loop, create a new node for the for loop
            JavaASTNode forLoop = new JavaASTNode("for", getText(ctx));
            // If the control part of the for loop is not null, add it as a child
            if (ctx.forControl() != null)
                forLoop.addChild(visit(ctx.forControl()));

            // If the statement part is not empty, add the statement as a child to the for loop
            if (ctx.statement().size() > 0) {
                // In order to normalise all codes, to make comparisons easier
                // force all the for loop statements inside a block ({})
                if (ctx.statement(0).blockLabel == null)
                    forLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                 else
                     forLoop.addChild(visit(ctx.statement(0)));
            }

            return forLoop;
        } else if (ctx.DO() != null) {
            // If the statement is a do-while loop, create a new node for the loop
            JavaASTNode doWhileLoop = new JavaASTNode("do-while",getText(ctx));
            // If the statement part is not empty, add the statement as a child to the do-while node
            if (ctx.statement().size() > 0) {
                // In order to normalise all codes, to make comparisons easier
                // force all the do-while loop statements inside a block ({})
                if (ctx.statement(0).blockLabel == null)
                    doWhileLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    doWhileLoop.addChild(visit(ctx.statement(0)));
            }

            // If the while part of the loop is present, add it as a child to the do-while node
            if (ctx.parExpression() != null)
                doWhileLoop.addChild(visit(ctx.parExpression()));

            return doWhileLoop;
        } else if (ctx.WHILE() != null) {
            // If the statement is a while loop, create a new node for the loop
            JavaASTNode whileLoop = new JavaASTNode("while", getText(ctx));
            // If the expression in parenthesis of the while loop is present, add it a child to the while node
            if (ctx.parExpression() != null)
                whileLoop.addChild(visit(ctx.parExpression()));
            // If the statement part is not empty, add the statement as a child to the do-while
            if (ctx.statement().size() > 0) {
                // In order to normalise all codes, to make comparisons easier
                // force all the while loop statements inside a block ({})
                if (ctx.statement(0).blockLabel == null)
                    whileLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    whileLoop.addChild(visit(ctx.statement(0)));
            }

            return whileLoop;
        } else if (ctx.SWITCH() != null) {
            // If the statement is a switch statement, create a new node for the statement
            JavaASTNode switchStatement = new JavaASTNode("switch", getText(ctx));

            /* Iterate through the children of the switch statement and add them as children
             to the newly created node */
            for (int i = 0; i < ctx.switchBlockStatementGroup().size(); ++i)
                switchStatement.addChild(visit(ctx.switchBlockStatementGroup(i)));

            for (int i = 0; i < ctx.switchLabel().size(); ++i)
                switchStatement.addChild(visit(ctx.switchLabel(i)));

            return switchStatement;
        } else if ( ctx.statementExpression != null)
            return visit(ctx.getChild(0));

        // If the passed expression does not match any value in the if-else block, then create a new
        // plain block that will simply hold the whole text of statement
        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    /*
     * This method visits expressions that are inside parentheses.
     * Essentially this method discards the "(" and ")" symbols and puts the expression
     * inside of them in a JavaAST node.
     */
    @Override
    public JavaASTNode visitParExpression(JavaParser.ParExpressionContext ctx) {
        if (ctx.expression() == null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        return visit(ctx.expression());
    }

    /*
     * This method visits expression types and adds them to Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitExpression(JavaParser.ExpressionContext ctx) {
        if (ctx.getChildCount() == 0) {
            return new JavaASTNode(ctx.getText(), ctx.getText());
        } else if (ctx.primary() != null) {
            return  visit(ctx.primary());
        } else if (ctx.DOT() != null) { // If the expression is a separator (.)
            /* Only construct a node if a separator is used to access either identifier or method calls
            and ignore everything else as they are not in the subset of the sought language */
            if (ctx.IDENTIFIER() != null || ctx.methodCall() != null) {
                JavaASTNode booleanOperator = new JavaASTNode(ctx.DOT().getText(), getText(ctx));
                if (ctx.expression() != null && ctx.expression().size() > 0) {
                    booleanOperator.addChild(visit(ctx.expression(0)));
                }
                if (ctx.IDENTIFIER() != null)
                    booleanOperator.addChild(new JavaASTNode(ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getText()));
                else
                    booleanOperator.addChild(visit(ctx.methodCall()));

                return booleanOperator;
            }
        } else if (ctx.methodCall() != null) { // If the expression is a method
            return visit(ctx.methodCall());
        } else if (ctx.postfix != null) { // If the expression contains a postfix operator, e.g i++
            // Create a postfix node
            JavaASTNode postFix = new JavaASTNode("post", getText(ctx));
            // Add the postfix operator as a child to the node
            postFix.addChild(new JavaASTNode(ctx.postfix.getText(), getText(ctx)));
            // Add the expressions on which the postfix operator was used as a child to the node
            if (ctx.getChild(0) != null)
                postFix.addChild(visit(ctx.getChild(0)));

            return postFix;
        } else if (ctx.prefix != null) { // If the expression contains a prefix operator, e.g ++i
            // Create a prefix node
            JavaASTNode preFix = new JavaASTNode("pre", getText(ctx));
            // Add the prefix operator as a child to the node
            preFix.addChild(new JavaASTNode(ctx.prefix.getText(),getText(ctx)));
            // Add the expressions on which the prefix operator was used as a child to the node
            if (ctx.getChild(1) != null)
                preFix.addChild(visit(ctx.getChild(1)));

            return preFix;
        } else if (ctx.bop != null) { // If the expression contains a boolean operator, e.g. ==, &&, +=
            boolean reverse = false;
            JavaASTNode booleanOperator;
            /* If the boolean operator is <= or <, we change them into >= and > respectively, and traverse
            them in reverse order.
            This is done in order to normalise all the parsed codes, and make the decisions if
            two programs are equivalent easier. */
            if (ctx.bop.getText().equals("<=")) {
                booleanOperator = new JavaASTNode(">=", getText(ctx));
                reverse = true;
            } else if (ctx.bop.getText().equals("<")) {
                booleanOperator = new JavaASTNode(">", getText(ctx));
                reverse = true;
            } else {
                booleanOperator = new JavaASTNode(ctx.bop.getText(), getText(ctx));
            }
            /* Visit the LHS and RHS of the boolean operators and add them as children */

            if (ctx.expression().size() > 0 && ctx.expression(0) != null) {
                if (reverse)
                    booleanOperator.addChild(visit(ctx.expression(1)));
                else
                    booleanOperator.addChild(visit(ctx.expression(0)));
            }

            if (ctx.expression().size() > 1 && ctx.expression(1) != null) {
                if (reverse)
                    booleanOperator.addChild(visit(ctx.expression(0)));
                else
                    booleanOperator.addChild(visit(ctx.expression(1)));
            }

            return booleanOperator;
        }

        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    /*
     * This method visits primary expressions and adds them to the Abstract Syntax Tree.
     * @param ctx The JavaParser context.
     * @return AST node of the primary.
     */
    @Override
    public JavaASTNode visitPrimary(JavaParser.PrimaryContext ctx) {
        // If terminals, create and return a new node for these terminals
        if (ctx.IDENTIFIER() != null || ctx.THIS() != null || ctx.SUPER() != null)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        // If an expression inside parentheses, i.e. "(expression)"
        // ignore the parentheses symbol and visit the expression
        if (ctx.LPAREN() != null && ctx.RPAREN() != null && ctx.expression() != null)
            return visit(ctx.expression());

        // In all other non-terminal tokens, continue visiting them
        return visitChildren(ctx);
    }

    /*
     * This method visits literals and adds them to the Abstract Syntax Tree.
     * @param ctx The JavaParser context.
     * @return AST node of the literal.
     */
    @Override
    public JavaASTNode visitLiteral(JavaParser.LiteralContext ctx) {
        // If a terminal literal, return a newly created AST node for that literal
        if(ctx.integerLiteral() == null && ctx.floatLiteral() == null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        // In case of non-terminal literals, continue visiting them
        return visitChildren(ctx);
    }

    /*
     * This method visits integer literals and adds them to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitIntegerLiteral(JavaParser.IntegerLiteralContext ctx) {
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    /*
     * This method visits float literals and adds them to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitFloatLiteral(JavaParser.FloatLiteralContext ctx) {
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    /*
     * This method visits a local variable declaration and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        JavaASTNode localVariable = new JavaASTNode("local-var", getText(ctx));

        localVariable.addChild(visit(ctx.typeType()));
        localVariable.addChild(visit(ctx.variableDeclarators().variableDeclarator(0)));

        return localVariable;
    }

    /*
     * This method visits a primitive type and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
        JavaASTNode type = new JavaASTNode("primitive-type", getText(ctx));
        type.addChild(new JavaASTNode(ctx.getText(), getText(ctx)));
        return type;
    }

    /*
     * This method visits a class type and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
        JavaASTNode type = new JavaASTNode("class-type", getText(ctx));
        type.addChild(new JavaASTNode(ctx.getText(), getText(ctx)));
        return type;
    }

    /*
     * This method visits a variable declarator.
     * A variable be declared with or without an assignment operator.
     * Note, even though a variable may be declared without an assignment operator, we still
     * identify the variable declarator node with an '=' sign in the AST.
     */
    @Override
    public JavaASTNode visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
        JavaASTNode declarator = new JavaASTNode("=", getText(ctx));
        declarator.addChild(visit(ctx.variableDeclaratorId()));

        if (ctx.ASSIGN() != null && ctx.variableInitializer() != null) {
            declarator.addChild(visit(ctx.variableInitializer()));
        }

        return declarator;
    }

    /*
     * This method visits a variable declarator identifier.
     * Essentially, a variable declarator identifier is equivalent to an identifier
     * with the possibility of having the array brackets '[]' next to it.
     */
    @Override
    public JavaASTNode visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    /*
     * This method visits a method call and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitMethodCall(JavaParser.MethodCallContext ctx) {
        JavaASTNode methodCall = new JavaASTNode("method-call",getText(ctx));
        JavaASTNode methodCallName = new JavaASTNode(ctx.getStart().getText(), getText(ctx));
        methodCall.addChild(methodCallName);
        // If the method is called with a parameter, add that parameter as a child
        if (ctx.expressionList() != null) {
            for (int i = 0; i < ctx.expressionList().expression().size(); ++i) {
                methodCall.addChild(visit(ctx.expressionList().expression(i)));
            }
        }
        return methodCall;
    }

    /*
     * This method visits a list of expressions separated by a comma and adds this list to the Abstract Syntax Tree.
     * This method only considers the first expression in the list, and discards the rest.
     */
    @Override
    public JavaASTNode visitExpressionList(JavaParser.ExpressionListContext ctx) {
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        return visit(ctx.getChild(0));
    }

    /*
     * This method visits a statement inside a block adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitBlockStatement(JavaParser.BlockStatementContext ctx) {
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        return visit(ctx.getChild(0));
    }

    /*
     * This method visits the control (parentheses) part of a for loop adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitForControl(JavaParser.ForControlContext ctx) {
        // Do not break down an enhanced for control statement
        if (ctx.enhancedForControl() != null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        JavaASTNode forControl = new JavaASTNode("for-control", getText(ctx));
        /* Check if all the individual parts of the control statement are
        present and them as children as necessary */

        if (ctx.forInit() != null)
            forControl.addChild(visit(ctx.forInit()));

        if (ctx.expression() != null)
            forControl.addChild(visit(ctx.expression()));

        if (ctx.forUpdate != null) {
            forControl.addChild(visit(ctx.forUpdate));
        }
        return forControl;
    }

    /*
     * This method visits the block inside a switch statement and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        JavaASTNode switchBlock = new JavaASTNode("switch-block", getText(ctx));

        for (int i = 0; i < ctx.switchLabel().size(); ++i)
            switchBlock.addChild(visit(ctx.switchLabel(i)));

        for (int i = 0; i < ctx.blockStatement().size(); ++i)
            switchBlock.addChild(visit(ctx.blockStatement(i)));

        return switchBlock;
    }

    /*
     * This method visits a switch label and adds it to the Abstract Syntax Tree.
     */
    @Override
    public JavaASTNode visitSwitchLabel(JavaParser.SwitchLabelContext ctx) {
        if (ctx.DEFAULT() != null)
            return new JavaASTNode(ctx.DEFAULT().getText(), getText(ctx));
        else if (ctx.CASE() != null) {
            // Create a new node for the expression after the case keyword and return it
            if (ctx.IDENTIFIER() != null)
                return new JavaASTNode(ctx.IDENTIFIER().getText(), getText(ctx));
            else if (ctx.constantExpression != null)
                return visit(ctx.constantExpression);
        }

        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    /*
     * This method puts a statement context inside a block node.
     * @param text The text of the block node to be created.
     * @param child The child node which is to be put inside the newly created block.
     * @return The generated block node.
     */
    private JavaASTNode addBlockToStatement(String text, JavaParser.StatementContext child) {
        JavaASTNode block = new JavaASTNode("block", text);
        block.addChild(visit(child));
        return block;
    }

    /*
     * This method generated a nice textual representation of a context with spaces.
     * @param ctx The context for which to generate the text.
     * @return The generated text.
     */
    private String getText(ParserRuleContext ctx) {
        int start = ctx.getStart().getStartIndex();
        int end = ctx.getStop().getStopIndex();
        return ctx.getStart().getInputStream().getText(new Interval(start, end));
    }
}
