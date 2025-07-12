package feedback;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
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
        driver.get("http://localhost:8080/acesso/login");
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
    public void sendFeedbackGerenteColaborador() throws InterruptedException {
        try{
            loginComoGerente("SergioAdMelo@gmail.com","Lorem12345");
        }catch (NoSuchElementException e){
            Assert.fail("Credenciais de Gerente inválidas.");
            return;
        }

        Thread.sleep(1000);

        WebElement feedbackButton, sendFeedbackButton, userDropdown, ratingDropdown, message, submitFeedbackButton;

        try {
            feedbackButton = driver.findElement(By.xpath("/html/body/div[1]/div/nav/div/ul/li[2]/a"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão Feedback não encontrado.");
            return;
        }

        feedbackButton.click();

        Thread.sleep(2000);

        try {
            sendFeedbackButton = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]/div/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão Enviar Feedback não encontrado.");
            return;
        }

        sendFeedbackButton.click();

        try {
            userDropdown = driver.findElement(By.xpath("/html/body/div[3]/div/div/form/div[2]/div[2]/div[1]/select"));
            Select select = new Select(userDropdown);
            select.selectByIndex(1);
        } catch (NoSuchElementException e) {
            Assert.fail("Dropdown destinatário não encontrado.");
        }

        try {
            ratingDropdown = driver.findElement(By.xpath("/html/body/div[3]/div/div/form/div[2]/div[2]/div[2]/select"));
            Select select = new Select(ratingDropdown);
            select.selectByIndex(3);
        } catch (NoSuchElementException e) {
            Assert.fail("Dropdown de avaliação não encontrado.");
        }

        try {
            message = driver.findElement(By.xpath("/html/body/div[3]/div/div/form/div[2]/div[3]/textarea"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de mensagem não encontrado.");
            return;
        }

        message.sendKeys("Quero conversar com você sobre alguns pontos de atenção que temos observado no seu desempenho recente. Sabemos que todos enfrentamos desafios no dia a dia, e é natural que existam áreas em que possamos melhorar. No seu caso, percebemos que aspectos como [ex: cumprimento de prazos, comunicação com a equipe, organização, etc.] precisam de mais atenção, pois têm impactado o andamento do trabalho.\n" + "\n" + "Nosso objetivo com esse feedback é apoiar o seu desenvolvimento e garantir que você tenha as condições e o suporte necessários para evoluir. Acreditamos no seu potencial e estamos à disposição para construir, junto com você, um plano de melhoria.");

        try {
            submitFeedbackButton = driver.findElement(By.xpath("/html/body/div[3]/div/div/form/div[3]/button[2]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão Submeter Feedback não encontrado.");
            return;
        }

        submitFeedbackButton.click();

        Thread.sleep(2000);

        try {
            WebElement alertDiv = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]"));

            String fullText = alertDiv.getText();

            if (!fullText.contains("Sucesso!") || !fullText.contains("Feedback enviado com sucesso")) {
                Assert.fail("Texto esperado não encontrado na mensagem de sucesso.");
            }

            String classValue = alertDiv.getAttribute("class");
            if (classValue == null || !classValue.contains("show")) {
                Assert.fail("A mensagem de sucesso não foi exibida");
            }

            System.out.println("Feedback enviado com sucesso!");


        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

    }

    public void loginComoGerente(String email, String password) throws InterruptedException {
        driver.get("http://localhost:8080/acesso/login");
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
