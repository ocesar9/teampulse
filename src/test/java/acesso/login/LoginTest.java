package acesso.login;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginTest {

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
        Thread.sleep(2000);
        driver.quit();
    }

    @Test
    public void loginComDadosValidos() throws InterruptedException {
        Thread.sleep(1000);
        WebElement email, senha, button;
        try {
            email = driver.findElement(By.id("email"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }
        try {
            senha = driver.findElement(By.id("senha"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }
        email.sendKeys("lorem.ipsum@gmail.com");
        senha.sendKeys("Lorem12345");

        try {
            button = driver.findElement(By.cssSelector("[data-login-btn]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }
        button.click();

        Thread.sleep(3000);

        String urlAtual = driver.getCurrentUrl();
        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login realizado com sucesso!");
        } else {
            Assert.fail("O login não redirecionou para a dashboard.");
        }
    }

    @Test
    public void loginComDadosInvalidos() throws InterruptedException {
        WebElement email, senha, button;
        try {
            email = driver.findElement(By.id("email"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }
        try {
            senha = driver.findElement(By.id("senha"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }
        email.sendKeys("invalido@exemplo.com");
        senha.sendKeys("senhaErrada");

        try {
            button = driver.findElement(By.cssSelector("[data-login-btn]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }
        button.click();

        Thread.sleep(1000);

        try {
            WebElement alerta = driver.findElement(By.cssSelector("[data-login-error-list]"));
            String classValue = alerta.getAttribute("class");
            if (!classValue.contains("d-none") && alerta.isDisplayed()) {
                System.out.println("Mensagem de erro de login exibida!");
            } else {
                Assert.fail("Mensagem de erro de login está oculta (possui 'd-none').");
            }
        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de erro não encontrada.");
        }
    }
}