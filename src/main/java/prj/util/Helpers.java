package prj.util;

import prj.ast.JavaASTNode;

import java.util.Locale;

public class Helpers {

    public static JavaASTNode getMostSenior(JavaASTNode node, String value) {
        if (node.getParent() == null)
            return node;

        if (node.getParent().getName().equals(value))
            return getMostSenior(node.getParent(), value);

        return node;
    }

    public static String getComparisonName(String operatorName) {
        switch (operatorName) {
            case ("=="):
                return "equal to";
            case ("!="):
                return "not equal to";
            case (">"):
                return "greater than";
            case (">="):
                return "greater than or equal to";
            case ("<"):
                return "smaller than";
            case ("<="):
                return "smaller than or equal to";
        }
        return "";
    }

    public static String deCapitalise(String string) {
        return string.substring(0, 1).toLowerCase(Locale.ROOT) + string.substring(1);
    }
}
