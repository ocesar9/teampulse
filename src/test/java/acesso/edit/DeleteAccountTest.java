package acesso.edit;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class DeleteAccountTest {
    WebDriver driver;
    WebDriverWait wait;

    // Login elements
    By loginEmail = By.id("email");
    By loginSenha = By.id("senha");
    By loginButton = By.cssSelector("[data-login-btn]");

    // Profile elements
    By menuDropdown = By.id("btn-dropdown-options");
    By perfilLink = By.id("profile-link");
    By deleteAccountBtn = By.id("openModalDeleteProfile");
    By confirmDeleteField = By.id("confirmDelete");
    By confirmDeleteBtn = By.id("deleteBtn");
    By errorMsgStrong = By.xpath("/html/body/div[1]/div/main/div[2]/div[1]/strong");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        driver.get("http://localhost:8080/login");
    }

    @BeforeMethod
    public void reload() {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmail));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void deleteOwnAccount() {
        login("SergioAdMelo@gmail.com", "Lorem12345");
        abrirMenuPerfil();
        tentarDeletarConta();
        validarMensagemErro("Não é possível deletar sua própria conta");
        System.out.println("Ação interrompida com sucesso");
    }

    private void login(String email, String senha) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmail));
        preencherCampoLogin(loginEmail, email);
        preencherCampoLogin(loginSenha, senha);
        clicarBotaoLogin();
        validarLoginComSucesso(email);
    }

    private void preencherCampoLogin(By campo, String valor) {
        WebElement element = driver.findElement(campo);
        element.clear();
        element.sendKeys(valor);
    }

    private void clicarBotaoLogin() {
        WebElement btn = driver.findElement(loginButton);
        btn.click();
    }

    private void validarLoginComSucesso(String email) {
        boolean loginSucesso = wait.until(ExpectedConditions.urlContains("/dashboard"));
        Assert.assertTrue(loginSucesso, "Login falhou para o usuário: " + email);
    }

    private void abrirMenuPerfil() {
        clicarElemento(menuDropdown);
        clicarElemento(perfilLink);
    }

    private void clicarElemento(By seletor) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(seletor));
        element.click();
    }

    private void tentarDeletarConta() {
        clicarElemento(deleteAccountBtn);
        preencherConfirmacaoDelecao("DELETAR");
        confirmarDelecao();
    }

    private void preencherConfirmacaoDelecao(String texto) {
        WebElement confirmField = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmDeleteField));
        confirmField.clear();
        confirmField.sendKeys(texto);
    }

    private void confirmarDelecao() {
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn));
        confirmBtn.click();
    }

    private void validarMensagemErro(String mensagemEsperada) {
        WebElement errorStrong = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsgStrong));
        validarTextoMensagemErro(errorStrong, mensagemEsperada);
        validarExibicaoMensagemErro(errorStrong);
    }

    private void validarTextoMensagemErro(WebElement elementoErro, String mensagemEsperada) {
        String textoAtual = elementoErro.getText();
        Assert.assertEquals(textoAtual, mensagemEsperada, "Mensagem de erro inesperada!");
    }

    private void validarExibicaoMensagemErro(WebElement elementoErro) {
        WebElement parentDiv = elementoErro.findElement(By.xpath("./.."));
        String classValue = parentDiv.getAttribute("class");
        Assert.assertTrue(classValue != null && classValue.contains("show"),
                "A mensagem de erro não foi exibida corretamente!");
    }

    // Métodos públicos para reutilização em outros testes
    public void tentarDeletarContaPropria(String email, String senha) {
        login(email, senha);
        abrirMenuPerfil();
        tentarDeletarConta();
        validarMensagemErro("Não é possível deletar sua própria conta");
    }

    public void navegarParaPerfil(String email, String senha) {
        login(email, senha);
        abrirMenuPerfil();
    }

    public boolean estaNaPaginaPerfil() {
        return driver.getCurrentUrl().contains("/profile");
    }

    public boolean modalDelecaoEstaVisivel() {
        try {
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmDeleteField));
            return modal.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void preencherConfirmacaoDelecaoConta(String textoConfirmacao) {
        preencherConfirmacaoDelecao(textoConfirmacao);
    }

    public void clicarConfirmarDelecao() {
        confirmarDelecao();
    }

    public String obterMensagemErro() {
        try {
            WebElement errorStrong = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsgStrong));
            return errorStrong.getText();
        } catch (TimeoutException e) {
            return "Nenhuma mensagem de erro encontrada";
        }
    }

    public void testarDelecaoComTextoIncorreto(String email, String senha, String textoIncorreto) {
        login(email, senha);
        abrirMenuPerfil();
        clicarElemento(deleteAccountBtn);
        preencherConfirmacaoDelecao(textoIncorreto);
        confirmarDelecao();
        // Validação específica para texto incorreto pode ser adicionada aqui
    }

    public void testarDelecaoComTextoCorreto(String email, String senha) {
        login(email, senha);
        abrirMenuPerfil();
        clicarElemento(deleteAccountBtn);
        preencherConfirmacaoDelecao("DELETAR");
        confirmarDelecao();
        // Validação específica para texto correto pode ser adicionada aqui
    }
}