package admin;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UserActions {
    static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }

    @Test
    public void editUser() throws InterruptedException {

        WebElement btnOpenModal, email, nome, btnEditUser;

        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");

        Thread.sleep(1000);

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        Thread.sleep(3000);

        try {
            List<WebElement> buttons = driver.findElements(By.id("test-btn-edit-user"));
            btnOpenModal = buttons.get(1);
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de abrir modal não encontrado.");
            return;
        }

        btnOpenModal.click();

        Thread.sleep(2000);

        try {
            email = driver.findElement(By.id("editUserEmail"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }

        try {
            nome = driver.findElement(By.id("editUserName"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de nome não encontrado.");
            return;
        }

        nome.clear();
        nome.sendKeys("Rogério Melo");
        email.clear();
        email.sendKeys("Rogerio.Holanda@gmail.com");

        try {
            btnEditUser = driver.findElement(By.id("btnActionEditUser"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de edição não encontrado");
            return;
        }

        btnEditUser.click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(., 'Usuário Rogério Melo atualizado com sucesso')]")));

            Assert.assertTrue(message.isDisplayed(), "Mensagem de sucesso não está visível");
        } catch (TimeoutException e) {
            Assert.fail("Mensagem de sucesso não encontrada dentro do tempo esperado");
        }
    }

    @Test
    public void deleteUser() throws InterruptedException {

        WebElement btnOpenModal, btnDelete, btnEditUser;

        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");

        Thread.sleep(1000);

        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        Thread.sleep(3000);

        try {
            List<WebElement> buttons = driver.findElements(By.id("test-btn-edit-user"));
            btnOpenModal = buttons.get(1);
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de abrir modal não encontrado.");
            return;
        }

        btnOpenModal.click();

        Thread.sleep(1000);

        try {
            btnEditUser = driver.findElement(By.id("btnActionEditUser"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de edição não encontrado");
            return;
        }

        btnEditUser.click();

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(., 'Usuário Rogério Melo atualizado com sucesso')]")));

            Assert.assertTrue(message.isDisplayed(), "Mensagem de sucesso não está visível");
        } catch (TimeoutException e) {
            Assert.fail("Mensagem de sucesso não encontrada dentro do tempo esperado");
        }
    }

    public void loginComoAdm(String email, String password) throws InterruptedException {

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