package com.proyecto.easytakeaway.funcionales;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LoginTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testUserCanLogin() {
        driver.get("http://ec2-15-237-184-81.eu-west-3.compute.amazonaws.com/login");

        driver.findElement(By.id("username")).sendKeys("admin@email.com");
        driver.findElement(By.id("password")).sendKeys("1234");
        driver.findElement(By.id("submit")).click();

        // Verify the login was successful
        String welcomeMessage = driver.findElement(By.id("carritoAccess")).getAttribute("href");
        assertThat(welcomeMessage).contains("/carrito");
    }
}