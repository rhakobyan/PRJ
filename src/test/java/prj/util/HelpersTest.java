package prj.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import prj.ast.JavaASTNode;

import static org.junit.jupiter.api.Assertions.*;

class HelpersTest {

    @Test
    public void getMostSeniorTest() {
        JavaASTNode seniorDot = new JavaASTNode(".", "System.out.print");
        JavaASTNode belowDot = new JavaASTNode(".", "System.out");
        seniorDot.addChild(belowDot);
        belowDot.addChild(new JavaASTNode("System", ""));
        belowDot.addChild(new JavaASTNode("out", ""));
        seniorDot.addChild(new JavaASTNode("print", ""));

        JavaASTNode node = Helpers.getMostSenior(belowDot, ".");

        assertEquals(seniorDot, node);
    }

    @ParameterizedTest
    @CsvSource({"==, equal to", "!=, not equal to", ">, greater than", "<, smaller than", ">=, greater than or equal to", "<=, smaller than or equal to"})
    public void getComparisonNameTest(String comparison, String name) {
        String obtainedName = Helpers.getComparisonName(comparison);

        assertEquals(name, obtainedName);
    }

    @Test
    public void getComparisonNameEmptyStringTest() {
        String obtainedName = Helpers.getComparisonName("test");

        assertEquals("", obtainedName);
    }

}