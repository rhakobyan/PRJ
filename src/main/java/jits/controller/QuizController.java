package jits.controller;

import jits.model.*;
import jits.repository.QuestionRepository;
import jits.repository.QuizRepository;
import jits.repository.UserRepository;
import jits.service.JITSUserDetails;
import jits.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;

@Controller
public class QuizController {
    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuizService quizService;

    @GetMapping("/quizzes/{quizId}")
    public String quiz(@PathVariable long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId);
        Lesson redirectedLesson = redirectedLesson(quiz);
        if (redirectedLesson != null)
            return "redirect:/lessons/" + redirectedLesson.getId();

        model.addAttribute("quiz", quiz);
        return "quizzes/quiz";
    }

    @GetMapping("/quizzes/{quizId}/attempt")
    public String attempt(@PathVariable long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId);
        Lesson redirectedLesson = redirectedLesson(quiz);
        if (redirectedLesson != null)
            return "redirect:/lessons/" + redirectedLesson.getId();

        model.addAttribute("quiz", quiz);
        return "quizzes/attempt";
    }

    @PostMapping("/quizzes/{quizId}/evaluate")
    public String evaluate(@PathVariable long quizId, HttpServletRequest request, Model model) {
        Quiz quiz = quizRepository.findById(quizId);
        String[] questionIds = request.getParameterValues("question");
        HashSet<Long> answers = new HashSet<>();
        int score = 0;
        for (String questionId : questionIds) {
            Question question = questionRepository.findById(Long.parseLong(questionId));
            System.out.println((request.getParameter("quiz-option-" + questionId)));
            long answer = Long.parseLong(request.getParameter("quiz-option-" + questionId));
            answers.add(answer);
            if (quizService.getCorrectAnswer(question) == answer)
                score++;
        }

        double result = (double) ((score * 100) / quiz.getQuestions().size());
        boolean passed = result >= quiz.getPassPercent();

        if (passed) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
            quiz.addStudentsCompleted(student);
            student.addCompletedQuiz(quiz);
            quizRepository.save(quiz);
            userRepository.save(student);
        }


        model.addAttribute("quiz", quiz);
        model.addAttribute("score", score);
        model.addAttribute("passed", passed);
        model.addAttribute("answers",answers);

        return "quizzes/evaluate";
    }

    private Lesson redirectedLesson(Quiz quiz) {
        Topic topic = quiz.getTopic();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
        Optional<Lesson> lastLesson = topic.getLessons().stream().max(Comparator.comparing(Lesson::getId));

         if (lastLesson.isPresent() && !student.getCompletedLessons().contains(lastLesson.get())) {
             return lastLesson.get();
         }

         return null;
    }
}
