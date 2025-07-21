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

public class EditTest {
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
    public void editAdmin() throws InterruptedException {
        Thread.sleep(1000);
        WebElement email, senha, buttonLogin, perfilDropdown, perfilButton, editPerfilButton, newName, newEmail,
                newPassword, saveChangesButton;

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
            Assert.fail("O perfil já foi editado.");
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
            Assert.fail("Botão de perfil não encontrado.");
            return;
        }

        perfilButton.click();

        try {
            editPerfilButton = driver.findElement(
                    By.xpath("/html/body/div[1]/div/main/div[2]/div/div/div[1]/div/div[1]/div[2]/button[1]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de editar perfil não encontrado.");
            return;
        }

        editPerfilButton.click();

        Thread.sleep(2000);

        try {
            newName = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de nome não encontrado.");
            return;
        }

        try {
            newEmail = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado.");
            return;
        }

        try {
            newPassword = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[2]/div/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }

        newName.sendKeys("Julio Guimaraes");
        newEmail.sendKeys("julio.guimaraes@gmail.com");
        newPassword.sendKeys("jul1o6u1m4r43s");

        try {
            saveChangesButton = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[3]/button[2]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de salvar alterações não encontrado.");
            return;
        }

        saveChangesButton.click();

        Thread.sleep(2000);

        try {
            WebElement successElement = driver
                    .findElement(By.xpath("//html/body/div[1]/div/main/div[2]/div[1]/strong"));
            String actualText = successElement.getText();
            String expectedText = "Perfil atualizado com sucesso! Você será redirecionado para realizar login novamente";

            if (actualText.equals(expectedText)) {
                WebElement parentDiv = successElement.findElement(By.xpath("./.."));
                String classValue = parentDiv.getAttribute("class");

                if (classValue == null || !classValue.contains("show")) {
                    Assert.fail("A mensagem de sucesso não foi exibida");
                }

                System.out.println("Perfil atualizado com sucesso!");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

        Thread.sleep(5000);

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

        email.sendKeys("julio.guimaraes@gmail.com");
        senha.sendKeys("jul1o6u1m4r43s");

        try {
            buttonLogin = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }

        buttonLogin.click();

        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login com as novas credenciais feito com sucesso!");
        } else {
            Assert.fail("Login com as novas credenciais falhou!");
        }
    }

    @Test
    public void editGerente() throws InterruptedException {
        Thread.sleep(1000);
        WebElement email, senha, buttonLogin, perfilDropdown, perfilButton, editPerfilButton, newName, newEmail,
                newPassword, saveChangesButton;

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

        email.sendKeys("SergioAdMelo@gmail.com");
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
            Assert.fail("O perfil já foi editado.");
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
            Assert.fail("Botão de perfil não encontrado.");
            return;
        }

        perfilButton.click();

        try {
            editPerfilButton = driver.findElement(
                    By.xpath("/html/body/div[1]/div/main/div[2]/div/div/div[1]/div/div[1]/div[2]/button[1]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de editar perfil não encontrado");
            return;
        }

        editPerfilButton.click();

        Thread.sleep(2000);

        try {
            newName = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de nome não encontrado");
            return;
        }

        try {
            newEmail = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado");
            return;
        }

        try {
            newPassword = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[2]/div/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado");
            return;
        }

        newName.sendKeys("Sergio Adriani Pereira");
        newEmail.sendKeys("SergioAdriani@gmail.com");
        newPassword.sendKeys("Lorem123456789");

        try {
            saveChangesButton = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[3]/button[2]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de salvar alterações não encontrado.");
            return;
        }

        saveChangesButton.click();

        Thread.sleep(2000);

        try {
            WebElement successElement = driver
                    .findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]/strong"));
            String actualText = successElement.getText();
            String expectedText = "Perfil atualizado com sucesso! Você será redirecionado para realizar login novamente";

            if (actualText.equals(expectedText)) {
                WebElement parentDiv = successElement.findElement(By.xpath("./.."));
                String classValue = parentDiv.getAttribute("class");

                if (classValue == null || !classValue.contains("show")) {
                    Assert.fail("A mensagem de sucesso não foi exibida");
                }

                System.out.println("Perfil atualizado com sucesso!");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

        Thread.sleep(2000);

        try {
            email = driver.findElement(By.id("email"));
            ;
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado");
            return;
        }
        try {
            senha = driver.findElement(By.id("senha"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado");
            return;
        }

        email.sendKeys("SergioAdriani@gmail.com");
        senha.sendKeys("Lorem123456789");

        try {
            buttonLogin = driver.findElement(By.cssSelector("[data-login-btn]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }

        buttonLogin.click();

        Thread.sleep(2000);

        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login com as novas credenciais feito com sucesso!");
        } else {
            Assert.fail("Login com as novas credenciais falhou!");
        }
    }

    @Test
    public void editColaborador() throws InterruptedException {
        Thread.sleep(1000);
        WebElement email, senha, buttonLogin, perfilDropdown, perfilButton, editPerfilButton, newName, newEmail,
                newPassword, saveChangesButton;

        try {
            email = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado");
            return;
        }
        try {
            senha = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado.");
            return;
        }

        email.sendKeys("carlos.meliodas@gmail.com");
        senha.sendKeys("12345678910");

        try {
            buttonLogin = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }
        buttonLogin.click();

        Thread.sleep(2000);

        String urlAtual = driver.getCurrentUrl();
        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login realizado com sucesso!");
        } else {
            Assert.fail("O perfil já foi editado");
        }

        try {
            perfilDropdown = driver.findElement(By.xpath("/html/body/div[1]/div/main/div[1]/div"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de dropdown não encontrado");
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
            editPerfilButton = driver.findElement(
                    By.xpath("/html/body/div[1]/div/main/div[2]/div/div/div[1]/div/div[1]/div[2]/button[1]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de editar perfil não encontrado");
            return;
        }

        editPerfilButton.click();

        Thread.sleep(2000);

        try {
            newName = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[1]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de nome não encontrado");
            return;
        }

        try {
            newEmail = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[1]/div[2]/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de email não encontrado");
            return;
        }

        try {
            newPassword = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[2]/div[2]/div/input"));
        } catch (NoSuchElementException e) {
            Assert.fail("Campo de senha não encontrado");
            return;
        }

        newName.sendKeys("Carlos Alberto de Oliveira");
        newEmail.sendKeys("carlos.oliveira@gmail.com");
        newPassword.sendKeys("abcdefghijklmno");

        try {
            saveChangesButton = driver.findElement(By.xpath("/html/body/div[2]/div/form/div/div[3]/button[2]"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de salvar alterações não encontrado.");
            return;
        }

        saveChangesButton.click();

        Thread.sleep(2000);

        try {
            WebElement successElement = driver
                    .findElement(By.xpath("/html/body/div[1]/div/main/div[2]/div[1]/strong"));
            String actualText = successElement.getText();
            String expectedText = "Perfil atualizado com sucesso! Você será redirecionado para realizar login novamente";

            if (actualText.equals(expectedText)) {
                WebElement parentDiv = successElement.findElement(By.xpath("./.."));
                String classValue = parentDiv.getAttribute("class");

                if (classValue == null || !classValue.contains("show")) {
                    Assert.fail("A mensagem de sucesso não foi exibida");
                }

                System.out.println("Perfil atualizado com sucesso!");
            }

        } catch (NoSuchElementException e) {
            Assert.fail("Mensagem de sucesso não encontrada");
        }

        Thread.sleep(2000);

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

        email.sendKeys("carlos.oliveira@gmail.com");
        senha.sendKeys("abcdefghijklmno");

        try {
            buttonLogin = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/button"));
        } catch (NoSuchElementException e) {
            Assert.fail("Botão de submissão não encontrado.");
            return;
        }

        buttonLogin.click();

        if (urlAtual.contains("/dashboard")) {
            System.out.println("Login com as novas credenciais feito com sucesso!");
        } else {
            Assert.fail("Login com as novas credenciais falhou!");
        }
    }
}
