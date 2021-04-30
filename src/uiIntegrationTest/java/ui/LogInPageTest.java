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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class LogInPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty(" webdriver.chrome.driver", ChromeDriverLocation.location);
        driver = new ChromeDriver();
    }

    @Test
    public void logInPageTest() throws InterruptedException {
        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        String title = driver.findElement(By.tagName("h2")).getText();

        assertEquals("Log In", title);
    }

    @Test
    public void successfulLogInTest() throws InterruptedException {
        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        driver.findElement(By.name("email")).sendKeys("test@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("test");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/", driver.getCurrentUrl());
    }

    @Test
    public void wrongEmailLogInTest() throws InterruptedException {
        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        driver.findElement(By.name("email")).sendKeys("NoTest@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("test");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/login?error", driver.getCurrentUrl());

        String errorMessage = driver.findElement(By.className("alert")).getText();
        assertEquals("Your login credentials do not match", errorMessage);
    }

    @Test
    public void wrongPasswordLogInTest() throws InterruptedException {
        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        driver.findElement(By.name("email")).sendKeys("test@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("wrong");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/login?error", driver.getCurrentUrl());

        String errorMessage = driver.findElement(By.className("alert")).getText();
        assertEquals("Your login credentials do not match", errorMessage);
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
