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

    // Login elements
    By emailInput = By.id("email");
    By passwordInput = By.id("senha");
    By loginBtn = By.cssSelector("[data-login-btn]");

    // Squad elements
    By squadsPane = By.id("teams-tab");
    By modalNewTeam = By.id("modalNewTeam");
    By modalDeleteTeam = By.cssSelector("[data-open-modal-delete-team]");
    By optionsDelete = By.cssSelector("[data-open-options-delete]");
    By teamName = By.id("teamName");
    By salvarTimeBtn = By.id("saveTeam");
    By alertDiv = By.cssSelector("[data-alerts-users-squads]");
    By confirmDeleteSquad = By.id("confirmDeleteBtn");

    // Collaborators elements
    By collaboratorsContainer = By.cssSelector("div.border.rounded.p-3[style*='overflow-y']");
    By collaboratorsList = By.cssSelector("[data-list-collaborators]");

    // Team management elements
    By openEditTeamBtn = By.cssSelector("[data-edit-team]");
    By teamMembersContainer = By.cssSelector("[data-team-members]");
    By teamMembersList = By.cssSelector("[data-list-collaborators]");

    @BeforeClass
    public void setUp() {
        inicializarDriver();
        navegarParaLogin();
    }

    @BeforeMethod
    public void reloadBeforeEach() {
        limparDadosNavegador();
        navegarParaLogin();
    }

    @AfterClass
    public void tearDown() {
        fecharDriver();
    }

    @Test
    public void createTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaSquads();
        abrirModalNovoTime();
        adicionarMembrosAoTime("Carlos Alberto", "Vitoria Batista");
        preencherNomeTime("Bug hunters");
        salvarTime();
        validarMensagemSucesso("criado com sucesso!");
        System.out.println("Time criado com sucesso");
    }

    @Test
    public void editTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaSquads();
        abrirEdicaoTime();
        removerMembrosDoTime("Carlos Alberto", "Vitoria Batista");
        adicionarMembrosAoTime("Patrício Carvalho", "Rosana Melo");
        preencherNomeTime("Bug hunters 2");
        salvarTime();
        validarMensagemSucesso("editado com sucesso!");
        System.out.println("Time editado com sucesso");
    }

    @Test
    public void deleteTeam() {
        loginComoGerente("SergioAdMelo@gmail.com", "Lorem12345");
        navegarParaSquads();
        abrirOpcoesDelecao();
        abrirModalDelecaoTime();
        confirmarDelecaoTime();
        validarMensagemSucesso("Deletado com sucesso!");
        System.out.println("Time deletado com sucesso");
    }

    private void inicializarDriver() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private void navegarParaLogin() {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    private void limparDadosNavegador() {
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");
    }

    private void fecharDriver() {
        if (driver != null)
            driver.quit();
    }

    private void loginComoGerente(String email, String password) {
        driver.get("http://localhost:8080/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        preencherCampo(emailInput, email);
        preencherCampo(passwordInput, password);
        clicarElemento(loginBtn);
        validarLoginComSucesso();
    }

    private void preencherCampo(By campo, String valor) {
        WebElement element = driver.findElement(campo);
        element.clear();
        element.sendKeys(valor);
    }

    private void clicarElemento(By seletor) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(seletor));
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void validarLoginComSucesso() {
        wait.until(ExpectedConditions.presenceOfElementLocated(squadsPane));
    }

    private void navegarParaSquads() {
        clicarElemento(squadsPane);
    }

    private void abrirModalNovoTime() {
        clicarElemento(modalNewTeam);
    }

    private void abrirEdicaoTime() {
        clicarElemento(openEditTeamBtn);
    }

    private void abrirOpcoesDelecao() {
        clicarElemento(optionsDelete);
    }

    private void abrirModalDelecaoTime() {
        clicarElemento(modalDeleteTeam);
    }

    private void confirmarDelecaoTime() {
        clicarElemento(confirmDeleteSquad);
    }

    private void adicionarMembrosAoTime(String... nomesMembros) {
        for (String nome : nomesMembros) {
            adicionarMembroDaLista(nome);
        }
    }

    private void removerMembrosDoTime(String... nomesMembros) {
        for (String nome : nomesMembros) {
            removerMembroDoTime(nome);
        }
    }

    private void preencherNomeTime(String nomeTime) {
        WebElement nameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(teamName));
        try {
            nameElement.clear();
            nameElement.sendKeys(nomeTime);
        } catch (Exception e) {
            setarValorInput(nameElement, nomeTime);
        }
    }

    private void salvarTime() {
        clicarElemento(salvarTimeBtn);
    }

    private void validarMensagemSucesso(String textoEsperado) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String textoCompleto = alert.getText();
            Assert.assertTrue(textoCompleto.contains(textoEsperado),
                    "Texto esperado não encontrado na mensagem (" + textoEsperado + "). Texto recebido: " + textoCompleto);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem não encontrada/exibida: " + e.getMessage());
        }
    }

    private void adicionarMembroDaLista(String nomePessoa) {
        WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(collaboratorsContainer));
        WebElement lista = container.findElement(collaboratorsList);

        long ultimoScrollTop = -1;
        for (int i = 0; i < 20; i++) {
            List<WebElement> matches = lista.findElements(botaoAdicionarPorNome(nomePessoa));
            if (!matches.isEmpty()) {
                WebElement botao = matches.get(0);
                scrollParaElemento(botao);
                aguardarElementoClicavel(botao);
                clicarElementoComJS(botao);
                aguardarRemocaoBotao(botao);
                return;
            }

            long scrollAtual = toLong(executarJS("return arguments[0].scrollTop;", container));
            long alturaCliente = toLong(executarJS("return arguments[0].clientHeight;", container));
            long alturaScroll = toLong(executarJS("return arguments[0].scrollHeight;", container));

            executarJS("arguments[0].scrollTop = Math.min(arguments[0].scrollTop + arguments[0].clientHeight - 40, arguments[0].scrollHeight);", container);

            new WebDriverWait(driver, Duration.ofSeconds(2)).until(d -> {
                long novoTopo = toLong(executarJS("return arguments[0].scrollTop;", container));
                return novoTopo != scrollAtual || (scrollAtual + alturaCliente >= alturaScroll);
            });

            long novoTopo = toLong(executarJS("return arguments[0].scrollTop;", container));
            if (novoTopo == ultimoScrollTop)
                break;
            ultimoScrollTop = novoTopo;
        }
        Assert.fail("Pessoa não encontrada na lista de colaboradores: " + nomePessoa);
    }

    private void removerMembroDoTime(String nomePessoa) {
        WebElement container;
        try {
            container = wait.until(ExpectedConditions.visibilityOfElementLocated(teamMembersContainer));
        } catch (TimeoutException e) {
            container = wait.until(ExpectedConditions.visibilityOfElementLocated(teamMembersList));
        }

        WebElement lista;
        try {
            lista = container.findElement(teamMembersList);
        } catch (NoSuchElementException e) {
            lista = container;
        }

        List<WebElement> matches = lista.findElements(botaoRemoverPorNome(nomePessoa));
        if (matches.isEmpty()) {
            Assert.fail("Membro não encontrado no time para remoção: " + nomePessoa);
        }

        WebElement botao = matches.get(0);
        scrollParaElemento(botao);
        aguardarElementoClicavel(botao);
        clicarElementoComJS(botao);
        aguardarRemocaoElemento(botao);
    }

    private By botaoAdicionarPorNome(String nome) {
        return By.xpath(
                ".//div[contains(@class,'list-group-item')]" +
                        "[.//div[contains(@class,'fw-semibold') and normalize-space(text())='" + nome + "']]" +
                        "//button");
    }

    private By botaoRemoverPorNome(String nome) {
        return By.xpath(
                ".//div[contains(@class,'list-group-item')]" +
                        "[.//div[contains(@class,'fw-semibold') and normalize-space(text())='" + nome + "']]" +
                        "//button");
    }

    private void scrollParaElemento(WebElement elemento) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center', inline:'nearest'});", elemento);
    }

    private void aguardarElementoClicavel(WebElement elemento) {
        wait.until(ExpectedConditions.elementToBeClickable(elemento));
    }

    private void clicarElementoComJS(WebElement elemento) {
        try {
            elemento.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", elemento);
        }
    }

    private void aguardarRemocaoBotao(WebElement botao) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.stalenessOf(botao),
                    ExpectedConditions.invisibilityOf(botao),
                    ExpectedConditions.not(ExpectedConditions.attributeContains(botao, "class", "btn-outline-success"))));
        } catch (TimeoutException ignore) {
        }
    }

    private void aguardarRemocaoElemento(WebElement elemento) {
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.stalenessOf(elemento),
                    ExpectedConditions.invisibilityOf(elemento)));
        } catch (TimeoutException ignore) {
        }
    }

    private Object executarJS(String script, Object... args) {
        return ((JavascriptExecutor) driver).executeScript(script, args);
    }

    private long toLong(Object objeto) {
        return ((Number) objeto).longValue();
    }

    private void setarValorInput(WebElement input, String valor) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].value = arguments[1];", input, valor);
    }

    // Métodos públicos para reutilização
    public void criarTime(String email, String senha, String nomeTime, String... membros) {
        loginComoGerente(email, senha);
        navegarParaSquads();
        abrirModalNovoTime();
        adicionarMembrosAoTime(membros);
        preencherNomeTime(nomeTime);
        salvarTime();
        validarMensagemSucesso("criado com sucesso!");
    }

    public void editarTime(String email, String senha, String novoNome, String[] membrosRemover, String[] membrosAdicionar) {
        loginComoGerente(email, senha);
        navegarParaSquads();
        abrirEdicaoTime();
        removerMembrosDoTime(membrosRemover);
        adicionarMembrosAoTime(membrosAdicionar);
        preencherNomeTime(novoNome);
        salvarTime();
        validarMensagemSucesso("editado com sucesso!");
    }

    public void deletarTime(String email, String senha) {
        loginComoGerente(email, senha);
        navegarParaSquads();
        abrirOpcoesDelecao();
        abrirModalDelecaoTime();
        confirmarDelecaoTime();
        validarMensagemSucesso("Deletado com sucesso!");
    }

    public int obterQuantidadeMembrosTime() {
        try {
            WebElement container = wait.until(ExpectedConditions.visibilityOfElementLocated(teamMembersContainer));
            WebElement lista = container.findElement(teamMembersList);
            return lista.findElements(botaoRemoverPorNome("")).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean timeExiste(String nomeTime) {
        navegarParaSquads();
        try {
            WebElement timeElement = driver.findElement(By.xpath("//*[contains(text(), '" + nomeTime + "')]"));
            return timeElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void testarCriacaoTimeSemMembros(String email, String senha, String nomeTime) {
        loginComoGerente(email, senha);
        navegarParaSquads();
        abrirModalNovoTime();
        preencherNomeTime(nomeTime);
        salvarTime();
        validarMensagemErro("Adicione pelo menos um membro");
    }

    public void testarCriacaoTimeSemNome(String email, String senha, String... membros) {
        loginComoGerente(email, senha);
        navegarParaSquads();
        abrirModalNovoTime();
        adicionarMembrosAoTime(membros);
        salvarTime();
        validarMensagemErro("Informe o nome do time");
    }

    private void validarMensagemErro(String mensagemEsperada) {
        try {
            WebElement alert = wait.until(ExpectedConditions.visibilityOfElementLocated(alertDiv));
            String textoCompleto = alert.getText();
            Assert.assertTrue(textoCompleto.contains(mensagemEsperada),
                    "Texto de erro esperado não encontrado. Texto recebido: " + textoCompleto);
        } catch (TimeoutException | NoSuchElementException e) {
            Assert.fail("Mensagem de erro não encontrada/exibida: " + e.getMessage());
        }
    }
}