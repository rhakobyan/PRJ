package prj.util;

import prj.ast.JavaASTNode;

import java.util.Locale;

/*
 * The Helpers class provides some helper methods for Drool rules.drl file when generating hint messages.
 */
public class Helpers {

    /*
     * Given @param node and a value, this method recursively checks all its parents until one
     * their names is not @param value.
     * @return the most senior node whose name is value starting from the first passed @param node.
     */
    public static JavaASTNode getMostSenior(JavaASTNode node, String value) {
        if (node.getParent() == null)
            return node;

        if (node.getParent().getName().equals(value))
            return getMostSenior(node.getParent(), value);

        return node;
    }

    /*
     * Translate comparison operator names to their String values.
     */
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

    /*
     * Make the first letter of @param string lowercase and return it.
     */
    public static String deCapitalise(String string) {
        return string.substring(0, 1).toLowerCase(Locale.ROOT) + string.substring(1);
    }
}
