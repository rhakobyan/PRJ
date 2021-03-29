package prj.util;

import prj.antlr.JavaASTNode;

import java.util.Locale;

public class Helpers {

    public static JavaASTNode getMostSenior(JavaASTNode node, String value) {
        if (node.getParent() == null)
            return node;

        if (node.getParent().getName().equals(value))
            return getMostSenior(node.getParent(), value);

        return node;
    }

    public static String deCapitalise(String string) {
        return string.substring(0, 1).toLowerCase(Locale.ROOT) + string.substring(1);
    }
}
