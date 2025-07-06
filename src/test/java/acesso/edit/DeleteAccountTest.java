package acesso.edit;

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

public class DeleteAccountTest {
    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/acesso/login.html");
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
    public void deleteOwnAccount() throws InterruptedException {
        WebElement email, senha, buttonLogin, perfilDropdown, perfilButton, deleteAccountButton, confirmDelete,
                deleteAcc;

        try {
            email = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }
        try {
            senha = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }

        email.sendKeys("lorem.ipsum@gmail.com");
        senha.sendKeys("Lorem12345");

        try {
            buttonLogin = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }
        buttonLogin.click();

        Thread.sleep(3000);

        String urlAtual = driver.getCurrentUrl();
        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login realizado com sucesso!");
        } else {
            Assert.fail("Credenciais de acesso estão inválidas");
        }

        try {
            perfilDropdown = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[1]/div"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de dropdown não encontrado.");
            return;
        }

        perfilDropdown.click();

        try {
            perfilButton = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[1]/div/ul/li[1]/a"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de perfil não encontrado");
            return;
        }

        perfilButton.click();

        try {
            deleteAccountButton = driver.findElement(
                    By.xpath("/html/body/div[1]/div/main/div[2]/div/div/div[1]/div/div[1]/div[2]/button[2]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de deletar conta não encontrado");
            return;
        }

        deleteAccountButton.click();

        Thread.sleep(2000);

        try {
            confirmDelete = driver.findElement(By.id("confirmDelete"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de confirmação de exclusão não encontrado");
            return;
        }

        confirmDelete.sendKeys("DELETAR");

        try {
            deleteAcc = driver.findElement(By.id("deleteBtn"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de confirmar deleção não encontrado");
            return;
        }

        deleteAcc.click();

        Thread.sleep(2000);

        try {
            WebElement ErrorElement = driver
                    .findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]/strong"));
            String actualText = ErrorElement.getText();
            String expectedText = "Não é possível deletar sua própria conta";

            if (actualText.equals(expectedText)) {
                WebElement parentDiv = ErrorElement.findElement(By.xpath("./.."));
                String classValue = parentDiv.getAttribute("class");

                if (classValue == null || !classValue.contains("show")) {
                    Assert.fail("A mensagem de erro não foi exibida");
                }

                System.out.println("Ação interrompida com sucesso");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de erro não encontrada");
        }
    }
}
