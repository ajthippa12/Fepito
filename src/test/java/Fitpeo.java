import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

public class Fitpeo {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String expectedReimbursement = "$110700"; 

    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        driver.navigate().to("https://www.fitpeo.com");
    }

    public void revenueCalculator() throws InterruptedException {
        // Navigate to the Revenue Calculator Page
        WebElement revenueCalculator = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Revenue Calculator")));
        revenueCalculator.click();
        
        // Wait for the slider to be visible
        WebElement slider = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'MuiSlider-thumb')]")));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", slider);

        // Move the slider using JavaScript
        js.executeScript("arguments[0].setAttribute('style', 'left: 41.01%;')", slider);

        // Wait for the slider to be moved
        Thread.sleep(3000); 

        // Click the slider using JavaScript if traditional click fails
        try {
            slider.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            js.executeScript("arguments[0].click();", slider);
        }
        
        // Extract and verify the slider value
        String sliderValue = slider.getAttribute("aria-valuenow");
        System.out.println("Slider Value: " + sliderValue);

        // Select checkboxes
        int count = driver.findElements(By.xpath("//*[@class='MuiTypography-root MuiTypography-body1 inter css-1s3unkt']")).size();
        for (int i = 1; i <= count; i++) {
            String boxText = driver.findElement(By.xpath("(//*[@class='MuiTypography-root MuiTypography-body1 inter css-1s3unkt'])[" + i + "]")).getText();
            if (boxText.contains("CPT-99473") || boxText.contains("CPT-99453") || boxText.contains("CPT-99454") || boxText.contains("CPT-99474")) {
                driver.findElement(By.xpath("(//*[@class='MuiBox-root css-4o8pys'])[" + i + "]//label//input")).click();
            }
        }

        // Wait and verify total reimbursement
        WebElement totalReimbursementElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@class='MuiTypography-root MuiTypography-body1 inter css-hocx5c']")));
        String totalReimbursement = totalReimbursementElement.getText();

        if (totalReimbursement.equals(expectedReimbursement)) {
            System.out.println("Total reimbursement is as expected: " + totalReimbursement);
        } else {
            System.out.println("Total reimbursement is not as expected. Found: " + totalReimbursement);
        }
    }

    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public static void main(String[] args) {
        Fitpeo fitpeo = new Fitpeo();
        try {
            fitpeo.setUp();
            fitpeo.revenueCalculator();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            fitpeo.tearDown();
        }
    }
}
