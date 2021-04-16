package prj.ast;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaASTNodeTest {

    private JavaASTNode javaASTNode;

    @BeforeEach
    public void init() {
        javaASTNode = new JavaASTNode("local-var", "int i = 0;");
    }

    @Test
    public void correctFieldsTest() {
        assertEquals(javaASTNode.getName(), "local-var");
        assertEquals(javaASTNode.getText(), "int i = 0;");
    }

    @Test
    public void addNotNullChildTest() {
        JavaASTNode childNode = new JavaASTNode("=", "i = 0;");
        javaASTNode.addChild(childNode);

        assertEquals(javaASTNode.getChildren().size(), 1);
        assertEquals(childNode.getName(), javaASTNode.getChildren().get(0).getName());
        assertEquals(childNode.getParent(), javaASTNode);
    }

    @Test
    public void addNullChildTest() {
       javaASTNode.addChild(null);

        assertEquals(javaASTNode.getChildren().size(), 1);
        assertEquals("null-node", javaASTNode.getChildren().get(0).getName());
    }
}