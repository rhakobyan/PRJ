package prj.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import prj.model.Lesson;
import prj.model.Quiz;
import prj.repository.LessonRepository;
import prj.repository.QuizRepository;

import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ProgressServiceTest {

    @MockBean
    private LessonRepository lessonRepository;
    @MockBean
    private QuizRepository quizRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    public void getLatestInCompleteLessonEmptySetTest() {
        Lesson lesson = new Lesson();
        lesson.setId(1);

        Mockito.when(lessonRepository.findLatestInComplete(any(Long.class))).thenReturn(lesson);

        HashSet<Lesson> completedLessons = new HashSet<>();
        Lesson latestInComplete = progressService.getLatestInCompleteLesson(completedLessons);

        assertEquals(latestInComplete.getId(), lesson.getId());
    }

    @Test
    public void getLatestInCompleteLessonTest() {
        Lesson lesson1 = new Lesson();
        lesson1.setId(1);

        Lesson lesson2 = new Lesson();
        lesson2.setId(2);

        Lesson lesson3 = new Lesson();
        lesson3.setId(3);

        HashSet<Lesson> completedLessons = new HashSet<>();
        completedLessons.add(lesson1);
        completedLessons.add(lesson2);

        Mockito.when(lessonRepository.findLatestInComplete(lesson2.getId())).thenReturn(lesson3);

        Lesson latestInComplete = progressService.getLatestInCompleteLesson(completedLessons);

        assertEquals(latestInComplete.getId(), lesson3.getId());
    }

    @Test
    public void getLatestInCompleteQuizEmptySetTest() {
        Quiz quiz = new Quiz();
        quiz.setId(1L);

        Mockito.when(quizRepository.findLatestInComplete(any(Long.class))).thenReturn(quiz);

        HashSet<Quiz> completedQuizzes = new HashSet<>();
        Quiz latestInComplete = progressService.getLatestInCompleteQuiz(completedQuizzes);

        assertEquals(latestInComplete.getId(), quiz.getId());
    }

    @Test
    public void getLatestInCompleteQuizTest() {
        Quiz quiz1 = new Quiz();
        quiz1.setId(1L);

        Quiz quiz2 = new Quiz();
        quiz2.setId(2L);

        Quiz quiz3 = new Quiz();
        quiz3.setId(3L);

        HashSet<Quiz> completedQuizzes = new HashSet<>();
        completedQuizzes.add(quiz1);
        completedQuizzes.add(quiz2);

        Mockito.when(quizRepository.findLatestInComplete(quiz2.getId())).thenReturn(quiz3);

        Quiz latestInComplete = progressService.getLatestInCompleteQuiz(completedQuizzes);

        assertEquals(latestInComplete.getId(), quiz3.getId());
    }
}