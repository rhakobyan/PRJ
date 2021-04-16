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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TopicControllerTest {
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
    public void NotLoggedInTopicPageTest() throws Exception {
        mvc.perform(get("/topics/{topicId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void NoSuchTopicTest() throws Exception {
        mvc.perform(get("/topics/{topicId}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void WrongTopicFormatTest() throws Exception {
        mvc.perform(get("/topics/{topicId}", "test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void TopicWithoutQuizTest() throws Exception {
        mvc.perform(get("/topics/{topicId}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("topics/topic"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("topic", "completedPercentage", "latestInComplete", "latestInCompleteQuiz"));
    }

    @Test
    @WithUserDetails("test@kcl.ac.uk")
    public void TopicWithQuizTest() throws Exception {
        mvc.perform(get("/topics/{topicId}", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(view().name("topics/topic"))
                .andExpect(model().hasNoErrors())
                .andExpect(model().size(4))
                .andExpect(model().attributeExists("topic", "completedPercentage", "latestInComplete", "latestInCompleteQuiz"));
    }
}
