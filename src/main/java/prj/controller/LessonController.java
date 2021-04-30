package prj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import prj.model.*;
import prj.repository.LessonRepository;

import prj.repository.UserRepository;
import prj.userdetails.AppUserDetails;
import prj.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prj.service.ProgressService;

import java.io.IOException;
import java.util.*;

/*
 * The Lesson Controller handles the requests related to the lesson page.
 * It handles the requests for viewing the lesson page, running a code in the lesson page,
 * requesting a hint and showing a solution to the task of the lesson.
 */
@Controller
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private ProgressService progressService;

    /*
     * Handle GET requests direct at the /lessons/{lessonId} URL, where lessonId is a placeholder
     * for an actual lesson id, for example, /lessons/4.
     * This method obtains the relevant lesson from the database and passes it as a Model attribute to the view.
     * This method directs to the the relevant lesson page only if that lesson has been already completed
     * or it is the current one to be completed (latest incomplete). All other lessons are redirected
     * to the current lesson or quiz (if quiz is the next learning material to complete).
     * Additionally, this method initialises the hinting mechanism of the lessonService, which will
     * be used for obtaining hints, correct solutions and checking if the user code is correct.
     * Finally, this method sets up previous and next buttons for the page and passes them to the view.
     * @param lessonId The id of the lesson to obtain. Replaces the {lessonId}.
     * @param model A Model object to be passed on to the view.
     * @return The lesson view page.
     */
    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);

        // If there is no such lesson with the requested id, show a 404 error page
        if (lesson == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");

        // Obtain the current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((AppUserDetails) auth.getPrincipal()).getUser();

        Set<Lesson> completedLessons = student.getCompletedLessons();
        Set<Quiz> completedQuizzes = student.getCompletedQuizzes();
        Lesson latestInComplete = progressService.getLatestInCompleteLesson(completedLessons);
        Quiz latestInCompleteQuiz = progressService.getLatestInCompleteQuiz(completedQuizzes);

        if (latestInCompleteQuiz != null) {
            Topic latestInCompleteQuizTopic = latestInCompleteQuiz.getTopic();
            // If the current lesson has not been completed and it is not the one in order of
            // of the learning materials (lesson, quiz) to be completed next, then redirect
            // the user to the one learning material they need to complete first.
            if(!student.getCompletedLessons().contains(lesson)) {
                Optional<Lesson> maxLatestCompleteInTopic = latestInCompleteQuizTopic.getLessons().stream().max(Comparator.comparing(Lesson::getId));
                if (maxLatestCompleteInTopic.isPresent() && maxLatestCompleteInTopic.get().getId() < latestInComplete.getId())
                    return "redirect:/quizzes/" + latestInCompleteQuiz.getId();
                if (lesson.getId() != latestInComplete.getId())
                    return "redirect:/lessons/" + latestInComplete.getId();
            }
        }

        lesson.getProblem().setProblemBody();
        // Initialise the Tracers and Abstract Syntax Trees of the lesson's problem.
        // We initialise them once here to avoid the need of currently building the same
        // model solution ASTs when a hint or solution is requested.
        if (lesson.getProblem().isSolutionRequired()) {
            try {
                lessonService.initialise(lesson.getProblem());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        // Set up the previous and next buttons in the topic page, so users can easily navigate by clicking them
        Topic currentTopic = lesson.getTopic();
        Optional<Lesson> lastLesson = currentTopic.getLessons().stream().max(Comparator.comparing(Lesson::getId));
        // If the lesson is the last one in its topic, and the there is a quiz in the lesson, redirect the
        // user to the quiz. (Quizzes always come as the last items of topics)
        if (lastLesson.isPresent() && lesson.getId() == lastLesson.get().getId() && currentTopic.getQuiz() != null)
            model.addAttribute("next", "/quizzes/" + currentTopic.getQuiz().getId());
        else {
            Lesson nextLesson = lessonRepository.findById(lessonId + 1);
            // If there is no next lesson or quiz, then the next button will direct the user to its (now fully completed) topic
            if (nextLesson == null || !currentTopic.getLessons().contains(nextLesson))
                model.addAttribute("next", "/topics/" + currentTopic.getId());
            else // Otherwise, the next button points to the next lesson
                model.addAttribute("next", "/lessons/" + nextLesson.getId());
        }

        Optional<Lesson> firstLesson = currentTopic.getLessons().stream().min(Comparator.comparing(Lesson::getId));
        Lesson prevLesson = lessonRepository.findById(lessonId - 1);
        // If the current lesson is the first one in its topic, then the prev button should point to nothing
        if ((firstLesson.isPresent() && firstLesson.get().getId() == lessonId) || prevLesson == null)
            model.addAttribute("prev", null);
        else // Otherwise it points to the previous lesson
            model.addAttribute("prev", "/lessons/" + prevLesson.getId());

        // Add model attributes
        model.addAttribute("lesson", lesson);
        model.addAttribute("latestInComplete",latestInComplete);
        model.addAttribute("latestInCompleteQuiz", latestInCompleteQuiz);

        // Return the lesson view page
        return "lessons/lesson";
    }

    /*
     * Handle REST POST request for running the code written by the user in the lessons page.
     * This method runs the code by calling the runCode method of the lesson service.
     * The results of the compilation, which include the console message, compilation errors
     * and so on are returned by the runCode method and stored in compilation Map.
     * The run results are then returned back to requester.
     * Note that this is a REST method Handler and does not return a view, but rather JSON.
     * Maps are translated to JSON objects in the receiving end.
     * @param id The id of the lesson for whose problem to run the code.
     * @param code The user code that needs running.
     * @return The resulting messages of running the code.
     */
    @PostMapping("/lessons/run")
    @ResponseBody
    public Map<String, String> runLesson(@RequestParam int id, @RequestParam String code) {
        try {
            // Obtain the problem of the lesson
            Problem problem = lessonRepository.findById(id).getProblem();
            // Run the code and get obtain a map with information about the results of running the code
            Map <String, String> result = lessonService.runCode(problem, code);
            // If the code that was run solves the problem successfully it will be marked as such in the lessonService.
            // We save here the lesson object just in case it gets correctly solved.
            lessonRepository.save(problem.getLesson());
            return result;
            // If there are Null pointer errors when running the code send a Bad Request code to the requester.
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    /*
     * Handle REST POST request for requesting hints for solving a problem in a lesson.
     * The hints are obtained using the lessonService and returned back to the requester as Strings.
     * Note that this is a REST method Handler and does not return a view, but rather JSON.
     * @param id The id of the lesson for whose problem to request a hint.
     * @param code The current state of the user. Needed for generating an appropriate hint.
     * @return The hint message as a String.
     */
    @PostMapping("/lessons/hint")
    @ResponseBody
    public String getHint(@RequestParam int id, @RequestParam String code) {
        try {
            return lessonService.getHint(lessonRepository.findById(id).getProblem(), code);
        } catch (NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    /*
     * Handle REST POST request for requesting model solutions for a problem in a lesson.
     * The solutions are obtained using the lessonService and returned back to the requester as Strings.
     * Note that this is a REST method Handler and does not return a view, but rather JSON.
     * @param id The id of the lesson for whose problem to give the solution.
     * @param code The current state of the user. Needed for obtaining the appropriate model solution.
     * @return The model solution as a String.
     */
    @PostMapping("/lessons/solution")
    @ResponseBody
    public String showSolution(@RequestParam int id, @RequestParam String code) {
        try {
            return lessonService.getSolution(lessonRepository.findById(id).getProblem(), code);
        } catch (IOException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
