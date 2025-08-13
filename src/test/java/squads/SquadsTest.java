package squads;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.*;

public class SquadsTest {
    WebDriver driver;
    WebDriverWait wait;

    // Login
    By emailInput = By.id("email");
    By passwordInput = By.id("senha");
    By loginBtn = By.cssSelector("[data-login-btn]");

    By squadsPane = By.id("teams-tab");
    By modalNewTeam = By.id("modalNewTeam");
    By modalDeleteTeam = By.cssSelector("[data-open-modal-delete-team]");
    By optionsDelete = By.cssSelector("[data-open-options-delete]");
    By teamName = By.id("teamName");
    By salvarTimeBtn = By.id("saveTeam");
    By alertDiv = By.cssSelector("[data-alerts-users-squads]");
    By confirmDeleteSquad = By.id("confirmDeleteBtn");

    By collaboratorsContainer = By.cssSelector("div.border.rounded.p-3[style*='overflow-y']");
    By collaboratorsList = By.cssSelector("[data-list-collaborators]");

    By openEditTeamBtn = By.cssSelector("[data-edit-team]");
    By teamMembersContainer = By.cssSelector("[data-team-members]");
    By teamMembersList = By.cssSelector("[data-list-collaborators]");

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        driver.get("http://localhost:8080/login");
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null)
            driver.quit();
    }

    @Test
    public void createTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        clickAndWait(squadsPane);
        clickAndWait(modalNewTeam);

        addMemberFromScrollableList("Carlos Alberto");
        addMemberFromScrollableList("Vitoria Batista");

        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(teamName));
        String name = "Bug hunters";
        try {
            nameEl.clear();
            nameEl.sendKeys(name);
        } catch (Exception e) {
            setInputValue(nameEl, name);
        }

        clickAndWait(salvarTimeBtn);
        validaAlert("criado com sucesso!");
        System.out.println("adicionado sucesso");
    }

    @Test
    public void editTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        clickAndWait(squadsPane);

        clickAndWait(openEditTeamBtn);

        removeMemberFromTeamList("Carlos Alberto");
        removeMemberFromTeamList("Vitoria Batista");

        addMemberFromScrollableList("Patrício Carvalho");
        addMemberFromScrollableList("Rosana Melo");

        WebElement nameEl = wait.until(ExpectedConditions.visibilityOfElementLocated(teamName));
        String newName = "Bug hunters 2";
        try {
            nameEl.clear();
            nameEl.sendKeys(newName);
        } catch (Exception e) {
            setInputValue(nameEl, newName);
        }

        clickAndWait(salvarTimeBtn);

        validaAlert("editado com sucesso!");
        System.out.println("time editado com sucesso");
    }

    @Test
    public void deleteTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        clickAndWait(squadsPane);
        clickAndWait(optionsDelete);
        clickAndWait(modalDeleteTeam);
        clickAndWait(confirmDeleteSquad);
        validaAlert("Deletado com sucesso!");
    }

    private void loginComoGerente(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        driver.findElement(emailInput).clear();
        driver.findElement(passwordInput).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginBtn).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(squadsPane));
    }

    private void clickAndWait(By selector) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(selector));
        try {
            el.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void validaAlert(String textoEsperado) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String fullText = alert.getText();
            Assert.assertTrue(fullText.contains(textoEsperado),
                    "Texto esperado não encontrado na mensagem (" + textoEsperado + "). Texto recebido: " + fullText);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem não encontrada/exibida: " + e.getMessage());
        }
    }

    private By addButtonForName(String name) {
        return By.xpath(
                ".//div[contains(@class,'list-group-item')]" +
                        "[.//div[contains(@class,'fw-semibold') and normalize-space(text())='" + name + "']]" +
                        "//button");
    }

    private By removeButtonForName(String name) {
        return By.xpath(
                ".//div[contains(@class,'list-group-item')]" +
                        "[.//div[contains(@class,'fw-semibold') and normalize-space(text())='" + name + "']]" +
                        "//button");
    }

    private void addMemberFromScrollableList(String personName) {
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(collaboratorsContainer));
        WebElement list = container.findElement(collaboratorsList);

        long lastScrollTop = -1;
        for (int i = 0; i < 20; i++) {
            List<WebElement> matches = list.findElements(addButtonForName(personName));
            if (!matches.isEmpty()) {
                WebElement btn = matches.get(0);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", btn);
                wait.until(ExpectedConditions.elementToBeClickable(btn));
                try {
                    btn.click();
                } catch (ElementClickInterceptedException e) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                }
                try {
                    wait.until(ExpectedConditions.or(
                            ExpectedConditions.stalenessOf(btn),
                            ExpectedConditions.invisibilityOf(btn),
                            ExpectedConditions
                                    .not(ExpectedConditions.attributeContains(btn, "class", "btn-outline-success"))));
                } catch (TimeoutException ignore) {
                }
                return;
            }

            long currentTop = toLong(js("return arguments[0].scrollTop;", container));
            long clientH = toLong(js("return arguments[0].clientHeight;", container));
            long scrollH = toLong(js("return arguments[0].scrollHeight;", container));

            js("arguments[0].scrollTop = Math.min(arguments[0].scrollTop + arguments[0].clientHeight - 40, arguments[0].scrollHeight);",
                    container);

            new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
                long newTop = toLong(js("return arguments[0].scrollTop;", container));
                return newTop != currentTop || (currentTop + clientH >= scrollH);
            });

            long newTop = toLong(js("return arguments[0].scrollTop;", container));
            if (newTop == lastScrollTop)
                break;
            lastScrollTop = newTop;
        }
        Assert.fail("Pessoa não encontrada na lista de colaboradores: " + personName);
    }

    private void removeMemberFromTeamList(String personName) {
        WebElement container;
        try {
            container = wait.until(ExpectedConditions.visibilityOfElementLocated(teamMembersContainer));
        } catch (TimeoutException e) {

            container = wait.until(ExpectedConditions.visibilityOfElementLocated(teamMembersList));
        }

        WebElement list;
        try {
            list = container.findElement(teamMembersList);
        } catch (NoSuchElementException e) {
            list = container;
        }

        List<WebElement> matches = list.findElements(removeButtonForName(personName));
        if (matches.isEmpty()) {
            Assert.fail("Membro não encontrado no time para remoção: " + personName
                    + " (ajuste o seletor removeButtonForName/containers)");
        }

        WebElement btn = matches.get(0);
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", btn);
        wait.until(ExpectedConditions.elementToBeClickable(btn));
        try {
            btn.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }

        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.stalenessOf(btn),
                    ExpectedConditions.invisibilityOf(btn)));
        } catch (TimeoutException ignore) {
        }
    }

    private Object js(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    private long toLong(Object o) {
        return ((Number) o).longValue();
    }

    private void setInputValue(WebElement input, String value) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", input, value);
    }
}