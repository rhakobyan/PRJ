package jits.controller;

import jits.model.*;
import jits.repository.LessonRepository;

import jits.repository.QuizRepository;
import jits.repository.TopicRepository;
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
import java.util.stream.Collectors;

@Controller
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    TopicRepository topicRepository;
    @Autowired
    QuizRepository quizRepository;
    @Autowired
    private LessonService lessonService;


//    @GetMapping("/lessons/{lessonId}")
//    public String lesson(@PathVariable long lessonId, Model model) {
//        Lesson lesson = lessonRepository.findById(lessonId);
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
//        Set<Lesson> completedLessons = student.getCompletedLessons();
//
//        if (!completedLessons.contains(student)) {
//            Topic previousTopic = topicRepository.findById(lesson.getTopic().getId() - 1);
//            if (previousTopic != null) {
//                List<Lesson> topicLessons = completedLessons.stream().filter(l -> l.getTopic().getId() == previousTopic.getId()).collect(Collectors.toList());
//                Optional<Lesson> previousLatestComplete = topicLessons.stream().max(Comparator.comparing(Lesson::getId));
//                Quiz previousQuiz = previousTopic.getQuiz();
//                Lesson latestInComplete;
//                if (previousLatestComplete.isPresent())
//                    latestInComplete = lessonRepository.findLatestInComplete(previousLatestComplete.get().getId());
//                else {
//                    System.out.println("Not present...");
//                    latestInComplete = lessonRepository.findLatestInComplete(0);
//                }
//            }
//
//
//            List<Lesson> topicLessons = completedLessons.stream().filter(l -> l.getTopic().getId() == lesson.getTopic().getId()).collect(Collectors.toList());
//            Optional<Lesson> latestComplete = topicLessons.stream().max(Comparator.comparing(Lesson::getId));
//            Lesson latestInComplete;
//            if (latestComplete.isPresent())
//                latestInComplete = lessonRepository.findLatestInComplete(latestComplete.get().getId());
//            else {
//                System.out.println("Not present...");
//                latestInComplete = lessonRepository.findLatestInComplete(0);
//            }
//
//            lesson.getProblem().setProblemBody();
//            if (lesson.getProblem().isSolutionRequired()) {
//                try {
//                    lessonService.initialise(lesson.getProblem());
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//
//        model.addAttribute("prev", lessonRepository.findById(lessonId - 1));
//        model.addAttribute("next", lessonRepository.findById(lessonId + 1));
//        model.addAttribute("lesson", lesson);
//
//        return "lessons/lesson";
//    }

    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
        Set<Lesson> completedLessons = student.getCompletedLessons();
        Set<Quiz> completedQuizzes = student.getCompletedQuizzes();
        Optional<Lesson> latestComplete = completedLessons.stream().max(Comparator.comparing(Lesson::getId));
        Optional<Quiz> latestCompleteQuiz = completedQuizzes.stream().max(Comparator.comparing(Quiz::getId));
        Lesson latestInComplete;
        Quiz latestInCompleteQuiz;

        if (latestComplete.isPresent())
            latestInComplete = lessonRepository.findLatestInComplete(latestComplete.get().getId());
        else {
            System.out.println("Not present...");
            latestInComplete = lessonRepository.findLatestInComplete(0);
        }

        if (latestCompleteQuiz.isPresent())
            latestInCompleteQuiz = quizRepository.findLatestInComplete(latestCompleteQuiz.get().getId());
        else {
            System.out.println("Not present...");
            latestInCompleteQuiz = quizRepository.findLatestInComplete(0);
        }

        Topic latestInCompleteQuizTopic = latestInCompleteQuiz.getTopic();
        if(!student.getCompletedLessons().contains(lesson)) {
            Optional<Lesson> maxLatestCompleteInTopic = latestInCompleteQuizTopic.getLessons().stream().max(Comparator.comparing(Lesson::getId));
            if (maxLatestCompleteInTopic.isPresent() && maxLatestCompleteInTopic.get().getId() < latestInComplete.getId())
                return "redirect:/quizzes/" + latestInCompleteQuiz.getId();
            if (lesson.getId() != latestInComplete.getId())
                return "redirect:/lessons/" + latestInComplete.getId();
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
            if (nextLesson == null)
                model.addAttribute("next", null);
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
