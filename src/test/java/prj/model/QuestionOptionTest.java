package prj.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionOptionTest {

    @Test
    public void settersAndGettersTest() {
        QuestionOption questionOption = new QuestionOption();

        questionOption.setId(5L);

        Question question = new Question();
        questionOption.setQuestion(question);

        questionOption.setRightOption(false);
        questionOption.setOptionText("Test text");

        assertEquals(questionOption.getId(), 5);
        assertEquals(questionOption.getQuestion(), question);
        assertFalse(questionOption.getIsRightOption());
        assertEquals(questionOption.getOptionText(), "Test text");
    }

}