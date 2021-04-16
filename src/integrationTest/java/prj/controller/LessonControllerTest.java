package prj.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import prj.util.FileIO;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LessonControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void init() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void NotLoggedInLessonPagesTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void NoSuchLessonTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "0")
                .param("code", "code"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/lessons/hint")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "0")
                .param("code", "code"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/lessons/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "0")
                .param("code", "code"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void NonMostRecentLessonRedirectToQuizTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 10)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/quizzes/*"));
    }

    @Test
    @WithUserDetails("test2@kcl.ac.uk")
    public void NonMostRecentLessonRedirectToLessonTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/lessons/*"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void WrongLessonFormatTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "test")
                .param("code", "code"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/lessons/hint")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "test")
                .param("code", "code"))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/lessons/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "test")
                .param("code", "code"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonPageTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 4)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/lesson"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(5))
                .andExpect(model().attributeExists("next", "prev", "lesson", "latestInComplete", "latestInCompleteQuiz"))
                .andExpect(model().attribute("prev", "/lessons/3"))
                .andExpect(model().attribute("next", "/lessons/5"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonPagePrevNullTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/lesson"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(5))
                .andExpect(model().attributeExists("next", "lesson", "latestInComplete", "latestInCompleteQuiz"))
                .andExpect(model().attribute("prev", nullValue()))
                .andExpect(model().attribute("next", "/lessons/2"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonPageNextQuizTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 8)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("lessons/lesson"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(5))
                .andExpect(model().attributeExists("next", "prev", "lesson", "latestInComplete", "latestInCompleteQuiz"))
                .andExpect(model().attribute("prev", "/lessons/7"))
                .andExpect(model().attribute("next", "/quizzes/1"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonGetHintWithNoSolutionRequiredTest() throws Exception {
        mvc.perform(post("/lessons/hint")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .param("code", "public class Main {\\n \\n public static void main(String[] args) {\\n System.out.println(\"Hello World!\");\\n }\\n }\\n"))
                .andExpect(status().isOk())
                .andExpect(content().string("Try running your code!"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonGetNormalHintTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/hint")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .param("code", "public class Main {\\n \\n public static void main(String[] args) {\\n //Write your code here \\n \\n }\\n }"))
                .andExpect(status().isOk())
                .andExpect(content().string("First, you need to print out <code>\"Learning Java\"</code>"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonGetSolutionTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/solution")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .param("code", "public class Main {\\n \\n public static void main(String[] args) {\\n //Write your code here \\n \\n }\\n }"))
                .andExpect(status().isOk())
                .andExpect(content().string("System.out.println(\"Learning Java\");"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonRunCorrectSolutionTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .param("code", "public class Main {\n \n public static void main(String[] args) {\n //Write your code here \n System.out.println(\"Learning Java\");\n }\n }"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"solved\":\"true\",\"type\":\"success\",\"message\":\"Learning Java\\n\"}"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonRunInCorrectSolutionTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .param("code", "public class Main {\n \n public static void main(String[] args) {\n //Write your code here \n System.out.println(\"Java\");\n }\n }"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"solved\":\"false\",\"type\":\"success\",\"message\":\"Java\\n\"}"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonRunErrorSolutionTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "2")
                .param("code", "public class Main {\n \n public static void main(String[] args) {\n //Write your code here \n System.println(\"Learning Java\");\n }\n }"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"type\":\"error\",\"message\":\"Error on line 5: cannot find symbol\\n  symbol:   method println(java.lang.String)\\n  location: class java.lang.System\\n\"}"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void LessonRunNoSolutionRequiredTest() throws Exception {
        mvc.perform(get("/lessons/{lessonId}", 2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/lessons/run")
                .contentType(MediaType.APPLICATION_JSON)
                .param("id", "1")
                .param("code", "public class Main {\n \n public static void main(String[] args) {\n System.out.println(\"Hello World!\");\n }\n }\n"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"solved\":\"true\",\"type\":\"success\",\"message\":\"Hello World!\\n\"}"));
    }

}
