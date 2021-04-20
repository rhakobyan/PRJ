package prj.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {

    @Test
    public void settersAndGettersTest() {
        Question question = new Question();

        question.setId(1L);
        question.setQuestion("Test question");

        Quiz quiz = new Quiz();
        quiz.setId(1L);
        question.setQuiz(quiz);

        ArrayList<QuestionOption> questionOptions = new ArrayList<>();
        question.setQuestionOptions(questionOptions);

        assertEquals(question.getId(), 1);
        assertEquals(question.getQuestion(), "Test question");
        assertEquals(question.getQuiz(), quiz);
        assertEquals(question.getQuestionOptions(), questionOptions);
    }

}