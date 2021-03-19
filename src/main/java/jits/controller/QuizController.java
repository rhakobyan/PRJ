package jits.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class QuizController {

    @GetMapping("/quizzes/{quizId}")
    public String quiz(@PathVariable long id, Model model) {
        return "";
    }
}
