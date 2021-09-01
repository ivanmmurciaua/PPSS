package ejercicio3.conPOyPFact;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ScreenShoot {
    //realizamos una captura de pantalla del navegador, que guardamos en target
    public static void captura(WebDriver driver, String filename) throws IOException {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFile = new File("./target/"+filename+".png");
        Files.copy(scrFile.toPath(),destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}
