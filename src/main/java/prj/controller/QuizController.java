package prj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import prj.model.*;
import prj.repository.QuestionRepository;
import prj.repository.QuizRepository;
import prj.repository.UserRepository;
import prj.userdetails.AppUserDetails;
import prj.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;

/*
 * The Quiz Controller handles the requests related to the quiz page.
 * It handles the requests for viewing the quiz page, submitting a quiz
 * and evaluating it, and presenting the evaluation results to the view.
 */
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

    /*
     * Handle GET requests direct at the /quizzes/{quizId} URL, where quizId is a placeholder
     * for an actual quiz id, for example, /quizzes/2.
     * This method obtains the relevant quiz from the database and passes it as a Model attribute to the view.
     * This method directs to the the relevant quiz page only if that quiz has been already completed
     * or it is the current one to be completed (latest incomplete). All other quizzes are redirected
     * to the current lesson or quiz (if quiz is the next learning material to complete).
     * @param quizId The id of the quiz to obtain. Replaces the {quizId}.
     * @param model A Model object to be passed on to the view.
     * @return The quiz view page.
     */
    @GetMapping("/quizzes/{quizId}")
    public String quiz(@PathVariable long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId);

        // If there is no such quiz with the requested id, show a 404 error page
        if (quiz == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");

        // If the current quiz is not in the order of completion, redirect the user to the latest incomplete lesson
        // (the redirected lesson will further redirect the user, if it is not the latest incomplete one)
        Lesson redirectedLesson = quizService.redirectedLesson(quiz);
        if (redirectedLesson != null)
            return "redirect:/lessons/" + redirectedLesson.getId();

        // Add model a attributes
        model.addAttribute("quiz", quiz);

        // return the quiz view page
        return "quizzes/quiz";
    }

    /*
     * Handle GET requests directed at viewing an attempt page of a quiz.
     * This method directs to the the relevant quiz attempt page only if that quiz has been already completed
     * or it is the current one to be completed (latest incomplete). All other quizzes are redirected
     * to the current lesson or quiz (if quiz is the next learning material to complete).
     * @param quizId The id of the quiz to obtain. Replaces the {quizId}.
     * @param model A Model object to be passed on to the view.
     * @return The attempt quiz view page.
     */
    @GetMapping("/quizzes/{quizId}/attempt")
    public String attempt(@PathVariable long quizId, Model model) {
        Quiz quiz = quizRepository.findById(quizId);

        // If there is no such quiz with the requested id, show a 404 error page
        if (quiz == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");

        // If the current quiz is not in the order of completion, redirect the user to the latest incomplete lesson
        // (the redirected lesson will further redirect the user, if it is not the latest incomplete one)
        Lesson redirectedLesson = quizService.redirectedLesson(quiz);
        if (redirectedLesson != null)
            return "redirect:/lessons/" + redirectedLesson.getId();

        // Add model a attribute
        model.addAttribute("quiz", quiz);

        // return the quiz attempt view page
        return "quizzes/attempt";
    }

    /*
     * Handle POST requests for evaluating the user submitted quiz answers.
     * This method iterates through the question options in the quiz and checks if they are the correct ones.
     * An overall score of correct answers is obtained which is compared with the passing percentage of the quiz.
     * If the score is greater than or equal to the passing mark the quiz is added to the authenticated user's
     * completed quizzes and saved in the repository.
     * @param quizId The id of the quiz that is being evaluated.
     * @param request ServletRequest containing the question options selected by the user.
     * @param model A Model object to be passed on to the view.
     * @return The evaluate quiz view page.
     */
    @PostMapping("/quizzes/{quizId}/evaluate")
    public String evaluate(@PathVariable long quizId, HttpServletRequest request, Model model) {
        Quiz quiz = quizRepository.findById(quizId);

        // If there is no such quiz with the requested id, show a 404 error page
        if (quiz == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz not found");

        // If the user tries to send a POST request to a quiz that is not the latest incomplete learning material,
        // than a 405 error page is displayed
        if (quizService.redirectedLesson(quiz) != null)
            throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED);

        // Obtain the questions from the POST parameters
        String[] questionIds = request.getParameterValues("question");
        HashSet<Long> answers = new HashSet<>();
        int score = 0;

        // If there are no present, then the POST request is badly constructed, so display a 400 error page
        if (questionIds == null)
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST);

        // Iterate through the questions and check if the question option selected by the user is the correct answer
        for (String questionId : questionIds) {
            Question question = questionRepository.findById(Long.parseLong(questionId));
            long answer;
            try {
                answer = Long.parseLong(request.getParameter("quiz-option-" + questionId));
            } catch (NumberFormatException ex) {
                answer = -1;
            }
            answers.add(answer);

            // If the answer is correct then increment the score
            if (quizService.getCorrectAnswer(question) == answer)
                score++;
        }

        // Calculate the percentage of the correct answers and determine if the user has passed the quiz
        double result = (double) ((score * 100) / quiz.getQuestions().size());
        boolean passed = result >= quiz.getPassPercent();

        // If the user has passed the quiz then add it to the user's completed quizzes
        if (passed) {
            // Obtain the current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User student = ((AppUserDetails) auth.getPrincipal()).getUser();

            quiz.addStudentsCompleted(student);
            student.addCompletedQuiz(quiz);
            quizRepository.save(quiz);
            userRepository.save(student);
        }

        // Add model attributes
        model.addAttribute("quiz", quiz);
        model.addAttribute("score", score);
        model.addAttribute("passed", passed);
        model.addAttribute("answers",answers);

        // return the quiz evaluate view page
        return "quizzes/evaluate";
    }
}
