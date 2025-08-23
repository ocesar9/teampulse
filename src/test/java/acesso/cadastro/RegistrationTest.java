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

    // Locators
    By emailField = By.id("email");
    By usernameField = By.id("nome");
    By passwordField = By.id("senha");
    By submitButton = By.cssSelector("[data-register-btn]");
    By gerenteCargo = By.id("select-cargo-gerente");
    By colaboradorCargo = By.id("select-cargo-colaborador");
    By feedbackMessage = By.cssSelector("[data-alerts]");
    By closeAlertButton = By.cssSelector("[data-close-alert]");

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
        navegarParaCadastro();
        preencherCadastro("SergioAdMelo@gmail.com", "Sergio Adriani", "Lorem12345", gerenteCargo);
        validarCadastro();
        limparSessionLocalStorage();
    }

    @Test
    public void registerColaboradoresComAutenticacao() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        navegarParaCadastro();

        cadastrarColaborador("carlos.meliodas@gmail.com", "Carlos Alberto", "12345678910");
        cadastrarColaborador("luanaPortela@gmail.com", "Luana Portela", "12345678910");
        cadastrarColaborador("Raylson.carlos@gmail.com", "Raylson Sobral", "12345678910");
        cadastrarColaborador("Patricio563@gmail.com", "Patrício Carvalho", "12345678910");
        cadastrarColaborador("Rosana99020@gmail.com", "Rosana Melo", "12345678910");
        cadastrarColaborador("Vitoria.gbatista@gmail.com", "Vitoria Batista", "12345678910");

        limparSessionLocalStorage();
    }

    private void cadastrarColaborador(String email, String nome, String senha) {
        preencherCadastro(email, nome, senha, colaboradorCargo);
        validarCadastro();
        fecharAlerta();
    }

    private void preencherCadastro(String email, String nome, String senha, By cargo) {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
            preencherCampoEmail(email);
            preencherCampoNome(nome);
            preencherCampoSenha(senha);
            selecionarCargo(cargo);
            clicarBotaoCadastrar();
        } catch (NoSuchElementException | TimeoutException e) {
            Assert.fail("Falha ao preencher o form de cadastro: " + e.getMessage());
        }
    }

    private void preencherCampoEmail(String email) {
        WebElement emailElement = driver.findElement(emailField);
        emailElement.clear();
        emailElement.sendKeys(email);
    }

    private void preencherCampoNome(String nome) {
        WebElement nomeElement = driver.findElement(usernameField);
        nomeElement.clear();
        nomeElement.sendKeys(nome);
    }

    private void preencherCampoSenha(String senha) {
        WebElement senhaElement = driver.findElement(passwordField);
        senhaElement.clear();
        senhaElement.sendKeys(senha);
    }

    private void selecionarCargo(By cargo) {
        wait.until(ExpectedConditions.elementToBeClickable(cargo));
        WebElement cargoElement = driver.findElement(cargo);
        cargoElement.click();
    }

    private void clicarBotaoCadastrar() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
        btn.click();
    }

    private void validarCadastro() {
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

    private void limparSessionLocalStorage() {
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
    }

    private void fecharAlerta() {
        try {
            WebElement buttonCloseAlert = wait.until(ExpectedConditions.elementToBeClickable(closeAlertButton));
            buttonCloseAlert.click();
        } catch (TimeoutException e) {
            System.out.println("Alerta não encontrado para fechar");
        }
    }

    private void navegarParaCadastro() {
        driver.get("http://localhost:8080/cadastro");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
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

        validarLoginComSucesso();
    }

    private void validarLoginComSucesso() {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.urlContains("/dashboard"),
                    ExpectedConditions.not(ExpectedConditions.urlContains("/login"))));
        } catch (TimeoutException e) {
            Assert.fail("Não conseguiu logar como admin em tempo hábil!");
        }
    }

    // Métodos públicos para reutilização em outros testes
    public void cadastrarGerente(String emailAdmin, String senhaAdmin, String emailGerente, String nomeGerente, String senhaGerente) {
        loginComoAdm(emailAdmin, senhaAdmin);
        navegarParaCadastro();
        preencherCadastro(emailGerente, nomeGerente, senhaGerente, gerenteCargo);
        validarCadastro();
        limparSessionLocalStorage();
    }

    public void cadastrarColaboradores(String emailAdmin, String senhaAdmin, String[][] colaboradores) {
        loginComoAdm(emailAdmin, senhaAdmin);
        navegarParaCadastro();

        for (String[] colaborador : colaboradores) {
            cadastrarColaborador(colaborador[0], colaborador[1], colaborador[2]);
        }

        limparSessionLocalStorage();
    }

    public boolean estaNaPaginaCadastro() {
        return driver.getCurrentUrl().contains("/cadastro");
    }

    public void recarregarPaginaCadastro() {
        driver.get("http://localhost:8080/cadastro");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }
}