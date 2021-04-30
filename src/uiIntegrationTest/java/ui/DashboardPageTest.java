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
public class DashboardPageTest {

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
        Thread.sleep(1000);
    }

    @Test
    public void dashboardPageTest() throws InterruptedException {
        driver.get("http://localhost:8000/");
        Thread.sleep(1000);

        String title = driver.findElement(By.tagName("h2")).getText();

        assertEquals("Learn Java", title);

        assertNotNull(driver.findElement(By.className("progress-bar")));
        assertNotNull(driver.findElements(By.className("topic-container")));
        assertEquals(driver.findElements(By.className("topic-container")).size(), 5);
        // Is also aligned right
        assertEquals(driver.findElements(By.className("topic-container")).get(1), driver.findElements(By.className("topic-container-right")).get(0));
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
