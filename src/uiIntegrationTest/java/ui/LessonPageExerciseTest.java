package ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import prj.PRJApplication;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class LessonPageExerciseTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() throws InterruptedException {
        System.setProperty(" webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();

        driver.get("http://localhost:8000/login");
        Thread.sleep(1000);

        // Log In
        driver.findElement(By.name("email")).sendKeys("test3@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("test");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(500);
    }

    @Test
    public void noSolutionRequiredLessonGetHintTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/3");
        Thread.sleep(1000);

        WebElement hint = driver.findElement(By.id("hint"));
        WebElement modal = driver.findElement(By.id("modal"));

        assertTrue(hint.isDisplayed());
        assertFalse(modal.isDisplayed());

        hint.click();
        Thread.sleep(500);

        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "Try running your code!");
    }

    @Test
    public void hintModalCloseTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/3");
        Thread.sleep(1000);

        WebElement hint = driver.findElement(By.id("hint"));
        WebElement modal = driver.findElement(By.id("modal"));

        assertTrue(hint.isDisplayed());
        assertFalse(modal.isDisplayed());

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());

        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());

        modal.findElement(By.className("btn-primary")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());
    }

    @Test
    public void noSolutionRequiredRunCodeTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/3");
        Thread.sleep(1000);

        WebElement run = driver.findElement(By.id("run"));

        assertTrue(run.isDisplayed());

        run.click();
        Thread.sleep(2000);

        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//span")).getText(),
                "Nate River is 18 years old");
        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//p")).getText(),
                "Your solution is correct!");
    }

    @Test
    public void solutionRequiredLessonGetHintTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/4");
        Thread.sleep(1000);

        WebElement hint = driver.findElement(By.id("hint"));
        WebElement modal = driver.findElement(By.id("modal"));
        WebElement codeMirror = driver.findElement(By.className("CodeMirror"));

        assertTrue(hint.isDisplayed());
        assertFalse(modal.isDisplayed());

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "First, you need to declare a local variable of primitive type int");

        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("int a;");

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "You should name your variable daysInWeek");

        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("\b\b");
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("daysInWeek;");

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "You should equate your variable daysInWeek to 7");

        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("\b");
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys(" = 7;");

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "Next, you need to print out daysInWeek");

        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(modal.isDisplayed());

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys(Keys.RETURN);
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("System.out.println(daysInWeek);");

        hint.click();
        Thread.sleep(500);
        assertTrue(modal.isDisplayed());
        assertEquals(driver.findElement(By.id("modalTitle")).getText(), "Hint");
        assertEquals(driver.findElement(By.id("modalParagraph")).getText(), "Next, try running your code!");
    }

    @Test
    public void solutionRequiredRunWrongCodeTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/4");
        Thread.sleep(1000);

        WebElement run = driver.findElement(By.id("run"));
        WebElement codeMirror = driver.findElement(By.className("CodeMirror"));

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("int daysInWeek = 10;");
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys(Keys.RETURN);
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("System.out.println(daysInWeek);");

        assertTrue(run.isDisplayed());
        run.click();
        Thread.sleep(2000);

        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//span")).getText(),
                "10");
        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//p")).getText(),
                "Your solution is incorrect!");
    }

    @Test
    public void solutionRequiredRunErrorCodeTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/4");
        Thread.sleep(1000);

        WebElement run = driver.findElement(By.id("run"));
        WebElement codeMirror = driver.findElement(By.className("CodeMirror"));

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("int daysInWeek = \"test\";");

        assertTrue(run.isDisplayed());
        run.click();
        Thread.sleep(2000);

        // Could either take more than 5 seconds and cause TimeoutException or overfill the heap
        // and cause IllegalStateException
        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//span")).getText(),
                "Error on line 5: incompatible types: java.lang.String cannot be converted to int");
    }

    @Test
    public void solutionRequiredRunCorrectCodeTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/4");
        Thread.sleep(1000);

        WebElement next = driver.findElement(By.linkText("Next"));
        assertEquals("none", next.getCssValue("pointer-events"));

        WebElement run = driver.findElement(By.id("run"));
        WebElement codeMirror = driver.findElement(By.className("CodeMirror"));

        codeMirror.findElements(By.className("CodeMirror-line")).get(4).click();
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("int daysInWeek = 7;");
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys(Keys.RETURN);
        codeMirror.findElement(By.cssSelector("textarea")).sendKeys("System.out.println(daysInWeek);");

        assertTrue(run.isDisplayed());
        run.click();
        Thread.sleep(2000);

        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//span")).getText(),
                "7");
        assertEquals(driver.findElement(By.xpath("//div[contains(@id, 'output')]//div//p")).getText(),
                "Your solution is correct!");

        next.click();
        assertEquals("http://localhost:8000/lessons/5", driver.getCurrentUrl());
    }

    @Test
    public void solutionRequiredGetSolutionTest() throws InterruptedException {
        driver.get("http://localhost:8000/lessons/4");
        Thread.sleep(1000);

        WebElement run = driver.findElement(By.id("run"));
        WebElement hint = driver.findElement(By.id("hint"));
        WebElement solutionButton = driver.findElement(By.id("showSolution"));
        WebElement modal = driver.findElement(By.id("modal"));

        assertTrue(run.isDisplayed());
        assertTrue(hint.isDisplayed());
        assertFalse(solutionButton.isDisplayed());
        run.click();
        Thread.sleep(2000);
        assertFalse(solutionButton.isDisplayed());
        hint.click();
        Thread.sleep(500);
        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(solutionButton.isDisplayed());
        hint.click();
        Thread.sleep(500);
        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertFalse(solutionButton.isDisplayed());
        hint.click();
        Thread.sleep(500);
        modal.findElement(By.className("close")).click();
        Thread.sleep(500);
        assertTrue(solutionButton.isDisplayed());

        solutionButton.click();
        Thread.sleep(500);

        assertEquals("int daysInWeek = 7;\nSystem.out.println(daysInWeek);", driver.findElement(By.xpath("//*[@id=\"modalParagraph\"]/pre/code")).getText());
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
