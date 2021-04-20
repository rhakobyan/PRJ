package prj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MisconceptionTest {

    @Test
    public void settersAndGettersTest() {
        Misconception misconception = new Misconception();

        misconception.setId(1);
        misconception.setHint("Test hint");
        misconception.setCode("print(test);");

        Problem problem = new Problem();
        misconception.setProblem(problem);

        assertEquals(misconception.getId(), 1);
        assertEquals(misconception.getHint(), "Test hint");
        assertEquals(misconception.getCode(), "print(test);");
        assertEquals(misconception.getProblem(), problem);
    }

}