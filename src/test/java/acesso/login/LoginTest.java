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
        inicializarDriver();
        navegarParaLogin();
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        recarregarPagina();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        fecharDriver();
    }

    @Test
    public void loginComDadosValidos() {
        realizarLogin("lorem.ipsum@gmail.com", "Lorem12345");
        validarRedirecionamentoDashboard();
    }

    @Test
    public void loginComDadosInvalidos() {
        realizarLogin("invalido@exemplo.com", "senhaErrada");
        validarMensagemErroLogin();
    }

    private void inicializarDriver() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    private void navegarParaLogin() {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    private void recarregarPagina() {
        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
    }

    private void fecharDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void realizarLogin(String email, String senha) {
        preencherCampoEmail(email);
        preencherCampoSenha(senha);
        clicarBotaoLogin();
    }

    private void preencherCampoEmail(String email) {
        WebElement emailElement = driver.findElement(emailField);
        emailElement.clear();
        emailElement.sendKeys(email);
    }

    private void preencherCampoSenha(String senha) {
        WebElement passwordElement = driver.findElement(passwordField);
        passwordElement.clear();
        passwordElement.sendKeys(senha);
    }

    private void clicarBotaoLogin() {
        driver.findElement(loginButton).click();
    }

    private void validarRedirecionamentoDashboard() {
        try {
            boolean redirecionou = wait.until(ExpectedConditions.urlContains("/dashboard"));
            Assert.assertTrue(redirecionou, "O login não redirecionou para a dashboard.");
            System.out.println("Login realizado com sucesso!");
        } catch (TimeoutException e) {
            Assert.fail("Não redirecionou para dashboard em tempo hábil. URL atual: " + driver.getCurrentUrl());
        }
    }

    private void validarMensagemErroLogin() {
        try {
            WebElement alerta = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
            validarVisibilidadeMensagemErro(alerta);
            exibirMensagemErro(alerta);
        } catch (TimeoutException e) {
            Assert.fail("Mensagem de erro não apareceu após login inválido.");
        }
    }

    private void validarVisibilidadeMensagemErro(WebElement alerta) {
        String classValue = alerta.getAttribute("class");
        Assert.assertTrue(alerta.isDisplayed() && (classValue == null || !classValue.contains("d-none")),
                "Mensagem de erro de login está oculta ou invisível. Classe: " + classValue);
    }

    private void exibirMensagemErro(WebElement alerta) {
        String msg = alerta.getText();
        System.out.println("Mensagem de erro exibida: " + msg);
    }

    // Métodos públicos para reutilização em outros testes
    public void loginComoAdministrador() {
        realizarLogin("lorem.ipsum@gmail.com", "Lorem12345");
        validarRedirecionamentoDashboard();
    }

    public void loginComoUsuario(String email, String senha) {
        realizarLogin(email, senha);
        validarRedirecionamentoDashboard();
    }

    public void tentarLoginComCredenciaisInvalidas(String email, String senha) {
        realizarLogin(email, senha);
        validarMensagemErroLogin();
    }

    public boolean estaNaPaginaLogin() {
        return driver.getCurrentUrl().contains("/login");
    }

    public boolean estaNaPaginaDashboard() {
        return driver.getCurrentUrl().contains("/dashboard");
    }

    public void fazerLogout() {
        if (estaNaPaginaDashboard()) {
            driver.get("http://localhost:8080/logout");
            wait.until(ExpectedConditions.urlContains("/login"));
        }
    }

    public void testarMultiplosLogins(String[][] credenciais) {
        for (String[] credencial : credenciais) {
            recarregarPagina();
            realizarLogin(credencial[0], credencial[1]);

            if (credencial[0].equals("lorem.ipsum@gmail.com") && credencial[1].equals("Lorem12345")) {
                validarRedirecionamentoDashboard();
                fazerLogout();
            } else {
                validarMensagemErroLogin();
            }
        }
    }

    public void testarLoginComEmailInvalido(String senha) {
        realizarLogin("email-invalido@teste.com", senha);
        validarMensagemErroLogin();
    }

    public void testarLoginComSenhaInvalida(String email) {
        realizarLogin(email, "senha-invalida");
        validarMensagemErroLogin();
    }

    public void testarLoginComCamposVazios() {
        preencherCampoEmail("");
        preencherCampoSenha("");
        clicarBotaoLogin();
        validarMensagemErroLogin();
    }
}