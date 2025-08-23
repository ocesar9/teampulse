package admin;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class AdminRegistrationTest {
    WebDriver driver;
    WebDriverWait wait;

    // Locators
    By emailField = By.name("email");
    By usernameField = By.name("username");
    By passwordField = By.name("password");
    By submitButton = By.cssSelector("button[type='submit']");
    By feedbackMessage = By.cssSelector("[data-alerts]");

    @BeforeClass
    public void setUp() {
        inicializarDriver();
        navegarParaCadastroAdmin();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        fecharDriver();
    }

    @Test
    public void shouldRegisterAdminSuccessfully() {
        preencherFormularioAdmin("lorem.ipsum@gmail.com", "Lorem Ipsum", "Lorem12345");
        submeterFormulario();
        validarCadastroSucesso("Cadastro realizado com sucesso");
    }

    private void inicializarDriver() {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.manage().window().maximize();
    }

    private void navegarParaCadastroAdmin() {
        driver.get("http://localhost:8080/cadastroadmin");
        aguardarCarregamentoFormulario();
    }

    private void aguardarCarregamentoFormulario() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        wait.until(ExpectedConditions.elementToBeClickable(submitButton));
    }

    private void fecharDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void preencherFormularioAdmin(String email, String username, String password) {
        preencherCampoEmail(email);
        preencherCampoUsername(username);
        preencherCampoPassword(password);
    }

    private void preencherCampoEmail(String email) {
        WebElement emailElement = driver.findElement(emailField);
        emailElement.clear();
        emailElement.sendKeys(email);
    }

    private void preencherCampoUsername(String username) {
        WebElement usernameElement = driver.findElement(usernameField);
        usernameElement.clear();
        usernameElement.sendKeys(username);
    }

    private void preencherCampoPassword(String password) {
        WebElement passwordElement = driver.findElement(passwordField);
        passwordElement.clear();
        passwordElement.sendKeys(password);
    }

    private void submeterFormulario() {
        WebElement submitBtn = driver.findElement(submitButton);
        submitBtn.click();
    }

    private void validarCadastroSucesso(String mensagemEsperada) {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
            String msgText = msg.getText();

            Assert.assertTrue(
                    msg.isDisplayed() && msgText.contains(mensagemEsperada),
                    "Mensagem de sucesso não recebida. Texto recebido: " + msgText);

            System.out.println("Cadastro realizado com sucesso! Mensagem: " + msgText);

        } catch (TimeoutException te) {
            Assert.fail("Mensagem de feedback não apareceu em tempo hábil: " + te.getMessage());
        } catch (NoSuchElementException nsee) {
            Assert.fail("Elemento não encontrado: " + nsee.getMessage());
        }
    }

    // Métodos públicos para reutilização em outros testes
    public void cadastrarAdministrador(String email, String username, String password) {
        navegarParaCadastroAdmin();
        preencherFormularioAdmin(email, username, password);
        submeterFormulario();
        validarCadastroSucesso("Cadastro realizado com sucesso");
    }

    public void testarCadastroComEmailInvalido(String emailInvalido, String username, String password) {
        navegarParaCadastroAdmin();
        preencherFormularioAdmin(emailInvalido, username, password);
        submeterFormulario();
        validarMensagemErro("Email inválido");
    }

    public void testarCadastroComSenhaFraca(String email, String username, String senhaFraca) {
        navegarParaCadastroAdmin();
        preencherFormularioAdmin(email, username, senhaFraca);
        submeterFormulario();
        validarMensagemErro("Senha fraca");
    }

    public void testarCadastroComCamposVazios() {
        navegarParaCadastroAdmin();
        submeterFormulario();
        validarMensagemErro("Preencha todos os campos");
    }

    private void validarMensagemErro(String mensagemEsperada) {
        try {
            WebElement msg = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
            String msgText = msg.getText();

            Assert.assertTrue(
                    msg.isDisplayed() && msgText.contains(mensagemEsperada),
                    "Mensagem de erro não recebida. Texto recebido: " + msgText);

        } catch (TimeoutException te) {
            Assert.fail("Mensagem de erro não apareceu em tempo hábil: " + te.getMessage());
        }
    }

    public boolean estaNaPaginaCadastroAdmin() {
        return driver.getCurrentUrl().contains("/cadastroadmin");
    }

    public void recarregarPaginaCadastro() {
        driver.get("http://localhost:8080/cadastroadmin");
        aguardarCarregamentoFormulario();
    }

    public void testarMultiplosAdministradores(String[][] administradores) {
        for (String[] admin : administradores) {
            recarregarPaginaCadastro();
            cadastrarAdministrador(admin[0], admin[1], admin[2]);
        }
    }

    public void preencherApenasEmail(String email) {
        preencherCampoEmail(email);
    }

    public void preencherApenasUsername(String username) {
        preencherCampoUsername(username);
    }

    public void preencherApenasPassword(String password) {
        preencherCampoPassword(password);
    }

    public void clicarSubmitSemPreencher() {
        submeterFormulario();
    }
}