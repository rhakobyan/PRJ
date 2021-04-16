package prj.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TopicTest {

    @Test
    public void settersAndGettersTest() {
        Topic topic = new Topic();

        topic.setId(1);
        topic.setTitle("Title");
        topic.setDescription("Description");
        topic.setImage("image.png");

        ArrayList<Lesson> lessons = new ArrayList<>();
        topic.setLessons(lessons);

        Quiz quiz = Mockito.mock(Quiz.class);
        Mockito.when(quiz.getId()).thenReturn(1L);
        topic.setQuiz(quiz);

        assertEquals(topic.getId(), 1);
        assertEquals(topic.getTitle(), "Title");
        assertEquals(topic.getDescription(), "Description");
        assertEquals(topic.getImage(), "image.png");
        assertEquals(topic.getLessons(), lessons);
        assertEquals(topic.getQuiz().getId(), quiz.getId());
    }
}