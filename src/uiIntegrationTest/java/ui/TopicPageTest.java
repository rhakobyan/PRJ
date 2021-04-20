package ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import prj.PRJApplication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class TopicPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws InterruptedException {
        System.setProperty(" webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();

        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        // Log In
        driver.findElement(By.name("email")).sendKeys("test@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("test");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(500);
    }

    @Test
    public void topicsPageWithoutQuizTest() throws InterruptedException {
        driver.get("http://localhost:8000/topics/1");
        Thread.sleep(1000);

        String title = driver.findElement(By.id("title")).getText();

        assertEquals("Introduction", title);

        assertTrue(driver.findElement(By.className("progress-bar")).isDisplayed());
        assertTrue(driver.findElement(By.id("description")).isDisplayed());
        // Only one h4 - title
        assertEquals(driver.findElements(By.tagName("h4")).size(), 1);
        assertEquals(driver.findElement(By.tagName("h4")).getText(), "Lessons");

        List<WebElement> lessons = driver.findElements(By.className("lessonTitle"));
        // All elements are enabled
        for (WebElement lesson : lessons)
            assertTrue(lesson.isEnabled());

        lessons.get(lessons.size() - 1).click();
        assertEquals(driver.getCurrentUrl(), "http://localhost:8000/lessons/2");

    }

    @Test
    public void topicsPageWithQuizTest() throws InterruptedException {
        driver.get("http://localhost:8000/topics/2");
        Thread.sleep(1000);

        String title = driver.findElement(By.id("title")).getText();

        assertEquals("Variables", title);

        assertTrue(driver.findElement(By.className("progress-bar")).isDisplayed());
        assertTrue(driver.findElement(By.id("description")).isDisplayed());
        // Only one h4 - title
        assertEquals(driver.findElements(By.tagName("h4")).size(), 2);
        assertEquals(driver.findElement(By.tagName("h4")).getText(), "Lessons");
        assertEquals(driver.findElements(By.tagName("h4")).get(1).getText(), "Quiz");

        List<WebElement> lessons = driver.findElements(By.className("lessonTitle"));
        // All elements are enabled
        for (WebElement lesson : lessons)
            assertTrue(lesson.isEnabled());
    }

    @Test
    public void topicsPageWithDisabledLinksTest() throws InterruptedException {
        driver.get("http://localhost:8000/topics/3");
        Thread.sleep(1000);

        String title = driver.findElement(By.id("title")).getText();

        assertEquals("Operators", title);

        assertNotNull(driver.findElement(By.className("progress-bar")));
        assertNotNull(driver.findElements(By.id("description")));
        // Only one h4 - title
        assertEquals(driver.findElements(By.tagName("h4")).size(), 2);
        assertEquals(driver.findElement(By.tagName("h4")).getText(), "Lessons");
        assertEquals(driver.findElements(By.tagName("h4")).get(1).getText(), "Quiz");

        List<WebElement> lessons = driver.findElements(By.className("lessonTitle"));
        // All elements are disabled
        for (WebElement lesson : lessons) {
            assertEquals(lesson.getAttribute("href"), "http://localhost:8000/topics/3#");
        }
    }

    @Test
    public void topicsPageWithPreviousIncompleteTest() throws InterruptedException {
        driver.get("http://localhost:8000/topics/4");
        Thread.sleep(1000);

        String title = driver.findElement(By.id("title")).getText();

        assertEquals("Conditional Statements", title);

        assertTrue(driver.findElement(By.className("alert-danger")).isDisplayed());
        assertEquals(driver.findElement(By.className("alert-danger")).getText(), "You need to complete the previous section before moving on to this one!");
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
