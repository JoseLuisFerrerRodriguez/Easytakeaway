package com.proyecto.easytakeaway.funcionales;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaginaPrincipalTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Especificar la versión de chrome instalada
        // WebDriverManager.chromedriver().browserVersion("125.0.6422.112").setup();
        // se puede revisar con chrome://settings/help
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
    public void testHomePage() {
        driver.get("http://ec2-15-237-184-81.eu-west-3.compute.amazonaws.com/");
        assertThat(driver.getTitle()).isEqualTo("CakelY");
    }

}
