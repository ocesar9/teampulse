package admin;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class RegisterAdmin {
    WebDriver driver;

    @BeforeClass
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/acesso/cadastroadmin.html");
    }

    @AfterClass
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }

    @Test
    public void registerAdmin() throws InterruptedException {
        Thread.sleep(2000);
        var email = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[1]/input"));
        email.sendKeys("julio.guimaraes@gmail.com");

        var username = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[2]/input"));
        username.sendKeys("Júlio Guimarães");

        var password = driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[3]/input"));
        password.sendKeys("matue1999");

        driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[5]/button")).click();
    }
}
