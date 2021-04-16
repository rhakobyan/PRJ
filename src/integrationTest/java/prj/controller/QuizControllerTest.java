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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuizControllerTest {
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
    public void NotLoggedInQuizPagesTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        mvc.perform(get("/quizzes/{quizId}/attempt", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());

        mvc.perform(post("/quizzes/{quizId}/evaluate", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void NoSuchQuizTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(get("/quizzes/{quizId}/attempt", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mvc.perform(post("/quizzes/{quizId}/evaluate", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void NonMostRecentQuizTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/lessons/*"));

        mvc.perform(get("/quizzes/{quizId}/attempt", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/lessons/*"));

        mvc.perform(post("/quizzes/{quizId}/evaluate", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void WrongQuizFormatTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/quizzes/{quizId}/attempt", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/quizzes/{quizId}/evaluate", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void QuizFrontPageTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes/quiz"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("quiz"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void QuizAttemptPageTest() throws Exception {
        mvc.perform(get("/quizzes/{quizId}/attempt", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes/attempt"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(1))
                .andExpect(model().attributeExists("quiz"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void QuizIncorrectAnswersSubmitTest() throws Exception {
        mvc.perform(post("/quizzes/{quizId}/evaluate", 1)
                .param("question","1").param("quiz-option-1", "2")
                .param("question", "2")
                .param("question", "3")
                .param("question", "4").param("quiz-option-4", "13")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes/evaluate"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("quiz", "score", "passed", "answers"))
                .andExpect(model().attribute("passed", false));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void QuizBadRequestSubmitTest() throws Exception {
        mvc.perform(post("/quizzes/{quizId}/evaluate", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void QuizCorrectAnswersSubmitTest() throws Exception {
        mvc.perform(post("/quizzes/{quizId}/evaluate", 1)
                .param("question","1").param("quiz-option-1", "1")
                .param("question", "2").param("quiz-option-2", "6")
                .param("question", "3").param("quiz-option-3", "12")
                .param("question", "4").param("quiz-option-4", "14")
                .param("question", "5").param("quiz-option-5", "20")
                .param("question", "6").param("quiz-option-6", "22")
                .param("question", "7").param("quiz-option-7", "28")
                .param("question", "8").param("quiz-option-8", "31")
                .param("question", "9").param("quiz-option-9", "33")
                .param("question", "10").param("quiz-option-10", "40")
                .param("question", "11").param("quiz-option-11", "42")
                .param("question", "12").param("quiz-option-12", "46")
                .param("question", "13").param("quiz-option-13", "51")
                .param("question", "14").param("quiz-option-14", "55")
                .param("question", "15").param("quiz-option-15", "59")
                .param("question", "16").param("quiz-option-16", "63")
                .param("question", "17").param("quiz-option-17", "67")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("quizzes/evaluate"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("quiz", "score", "passed", "answers"))
                .andExpect(model().attribute("passed", true))
                .andExpect(model().attribute("score", 15));
    }

}
