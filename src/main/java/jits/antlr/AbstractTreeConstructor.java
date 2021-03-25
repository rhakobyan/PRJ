package jits.antlr;

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
            JavaASTNode ifStatement = new JavaASTNode("if",ctx.getText());
            ifStatement.addChild(visit(ctx.parExpression()));

            if (ctx.statement().size() > 0) {
                if (ctx.statement(0).blockLabel == null) {
                    JavaASTNode block = new JavaASTNode("block",ctx.getText());
                    block.addChild(visit(ctx.statement(0)));
                    ifStatement.addChild(block);
                } else {
                    ifStatement.addChild(visit(ctx.statement(0)));
                }
            }

            if (ctx.ELSE() != null && ctx.statement().size() > 1) {
                JavaASTNode elseBlock = new JavaASTNode("else",ctx.getText());
                if (ctx.statement(1).blockLabel == null) {
                    JavaASTNode block = new JavaASTNode("block",ctx.getText());
                    block.addChild(visit(ctx.statement(1)));
                    elseBlock.addChild(block);
                    ifStatement.addChild(block);
                } else {
                    elseBlock.addChild(visit(ctx.statement(1)));
                    ifStatement.addChild(elseBlock);
                }
            }

            return ifStatement;
        } else if (ctx.FOR() != null) {
            JavaASTNode forLoop = new JavaASTNode("for", ctx.getText());
            forLoop.addChild(visit(ctx.forControl()));

            if (ctx.statement().size() > 0)
                forLoop.addChild(visit(ctx.statement(0)));

            return forLoop;
        } else if (ctx.DO() != null) {
            JavaASTNode doWhileLoop = new JavaASTNode("do-while", ctx.getText());
            doWhileLoop.addChild(visit(ctx.parExpression()));
            if (ctx.statement().size() > 0)
                doWhileLoop.addChild(visitStatement(ctx.statement(0)));

            return doWhileLoop;
        } else if (ctx.WHILE() != null) {
            JavaASTNode whileLoop = new JavaASTNode("while", ctx.getText());
            whileLoop.addChild(visit(ctx.parExpression()));
            if (ctx.statement().size() > 0)
                whileLoop.addChild(visitStatement(ctx.statement(0)));

            return whileLoop;
        } else if (ctx.SWITCH() != null) {
            JavaASTNode switchStatement = new JavaASTNode("switch", ctx.getText());
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
        System.out.println("visiting expression " + ctx.getText());
//        System.out.println("visiting expression");
        if (ctx.getChildCount() == 0) {
            return new JavaASTNode(ctx.getText(), ctx.getText());
        } else if (ctx.primary() != null) {
            return  visit(ctx.primary());
        } else if (ctx.DOT() != null) {
            if (ctx.IDENTIFIER() != null || ctx.methodCall() != null) {
                JavaASTNode booleanOperator = new JavaASTNode(ctx.DOT().getText(), ctx.getText());
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
            JavaASTNode postFix = new JavaASTNode("post", ctx.getText());
            postFix.addChild(new JavaASTNode(ctx.postfix.getText(), ctx.getText()));
            if (ctx.getChild(0) != null)
                postFix.addChild(visit(ctx.getChild(0)));

            return postFix;

        } else if (ctx.prefix != null) {
            JavaASTNode preFix = new JavaASTNode("pre", ctx.getText());
            preFix.addChild(new JavaASTNode(ctx.prefix.getText(), ctx.getText()));
            if (ctx.getChild(1) != null)
                preFix.addChild(visit(ctx.getChild(1)));

            return preFix;
        } else if (ctx.bop != null) {
            boolean reverse = false;
            JavaASTNode booleanOperator;
            if (ctx.bop.getText().equals(">=")) {
                booleanOperator = new JavaASTNode("<=", ctx.getText());
                reverse = true;
            } else if (ctx.bop.getText().equals(">")) {
                booleanOperator = new JavaASTNode("<", ctx.getText());
                reverse = true;
            } else {
                booleanOperator = new JavaASTNode(ctx.bop.getText(), ctx.getText());
            }

            if (ctx.expression().size() > 0 && ctx.expression(0) != null) {
                if (reverse)
                    booleanOperator.addChild(visit(ctx.expression(0)));
                else
                    booleanOperator.addChild(visit(ctx.expression(0)));
            }

            if (ctx.expression().size() > 1 && ctx.expression(1) != null) {
                if (reverse)
                    booleanOperator.addChild(visit(ctx.expression(1)));
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
        JavaASTNode localVariable = new JavaASTNode("local-var",ctx.getText());

        localVariable.addChild(visit(ctx.typeType()));
        localVariable.addChild(visit(ctx.variableDeclarators().variableDeclarator(0)));

        return localVariable;
    }

    @Override
    public JavaASTNode visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
//        System.out.println("visiting primitive");
        JavaASTNode type = new JavaASTNode("primitive-type", ctx.getText());
        type.addChild(new JavaASTNode(ctx.getText(), ctx.getText()));
        return type;
    }

    @Override
    public JavaASTNode visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
//        System.out.println("visiting class");
        JavaASTNode type = new JavaASTNode("class-type", ctx.getText());
        type.addChild(new JavaASTNode(ctx.getText(), ctx.getText()));
        return type;
    }

    @Override
    public JavaASTNode visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
//        System.out.println("visiting variable declaration");
        JavaASTNode declarator = new JavaASTNode("=", ctx.getText());
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
        JavaASTNode methodCall = new JavaASTNode("method-call", ctx.getText());
        JavaASTNode methodCallName = new JavaASTNode(ctx.getStart().getText(), ctx.getText());
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

        JavaASTNode forControl = new JavaASTNode("for-control",ctx.getText());
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
        JavaASTNode switchBlock = new JavaASTNode("switch-block",ctx.getText());

        for (int i = 0; i < ctx.switchLabel().size(); ++i)
            switchBlock.addChild(visit(ctx.switchLabel(i)));

        for (int i = 0; i < ctx.blockStatement().size(); ++i)
            switchBlock.addChild(visit(ctx.blockStatement(i)));

        return switchBlock;
    }

    @Override
    public JavaASTNode visitSwitchLabel(JavaParser.SwitchLabelContext ctx) {
        if (ctx.DEFAULT() != null)
            return new JavaASTNode(ctx.DEFAULT().getText(), ctx.getText());
        else if (ctx.CASE() != null) {
            JavaASTNode switchLabel = new JavaASTNode(ctx.CASE().getText(), ctx.getText());

            if (ctx.IDENTIFIER() != null)
                return new JavaASTNode(ctx.IDENTIFIER().getText(), ctx.getText());
            else if (ctx.constantExpression != null)
                return visit(ctx.constantExpression);
        }

        return new JavaASTNode(ctx.getText(), ctx.getText());
    }
}
