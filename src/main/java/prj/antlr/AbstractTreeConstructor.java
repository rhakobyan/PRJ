package prj.antlr;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;

public class AbstractTreeConstructor extends JavaParserBaseVisitor<JavaASTNode> {

    @Override
    public JavaASTNode visitBlock(JavaParser.BlockContext ctx) {
//        System.out.println("visiting block");
        JavaASTNode block = new JavaASTNode("block",ctx.getText());

        for (int i = 0; i < ctx.blockStatement().size(); ++i)
            block.addChild(visit(ctx.blockStatement(i)));

        return block;
    }

    @Override
    public JavaASTNode visitStatement(JavaParser.StatementContext ctx) {
//        System.out.println("visiting statement");
        if (ctx.blockLabel != null) {
            return visit(ctx.blockLabel);
        }
        else if (ctx.IF() != null) {
            JavaASTNode ifStatement = new JavaASTNode("if",getText(ctx));
            ifStatement.addChild(visit(ctx.parExpression()));

            if (ctx.statement().size() > 0) {
                if (ctx.statement(0).blockLabel == null)
                    ifStatement.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    ifStatement.addChild(visit(ctx.statement(0)));
            }

            if (ctx.ELSE() != null && ctx.statement().size() > 1) {
                JavaASTNode elseBlock = new JavaASTNode("else", getText(ctx));
                if (ctx.statement(1).blockLabel == null)
                    elseBlock.addChild(addBlockToStatement(ctx.getText(), ctx.statement(1)));
                else
                    elseBlock.addChild(visit(ctx.statement(1)));

                ifStatement.addChild(elseBlock);
            }

            return ifStatement;
        } else if (ctx.FOR() != null) {
            JavaASTNode forLoop = new JavaASTNode("for", getText(ctx));
            if (ctx.forControl() != null)
                forLoop.addChild(visit(ctx.forControl()));

            if (ctx.statement().size() > 0) {
                if (ctx.statement(0).blockLabel == null)
                    forLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                 else
                     forLoop.addChild(visit(ctx.statement(0)));
            }

            return forLoop;
        } else if (ctx.DO() != null) {
            JavaASTNode doWhileLoop = new JavaASTNode("do-while",getText(ctx));
            if (ctx.statement().size() > 0) {
                if (ctx.statement(0).blockLabel == null)
                    doWhileLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    doWhileLoop.addChild(visit(ctx.statement(0)));
            }

            if (ctx.parExpression() != null)
                doWhileLoop.addChild(visit(ctx.parExpression()));

            return doWhileLoop;
        } else if (ctx.WHILE() != null) {
            JavaASTNode whileLoop = new JavaASTNode("while", getText(ctx));
            if (ctx.parExpression() != null)
                whileLoop.addChild(visit(ctx.parExpression()));
            if (ctx.statement().size() > 0) {
                if (ctx.statement(0).blockLabel == null)
                    whileLoop.addChild(addBlockToStatement(ctx.getText(), ctx.statement(0)));
                else
                    whileLoop.addChild(visit(ctx.statement(0)));
            }

            return whileLoop;
        } else if (ctx.SWITCH() != null) {
            JavaASTNode switchStatement = new JavaASTNode("switch", getText(ctx));
            for (int i = 0; i < ctx.switchBlockStatementGroup().size(); ++i)
                switchStatement.addChild(visit(ctx.switchBlockStatementGroup(i)));

            for (int i = 0; i < ctx.switchLabel().size(); ++i)
                switchStatement.addChild(visit(ctx.switchLabel(i)));

            return switchStatement;
        } else if ( ctx.statementExpression != null)
            return visit(ctx.getChild(0));

        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitParExpression(JavaParser.ParExpressionContext ctx) {
//        System.out.println("visiting parExpression");
        if (ctx.expression() == null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        return visit(ctx.expression());
    }

    @Override
    public JavaASTNode visitExpression(JavaParser.ExpressionContext ctx) {
//        System.out.println("visiting expression " + ctx.getText());
//        System.out.println("visiting expression");
        if (ctx.getChildCount() == 0) {
            return new JavaASTNode(ctx.getText(), ctx.getText());
        } else if (ctx.primary() != null) {
            return  visit(ctx.primary());
        } else if (ctx.DOT() != null) {
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
        } else if (ctx.methodCall() != null) {
            return visit(ctx.methodCall());
        } else if (ctx.postfix != null) {
            JavaASTNode postFix = new JavaASTNode("post", getText(ctx));
            postFix.addChild(new JavaASTNode(ctx.postfix.getText(), getText(ctx)));
            if (ctx.getChild(0) != null)
                postFix.addChild(visit(ctx.getChild(0)));

            return postFix;

        } else if (ctx.prefix != null) {
            JavaASTNode preFix = new JavaASTNode("pre", getText(ctx));
            preFix.addChild(new JavaASTNode(ctx.prefix.getText(),getText(ctx)));
            if (ctx.getChild(1) != null)
                preFix.addChild(visit(ctx.getChild(1)));

            return preFix;
        } else if (ctx.bop != null) {
            boolean reverse = false;
            JavaASTNode booleanOperator;
            if (ctx.bop.getText().equals(">=")) {
                booleanOperator = new JavaASTNode("<=", getText(ctx));
                reverse = true;
            } else if (ctx.bop.getText().equals(">")) {
                booleanOperator = new JavaASTNode("<", getText(ctx));
                reverse = true;
            } else {
                booleanOperator = new JavaASTNode(ctx.bop.getText(), getText(ctx));
            }

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

    @Override
    public JavaASTNode visitPrimary(JavaParser.PrimaryContext ctx) {
//        System.out.println("visiting primary");
        if (ctx.IDENTIFIER() != null || ctx.THIS() != null || ctx.SUPER() != null)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        if (ctx.LPAREN() != null && ctx.RPAREN() != null && ctx.expression() != null)
            return visit(ctx.expression());

        return visitChildren(ctx);
    }

    @Override
    public JavaASTNode visitLiteral(JavaParser.LiteralContext ctx) {
//        System.out.println("visiting literal");
        if(ctx.integerLiteral() == null && ctx.floatLiteral() == null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        return visitChildren(ctx);
    }

    @Override
    public JavaASTNode visitIntegerLiteral(JavaParser.IntegerLiteralContext ctx) {
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitFloatLiteral(JavaParser.FloatLiteralContext ctx) {
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
//        System.out.println("visiting local variable declaration");
        JavaASTNode localVariable = new JavaASTNode("local-var", getText(ctx));

        localVariable.addChild(visit(ctx.typeType()));
        localVariable.addChild(visit(ctx.variableDeclarators().variableDeclarator(0)));

        return localVariable;
    }

    @Override
    public JavaASTNode visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
//        System.out.println("visiting primitive");
        JavaASTNode type = new JavaASTNode("primitive-type", getText(ctx));
        type.addChild(new JavaASTNode(ctx.getText(), getText(ctx)));
        return type;
    }

    @Override
    public JavaASTNode visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
//        System.out.println("visiting class");
        JavaASTNode type = new JavaASTNode("class-type", getText(ctx));
        type.addChild(new JavaASTNode(ctx.getText(), getText(ctx)));
        return type;
    }

    @Override
    public JavaASTNode visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
//        System.out.println("visiting variable declaration");
        JavaASTNode declarator = new JavaASTNode("=", getText(ctx));
        declarator.addChild(visit(ctx.variableDeclaratorId()));

        if (ctx.ASSIGN() != null && ctx.variableInitializer() != null) {
            declarator.addChild(visit(ctx.variableInitializer()));
        }

        return declarator;
    }

    @Override
    public JavaASTNode visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
//        System.out.println("visiting variable declaration id");
        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitMethodCall(JavaParser.MethodCallContext ctx) {
//        System.out.println("visiting method call");
        JavaASTNode methodCall = new JavaASTNode("method-call",getText(ctx));
        JavaASTNode methodCallName = new JavaASTNode(ctx.getStart().getText(), getText(ctx));
        methodCall.addChild(methodCallName);
        if (ctx.expressionList() != null) {
            for (int i = 0; i < ctx.expressionList().expression().size(); ++i) {
                methodCall.addChild(visit(ctx.expressionList().expression(i)));
            }
        }
        return methodCall;
    }

    @Override
    public JavaASTNode visitExpressionList(JavaParser.ExpressionListContext ctx) {
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        return visit(ctx.getChild(0));
    }

    @Override
    public JavaASTNode visitBlockStatement(JavaParser.BlockStatementContext ctx) {
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        return visit(ctx.getChild(0));
    }

    @Override
    public JavaASTNode visitForControl(JavaParser.ForControlContext ctx) {
//        System.out.println("Visiting for control");
        if (ctx.enhancedForControl() != null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

        JavaASTNode forControl = new JavaASTNode("for-control", getText(ctx));
        if (ctx.forInit() != null)
            forControl.addChild(visit(ctx.forInit()));

        if (ctx.expression() != null)
            forControl.addChild(visit(ctx.expression()));

        if (ctx.forUpdate != null) {
            forControl.addChild(visit(ctx.forUpdate));
        }
        return forControl;
    }

    @Override
    public JavaASTNode visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        JavaASTNode switchBlock = new JavaASTNode("switch-block", getText(ctx));

        for (int i = 0; i < ctx.switchLabel().size(); ++i)
            switchBlock.addChild(visit(ctx.switchLabel(i)));

        for (int i = 0; i < ctx.blockStatement().size(); ++i)
            switchBlock.addChild(visit(ctx.blockStatement(i)));

        return switchBlock;
    }

    @Override
    public JavaASTNode visitSwitchLabel(JavaParser.SwitchLabelContext ctx) {
        if (ctx.DEFAULT() != null)
            return new JavaASTNode(ctx.DEFAULT().getText(), getText(ctx));
        else if (ctx.CASE() != null) {
            JavaASTNode switchLabel = new JavaASTNode(ctx.CASE().getText(), getText(ctx));

            if (ctx.IDENTIFIER() != null)
                return new JavaASTNode(ctx.IDENTIFIER().getText(), getText(ctx));
            else if (ctx.constantExpression != null)
                return visit(ctx.constantExpression);
        }

        return new JavaASTNode(ctx.getText(), ctx.getText());
    }

    private JavaASTNode addBlockToStatement(String text, JavaParser.StatementContext child) {
        JavaASTNode block = new JavaASTNode("block", text);
        block.addChild(visit(child));
        return block;
    }

    private String getText(ParserRuleContext ctx) {
        int start = ctx.getStart().getStartIndex();
        int end = ctx.getStop().getStopIndex();
        return ctx.getStart().getInputStream().getText(new Interval(start, end));
    }
}
