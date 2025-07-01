package acesso.cadastro;

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

public class RegistrationWithAuthTest {

    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
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
    public void registerColaborador() throws InterruptedException {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        driver.get("http://localhost:8080/acesso/cadastro");
        WebElement email, cargo, username, password, button;

        try {
            email = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }

        try {
            username = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de username não encontrado.");
            return;
        }

        try {
            password = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }

        try {
            cargo = driver.findElement(By.id("select-cargo-colaborador"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de cargo não encontrado.");
            return;
        }

        email.sendKeys("carlos.meliodas@gmail.com");
        username.sendKeys("Carlos Alberto");
        password.sendKeys("12345678910");
        cargo.click();

        try {
            button = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[5]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de cadastro não encontrado.");
            return;
        }

        button.click();
        Thread.sleep(1000);

        try {
            WebElement Message = driver
                    .findElement(By.xpath("//*[contains(text(), 'Cadastro realizado com sucesso')]"));
            String classValue = Message.getDomAttribute("class");

            if (classValue != null && !classValue.contains("d-none")) {
                System.out.println("Cadastro realizado com sucesso");
            } else {
                Assert.fail("A mensagem de success está oculta (possui 'd-none').");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("A ação não foi bem sucedida");
        }
    }

    @Test
    public void registerGerente() throws InterruptedException {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");

        driver.get("http://localhost:8080/acesso/cadastro");

        WebElement email, cargo, username, password, button;

        try {
            email = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }

        try {
            username = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de username não encontrado.");
            return;
        }

        try {
            password = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }

        try {
            cargo = driver.findElement(By.id("select-cargo-gerente"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de cargo não encontrado.");
            return;
        }

        email.sendKeys("SergioAdMelo@gmail.com");
        username.sendKeys("Sergio Adriani");
        password.sendKeys("Lorem12345");
        cargo.click();

        try {
            button = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[5]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }

        button.click();
        Thread.sleep(1000);

        try {
            WebElement Message = driver
                    .findElement(By.xpath("//*[contains(text(), 'Cadastro realizado com sucesso')]"));
            String classValue = Message.getDomAttribute("class");

            if (classValue != null && !classValue.contains("d-none")) {
                System.out.println("Cadastro realizado com sucesso");
            } else {
                Assert.fail("A mensagem de success está oculta (possui 'd-none').");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("A ação não foi bem sucedida");
        }
    }

    public void loginComoAdm(String email, String password) throws InterruptedException {
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
