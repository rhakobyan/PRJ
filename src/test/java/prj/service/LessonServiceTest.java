package prj.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import prj.ast.JavaASTNode;
import prj.model.Lesson;
import prj.model.Problem;
import prj.model.User;
import prj.repository.LessonRepository;
import prj.repository.UserRepository;
import prj.studentmodel.FeedbackModule;
import prj.studentmodel.MisconceptionTracer;
import prj.studentmodel.SolutionTracer;
import prj.userdetails.AppUserDetails;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class LessonServiceTest {
    @MockBean
    private SolutionTracer solutionTracer;
    @MockBean
    private MisconceptionTracer misconceptionTracer;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private LessonRepository lessonRepository;
    @MockBean
    private FeedbackModule feedbackModule;

    @InjectMocks
    private LessonService lessonService;

    @Test
    public void runCodeCompilationErrorTest() {
        Map<String, String> output = lessonService.runCode(new Problem(), "compilation-error");

        assertEquals(output.get("type"), "error");
    }

    @Test
    public void runCodeSuccessfulCompilationTest() {
        Authentication authentication = Mockito.mock(Authentication.class);
        User user = new User();
        user.setCompletedLessons(new HashSet<>());
        Mockito.when(authentication.getPrincipal()).thenReturn(new AppUserDetails(user));

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Problem problem = new Problem();
        problem.setSolutionRequired(false);

        Lesson lesson = new Lesson();
        lesson.setId(1);
        lesson.setStudentsCompleted(new HashSet<>());
        problem.setLesson(lesson);

        Map<String, String> output = lessonService.runCode(problem, "public class Main {\n \n public static void main(String[] args) {\n System.out.println(\"Hello World!\");\n }\n }\n");

        assertEquals(output.get("type"), "success");
        assertEquals(output.get("solved"), "true");
    }

    @Test
    public void getHintNoSolutionRequiredTest() {
        Problem problem = new Problem();
        problem.setSolutionRequired(false);

        String hint = lessonService.getHint(problem, "");

        assertEquals(hint, "Try running your code!");
    }

    @Test
    public void getHintWithMisconceptionTest() {
        Problem problem = new Problem();
        problem.setSolutionRequired(true);

        Mockito.when(misconceptionTracer.trace(problem, "")).thenReturn(new JavaASTNode("test-node", ""));
        Mockito.when(misconceptionTracer.successfulTrace(misconceptionTracer.trace(problem, ""))).thenReturn(true);
        Mockito.when(misconceptionTracer.getMisconception()).thenReturn("Test misconception");

        String hint = lessonService.getHint(problem, "");

        assertEquals(hint, "Test misconception");
    }

    @Test
    public void getNormalHintTest() {
        Problem problem = new Problem();
        problem.setSolutionRequired(true);

        JavaASTNode solutionNode = new JavaASTNode("test-node", "");

        Mockito.when(misconceptionTracer.trace(problem, "")).thenReturn(new JavaASTNode("test-node", ""));
        Mockito.when(misconceptionTracer.successfulTrace(misconceptionTracer.trace(problem, ""))).thenReturn(false);
        Mockito.when(solutionTracer.trace(problem, "")).thenReturn(solutionNode);
        Mockito.when(feedbackModule.generateFeedback(solutionNode)).thenReturn("Test solution feedback");

        String hint = lessonService.getHint(problem, "");

        assertEquals(hint, "Test solution feedback");
    }

    @Test
    public void getSolutionTest() throws IOException {
        Problem problem = new Problem();
        String code = "";

        Mockito.when(solutionTracer.getSolution(problem, code)).thenReturn("Success");

        String solution = lessonService.getSolution(problem, code);

        assertEquals(solution, "Success");
    }

}