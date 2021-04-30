package prj.service;

import prj.model.Problem;
import prj.model.User;
import prj.repository.LessonRepository;
import prj.repository.UserRepository;
import prj.studentmodel.FeedbackModule;
import prj.studentmodel.MisconceptionTracer;
import prj.runner.Runner;
import prj.studentmodel.SolutionTracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import prj.userdetails.AppUserDetails;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.*;

/*
 * The LessonService class defines a Spring service and is used for handling important lesson
 * related logic, encapsulating them away from the controller.
 */
@Service
public class LessonService {

    // Traces the code provided by the user and say at which point the user is stuck.
    private SolutionTracer solutionTracer;
    // Traces the code provided by the user to see if the user has produced any misconceptions.
    private MisconceptionTracer misconceptionTracer;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private FeedbackModule feedbackModule;

    /*
     * The initialise method initialises the tracer objects.
     * The tracers are initialised once, to avoid the need of initialising them at every method call.
     * @param problem The problem object for which to initialise the tracer.
     */
    public void initialise(Problem problem) throws IOException {
        solutionTracer = new SolutionTracer();
        solutionTracer.initialise(problem);

        misconceptionTracer = new MisconceptionTracer();
        misconceptionTracer.initialise(problem);

    }

    /*
     * This method run the user provided code.
     * It also checks if the code provided solves the given problem and if it does, marks the lesson as solved.
     * This method produces an Map which contains information about the status of the code run.
     * It contains information if the code has been successfully compiler, if it solves the given problem,
     * its compilation errors if such exist and a custom message.
     * The actual logic for running the code is in the Runner class.
     * This methods start the code execution process in a different thread and kills it if it has run
     * for too long, in order to avoid infinite loops.
     * @param problem The problem for which to run the code.
     * @param code The code to be compiled and executed.
     * @return A map containing the running status of the code.
     */
    @SuppressWarnings("deprecation")
    public Map<String, String> runCode(Problem problem, String code) {
        Map<String, String> compilation;
        Runner runner = new Runner(code);

        // Start an new thread for the execution of the code
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Map<String, String>> future = executor.submit(runner);
        try {
            // Limit the execution time to 5 seconds, more than that will produce a Timeout or Interrupted Exception.
            compilation = future.get(5, TimeUnit.SECONDS);
            if (!compilation.get("type").equals("success"))
                return compilation;

            // If no solution is required for solving the given problem, then mark it as solved by default.
            // If a solution is required and the solutionTracer has successfully traced the code with the model solution
            // and seen that this code matches them, the mark it as solved as well.
            if (!problem.isSolutionRequired() ||
                    (problem.isSolutionRequired() && solutionTracer.successfulTrace(solutionTracer.trace(problem, code)))) {
                compilation.put("solved", "true");
                // Obtain the current student and add the current lesson to its solved lessons list
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User student = ((AppUserDetails) auth.getPrincipal()).getUser();

                student.addCompletedLesson(problem.getLesson());
                problem.getLesson().addStudentsCompleted(student);

                lessonRepository.save(problem.getLesson());
                userRepository.save(student);
            } else { // Otherwise mark the code as not solved.
                compilation.put("solved", "false");
            }
            // If the code has run for too long, or the execution thread was interrupted, stop that thread
        } catch (TimeoutException | ExecutionException | InterruptedException ex) {
            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            // Obtain the thread with runner thread id from the thread set and stop it
            for(Thread thread : threadSet) {
                if(thread.getId() == runner.getThreadId()) {
                        thread.stop();
                }
            }
            // Reset the output stream, as during execution, the results of output are written in an object
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
            compilation = new HashMap<>();
            // Set the compilation type to error and add the appropriate error message
            compilation.put("type", "error");
            compilation.put("message", "Program took too long to execute!");
        }

            return compilation;
    }

    /*
     * This method is used for obtaining a hint for solving a problem.
     * The hint is obtained by using misconception and solution tracer objects.
     * These object will correctly trace the student code against the model solutions and
     * obtain the correct divergence point.
     * Firstly the code is checked for misconception hints as these are more specific and after that
     * automatic hints are generated using the feedback module and the solution tracer.
     * @param problem The problem for which to generate a hint.
     * @param code The user code for generating an appropriate hint at that stage.
     * @return The hint message as a String.
     */
    public String getHint(Problem problem, String code) {
        try {
            // If no solution is required, then simply running the problem will solve it
            if (!problem.isSolutionRequired())
                return "Try running your code!";

            // Firstly check the code for misconception hints.
            // If the misconceptionTracer determines that the user code fully matches the
            // one of the problem misconceptions, then the relevant hint is displayed
            if (misconceptionTracer.successfulTrace(misconceptionTracer.trace(problem, code)))
                return misconceptionTracer.getMisconception();

            // Secondly, use the solution tracer to obtain the point at which to generate the hint
            // Pass this point to the feedback (expert) module which will generate appropriate hint
            // at that point.
            return feedbackModule.generateFeedback(solutionTracer.trace(problem, code));
        } catch (NullPointerException ex) {
            return "<p class='text-danger'>Internal Server Error</p>";
        }
    }

    /*
     * This method returns one of the model solutions of the problem.
     * As there are multiple model solutions, the solution tracer determines which of the solutions
     * resembles the student code the most and returns that solution.
     * @param problem The problem for which to obtain a solution.
     * @param code The user code for returning the appropriate solution.
     * @return An appropriate solution of the problem.
     */
    public String getSolution (Problem problem, String code) throws IOException {
        return solutionTracer.getSolution(problem, code);
    }
}
