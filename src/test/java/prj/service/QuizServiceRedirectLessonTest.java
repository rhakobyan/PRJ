package prj.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import prj.model.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class QuizServiceRedirectLessonTest {

    @Autowired
    private QuizService quizService;

    private Quiz quiz;
    private User user;
    private Lesson lesson1;
    private Lesson lesson2;

    @BeforeEach
    public void setUp() {
        Authentication authentication = Mockito.mock(Authentication.class);
        user = new User();
        user.setCompletedLessons(new HashSet<>());
        Mockito.when(authentication.getPrincipal()).thenReturn(new AppUserDetails(user));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Topic topic = new Topic();
        quiz = new Quiz();

        topic.setQuiz(quiz);
        quiz.setTopic(topic);

        lesson1 = new Lesson();
        lesson1.setId(1);
        lesson1.setTopic(topic);

        lesson2 = new Lesson();
        lesson2.setId(2);
        lesson2.setTopic(topic);

        List<Lesson> lessonList = new LinkedList<>();
        lessonList.add(lesson1);
        lessonList.add(lesson2);

        topic.setLessons(lessonList);
    }

    @Test
    public void redirectLessonTest() {
        Lesson redirectedLesson = quizService.redirectedLesson(quiz);

        assertEquals(lesson2.getId(), redirectedLesson.getId());
    }

    @Test
    public void redirectLessonReturnNullTest() {
        HashSet<Lesson> lessonHashSet = new HashSet<>();
        lessonHashSet.add(lesson1);
        lessonHashSet.add(lesson2);
        user.setCompletedLessons(lessonHashSet);

        Lesson redirectedLesson = quizService.redirectedLesson(quiz);

        assertNull(redirectedLesson);
    }
}
