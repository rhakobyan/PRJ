package prj.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import prj.model.Question;
import prj.model.QuestionOption;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class QuizServiceGetCorrectAnswerTest {

    @Autowired
    private QuizService quizService;

    private Question question;
    private QuestionOption questionOption2;

    @BeforeEach
    public void setUp() {
        question = new Question();
        question.setQuestion("What is 1 + 1?");

        QuestionOption questionOption1 = new QuestionOption();
        questionOption1.setId(1L);
        questionOption1.setQuestion(question);
        questionOption1.setOptionText("1");
        questionOption1.setRightOption(false);

        questionOption2 = new QuestionOption();
        questionOption2.setId(2L);
        questionOption2.setQuestion(question);
        questionOption2.setOptionText("2");
        questionOption2.setRightOption(true);

        List<QuestionOption> questionOptionList = new LinkedList<>();
        questionOptionList.add(questionOption1);
        questionOptionList.add(questionOption2);
        question.setQuestionOptions(questionOptionList);

    }

    @Test
    public void getCorrectAnswerTest() {
        long correctAnswer = quizService.getCorrectAnswer(question);

        assertEquals(correctAnswer, questionOption2.getId());
    }

    @Test
    public void getCorrectAnswerWithoutRightOptionTest() {
        questionOption2.setRightOption(false);

        long correctAnswer = quizService.getCorrectAnswer(question);

        assertEquals(correctAnswer, -1);
    }
}
