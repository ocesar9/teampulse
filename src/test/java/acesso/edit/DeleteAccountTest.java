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

    By loginEmail = By.id("email");
    By loginSenha = By.id("senha");
    By loginButton = By.cssSelector("[data-login-btn]");

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

        abreMenuPerfil();
        deletaContaTentativa();

        validaErro("Não é possível deletar sua própria conta");
        System.out.println("Ação interrompida com sucesso");
    }

    private void login(String email, String senha) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginEmail));
        WebElement emailField = driver.findElement(loginEmail);
        WebElement senhaField = driver.findElement(loginSenha);
        WebElement btn = driver.findElement(loginButton);
        emailField.clear();
        senhaField.clear();
        emailField.sendKeys(email);
        senhaField.sendKeys(senha);
        btn.click();
        boolean entrou = wait.until(ExpectedConditions.urlContains("/dashboard"));
        Assert.assertTrue(entrou, "Login falhou para o usuário: " + email);
    }

    private void abreMenuPerfil() {
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(menuDropdown));
        dropdown.click();
        WebElement perfil = wait.until(ExpectedConditions.elementToBeClickable(perfilLink));
        perfil.click();
    }

    private void deletaContaTentativa() {
        WebElement delBtn = wait.until(ExpectedConditions.elementToBeClickable(deleteAccountBtn));
        delBtn.click();

        WebElement confirmField = wait.until(ExpectedConditions.visibilityOfElementLocated(confirmDeleteField));
        confirmField.clear();
        confirmField.sendKeys("DELETAR");

        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(confirmDeleteBtn));
        confirmBtn.click();
    }

    private void validaErro(String esperado) {
        WebElement errorStrong = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsgStrong));
        String actualText = errorStrong.getText();
        Assert.assertEquals(actualText, esperado, "Mensagem de erro inesperada!");
        WebElement parentDiv = errorStrong.findElement(By.xpath("./.."));
        String classValue = parentDiv.getAttribute("class");
        Assert.assertTrue(classValue != null && classValue.contains("show"),
                "A mensagem de erro não foi exibida corretamente!");
    }
}