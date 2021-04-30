package prj.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import prj.model.*;
import org.springframework.stereotype.Service;
import prj.userdetails.AppUserDetails;

import java.util.Comparator;
import java.util.Optional;

/*
 * The QuizService class defines a Spring Service.
 * This class implements useful methods for getting a correct answer of a question in the quiz and
 * redirecting the user to the latest incomplete lesson.
 */
@Service
public class QuizService {

    /*
     * This method obtains the correct option for a given question.
     * It iterates over all the options of the question and finds the one which is correct.
     * @param question The quiz for which to find the correct option.
     * @return the Id of the correct option, -1 if the question does not a correct option.
     */
    public long getCorrectAnswer(Question question) {
        for (QuestionOption questionOption : question.getQuestionOptions()) {
            if (questionOption.getIsRightOption())
                return questionOption.getId();
        }

        return -1;
    }

    /*
     * This method obtains the latest incomplete lesson in the topic that @param quiz is inside.
     * The redirected lesson will further redirect the user, if it is not the latest incomplete one overall.
     * @return the appropriate redirected lesson.
     */
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
