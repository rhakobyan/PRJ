package prj.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class LessonTest {

    @Test
    public void settersAndGettersTest() {
        Lesson lesson = new Lesson();

        lesson.setId(1);
        lesson.setTitle("Title");
        lesson.setExplanation("Explanation");

        Problem problem = new Problem();
        lesson.setProblem(problem);

        Topic topic = new Topic();
        lesson.setTopic(topic);

        lesson.setStudentsCompleted(new HashSet<>());
        User user = new User();
        lesson.addStudentsCompleted(user);

        assertEquals(lesson.getId(), 1);
        assertEquals(lesson.getTitle(), "Title");
        assertEquals(lesson.getExplanation(), "Explanation");
        assertEquals(lesson.getProblem(), problem);
        assertEquals(lesson.getTopic(), topic);
        assertEquals(lesson.getStudentsCompleted().size(), 1);
        assertTrue(lesson.getStudentsCompleted().contains(user));
    }

    @Test
    public void equalsTest() {
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);

        Lesson lesson3 = new Lesson();
        lesson3.setId(1);

        User user = new User();

        assertEquals(lesson1, lesson1);
        assertNotEquals(lesson1, lesson2);
        assertEquals(lesson1, lesson3);
        assertNotEquals(lesson1, user);
    }

    @Test
    public void hashCodeTest() {
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);

        assertEquals(lesson1.hashCode(), 528);
        assertEquals(lesson2.hashCode(), 529);
    }

}
