package prj.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import prj.model.*;
import org.springframework.stereotype.Service;
import prj.userdetails.AppUserDetails;

import java.util.Comparator;
import java.util.Optional;

@Service
public class QuizService {

    public long getCorrectAnswer(Question question) {
        for (QuestionOption questionOption : question.getQuestionOptions()) {
            if (questionOption.getIsRightOption())
                return questionOption.getId();
        }

        return -1;
    }

    public Lesson redirectedLesson(Quiz quiz) {
        Topic topic = quiz.getTopic();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((AppUserDetails) auth.getPrincipal()).getUser();
        Optional<Lesson> lastLesson = topic.getLessons().stream().max(Comparator.comparing(Lesson::getId));

        if (lastLesson.isPresent() && !student.getCompletedLessons().contains(lastLesson.get())) {
            return lastLesson.get();
        }

        return null;
    }
}
