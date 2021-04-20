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

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "server.port=8000", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = PRJApplication.class)
public class RegistrationPageTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty(" webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
    }

    @Test
    public void registrationPageTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        String title = driver.findElement(By.tagName("h2")).getText();

        assertEquals("Registration", title);
    }

    @Test
    public void successfulRegisterRedirectToLoginTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        driver.findElement(By.name("firstName")).sendKeys("Test");
        driver.findElement(By.name("lastName")).sendKeys("Test");
        driver.findElement(By.name("email")).sendKeys("newTest@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("password");
        driver.findElement(By.name("matchingPassword")).sendKeys("password");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        assertEquals("http://localhost:8000/login", driver.getCurrentUrl());
    }

    @Test
    public void usedEmailRegisterTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        driver.findElement(By.name("firstName")).sendKeys("Test");
        driver.findElement(By.name("lastName")).sendKeys("Test");
        driver.findElement(By.name("email")).sendKeys("test@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("password");
        driver.findElement(By.name("matchingPassword")).sendKeys("password");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        // the page did not change, as there was an error
        assertEquals("http://localhost:8000/register", driver.getCurrentUrl());

        String errorMessage = driver.findElement(By.className("invalid-feedback")).getText();
        assertEquals("This email is already registered", errorMessage);
    }

    @Test
    public void invalidEmailRegisterTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        driver.findElement(By.name("firstName")).sendKeys("Test");
        driver.findElement(By.name("lastName")).sendKeys("Test");
        driver.findElement(By.name("email")).sendKeys("test@kcl.uk");
        driver.findElement(By.name("password")).sendKeys("password");
        driver.findElement(By.name("matchingPassword")).sendKeys("password");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        // the page did not change, as there was an error
        assertEquals("http://localhost:8000/register", driver.getCurrentUrl());

        String errorMessage = driver.findElement(By.className("invalid-feedback")).getText();
        assertEquals("Invalid KCL Email address", errorMessage);
    }

    @Test
    public void emptyFieldsRegisterTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        driver.findElement(By.name("email")).sendKeys("new-test@kcl.ac.uk");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        // the page did not change, as there was an error
        assertEquals("http://localhost:8000/register", driver.getCurrentUrl());

        List<WebElement> elements = driver.findElements(By.className("invalid-feedback"));
        for (WebElement element: elements) {
            assertEquals("must not be empty", element.getText());
        }
    }

    @Test
    public void nonMatchingPasswordsRegisterTest() throws InterruptedException {
        driver.get("http://localhost:8000/register");
        Thread.sleep(1000);

        driver.findElement(By.name("firstName")).sendKeys("Test");
        driver.findElement(By.name("lastName")).sendKeys("Test");
        driver.findElement(By.name("email")).sendKeys("newTest@kcl.ac.uk");
        driver.findElement(By.name("password")).sendKeys("password");
        driver.findElement(By.name("matchingPassword")).sendKeys("pwd");

        driver.findElement(By.xpath("//*[@type='submit']")).click();
        Thread.sleep(1000);

        // the page did not change, as there was an error
        assertEquals("http://localhost:8000/register", driver.getCurrentUrl());

        String errorMessage = driver.findElement(By.className("alert")).getText();
        assertEquals("Passwords do not match", errorMessage);
    }

    @AfterEach
    public void tearDown() {
        driver.close();
    }
}
