package ui;

import org.junit.jupiter.api.*;
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
public class HomePageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty(" webdriver.chrome.driver", ChromeDriverLocation.location);
        driver = new ChromeDriver();
    }

    @Test
    public void visitNotLoggedInPage() throws InterruptedException {
        driver.get("http://localhost:8000");
        Thread.sleep(1000);
        String welcomeText = driver.findElement(By.className("display-3")).getText();

        assertEquals("Welcome!", welcomeText);
    }

    @Test
    public void navigateToRegisterPage() throws InterruptedException {
        driver.get("http://localhost:8000");
        Thread.sleep(1000);
        driver.findElement(By.linkText("Get Started!")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/register", driver.getCurrentUrl());
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
