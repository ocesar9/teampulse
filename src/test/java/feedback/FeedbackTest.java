package feedback;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class FeedbackTest {
    WebDriver driver;
    WebDriverWait wait;

    // Locators
    By emailInput = By.id("email");
    By passwordInput = By.id("senha");
    By loginBtn = By.cssSelector("[data-login-btn]");
    By feedbackMenuBtn = By.id("feedbacks-link");
    By sendFeedbackBtn = By.id("createFeedbackBtn");
    By recipientDropdown = By.id("recipientSelect");
    By ratingDropdown = By.id("categorySelect");
    By feedbackMessage = By.id("feedbackMessage");
    By submitFeedbackBtn = By.id("btnActionFeedback");
    By alertDiv = By.cssSelector("[data-alert-wrapper]");
    By draftsBtn = By.id("draft-pane-button");
    By sentBtn = By.id("sent-pane-button");
    By editDraftBtn = By.cssSelector("[data-edit-draft-btn]");
    By deleteDraftBtn = By.cssSelector("[data-btn-delete-draft]");
    By sendDraftBtn = By.cssSelector("[data-send-draft-btn]");
    By draftItems = By.cssSelector("[data-draft-item]");

    @BeforeClass
    public void setUp() {
        inicializarDriver();
        navegarParaLogin();
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        limparDadosNavegador();
        navegarParaLogin();
    }

    @AfterClass
    public void tearDown() {
        fecharDriver();
    }

    @Test
    public void createDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaFeedback();
        abrirCriacaoFeedback();
        preencherFormularioFeedback(1, 4, "Quero conversar com você sobre alguns pontos de atenção que temos observado no seu desempenho recente. Sabemos que todos enfrentamos desafios no dia a dia, e é natural que existam áreas em que possamos melhorar. No seu caso, percebemos que aspectos como: cumprimento de prazos, comunicação com a equipe e organização precisam de mais atenção, pois têm impactado o andamento do trabalho.\n");
        submeterFormulario();
        validarMensagemSucesso("Sucesso! Rascunho de feedback salvo com sucesso");
        System.out.println("Rascunho criado com sucesso!");
    }

    @Test
    public void editDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaFeedback();
        abrirRascunhos();
        editarRascunho();
        preencherFormularioFeedback(1, 4, "Gostaria de reconhecer alguns pontos muito positivos que tenho observado no seu desempenho recente. Sabemos que o dia a dia apresenta muitos desafios, e é justamente por isso que quero destacar a sua dedicação e os bons resultados que vem demonstrando. \n Aspectos como: cumprimento de prazos, comunicação com a equipe e organização têm se destacado de forma significativa para o bom andamento do trabalho. Sua postura tem sido exemplar e inspira confiança em todos ao seu redor");
        submeterFormulario();
        validarMensagemSucesso("Sucesso! Rascunho atualizado com sucesso");
        System.out.println("Rascunho editado com sucesso!");
    }

    @Test
    public void deleteDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaFeedback();
        abrirRascunhos();
        editarRascunho();
        excluirRascunho();
        validarMensagemSucesso("Sucesso! Rascunho deletado com sucesso");
        System.out.println("Rascunho deletado com sucesso!");
    }

    @Test
    void sendDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaFeedback();
        abrirRascunhos();
        enviarRascunho();
        abrirEnviados();
        validarMensagemSucesso("Sucesso! Feedback enviado com sucesso");
        System.out.println("Rascunho enviado com sucesso!");
    }

    private void inicializarDriver() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private void navegarParaLogin() {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    private void limparDadosNavegador() {
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
    }

    private void fecharDriver() {
        if (driver != null)
            driver.quit();
    }

    private void loginComoGerente(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        preencherCampo(emailInput, email);
        preencherCampo(passwordInput, password);
        clicarElemento(loginBtn);
        validarLoginComSucesso();
    }

    private void preencherCampo(By campo, String valor) {
        WebElement element = driver.findElement(campo);
        element.clear();
        element.sendKeys(valor);
    }

    private void clicarElemento(By seletor) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(seletor));
        element.click();
    }

    private void validarLoginComSucesso() {
        wait.until(ExpectedConditions.presenceOfElementLocated(feedbackMenuBtn));
    }

    private void navegarParaFeedback() {
        clicarElemento(feedbackMenuBtn);
    }

    private void abrirCriacaoFeedback() {
        clicarElemento(sendFeedbackBtn);
    }

    private void abrirRascunhos() {
        clicarElemento(draftsBtn);
    }

    private void abrirEnviados() {
        clicarElemento(sentBtn);
    }

    private void editarRascunho() {
        clicarElemento(editDraftBtn);
    }

    private void excluirRascunho() {
        clicarElemento(deleteDraftBtn);
    }

    private void enviarRascunho() {
        clicarElemento(sendDraftBtn);
    }

    private void preencherFormularioFeedback(int indiceDestinatario, int indiceCategoria, String mensagem) {
        selecionarDestinatario(indiceDestinatario);
        selecionarCategoria(indiceCategoria);
        preencherMensagem(mensagem);
    }

    private void selecionarDestinatario(int indice) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(recipientDropdown));
        new Select(dropdown).selectByIndex(indice);
    }

    private void selecionarCategoria(int indice) {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(ratingDropdown));
        new Select(dropdown).selectByIndex(indice);
    }

    private void preencherMensagem(String mensagem) {
        WebElement messageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
        String textoAtual = messageElement.getAttribute("value");

        if (textoAtual == null || !textoAtual.contains(mensagem.substring(0, Math.min(20, mensagem.length())))) {
            messageElement.clear();
            messageElement.sendKeys(mensagem);
        }
    }

    private void submeterFormulario() {
        clicarElemento(submitFeedbackBtn);
    }

    private void validarMensagemSucesso(String textoEsperado) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String textoCompleto = alert.getText();
            Assert.assertTrue(
                    textoCompleto.contains(textoEsperado),
                    "Texto esperado não encontrado na mensagem (" + textoEsperado + "). Texto recebido: " + textoCompleto);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem não encontrada/exibida: " + e.getMessage());
        }
    }

    // Métodos públicos para reutilização em outros testes
    public void criarRascunhoFeedback(String email, String senha, int indiceDestinatario, int indiceCategoria, String mensagem) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirCriacaoFeedback();
        preencherFormularioFeedback(indiceDestinatario, indiceCategoria, mensagem);
        submeterFormulario();
        validarMensagemSucesso("Sucesso! Rascunho de feedback salvo com sucesso");
    }

    public void editarRascunhoExistente(String email, String senha, String novaMensagem) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirRascunhos();
        editarRascunho();
        preencherMensagem(novaMensagem);
        submeterFormulario();
        validarMensagemSucesso("Sucesso! Rascunho atualizado com sucesso");
    }

    public void excluirRascunhoExistente(String email, String senha) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirRascunhos();
        editarRascunho();
        excluirRascunho();
        validarMensagemSucesso("Sucesso! Rascunho deletado com sucesso");
    }

    public void enviarRascunhoExistente(String email, String senha) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirRascunhos();
        enviarRascunho();
        abrirEnviados();
        validarMensagemSucesso("Sucesso! Feedback enviado com sucesso");
    }

    public int obterQuantidadeRascunhos() {
        abrirRascunhos();
        wait.until(ExpectedConditions.visibilityOfElementLocated(draftItems));
        return driver.findElements(draftItems).size();
    }

    public boolean rascunhoExiste() {
        try {
            abrirRascunhos();
            wait.until(ExpectedConditions.visibilityOfElementLocated(draftItems));
            return driver.findElements(draftItems).size() > 0;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void testarFeedbackComMensagemVazia(String email, String senha, int indiceDestinatario, int indiceCategoria) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirCriacaoFeedback();
        selecionarDestinatario(indiceDestinatario);
        selecionarCategoria(indiceCategoria);
        preencherMensagem("");
        submeterFormulario();
        validarMensagemErro("Mensagem não pode estar vazia");
    }

    private void validarMensagemErro(String mensagemEsperada) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String textoCompleto = alert.getText();
            Assert.assertTrue(
                    textoCompleto.contains(mensagemEsperada),
                    "Texto de erro esperado não encontrado. Texto recebido: " + textoCompleto);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem de erro não encontrada/exibida: " + e.getMessage());
        }
    }

    public void testarFeedbackSemDestinatario(String email, String senha, String mensagem) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirCriacaoFeedback();
        selecionarCategoria(1);
        preencherMensagem(mensagem);
        submeterFormulario();
        validarMensagemErro("Selecione um destinatário");
    }

    public void testarFeedbackSemCategoria(String email, String senha, String mensagem) {
        loginComoGerente(email, senha);
        navegarParaFeedback();
        abrirCriacaoFeedback();
        selecionarDestinatario(1);
        preencherMensagem(mensagem);
        submeterFormulario();
        validarMensagemErro("Selecione uma categoria");
    }
}