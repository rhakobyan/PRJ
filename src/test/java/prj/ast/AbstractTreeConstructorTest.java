package prj.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import prj.antlr.JavaParser;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AbstractTreeConstructorTest {

    @Test
    void visitBlockTest() {
        AbstractTreeConstructor constructor = new AbstractTreeConstructor();
        JavaParser.BlockContext blockContext = Mockito.mock(JavaParser.BlockContext.class);

        Mockito.when(blockContext.getText()).thenReturn("{int x = 0;}");
        Mockito.when(blockContext.blockStatement()).thenReturn(new ArrayList<>());

        assertEquals("block", constructor.visitBlock(blockContext).getName());
    }
}