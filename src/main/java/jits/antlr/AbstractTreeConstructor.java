package jits.antlr;

public class AbstractTreeConstructor extends JavaParserBaseVisitor<JavaASTNode> {

    @Override
    public JavaASTNode visitBlock(JavaParser.BlockContext ctx) {
//        System.out.println("visiting block");
        JavaASTNode blockNode = new JavaASTNode("block", ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            if (!ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                blockNode.addChild(visit(ctx.getChild(i)));
        }
        return blockNode;
    }

    @Override
    public JavaASTNode visitStatement(JavaParser.StatementContext ctx) {
//        System.out.println("visiting statement");
//        System.out.println(ctx.getText());
//        if (ctx.statement(0) != null)
//            System.out.println(ctx.statement(0).getText());
        if ( ctx.statementExpression != null)
            return visit(ctx.getChild(0));

        if(ctx.blockLabel != null || ctx.identifierLabel != null)
            return visitChildren(ctx);

        JavaASTNode statementNode = new JavaASTNode(ctx.getStart().getText(), ctx.getText());
        for (int i = 1; i < ctx.getChildCount(); ++i) {
            if (!ctx.getChild(i).getText().equals("{") &&
                    !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") &&
                    !ctx.getChild(i).getText().equals(")") && !ctx.getChild(i).getText().equals(";") &&
                    (ctx.ELSE() == null || !ctx.getChild(i).getText().equals(ctx.ELSE().getText())) &&
                    (ctx.WHILE() == null || !ctx.getChild(i).getText().equals(ctx.WHILE().getText())))
                statementNode.addChild(visit(ctx.getChild(i)));

        }

        return statementNode;
    }

    @Override public JavaASTNode visitParExpression(JavaParser.ParExpressionContext ctx) {
//        System.out.println("visiting parExpression");
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        return visit(ctx.getChild(1));
    }

    @Override
    public JavaASTNode visitExpression(JavaParser.ExpressionContext ctx) {
//        System.out.println("visiting expression");
        if (ctx.getChildCount() == 0)
            return new JavaASTNode(ctx.getText(), ctx.getText());
        String name = "";
        if (ctx.DOT() != null) {
            JavaASTNode expressionNode = new JavaASTNode(".", ctx.getText());
//            System.out.println(ctx.getChildCount());
            for (int i = 0; i < ctx.getChildCount(); ++i) {
                if (!ctx.getChild(i).getText().equals(".") && !ctx.getChild(i).getText().equals("==") && !ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                {
                    if(ctx.IDENTIFIER() != null && ctx.getChild(i).getText().equals(ctx.IDENTIFIER().getText()))
                        expressionNode.addChild(new JavaASTNode(ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getText()));
                    else
                        expressionNode.addChild(visit(ctx.getChild(i)));
                }
            }

            return expressionNode;
        }
        else if (ctx.NEW() != null)
            name = "new";
        else if (ctx.postfix != null) {
            name = ctx.postfix.getText();
            JavaASTNode expressionNode = new JavaASTNode("post", ctx.getText());
            expressionNode.addChild(new JavaASTNode(name, name));
            if (ctx.getChild(0) != null)
                expressionNode.addChild(visit(ctx.getChild(0)));

            return expressionNode;
        }
        else if (ctx.prefix != null) {
            name = ctx.prefix.getText();
            JavaASTNode expressionNode = new JavaASTNode("pre", ctx.getText());
            expressionNode.addChild(new JavaASTNode(name, name));
            if (ctx.getChild(1) != null)
                expressionNode.addChild(visit(ctx.getChild(1)));

            return expressionNode;
        }
        else if (ctx.bop != null) {
            if (ctx.getChildCount() == 2)
                name = ctx.getChild(0).toStringTree();
            else
                name = ctx.getChild(1).toStringTree();
        }
        if (name.equals(""))
            return visitChildren(ctx);

        JavaASTNode expressionNode = new JavaASTNode(name, ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
//            System.out.println(ctx.getText());
            if (!ctx.getChild(i).getText().equals(ctx.bop.getText()) && !ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                expressionNode.addChild(visit(ctx.getChild(i)));
        }
        return  expressionNode;
    }

    @Override
    public JavaASTNode visitPrimary(JavaParser.PrimaryContext ctx) {
//        System.out.println("visiting primary");
        if (ctx.IDENTIFIER() != null || ctx.THIS() != null || ctx.SUPER() != null)
            return new JavaASTNode(ctx.getText(), ctx.getText());

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
        JavaASTNode localVariableNode = new JavaASTNode("local-var", ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            localVariableNode.addChild(visit(ctx.getChild(i)));
        }
        return localVariableNode;
    }

    @Override
    public JavaASTNode visitPrimitiveType(JavaParser.PrimitiveTypeContext ctx) {
//        System.out.println("visiting primitive");
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitClassOrInterfaceType(JavaParser.ClassOrInterfaceTypeContext ctx) {
//        System.out.println("visiting class");
        return new JavaASTNode(ctx.toStringTree(), ctx.getText());
    }

    @Override
    public JavaASTNode visitVariableDeclarator(JavaParser.VariableDeclaratorContext ctx) {
//        System.out.println("visiting variable declaration");
        if (ctx.ASSIGN() == null) {
            visitChildren(ctx);
        }

        JavaASTNode assignNode = new JavaASTNode("=", ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            if (!ctx.getChild(i).getText().equals("=") && !ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                assignNode.addChild(visit(ctx.getChild(i)));
        }
        return  assignNode;
    }

    @Override
    public JavaASTNode visitVariableDeclaratorId(JavaParser.VariableDeclaratorIdContext ctx) {
//        System.out.println("visiting variable declaration id");
        return new JavaASTNode(ctx.getStart().getText(), ctx.getText());
    }

    @Override
    public JavaASTNode visitMethodCall(JavaParser.MethodCallContext ctx) {
//        System.out.println("visiting method call");
        JavaASTNode methodCallNode = new JavaASTNode(ctx.getStart().getText(), ctx.getText());
        if (ctx.expressionList() != null)
            methodCallNode.addChild(visit(ctx.expressionList().expression(0)));

        return methodCallNode;
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
        System.out.println("Visiting for control");
        JavaASTNode forControlNode = new JavaASTNode("for-control", ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            if (!ctx.getChild(i).getText().equals(";") && !ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                forControlNode.addChild(visit(ctx.getChild(i)));
        }
        return forControlNode;
    }

    @Override
    public JavaASTNode visitSwitchBlockStatementGroup(JavaParser.SwitchBlockStatementGroupContext ctx) {
        JavaASTNode switchBlock = new JavaASTNode("switch-block", ctx.getText());
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            if (!ctx.getChild(i).getText().equals(";") && !ctx.getChild(i).getText().equals("{") && !ctx.getChild(i).getText().equals("}") && !ctx.getChild(i).getText().equals("(") && !ctx.getChild(i).getText().equals(")"))
                switchBlock.addChild(visit(ctx.getChild(i)));
        }
        return switchBlock;
    }

    public JavaASTNode visitSwitchLabel(JavaParser.SwitchLabelContext ctx) {
        if (ctx.DEFAULT() != null)
            return new JavaASTNode("switch-default", ctx.getText());
        else if (ctx.IDENTIFIER() != null)
            return new JavaASTNode(ctx.IDENTIFIER().getText(), ctx.IDENTIFIER().getText());
        else if (ctx.constantExpression != null)
            return visit(ctx.constantExpression);

        return visitChildren(ctx);
    }
}
