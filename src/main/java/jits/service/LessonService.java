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

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

@Service
public class LessonService {
    SolutionTracer solutionTracer;
    MisconceptionTracer misconceptionTracer;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    FeedbackModule feedbackModule;

    public void initialise(Problem problem) throws IOException {
        solutionTracer = new SolutionTracer();
        solutionTracer.initialise(problem);

        misconceptionTracer = new MisconceptionTracer();
        misconceptionTracer.initialise(problem);

    }

    @SuppressWarnings("deprecation")
    public Map<String, String> runCode(Problem problem, String code) {
        Map<String, String> compilation;
        Runner runner = new Runner(code);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Map<String, String>> future = executor.submit(runner);
        try {
            compilation = future.get(5, TimeUnit.SECONDS);
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
        } catch (TimeoutException | ExecutionException | InterruptedException ex) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for(Thread thread : threadSet){
                if(thread.getId()==runner.getThreadId()){
                        thread.stop();
                }
            }
//            future.cancel(true);
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            compilation = new HashMap<>();
            compilation.put("type", "error");
            compilation.put("message", "Program took too long to execute!");
        }

            return compilation;
    }

    public String getHint(Problem problem, String code) {
        if (misconceptionTracer.successfulTrace(misconceptionTracer.trace(problem, code)))
            return misconceptionTracer.getMisconception();

        return feedbackModule.generateFeedback(solutionTracer.trace(problem, code));
    }

    public String getSolution (Problem problem, String code) throws IOException{
        return solutionTracer.getSolution(problem, code);
    }
}
