package jits.controller;

import jits.model.Lesson;
import jits.model.Problem;
import jits.model.User;
import jits.repository.LessonRepository;

import jits.repository.UserRepository;
import jits.service.JITSUserDetails;
import jits.service.LessonService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
        Set<Lesson> completedLessons = student.getCompletedLessons();
        Optional<Lesson> latestComplete = completedLessons.stream().max(Comparator.comparing(Lesson::getId));
        Lesson latestInComplete;
        if (latestComplete.isPresent())
            latestInComplete = lessonRepository.findLatestInComplete(latestComplete.get().getId());
        else {
            System.out.println("Not present...");
            latestInComplete = lessonRepository.findLatestInComplete(0);
        }
        System.out.println(student.getCompletedLessons().contains(lesson));
        if(!student.getCompletedLessons().contains(lesson) && lesson.getId() != latestInComplete.getId())
            return "redirect:/lessons/" + latestInComplete.getId();

        lesson.getProblem().setProblemBody();
        if (lesson.getProblem().isSolutionRequired()) {
            try {
                lessonService.initialise(lesson.getProblem());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        model.addAttribute("prev", lessonRepository.findById(lessonId - 1));
        model.addAttribute("next", lessonRepository.findById(lessonId + 1));
        model.addAttribute("lesson", lesson);

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
