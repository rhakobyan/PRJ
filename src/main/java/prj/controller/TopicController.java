package prj.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.ui.Model;

import org.springframework.web.server.ResponseStatusException;
import prj.model.Topic;
import prj.model.User;
import prj.repository.TopicRepository;
import prj.userdetails.AppUserDetails;
import prj.service.ProgressService;

/*
 * The Home Controller handles the requests for viewing a topic page.
 */
@Controller
public class TopicController {

    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ProgressService progressService;

    /*
     * Handle GET requests direct at the /topics/{topicId} URL, where topicId is a placeholder
     * for an actual topic id, for example, /topics/2.
     * This method obtains the relevant topic from the database and passes it as a Model attribute to the view.
     * This method identifies the latestInComplete lesson (the next lesson to be completed by the user)
     * and passes it to the view as a Model attribute.
     * This method also calculates the percentage of the material in the topic that has been completed
     * and passes it as a Model attribute to the view.
     * @param topicId The id of the topic to obtain. Replaces {topicId}
     * @param model A Model object to be passed on to the view.
     * @return The topic view page.
     */
    @GetMapping("/topics/{topicId}")
    public String topic(@PathVariable long topicId, Model model) {
        Topic topic = topicRepository.findById(topicId);

        // If there is no such topic with the requested id, show a 404 error page
        if (topic == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");

        // Calculate the total number of material for the progress bar
        int totalNumberOfMaterial = topic.getLessons().size();
        // Obtain the currently authenticated user and calculate the number of completed lessons
        // in this topic.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((AppUserDetails) auth.getPrincipal()).getUser();
        int completedMaterialInTopic = (int) student.getCompletedLessons().stream()
                .filter(lesson -> lesson.getTopic().getId() == topic.getId()).count();

        // If there is a quiz in the topic, add it to to the total number of material in the topic
        if (topic.getQuiz() != null) {
            // If the topic is completed, add it to the completed material counter
            completedMaterialInTopic += student.getCompletedQuizzes().contains(topic.getQuiz()) ? 1 : 0;
            totalNumberOfMaterial += 1;
        }

        // Calculate the percentage of completed teaching material
        int completedPercentage = (completedMaterialInTopic * 100) / (totalNumberOfMaterial);

        model.addAttribute("topic", topic);
        model.addAttribute("completedPercentage", completedPercentage);
        // From the progress service obtain the latest incomplete lesson and quiz, and pass it to the view as a model attribute
        model.addAttribute("latestInComplete", progressService.getLatestInCompleteLesson(student.getCompletedLessons()));
        model.addAttribute("latestInCompleteQuiz", progressService.getLatestInCompleteQuiz(student.getCompletedQuizzes()));

        // Return the topic view page
        return "topics/topic";
    }
}
