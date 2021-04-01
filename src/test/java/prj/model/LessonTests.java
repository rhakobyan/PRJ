package prj.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LessonTests {
    private Lesson lesson;



    @BeforeEach
    public void init() {
        lesson = new Lesson();
    }

    @Test
    public void setIdTest() {
        lesson.setId(1);
        assertEquals(lesson.getId(), 1);
    }


}
