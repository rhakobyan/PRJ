package jits.controller;

import jits.model.Lesson;
import jits.repository.LessonRepository;
import jits.service.ExerciseCompiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class LessonController {
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private ExerciseCompiler exerciseCompiler;

    @GetMapping("/lessons/{lessonId}")
    public String lesson(@PathVariable long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);
        model.addAttribute("lesson", lesson);

        return "lessons/lesson";
    }

    @PostMapping("/lessons/run")
    @ResponseBody
    public Map<String, String> runLesson(@RequestParam String code) {
        return exerciseCompiler.runCode(code);
    }
}
