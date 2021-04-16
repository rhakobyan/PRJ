package prj.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessonTest {
    private Lesson lesson;

    @BeforeEach
    public void init() {
        lesson = new Lesson();
    }

    @Test
    public void settersAndGettersTest() {
        lesson.setId(1);
        lesson.setTitle("Title");
        lesson.setExplanation("Explanation");

        assertEquals(lesson.getId(), 1);
        assertEquals(lesson.getTitle(), "Title");
        assertEquals(lesson.getExplanation(), "Explanation");
    }


}
