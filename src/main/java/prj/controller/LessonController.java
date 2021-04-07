package prj.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import prj.model.*;
import prj.repository.LessonRepository;

import prj.repository.UserRepository;
import prj.service.AppUserDetails;
import prj.service.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import prj.service.LessonsProgressService;

import java.io.IOException;
import java.util.*;

@Controller
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private LessonsProgressService lessonsProgressService;


    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);

        if (lesson == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((AppUserDetails) auth.getPrincipal()).getUser();
        Set<Lesson> completedLessons = student.getCompletedLessons();
        Set<Quiz> completedQuizzes = student.getCompletedQuizzes();
        Lesson latestInComplete = lessonsProgressService.getLatestInCompleteLesson(completedLessons);
        Quiz latestInCompleteQuiz = lessonsProgressService.getLatestInCompleteQuiz(completedQuizzes);

        if (latestInCompleteQuiz != null) {
            Topic latestInCompleteQuizTopic = latestInCompleteQuiz.getTopic();
            if(!student.getCompletedLessons().contains(lesson)) {
                Optional<Lesson> maxLatestCompleteInTopic = latestInCompleteQuizTopic.getLessons().stream().max(Comparator.comparing(Lesson::getId));
                if (maxLatestCompleteInTopic.isPresent() && maxLatestCompleteInTopic.get().getId() < latestInComplete.getId())
                    return "redirect:/quizzes/" + latestInCompleteQuiz.getId();
                if (lesson.getId() != latestInComplete.getId())
                    return "redirect:/lessons/" + latestInComplete.getId();
            }
        }

        lesson.getProblem().setProblemBody();
        if (lesson.getProblem().isSolutionRequired()) {
            try {
                lessonService.initialise(lesson.getProblem());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Topic currentTopic = lesson.getTopic();
        Optional<Lesson> lastLesson = currentTopic.getLessons().stream().max(Comparator.comparing(Lesson::getId));
        if (lastLesson.isPresent() && lesson.getId() == lastLesson.get().getId() && currentTopic.getQuiz() != null)
            model.addAttribute("next", "/quizzes/" + currentTopic.getQuiz().getId());
        else {
            Lesson nextLesson = lessonRepository.findById(lessonId + 1);
            if (nextLesson == null || !currentTopic.getLessons().contains(nextLesson))
                model.addAttribute("next", "/topics/" + currentTopic.getId());
            else
                model.addAttribute("next", "/lessons/" + nextLesson.getId());
        }

        Optional<Lesson> firstLesson = currentTopic.getLessons().stream().min(Comparator.comparing(Lesson::getId));
        Lesson prevLesson = lessonRepository.findById(lessonId - 1);
        if ((firstLesson.isPresent() && firstLesson.get().getId() == lessonId) || prevLesson == null)
            model.addAttribute("prev", null);
        else
            model.addAttribute("prev", "/lessons/" + prevLesson.getId());

        model.addAttribute("lesson", lesson);
        model.addAttribute("latestInComplete",latestInComplete);
        model.addAttribute("latestInCompleteQuiz", latestInCompleteQuiz);

        return "lessons/lesson";
    }

    @PostMapping("/lessons/run")
    @ResponseBody
    public Map<String, String> runLesson(@RequestParam int id, @RequestParam String code) {
        Problem problem = lessonRepository.findById(id).getProblem();
        Map <String, String> compilation = lessonService.runCode(problem, code);
        lessonRepository.save(problem.getLesson());
        return compilation;
    }

    @PostMapping("/lessons/hint")
    @ResponseBody
    public String getHint(@RequestParam int id, @RequestParam String code) {
        return lessonService.getHint(lessonRepository.findById(id).getProblem(), code);
    }

    @PostMapping("/lessons/solution")
    @ResponseBody
    public String showSolution(@RequestParam int id, @RequestParam String code) {
        try {
            return lessonService.getSolution(lessonRepository.findById(id).getProblem(), code);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";
    }

}
