package acesso.edit;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class UserActions {
    WebDriver driver;
    WebDriverWait wait;

    // --- Centralização dos seletores ---
    By emailLogin = By.id("email");
    By senhaLogin = By.id("senha");
    By loginBtn = By.cssSelector("[data-login-btn]");
    By btnOpenModalList = By.id("test-btn-edit-user");
    By btnOpenModalDeleteList = By.cssSelector("[data-btn-delete-user]");
    By userEmail = By.id("editUserEmail");
    By userName = By.id("editUserName");
    By btnEditUser = By.id("btnActionEditUser");
    By btnDeleteUser = By.id("confirmDeleteBtn");
    By successMessage = By.xpath("//*[contains(., 'Usuário Rogério Melo atualizado com sucesso')]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void editUser() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        scrollAteFim();

        abrirModalEdicaoUsuario(1);
        preencherFormularioEdicao("Rogério Melo", "Rogerio.Holanda@gmail.com");
        clicarBotaoEditar();
        validarMensagemSucesso("Usuário Rogério Melo atualizado com sucesso");
    }

    @Test
    public void deleteUser() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        scrollAteFim();

        abrirModalExclusaoUsuario(1);
        confirmarExclusaoUsuario();
        validarMensagemSucesso("Deletado com sucesso!");
    }

    private void loginComoAdm(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailLogin));
        preencherCampoLogin(emailLogin, email);
        preencherCampoLogin(senhaLogin, password);
        clicarBotaoLogin();
        validarLoginComSucesso();
    }

    private void preencherCampoLogin(By campo, String valor) {
        WebElement element = driver.findElement(campo);
        element.clear();
        element.sendKeys(valor);
    }

    private void clicarBotaoLogin() {
        driver.findElement(loginBtn).click();
    }

    private void validarLoginComSucesso() {
        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
    }

    private void scrollAteFim() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        aguardar(1000);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalList));
    }

    private void aguardar(int milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void abrirModalEdicaoUsuario(int indice) {
        WebElement botaoEdicao = obterBotaoModalEdicao(indice);
        botaoEdicao.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(userName));
    }

    private void abrirModalExclusaoUsuario(int indice) {
        WebElement botaoExclusao = obterBotaoModalExclusao(indice);
        botaoExclusao.click();
        wait.until(ExpectedConditions.elementToBeClickable(btnDeleteUser));
    }

    private WebElement obterBotaoModalEdicao(int indice) {
        List<WebElement> botoes = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalList));
        validarIndiceBotao(botoes, indice);
        return botoes.get(indice);
    }

    private WebElement obterBotaoModalExclusao(int indice) {
        List<WebElement> botoes = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalDeleteList));
        validarIndiceBotao(botoes, indice);
        return botoes.get(indice);
    }

    private void validarIndiceBotao(List<WebElement> botoes, int indice) {
        Assert.assertTrue(botoes.size() > indice, "Índice de botão para modal não existe na lista.");
    }

    private void preencherFormularioEdicao(String nome, String email) {
        preencherCampoTexto(userName, nome);
        preencherCampoTexto(userEmail, email);
    }

    private void preencherCampoTexto(By campo, String texto) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(campo));
        element.clear();
        element.sendKeys(texto);
    }

    private void clicarBotaoEditar() {
        WebElement btnEditar = wait.until(ExpectedConditions.elementToBeClickable(btnEditUser));
        btnEditar.click();
    }

    private void confirmarExclusaoUsuario() {
        WebElement btnDeletar = wait.until(ExpectedConditions.elementToBeClickable(btnDeleteUser));
        btnDeletar.click();
    }

    private void validarMensagemSucesso(String textoEsperado) {
        try {
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(., '" + textoEsperado + "')]")));
            Assert.assertTrue(message.isDisplayed(), "Mensagem de sucesso não está visível");
        } catch (TimeoutException e) {
            Assert.fail("Mensagem \"" + textoEsperado + "\" não encontrada dentro do tempo esperado");
        }
    }

    // Métodos públicos para reutilização em outros testes
    public void editarUsuario(String emailAdmin, String senhaAdmin, int indiceUsuario, String novoNome, String novoEmail) {
        loginComoAdm(emailAdmin, senhaAdmin);
        scrollAteFim();
        abrirModalEdicaoUsuario(indiceUsuario);
        preencherFormularioEdicao(novoNome, novoEmail);
        clicarBotaoEditar();
        validarMensagemSucesso("Usuário " + novoNome + " atualizado com sucesso");
    }

    public void excluirUsuario(String emailAdmin, String senhaAdmin, int indiceUsuario) {
        loginComoAdm(emailAdmin, senhaAdmin);
        scrollAteFim();
        abrirModalExclusaoUsuario(indiceUsuario);
        confirmarExclusaoUsuario();
        validarMensagemSucesso("Deletado com sucesso!");
    }

    public void editarUsuarioComValidacao(String emailAdmin, String senhaAdmin, int indiceUsuario,
                                          String novoNome, String novoEmail, String mensagemEsperada) {
        loginComoAdm(emailAdmin, senhaAdmin);
        scrollAteFim();
        abrirModalEdicaoUsuario(indiceUsuario);
        preencherFormularioEdicao(novoNome, novoEmail);
        clicarBotaoEditar();
        validarMensagemSucesso(mensagemEsperada);
    }

    public int obterQuantidadeUsuarios() {
        scrollAteFim();
        List<WebElement> botoesEdicao = driver.findElements(btnOpenModalList);
        return botoesEdicao.size();
    }

    public boolean usuarioExisteNaLista(int indice) {
        try {
            scrollAteFim();
            List<WebElement> botoesEdicao = driver.findElements(btnOpenModalList);
            return botoesEdicao.size() > indice;
        } catch (Exception e) {
            return false;
        }
    }

    public void navegarParaListaUsuarios(String emailAdmin, String senhaAdmin) {
        loginComoAdm(emailAdmin, senhaAdmin);
        scrollAteFim();
    }
}