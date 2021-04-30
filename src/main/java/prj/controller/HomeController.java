package prj.controller;

import prj.model.Topic;
import prj.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/*
 * The Home Controller handles the requests for viewing the home and dashboard pages.
 */
@Controller
public class HomeController {
    @Autowired
    private TopicRepository topicRepository;

    /*
     * Handle GET requests directed at the root "/" URL.
     * If a user is not logged in, then return the home view page.
     * If a user is logged in, then obtain all the topics from the database, add it to the model
     * and return the dashboard view page, which will present these topics.
     * @param user The logged in user. Null if no user is logged in.
     * @param model A Model object to be passed on to the view.
     * @return the correct view page depending on the user authentication.
     */
    @GetMapping("/")
    public String home(Principal user, Model model) {
        // If the user is null, return the default home page
        if (user == null)
            return "home";

        // If the user is not null, obtain all the topics in the database, to be displayed to the user
        List<Topic> topics = topicRepository.findAll();

        // Additionally, count the total number of lessons and quizzes, to be used for the progress bar
        int totalMaterial = 0;
        for (Topic topic : topics) {
            totalMaterial += topic.getLessons().size();
            if (topic.getQuiz() != null)
                totalMaterial += 1;
        }

        // Add the topics and the lesson count to the model, which can be accessed from the view
        model.addAttribute("topics", topics);
        model.addAttribute("totalMaterial", totalMaterial);

        // Return the dashboard view page
        return "dashboard";
    }

}
