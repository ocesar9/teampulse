package admin;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class AdminRegistrationTest {
    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/acesso/cadastroadmin");
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }

    @Test
    public void registerAdmin() throws InterruptedException {
        Thread.sleep(1000);

        WebElement email, username, password, button;

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

        email.sendKeys("lorem.ipsum@gmail.com");
        username.sendKeys("Lorem Ipsum");
        password.sendKeys("Lorem12345");

        try {
            button = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[5]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }

        button.click();
        Thread.sleep(1000); // espera a resposta aparecer

        try {
            WebElement successMessage = driver
                    .findElement(By.xpath("//*[contains(text(), 'Cadastro realizado com sucesso')]"));
            String classValue = successMessage.getAttribute("class");

            if (classValue != null && !classValue.contains("d-none")) {
                System.out.println("Cadastro realizado com sucesso!");
            } else {
                Assert.fail("A mensagem de sucesso está oculta (possui 'd-none').");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada.");
        }
    }
}
