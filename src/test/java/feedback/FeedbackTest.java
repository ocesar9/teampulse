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

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("http://localhost:8080/login");
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void createDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");

        clickAndWait(feedbackMenuBtn);
        clickAndWait(sendFeedbackBtn);

        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(recipientDropdown));
        new Select(userDropdown).selectByIndex(1);

        WebElement ratingDropdownEl = wait.until(ExpectedConditions.elementToBeClickable(ratingDropdown));
        new Select(ratingDropdownEl).selectByIndex(4);

        WebElement messageEl = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
        String messageText = "Quero conversar com você sobre alguns pontos de atenção que temos observado no seu desempenho recente. Sabemos que todos enfrentamos desafios no dia a dia, e é natural que existam áreas em que possamos melhorar. No seu caso, percebemos que aspectos como: cumprimento de prazos, comunicação com a equipe e organização precisam de mais atenção, pois têm impactado o andamento do trabalho.\n";
        String atual = messageEl.getAttribute("value");
        if (atual == null || !atual.contains("Quero conversar com você")) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", messageEl, messageText);
        }

        clickAndWait(submitFeedbackBtn);

        validaAlert("Sucesso! Rascunho de feedback salvo com sucesso");
        System.out.println("Rascunho criado com sucesso!");
    }

    @Test
    public void editDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");

        clickAndWait(feedbackMenuBtn);
        clickAndWait(draftsBtn);
        clickAndWait(editDraftBtn);

        WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(recipientDropdown));
        new Select(userDropdown).selectByIndex(1);

        WebElement ratingDropdownEl = wait.until(ExpectedConditions.elementToBeClickable(ratingDropdown));
        new Select(ratingDropdownEl).selectByIndex(4);

        WebElement messageEl = wait.until(ExpectedConditions.visibilityOfElementLocated(feedbackMessage));
        messageEl.clear();
        messageEl.sendKeys(
                "Gostaria de reconhecer alguns pontos muito positivos que tenho observado no seu desempenho recente. Sabemos que o dia a dia apresenta muitos desafios, e é justamente por isso que quero destacar a sua dedicação e os bons resultados que vem demonstrando. \n Aspectos como: cumprimento de prazos, comunicação com a equipe e organização têm se destacado de forma significativa para o bom andamento do trabalho. Sua postura tem sido exemplar e inspira confiança em todos ao seu redor");

        clickAndWait(submitFeedbackBtn);

        validaAlert("Sucesso! Rascunho atualizado com sucesso");
        System.out.println("Rascunho editado com sucesso!");
    }

    @Test
    public void deleteDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");

        clickAndWait(feedbackMenuBtn);
        clickAndWait(draftsBtn);
        clickAndWait(editDraftBtn);
        clickAndWait(deleteDraftBtn);

        validaAlert("Sucesso! Rascunho deletado com sucesso");
        System.out.println("Rascunho deletado com sucesso!");
    }

    @Test
    void sendDraft() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        clickAndWait(feedbackMenuBtn);
        clickAndWait(draftsBtn);
        clickAndWait(sendDraftBtn);
        clickAndWait(sentBtn);

        validaAlert("Sucesso! Feedback enviado com sucesso");
        System.out.println("Rascunho enviado com sucesso!");
    }

    private void loginComoGerente(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        driver.findElement(emailInput).clear();
        driver.findElement(passwordInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginBtn).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(feedbackMenuBtn));
    }

    private void clickAndWait(By selector) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(selector));
        el.click();
    }

    private void validaAlert(String textoEsperado) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String fullText = alert.getText();
            Assert.assertTrue(
                    fullText.contains(textoEsperado),
                    "Texto esperado não encontrado na mensagem (" + textoEsperado + "). Texto recebido: "
                            + fullText);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem não encontrada/exibida: " + e.getMessage());
        }
    }
}