package prj.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class QuizTest {

    @Test
    public void settersAndGettersTest() {
        Quiz quiz = new Quiz();

        quiz.setId(1L);
        quiz.setTitle("Test Quiz");

        Topic topic = new Topic();
        quiz.setTopic(topic);

        List<Question> questions = new ArrayList<>();
        quiz.setQuestions(questions);

        quiz.setPassPercent(80.5);

        Set<User> userCompleted = new HashSet<>();
        quiz.setStudentsCompleted(userCompleted);

        User user = new User();
        quiz.addStudentsCompleted(user);

        assertEquals(quiz.getId(), 1);
        assertEquals(quiz.getTitle(), "Test Quiz");
        assertEquals(quiz.getTopic(), topic);
        assertEquals(quiz.getQuestions(), questions);
        assertEquals(quiz.getPassPercent(), 80.5);
        assertEquals(quiz.getStudentsCompleted(), userCompleted);
        assertTrue(quiz.getStudentsCompleted().contains(user));
    }

    @Test
    public void equalsTest() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);

        Quiz quiz3 = new Quiz();
        quiz3.setId(1L);

        User user = new User();

        assertEquals(quiz1, quiz1);
        assertNotEquals(quiz1, quiz2);
        assertEquals(quiz1, quiz3);
        assertNotEquals(quiz1, user);
    }

    @Test
    public void hashCodeTest() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);

        assertEquals(quiz1.hashCode(), 528);
        assertEquals(quiz2.hashCode(), 529);
    }

}