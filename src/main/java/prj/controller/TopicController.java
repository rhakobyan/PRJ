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

@Controller
public class TopicController {
    @Autowired
    private TopicRepository topicRepository;
    @Autowired
    private ProgressService progressService;

    @GetMapping("/topics/{topicId}")
    public String topic(@PathVariable long topicId, Model model) {
        Topic topic = topicRepository.findById(topicId);
        if (topic == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Topic not found");
        model.addAttribute("topic", topic);
        int totalNumberOfMaterial = topic.getLessons().size();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User student = ((AppUserDetails) auth.getPrincipal()).getUser();
        int completedMaterialInTopic = (int) student.getCompletedLessons().stream()
                .filter(lesson -> lesson.getTopic().getId() == topic.getId()).count();
        if (topic.getQuiz() != null) {
            completedMaterialInTopic += student.getCompletedQuizzes().contains(topic.getQuiz()) ? 1 : 0;
            totalNumberOfMaterial += 1;
        }
        int completedPercentage = (completedMaterialInTopic * 100) / (totalNumberOfMaterial);
        model.addAttribute("completedPercentage", completedPercentage);
        model.addAttribute("latestInComplete", progressService.getLatestInCompleteLesson(student.getCompletedLessons()));
        model.addAttribute("latestInCompleteQuiz", progressService.getLatestInCompleteQuiz(student.getCompletedQuizzes()));
        return "topics/topic";
    }
}
