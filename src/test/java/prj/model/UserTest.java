package prj.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import prj.dto.UserDto;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserTest {

    @Test
    public void settersAndGettersTest() {
        User user = new User();

        user.setId(1);
        user.setEmail("test@kcl.ac.uk");
        user.setPassword("password");
        user.setFirstName("TestF");
        user.setLastName("TestL");

        Lesson lesson = new Lesson();
        lesson.setId(1);
        user.setCompletedLessons(new HashSet<>());
        user.addCompletedLesson(lesson);

        Quiz quiz = new Quiz();
        quiz.setId(1L);
        user.setCompletedQuizzes(new HashSet<>());
        user.addCompletedQuiz(quiz);

        assertEquals(user.getId(), 1);
        assertEquals(user.getEmail(), "test@kcl.ac.uk");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getFirstName(), "TestF");
        assertEquals(user.getLastName(), "TestL");
        assertEquals(user.getCompletedLessons().size(), 1);
        assertTrue(user.getCompletedLessons().contains(lesson));
        assertEquals(user.getCompletedQuizzes().size(), 1);
        assertTrue(user.getCompletedQuizzes().contains(quiz));
    }

    @Test
    public void constructorTest() {
        UserDto userDto = Mockito.mock(UserDto.class);
        Mockito.when(userDto.getEmail()).thenReturn("test@kcl.ac.uk");
        Mockito.when(userDto.getPassword()).thenReturn("password");
        Mockito.when(userDto.getFirstName()).thenReturn("TestF");
        Mockito.when(userDto.getLastName()).thenReturn("TestL");

        User user = new User(userDto);

        assertEquals(user.getEmail(), "test@kcl.ac.uk");
        assertEquals(user.getPassword(), "password");
        assertEquals(user.getFirstName(), "TestF");
        assertEquals(user.getLastName(), "TestL");
    }
}