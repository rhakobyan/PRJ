package prj.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import prj.model.Lesson;
import prj.model.Quiz;
import prj.repository.LessonRepository;
import prj.repository.QuizRepository;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

/*
 * The ProgressService class defines a Spring Service.
 * This class implements useful methods for getting the latest incomplete lesson and or quiz in a give set.
 */
@Service
public class ProgressService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private QuizRepository quizRepository;

    /*
     * Given a set of lessons that are marked as complete for a given user, this method returns the first
     * lessons that is not in the given set.
     * It is important to note, that in a normal working environment of an application, there may be tables
     * that skip some ID values for their records. For example, they may be records with IDs 1,2,3 and then
     * the next record may have an ID of 5. This is due the fact that some records may be deleted.
     * Taking this issue into consideration, it is not correct to say that the record with id + 1 of the
     * latest completed lesson is the next one to completed and that is why the approach below is used.
     * @param completedLessons The set of completed lessons.
     * @return the latest (first) lessons that is not in the completedLessons set.
     */
    public Lesson getLatestInCompleteLesson(Set<Lesson> completedLessons) {
        Lesson latestInComplete;
        // Get the the lesson with the largest ID in the set
        Optional<Lesson> latestComplete = completedLessons.stream().max(Comparator.comparing(Lesson::getId));

        /* If there is a record with the largest ID, then find the next record in database that
        has not been completed yet
        If there is no record with the largest ID (could be because the set is empty)
        then return the first lesson (latest incomplete after ID of 0) */
        if (latestComplete.isPresent())
            latestInComplete = lessonRepository.findLatestInComplete(latestComplete.get().getId());
        else
            latestInComplete = lessonRepository.findLatestInComplete(0);

        return latestInComplete;
    }

    /*
     * Given a set of quizzes that are marked as complete for a given user, this method returns the first
     * quiz that is not in the given set.
     * It is important to note, that in a normal working environment of an application, there may be tables
     * that skip some ID values for their records. For example, they may be records with IDs 1,2,3 and then
     * the next record may have an ID of 5. This is due the fact that some records may be deleted.
     * Taking this issue into consideration, it is not correct to say that the record with id + 1 of the
     * latest completed quiz is the next one to completed and that is why the approach below is used.
     * @param completedQuizzes The set of completed quizzes.
     * @return the latest (first) quiz that is not in the completedQuizzes set.
     */
    public Quiz getLatestInCompleteQuiz(Set<Quiz> completedQuizzes) {
        Quiz latestInCompleteQuiz;
        // Get the the quiz with the largest ID in the set
        Optional<Quiz> latestCompleteQuiz = completedQuizzes.stream().max(Comparator.comparing(Quiz::getId));

        /* If there is a record with the largest ID, then find the next record in database that
        has not been completed yet
        If there is no record with the largest ID (could be because the set is empty)
        then return the first quiz (latest incomplete after ID of 0) */
        if (latestCompleteQuiz.isPresent())
            latestInCompleteQuiz = quizRepository.findLatestInComplete(latestCompleteQuiz.get().getId());
        else
            latestInCompleteQuiz = quizRepository.findLatestInComplete(0);

        return latestInCompleteQuiz;
    }
}
