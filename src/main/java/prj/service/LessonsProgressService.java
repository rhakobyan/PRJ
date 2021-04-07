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

@Service
public class LessonsProgressService {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private QuizRepository quizRepository;

    public Lesson getLatestInCompleteLesson(Set<Lesson> completedLessons) {
        Lesson latestInComplete;
        Optional<Lesson> latestComplete = completedLessons.stream().max(Comparator.comparing(Lesson::getId));
        if (latestComplete.isPresent())
            latestInComplete = lessonRepository.findLatestInComplete(latestComplete.get().getId());
        else
            latestInComplete = lessonRepository.findLatestInComplete(0);

        return latestInComplete;
    }

    public Quiz getLatestInCompleteQuiz(Set<Quiz> completedQuizzes) {
        Quiz latestInCompleteQuiz;
        Optional<Quiz> latestCompleteQuiz = completedQuizzes.stream().max(Comparator.comparing(Quiz::getId));

        if (latestCompleteQuiz.isPresent())
            latestInCompleteQuiz = quizRepository.findLatestInComplete(latestCompleteQuiz.get().getId());
        else
            latestInCompleteQuiz = quizRepository.findLatestInComplete(0);

        return latestInCompleteQuiz;
    }
}
