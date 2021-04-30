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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class LessonPageTest {

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
    public void lessonPageTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        assertTrue(driver.findElement(By.className("lesson")).isDisplayed());
        assertTrue(driver.findElement(By.id("lessonTitle")).isDisplayed());
        assertTrue(driver.findElement(By.id("explanation")).isDisplayed());
        assertTrue(driver.findElement(By.id("codeEditor")).isDisplayed());
        assertTrue(driver.findElement(By.id("output")).isDisplayed());

        assertTrue(driver.findElement(By.className("bottom-panel")).isDisplayed());
        assertTrue(driver.findElement(By.id("sidebarCollapse")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("Prev")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("Next")).isDisplayed());
        assertTrue(driver.findElement(By.id("run")).isDisplayed());
        assertTrue(driver.findElement(By.id("hint")).isDisplayed());
        assertFalse(driver.findElement(By.id("showSolution")).isDisplayed());

        assertFalse(driver.findElement(By.id("modal")).isDisplayed());
        assertFalse(driver.findElement(By.id("sidebar")).isDisplayed());
    }

    @Test
    public void lessonPageNextClickTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        WebElement next = driver.findElement(By.linkText("Next"));

        assertTrue(next.isDisplayed());
        next.click();
        Thread.sleep(100);
        assertEquals("http://localhost:8000/lessons/6", driver.getCurrentUrl());

    }

    @Test
    public void lessonPageNextClickQuizTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/8");
        Thread.sleep(1000);

        WebElement next = driver.findElement(By.linkText("Next"));

        assertTrue(next.isDisplayed());
        next.click();
        Thread.sleep(100);
        assertEquals("http://localhost:8000/quizzes/1", driver.getCurrentUrl());

    }

    @Test
    public void lessonPagePrevClickTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        WebElement prev = driver.findElement(By.linkText("Prev"));

        assertTrue(prev.isDisplayed());
        prev.click();
        Thread.sleep(100);
        assertEquals("http://localhost:8000/lessons/4", driver.getCurrentUrl());
    }

    @Test
    public void lessonPagePrevDisabledClickTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/1");
        Thread.sleep(1000);

        WebElement prev = driver.findElement(By.linkText("Prev"));

        assertTrue(prev.isDisplayed());

        assertNull(prev.getAttribute("href"));
        assertEquals("none", prev.getCssValue("pointer-events"));
    }

    @Test
    public void lessonPageDisplaySidebarTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        WebElement sidebarButton = driver.findElement(By.id("sidebarCollapse"));
        WebElement sidebar = driver.findElement(By.id("sidebar"));

        assertFalse(sidebar.isDisplayed());
        assertFalse(driver.findElement(By.className("overlay")).isDisplayed());
        assertTrue(sidebarButton.isDisplayed());

        sidebarButton.click();
        Thread.sleep(500);
        assertTrue(sidebar.isDisplayed());
        assertTrue(driver.findElement(By.className("overlay")).isDisplayed());

        assertEquals("Variables", sidebar.findElement(By.tagName("h3")).getText());
        assertEquals(sidebar.findElements(By.tagName("h5")).size(), 2);

        WebElement close = sidebar.findElement(By.id("closeSidebar"));
        assertTrue(close.isDisplayed());
        close.click();
        Thread.sleep(500);
        assertFalse(sidebar.isDisplayed());
        assertFalse(driver.findElement(By.className("overlay")).isDisplayed());
    }

    @Test
    public void lessonPageSidebarCloseByOverlayTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        WebElement sidebarButton = driver.findElement(By.id("sidebarCollapse"));
        WebElement sidebar = driver.findElement(By.id("sidebar"));
        WebElement overlay = driver.findElement(By.className("overlay"));

        assertFalse(sidebar.isDisplayed());
        assertFalse(overlay.isDisplayed());
        assertTrue(sidebarButton.isDisplayed());

        sidebarButton.click();
        Thread.sleep(500);
        assertTrue(sidebar.isDisplayed());
        assertTrue(overlay.isDisplayed());

        overlay.click();
        Thread.sleep(500);
        assertFalse(sidebar.isDisplayed());
        assertFalse(overlay.isDisplayed());
    }

    @Test
    public void lessonPageSidebarClickElementTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/5");
        Thread.sleep(1000);

        WebElement sidebarButton = driver.findElement(By.id("sidebarCollapse"));
        WebElement sidebar = driver.findElement(By.id("sidebar"));
        WebElement overlay = driver.findElement(By.className("overlay"));

        assertFalse(sidebar.isDisplayed());
        assertFalse(overlay.isDisplayed());
        assertTrue(sidebarButton.isDisplayed());

        sidebarButton.click();
        Thread.sleep(500);
        assertTrue(sidebar.isDisplayed());
        assertTrue(overlay.isDisplayed());

       driver.findElement(By.linkText("Data Types")).click();
       Thread.sleep(1000);
       assertEquals("http://localhost:8000/lessons/3", driver.getCurrentUrl());
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
