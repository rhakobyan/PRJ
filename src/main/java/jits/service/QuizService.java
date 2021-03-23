package jits.service;

import jits.model.Question;
import jits.model.QuestionOption;
import org.springframework.stereotype.Service;

@Service
public class QuizService {

    public long getCorrectAnswer(Question question) {
        for (QuestionOption questionOption : question.getQuestionOptions()) {
            if (questionOption.getIsRightOption())
                return questionOption.getId();
        }

        return -1;
    }
}
