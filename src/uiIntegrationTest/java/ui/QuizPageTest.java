package ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import prj.PRJApplication;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class QuizPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() throws InterruptedException {
        System.setProperty(" webdriver.chrome.driver", ChromeDriverLocation.location);
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
    public void quizFrontPageTest() throws InterruptedException {
        driver.get("http://localhost:8000/quizzes/1");
        Thread.sleep(1000);

        assertEquals(driver.findElement(By.tagName("h2")).getText(), "Variables Quiz");
        assertTrue(driver.findElement(By.linkText("Attempt Quiz")).isDisplayed());

    }

    @Test
    public void toSyllabusLinkTest() throws InterruptedException {
        driver.get("http://localhost:8000/quizzes/1");
        Thread.sleep(1000);

        driver.findElement(By.linkText("To Syllabus")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/", driver.getCurrentUrl());
    }

    @Test
    public void backButtonLinkTest() throws InterruptedException {
        driver.get("http://localhost:8000/topics/2");
        driver.get("http://localhost:8000/quizzes/1");
        Thread.sleep(1000);

        driver.findElement(By.linkText("<<<<Back")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/topics/2", driver.getCurrentUrl());
    }

    @Test
    public void attemptQuizClickTest() throws InterruptedException {
        driver.get("http://localhost:8000/quizzes/1");
        Thread.sleep(1000);

        driver.findElement(By.linkText("Attempt Quiz")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/quizzes/1/attempt", driver.getCurrentUrl());
    }

    @Test
    public void attemptQuizEmptyTest() throws InterruptedException {
        driver.get("http://localhost:8000/quizzes/1/attempt");
        Thread.sleep(1000);

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/quizzes/1/evaluate", driver.getCurrentUrl());
        assertEquals("You have not passed the quiz!", driver.findElement(By.className("text-danger")).getText());
        assertEquals("You scored 0/17", driver.findElement(By.tagName("h4")).getText());
    }

    @Test
    public void attemptQuizPassTest() throws InterruptedException {
        driver.get("http://localhost:8000/quizzes/1/attempt");
        Thread.sleep(1000);

        driver.findElement(By.xpath("//*[@id='flexRadioDefault1']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault6']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault12']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault14']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault20']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault22']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault28']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault31']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault33']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault40']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault42']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault46']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault49']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault55']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault59']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault64']")).click();
        driver.findElement(By.xpath("//*[@id='flexRadioDefault65']")).click();

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/quizzes/1/evaluate", driver.getCurrentUrl());
        assertEquals("You have passed the quiz!", driver.findElement(By.className("text-success")).getText());
        assertEquals("You scored 14/17", driver.findElement(By.tagName("h4")).getText());

        driver.get("http://localhost:8000/quizzes/1/");
        Thread.sleep(1000);

        assertEquals("You have already passed this quiz", driver.findElement(By.className("text-success")).getText());
        assertTrue(driver.findElement(By.linkText("Re-attempt Quiz")).isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
