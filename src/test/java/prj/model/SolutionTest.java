package prj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolutionTest {

    @Test
    public void settersAndGettersTest() {
        Solution solution = new Solution();

        solution.setId(1);
        solution.setSolutionFile("testFile");

        Problem problem = new Problem();
        solution.setProblem(problem);

        assertEquals(solution.getId(), 1);
        assertEquals(solution.getSolutionFile(), "testFile");
        assertEquals(solution.getProblem(), problem);
    }

}