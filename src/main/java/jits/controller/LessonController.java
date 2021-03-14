package jits.controller;

import jits.model.Lesson;
import jits.repository.LessonRepository;
import jits.service.ExerciseCompiler;

import jits.service.ProblemTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@Controller
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private ExerciseCompiler exerciseCompiler;
    @Autowired
    private ProblemTracer problemTracer;

    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);
        lesson.getProblem().setProblemBody();
        try {
            problemTracer.initialise(lesson.getProblem());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        model.addAttribute("lesson", lesson);

        return "lessons/lesson";
    }

    @PostMapping("/lessons/run")
    @ResponseBody
    public Map<String, String> runLesson(@RequestParam String code) {
        return exerciseCompiler.runCode(code);
    }

    @PostMapping("/lessons/hint")
    @ResponseBody
    public String getHint(@RequestParam int id, @RequestParam String code) {
        return problemTracer.getHint(lessonRepository.findById(id).getProblem(), code);
    }

}
