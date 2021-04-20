package prj.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ProblemTest {

    @Test
    public void settersAndGettersTest() {
        Problem problem = new Problem();

        problem.setId(1);
        problem.setProblemFile("problems/Problem1.java");
        problem.setSolutionRequired(true);

        Lesson lesson = new Lesson();
        lesson.setId(1);
        problem.setLesson(lesson);

        problem.setSolutionStartIndex(4);
        problem.setSolutionEndLength(5);

        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution());
        problem.setSolutions(solutions);

        ArrayList<Misconception> misconceptions = new ArrayList<>();
        misconceptions.add(new Misconception());
        problem.setMisconceptions(misconceptions);

        assertEquals(problem.getId(), 1);
        assertEquals(problem.getProblemFile(), "problems/Problem1.java");
        assertTrue(problem.isSolutionRequired());
        assertEquals(problem.getLesson(), lesson);
        assertEquals(problem.getSolutionStartIndex(), 4);
        assertEquals(problem.getSolutionEndLength(), 5);
        assertEquals(problem.getSolutions(), solutions);
        assertEquals(problem.getMisconceptions(), misconceptions);
    }

    @Test
    public void setProblemBodyTest() {
        Problem problem = new Problem();
        problem.setProblemFile("problems/Problem1.java");
        problem.setProblemBody();

        assertEquals(problem.getProblemBody(), "public class Main {\n\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}");
    }

    @Test
    public void setProblemBodyNoSuchFileTest() {
        Problem problem = new Problem();
        problem.setProblemFile("noSuchFile.java");
        problem.setProblemBody();

        assertEquals(problem.getProblemBody(), "");
    }

}