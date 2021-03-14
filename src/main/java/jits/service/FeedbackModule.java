package jits.service;

import jits.antlr.JavaASTNode;

public class FeedbackModule {
    public static String generateFeedback(JavaASTNode node) {
        StringBuilder stringBuilder = new StringBuilder();
        switch (node.getName())
        {
            case "if": {
                stringBuilder.append("You need to write an if statement");
                break;
            }
            case "for": {
                stringBuilder.append("You need to write a for loop");
                break;
            }
            case "for-control": {
                stringBuilder.append("Define the for loop by constructing the following statements: ")
                .append(node.getText());
                break;
            }
            case "while": {
                stringBuilder.append("You need to write a while loop");
                break;
            }
            case "==":
            case "!=":
            case ">":
            case "<":
            case ">=":
            case "<=": {
                stringBuilder.append("You need to use the '").append(node.getName()).append("' comparison operator ")
                        .append("to check for the following statement: ").append(node.getText());
                break;
            }
            case "+":
            case "-":
            case "*":
            case "/":
            case "%": {
                stringBuilder.append("Use the '").append(node.getName()).append("' operator")
                        .append("to write the following statement: ").append(node.getText());
                break;
            }
            case ".": {
                JavaASTNode seniorDot = getMostSenior(node, ".");
                stringBuilder.append("For now, you need to write the following statement: ")
                        .append(seniorDot.getText());
                break;
            }
            case "=":
            case "-=":
            case "+=":
            case "*=":
            case "/=": {
                stringBuilder.append("You need to use the assignment '").append(node.getName()).append("' operator ")
                        .append("and define the following statement: ").append(node.getText());
                break;
            }
            case "local-var": {
                stringBuilder.append("You need to declare a local variable");
                break;
            }
            case "pre":
            case "post": {
                stringBuilder.append("You need to use a ").append(node.getName()).append("fixed operator");
                if (node.getChildren().size() == 2) {
                    stringBuilder.append(" '").append(node.getChildren().get(0).getName()).append("'")
                            .append("on the variable ").append(node.getChildren().get(1).getName());
                }

            }
            default: {
                stringBuilder.append("Define the following statement: ").append(node.getName());
            }

        }
        return stringBuilder.toString();
    }

    private static JavaASTNode getMostSenior(JavaASTNode node, String value) {
        if (node.getParent() == null)
            return node;

        if (node.getParent().getName().equals(value))
            return getMostSenior(node.getParent(), value);

        return node.getParent();
    }
}
