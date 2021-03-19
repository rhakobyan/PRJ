package jits.service;

import jits.antlr.JavaASTNode;
import jits.model.Problem;
import jits.model.User;
import jits.repository.LessonRepository;
import jits.repository.UserRepository;
import jits.studentmodel.FeedbackModule;
import jits.studentmodel.MisconceptionTracer;
import jits.studentmodel.Runner;
import jits.studentmodel.SolutionTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class LessonService {
    SolutionTracer solutionTracer;
    MisconceptionTracer misconceptionTracer;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;

    public void initialise(Problem problem) throws IOException {
        solutionTracer = new SolutionTracer();
        solutionTracer.initialise(problem);

        misconceptionTracer = new MisconceptionTracer();
        misconceptionTracer.initialise(problem);

    }

    public Map<String, String> runCode(Problem problem, String code) {
        Map<String, String> compilation = Runner.run(code);
        if (!compilation.get("type").equals("success"))
            return compilation;

        if (!problem.isSolutionRequired() ||
                (problem.isSolutionRequired() && solutionTracer.successfulTrace(solutionTracer.trace(problem, code)))) {
            compilation.put("solved", "true");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User student = ((JITSUserDetails) auth.getPrincipal()).getUser();
            System.out.println(problem.getLesson().getId());
            student.addCompletedLesson(problem.getLesson());
            problem.getLesson().addStudentsCompleted(student);
            lessonRepository.save(problem.getLesson());
            userRepository.save(student);
        } else {
            compilation.put("solved", "false");
        }

        return compilation;
    }

    public String getHint(Problem problem, String code) {
        if (misconceptionTracer.successfulTrace(misconceptionTracer.trace(problem, code)))
            return misconceptionTracer.getMisconception();

        return FeedbackModule.generateFeedback(solutionTracer.trace(problem, code));
    }

    public String getSolution (Problem problem, String code) throws IOException{
        return solutionTracer.getSolution(problem, code);
    }
}
