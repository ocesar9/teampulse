package admin;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminRegistrationTest {
    WebDriver driver;
    WebDriverWait wait;

    // Sugestão para Selectors (mude conforme seu HTML)
    By emailField = By.name("email"); // ou By.id("email") etc
    By usernameField = By.name("username");
    By passwordField = By.name("password");
    By submitButton = By.cssSelector("button[type='submit']");
    By feedbackMessage = By.cssSelector("[data-alerts]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/cadastroadmin");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void shouldRegisterAdminSuccessfully() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
            wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
            wait.until(ExpectedConditions.elementToBeClickable(submitButton));

            driver.findElement(emailField).clear();
            driver.findElement(emailField).sendKeys("lorem.ipsum@gmail.com");
            driver.findElement(usernameField).clear();
            driver.findElement(usernameField).sendKeys("Lorem Ipsum");
            driver.findElement(passwordField).clear();
            driver.findElement(passwordField).sendKeys("Lorem12345");
            driver.findElement(submitButton).click();

            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
            String msgText = msg.getText();

            Assert.assertTrue(
                    msg.isDisplayed() && msgText.contains("Cadastro realizado com sucesso"),
                    "Mensagem de sucesso não recebida. Texto recebido: " + msgText);
            System.out.println("Cadastro realizado com sucesso! Mensagem: " + msgText);

        } catch (TimeoutException te) {
            Assert.fail("Mensagem de feedback não apareceu em tempo hábil: " + te.getMessage());
        } catch (NoSuchElementException nsee) {
            Assert.fail("Elemento não encontrado: " + nsee.getMessage());
        }
    }
}