package acesso.edit;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class UserActions {
    WebDriver driver;
    WebDriverWait wait;

    // --- Centralização dos seletores ---
    By emailLogin = By.id("email");
    By senhaLogin = By.id("senha");
    By loginBtn = By.cssSelector("[data-login-btn]");
    By btnOpenModalList = By.id("test-btn-edit-user");
    By btnOpenModalDeleteList = By.cssSelector("[data-btn-delete-user]");
    By userEmail = By.id("editUserEmail");
    By userName = By.id("editUserName");
    By btnEditUser = By.id("btnActionEditUser");
    By btnDeleteUser = By.id("confirmDeleteBtn"); // Modifique se houver id correto
    By sucessoMsg = By.xpath("//*[contains(., 'Usuário Rogério Melo atualizado com sucesso')]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void editUser() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        scrollAteFim();

        WebElement btnOpenModal = getModalEditButton(1);
        btnOpenModal.click();

        WebElement nome = wait.until(ExpectedConditions.visibilityOfElementLocated(userName));
        WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(userEmail));
        nome.clear();
        nome.sendKeys("Rogério Melo");
        email.clear();
        email.sendKeys("Rogerio.Holanda@gmail.com");

        WebElement btnEditar = wait.until(ExpectedConditions.elementToBeClickable(btnEditUser));
        btnEditar.click();

        validaMensagem("Usuário Rogério Melo atualizado com sucesso");
    }

    @Test
    public void deleteUser() {
        loginComoAdm("lorem.ipsum@gmail.com", "Lorem12345");
        scrollAteFim();

        WebElement btnOpenModal = getModalDeleteButton(1);
        btnOpenModal.click();

        WebElement btnDeletar = wait.until(ExpectedConditions.elementToBeClickable(btnDeleteUser));
        btnDeletar.click();

        validaMensagem("Deletado com sucesso!");
    }

    private void loginComoAdm(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailLogin));
        driver.findElement(emailLogin).clear();
        driver.findElement(senhaLogin).clear();
        driver.findElement(emailLogin).sendKeys(email);
        driver.findElement(senhaLogin).sendKeys(password);
        driver.findElement(loginBtn).click();
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("/login")));
    }

    private void scrollAteFim() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalList));
    }

    private WebElement getModalEditButton(int index) {
        List<WebElement> buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalList));
        Assert.assertTrue(buttons.size() > index, "Índice de botão para modal não existe na lista.");
        return buttons.get(index);
    }

    private WebElement getModalDeleteButton(int index) {
        List<WebElement> buttons = wait
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(btnOpenModalDeleteList));
        Assert.assertTrue(buttons.size() > index, "Índice de botão para modal não existe na lista.");
        return buttons.get(index);
    }

    private void validaMensagem(String textoEsperado) {
        try {
            WebElement message = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(., '" + textoEsperado + "')]")));
            Assert.assertTrue(message.isDisplayed(), "Mensagem de sucesso não está visível");
        } catch (TimeoutException e) {
            Assert.fail("Mensagem \"" + textoEsperado + "\" não encontrada dentro do tempo esperado");
        }
    }
}