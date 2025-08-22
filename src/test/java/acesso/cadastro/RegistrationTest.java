package acesso.cadastro;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class RegistrationTest {
    WebDriver driver;
    WebDriverWait wait;

    By emailField = By.id("email");
    By usernameField = By.id("nome");
    By passwordField = By.id("senha");
    By submitButton = By.cssSelector("[data-register-btn]");
    By gerenteCargo = By.id("select-cargo-gerente");
    By colaboradorCargo = By.id("select-cargo-colaborador");
    By feedbackMessage = By.cssSelector("[data-alerts]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @BeforeMethod
    public void voltaCadastro() {
        driver.get("http://localhost:8080/cadastro");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void registerGerenteComAutenticacao() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        driver.get("http://localhost:8080/cadastro");
        preencheCadastro("SergioAdMelo@gmail.com", "Sergio Adriani", "Lorem12345", gerenteCargo);
        validaCadastro();
        limpaSessionLocalStorage();
    }

    @Test
    public void registerColaboradoresComAutenticacao() {

        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        driver.get("http://localhost:8080/cadastro");
        preencheCadastro("carlos.meliodas@gmail.com", "Carlos Alberto", "12345678910", colaboradorCargo);
        validaCadastro();
        preencheCadastro("luanaPortela@gmail.com", "Luana Portela", "12345678910",
                colaboradorCargo);
        validaCadastro();
        closeAlert();
        preencheCadastro("Raylson.carlos@gmail.com", "Raylson Sobral", "12345678910",
                colaboradorCargo);
        validaCadastro();
        closeAlert();
        preencheCadastro("Patricio563@gmail.com", "Patrício Carvalho", "12345678910",
                colaboradorCargo);
        validaCadastro();

        preencheCadastro("Rosana99020@gmail.com", "Rosana Melo", "12345678910",
                colaboradorCargo);
        validaCadastro();
        closeAlert();
        preencheCadastro("Vitoria.gbatista@gmail.com", "Vitoria Batista",
                "12345678910", colaboradorCargo);
        validaCadastro();
        limpaSessionLocalStorage();
    }

    private void preencheCadastro(String email, String nome, String senha, By cargo) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
            driver.findElement(emailField).clear();
            driver.findElement(emailField).sendKeys(email);

            driver.findElement(usernameField).clear();
            driver.findElement(usernameField).sendKeys(nome);

            driver.findElement(passwordField).clear();
            driver.findElement(passwordField).sendKeys(senha);

            wait.until(ExpectedConditions.elementToBeClickable(cargo));
            driver.findElement(cargo).click();

            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            btn.click();
        } catch (NoSuchElementException | TimeoutException e) {
            Assert.fail("Falha ao preencher o form de cadastro: " + e.getMessage());
        }
    }

    private void validaCadastro() {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
            String msgText = msg.getText();
            Assert.assertTrue(
                    msg.isDisplayed() && msgText.contains("Cadastro realizado com sucesso"),
                    "Mensagem de sucesso não recebida. Texto recebido: " + msgText);
            System.out.println("Cadastro realizado com sucesso!");
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada ou não visível: " + e.getMessage());
        }
    }

    private void limpaSessionLocalStorage() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
    }

    private void closeAlert() {
        WebElement buttonCloseAlert = driver.findElement(By.cssSelector("[data-close-alert]"));
        buttonCloseAlert.click();
    }

    private void loginComoAdm(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("senha"));
        WebElement loginBtn = driver.findElement(By.cssSelector("[data-login-btn]"));
        emailInput.clear();
        passwordInput.clear();
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        loginBtn.click();
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard"),
                    ExpectedConditions.not(ExpectedConditions.urlContains("/login"))));
        } catch (TimeoutException e) {
            Assert.fail("Não conseguiu logar como admin em tempo hábil!");
        }
    }
}