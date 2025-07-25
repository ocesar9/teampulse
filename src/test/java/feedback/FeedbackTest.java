package feedback;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FeedbackTest {
    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/login");
    }

    @BeforeMethod
    public void reloadBeforeEach() throws InterruptedException {
        driver.navigate().refresh();
        Thread.sleep(500);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }

    @Test
    public void createDraft() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        } catch (InterruptedException e) {
            Assert.fail("Credenciais de gerente inválidas.");
            return;
        }

        try {
            WebElement feedbackButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("feedbacks-link")));
            feedbackButton.click();

            WebElement sendFeedbackButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("createFeedbackBtn")));
            sendFeedbackButton.click();

            WebElement userDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("recipientSelect")));
            new Select(userDropdown).selectByIndex(1);

            WebElement ratingDropdown = wait.until(ExpectedConditions.elementToBeClickable(By.id("categorySelect")));
            new Select(ratingDropdown).selectByIndex(4);

            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("feedbackMessage")));
            String messageText = "Quero conversar com você sobre alguns pontos de atenção que temos observado no seu desempenho recente. Sabemos que todos enfrentamos desafios no dia a dia, e é natural que existam áreas em que possamos melhorar. No seu caso, percebemos que aspectos como: cumprimento de prazos, comunicação com a equipe e organização precisam de mais atenção, pois têm impactado o andamento do trabalho.\n";

            String atual = message.getDomAttribute("value");
            if (atual == null || !atual.contains("Quero conversar com você")) {

                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript("arguments[0].value = arguments[1];", message, messageText);
            }

            WebElement submitFeedbackButton = wait
                    .until(ExpectedConditions.elementToBeClickable(By.id("btnActionFeedback")));
            submitFeedbackButton.click();

            WebElement alertDiv = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]")));

            alertDiv = wait.until(ExpectedConditions
                    .visibilityOfElementLocated(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]")));

            String fullText = alertDiv.getText();

            if (!fullText.contains("Sucesso!") || !fullText.contains("Rascunho de feedback salvo com sucesso")) {
                Assert.fail("Texto esperado não encontrado na mensagem de sucesso");
            }

            String classValue = alertDiv.getDomAttribute("class");
            if (classValue == null || !classValue.contains("show")) {
                Assert.fail("A mensagem de sucesso não foi exibida");
            }

            System.out.println("Rascunho criado com sucesso!");

        } catch (TimeoutException e) {
            Assert.fail("Tempo esgotado ao aguardar elementos da página: " + e.getMessage());
        } catch (NoSuchElementException e) {
            Assert.fail("Elemento não encontrado: " + e.getMessage());
        }
    }

    @Test
    public void editDraft() throws InterruptedException {
        try {
            loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        } catch (NoSuchElementException e) {
            Assert.fail("Credenciais do gerente inválidas");
            return;
        }

        Thread.sleep(1000);

        WebElement feedbackMenuButton, draftArea, userDropdown, cardEdit, ratingDropdown, message, submitFeedbackButton;

        try {
            feedbackMenuButton = driver.findElement(By.id("feedbacks-link"));
        } catch (NoSuchElementException e) {
            Assert.fail("Erro. Acesso para o meu de feedbacks via sidebar não realizado. Botão não encontrado");
            return;
        }

        feedbackMenuButton.click();

        Thread.sleep(2000);

        try {
            draftArea = driver.findElement(By.id("draft-pane-button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de acessar rascunhos não encontrado");
            return;
        }

        draftArea.click();

        try {
            cardEdit = driver.findElement(By.cssSelector("[data-edit-draft-btn]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Rascunho não disponível visualmente para o usuário");
            return;
        }

        cardEdit.click();

        try {
            userDropdown = driver.findElement(By.id("recipientSelect"));
            Select select = new Select(userDropdown);
            select.selectByIndex(1);
        } catch (NoSuchElementException e) {
            Assert.fail("Dropdown de destinatário não encontrado");
        }

        try {
            ratingDropdown = driver.findElement(By.id("categorySelect"));
            Select select = new Select(ratingDropdown);
            select.selectByIndex(4);
        } catch (NoSuchElementException e) {
            Assert.fail("Dropdown de avaliação não encontrado.");
        }

        try {
            message = driver.findElement(By.id("feedbackMessage"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de mensagem não encontrado.");
            return;
        }
        message.clear();
        message.sendKeys(
                "Gostaríamos de conversar com você para reconhecer alguns pontos muito positivos que temos observado no seu desempenho recente. Sabemos que o dia a dia apresenta muitos desafios, e é justamente por isso que queremos destacar a sua dedicação e os bons resultados que vem demonstrando. \n Aspectos como: cumprimento de prazos, comunicação com a equipe e organização têm se destacado e contribuído de forma significativa para o bom andamento do trabalho. Sua postura tem sido exemplar e inspira confiança em todos ao seu redor.");

        Thread.sleep(2000);

        try {
            submitFeedbackButton = driver.findElement(By.id("btnActionFeedback"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de editar rascunho não encontrado");
            return;
        }

        submitFeedbackButton.click();

        Thread.sleep(2000);

        try {
            WebElement alertDiv = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]"));

            String fullText = alertDiv.getText();

            if (!fullText.contains("Sucesso!") || !fullText.contains("Rascunho atualizado com sucesso")) {
                Assert.fail("Texto esperado não encontrado na mensagem de sucesso");
            }

            String classValue = alertDiv.getDomAttribute("class");
            if (classValue == null || !classValue.contains("show")) {
                Assert.fail("A mensagem de sucesso não foi exibida");
            }

            System.out.println("Rascunho editado com sucesso!");

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

    }

    @Test
    public void deleteDraft() throws InterruptedException {
        try {
            loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        } catch (NoSuchElementException e) {
            Assert.fail("Credenciais do gerente inválidas");
            return;
        }

        Thread.sleep(1000);

        WebElement feedbackMenuButton, draftArea, cardEdit, btnDelete;

        try {
            feedbackMenuButton = driver.findElement(By.id("feedbacks-link"));
        } catch (NoSuchElementException e) {
            Assert.fail("Erro. Acesso para o meu de feedbacks via sidebar não realizado. Botão não encontrado");
            return;
        }

        feedbackMenuButton.click();

        Thread.sleep(2000);

        try {
            draftArea = driver.findElement(By.id("draft-pane-button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de acessar rascunhos não encontrado");
            return;
        }

        draftArea.click();

        try {
            cardEdit = driver.findElement(By.cssSelector("[data-edit-draft-btn]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Rascunho não disponível visualmente para o usuário");
            return;
        }

        cardEdit.click();

        Thread.sleep(2000);

        try {
            btnDelete = driver.findElement(By.cssSelector("[data-btn-delete-draft]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de deletar rascunho não disponível visualmente");
            return;
        }

        btnDelete.click();

        Thread.sleep(2000);

        try {
            WebElement alertDiv = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]"));

            String fullText = alertDiv.getText();

            if (!fullText.contains("Sucesso!") || !fullText.contains("Rascunho deletado com sucesso")) {
                Assert.fail("Texto esperado não encontrado na mensagem de sucesso");
            }

            String classValue = alertDiv.getDomAttribute("class");
            if (classValue == null || !classValue.contains("show")) {
                Assert.fail("A mensagem de sucesso não foi exibida");
            }

            System.out.println("Rascunho deletado com sucesso!");

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

    }

    public void loginComoGerente(String email, String password) throws InterruptedException {
        driver.get("http://localhost:8080/login");
        WebElement emailInput = driver.findElement(By.id("email"));
        WebElement passwordInput = driver.findElement(By.id("senha"));
        WebElement loginBtn = driver.findElement(By.cssSelector("[data-login-btn]"));
        emailInput.clear();
        passwordInput.clear();
        emailInput.sendKeys(email);
        passwordInput.sendKeys(password);
        loginBtn.click();
        Thread.sleep(2000);
    }
}
