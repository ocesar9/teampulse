package acesso.login;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest {
    WebDriver driver;
    WebDriverWait wait;

    // Localizadores
    By emailField = By.id("email");
    By passwordField = By.id("senha");
    By loginButton = By.cssSelector("[data-login-btn]");
    By errorMessage = By.cssSelector("[data-login-error-list]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/login");
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void loginComDadosValidos() {
        // Preencher e submeter login
        driver.findElement(emailField).clear();
        driver.findElement(emailField).sendKeys("lorem.ipsum@gmail.com");
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys("Lorem12345");
        driver.findElement(loginButton).click();

        // Espera URL mudar para dashboard
        try {
            boolean redirecionou = wait.until(ExpectedConditions.urlContains("/dashboard"));
            Assert.assertTrue(redirecionou, "O login não redirecionou para a dashboard.");
            System.out.println("Login realizado com sucesso!");
        } catch (TimeoutException e) {
            Assert.fail("Não redirecionou para dashboard em tempo hábil. URL atual: " + driver.getCurrentUrl());
        }
    }

    @Test
    public void loginComDadosInvalidos() {
        // Preencher e submeter login errado
        driver.findElement(emailField).clear();
        driver.findElement(emailField).sendKeys("invalido@exemplo.com");
        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys("senhaErrada");
        driver.findElement(loginButton).click();

        // Espera mensagem de erro aparecer
        try {
            WebElement alerta = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            String classValue = alerta.getAttribute("class");
            Assert.assertTrue(alerta.isDisplayed() && (classValue == null || !classValue.contains("d-none")),
                    "Mensagem de erro de login está oculta ou invisível. Classe: " + classValue);
            String msg = alerta.getText();
            System.out.println("Mensagem de erro exibida: " + msg);
        } catch (TimeoutException e) {
            Assert.fail("Mensagem de erro não apareceu após login inválido.");
        }
    }
}