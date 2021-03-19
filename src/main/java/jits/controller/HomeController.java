package jits.controller;

import jits.model.Topic;
import jits.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private TopicRepository topicRepository;

    @GetMapping("/")
    public String home(Principal user, Model model) {
        if (user == null)
            return "home";

        List<Topic> topics = topicRepository.findAll();
        int totalLessons = 0;
        for (Topic topic : topics)
            totalLessons += topic.getLessons().size();

        model.addAttribute("topics", topics);
        model.addAttribute("totalLessons", totalLessons);
        return "dashboard";
    }

}
