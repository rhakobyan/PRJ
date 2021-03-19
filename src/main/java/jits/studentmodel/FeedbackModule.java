package jits.studentmodel;

import jits.antlr.JavaASTNode;
import jits.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeedbackModule {
    public static String generateFeedback(Pair<JavaASTNode, JavaASTNode> pair) {
        StringBuilder stringBuilder = new StringBuilder();
        if (pair == null)
            return "Internal Error";

        if (pair.getFirstElement().getParent() != null && pair.getFirstElement().getParent().getName().equals("if") &&
                pair.getFirstElement().getParent().getChildren().size() == 3 &&
                pair.getFirstElement().getParent().getChildren().get(2).getText().equals(pair.getFirstElement().getText()))
        {
            stringBuilder.append("In the else branch you need to define ")
            .append(pair.getFirstElement().getText());
            return stringBuilder.toString();
        }

        switch (pair.getFirstElement().getName())
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
                        .append(pair.getFirstElement().getText());
                break;
            }
            case "while": {
                stringBuilder.append("You need to write a while loop");
                break;
            }
            case "do": {
                stringBuilder.append("You need to write a do-while loop");
                break;
            }
            case "switch": {
                stringBuilder.append("You need to write a switch statement");
                break;
            }
            case "==":
            case "!=":
            case ">":
            case "<":
            case ">=":
            case "<=": {
                stringBuilder.append("You need to use a comparison operator ")
                        .append("to check for the following statement: ").append(pair.getFirstElement().getText());
                break;
            }
            case "&&":
            case "||":
                stringBuilder.append("You need to use the '").append(pair.getFirstElement().getName()).append("' logical operator ")
                        .append("to check for the following statement: ").append(pair.getFirstElement().getText());
                break;
            case "+":
            case "-":
            case "*":
            case "/":
            case "%": {
                stringBuilder.append("Use the '").append(pair.getFirstElement().getName()).append("' operator")
                        .append("to write the following statement: ").append(pair.getFirstElement().getText());
                break;
            }
            case ".": {
                JavaASTNode seniorDot = getMostSenior(pair.getFirstElement(), ".");
                stringBuilder.append("For now, you need to write the following statement: ")
                        .append(seniorDot.getText());
                break;
            }
            case "=":
            case "-=":
            case "+=":
            case "*=":
            case "/=": {
                stringBuilder.append("You need to use the assignment '").append(pair.getFirstElement().getName()).append("' operator ")
                        .append("and define the following statement: ").append(pair.getFirstElement().getText());
                break;
            }
            case "local-var": {
                stringBuilder.append("You need to declare a local variable");
                if (pair.getFirstElement().getChildren().size() > 0)
                    stringBuilder.append(" of type ").append(pair.getFirstElement().getChildren().get(0).getName());
                break;
            }
            case "pre":
            case "post": {
                stringBuilder.append("You need to use a ").append(pair.getFirstElement().getName()).append("fixed operator");
                if (pair.getFirstElement().getChildren().size() == 2) {
                    stringBuilder.append(" '").append(pair.getFirstElement().getChildren().get(0).getName()).append("'")
                            .append("on the variable ").append(pair.getFirstElement().getChildren().get(1).getName());
                }
                break;
            }
            case "success-node": {
                stringBuilder.append("Try running your code!");
                break;
            }
            case "method-call": {
                if (pair.getFirstElement().getChildren().size() >= 1)
                    stringBuilder.append("You need to call the method ")
                            .append(pair.getFirstElement().getChildren().get(0).getName());
                else
                    stringBuilder.append("You need to cal a method");
                break;
            }
            case "extra-node": {
                stringBuilder.append("Your code contains statements that are not necessary for the completion of this exercise");
                break;
            }
            case "switch-block": {
                stringBuilder.append("Define the body of the switch statement");
                break;
            }
            default: {
                Pattern pattern = Pattern.compile("\"[^\"\\\\]*(\\\\.[^\"\\\\]*)*\"");
                Matcher matcher = pattern.matcher(pair.getFirstElement().getName());
                 if (matcher.matches()) {
                    stringBuilder.append("You need to specify the string ").append(pair.getFirstElement().getName());
                    matcher = pattern.matcher(pair.getSecondElement().getName());
                    if (matcher.matches())
                        stringBuilder.append(" instead of ").append(pair.getSecondElement().getName());
                } else if (pair.getFirstElement().getParent() != null && pair.getFirstElement().getParent().getName().equals("method-call")) {
                     if (pair.getFirstElement().getName().equals(pair.getFirstElement().getParent().getChildren().get(0).getName()))
                         stringBuilder.append("You should call the method ").append(pair.getFirstElement().getName());
                     else if (pair.getFirstElement().getName().equals(pair.getFirstElement().getParent().getChildren().get(1).getName()))
                         stringBuilder.append("You should call the method with the parameter ").append(pair.getFirstElement().getName());

                } else if (pair.getFirstElement().getParent() != null && pair.getFirstElement().getParent().getName().equals("=")) {
                    JavaASTNode parent = pair.getFirstElement().getParent();
                    if (parent.getName().equals("=") && pair.getSecondElement().getParent().getChildren().size() > 1 && pair.getFirstElement().getParent().getChildren().get(1).getName().equals(pair.getFirstElement().getName()))
                        stringBuilder.append("You should equate your variable to ").append(pair.getFirstElement().getName());
                    else
                        stringBuilder.append("You should name your variable ").append(pair.getFirstElement().getName());
                }
                else {
                    stringBuilder.append("Define the identifier ").append(pair.getFirstElement().getName());
                }
            }

        }
        return stringBuilder.toString();
    }

    private static JavaASTNode getMostSenior(JavaASTNode node, String value) {
        if (node.getParent() == null)
            return node;

        if (node.getParent().getName().equals(value))
            return getMostSenior(node.getParent(), value);

        return node;
    }
}
