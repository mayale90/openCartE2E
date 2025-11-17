package openCart.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.Step;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Instant;

public class Scroll implements Interaction {
    private final Target target;
    private final long timeoutSeconds;

    public Scroll(Target target, long timeoutSeconds) {
        this.target = target;
        this.timeoutSeconds = timeoutSeconds;
    }

    public static Scroll to(Target target, long timeoutSeconds) {
        return new Scroll(target, timeoutSeconds);
    }

    @Override
    @Step("{0} desplaza hasta que el target #target sea visible")
    public <T extends Actor> void performAs(T actor) {
        WebDriver driver = BrowseTheWeb.as(actor).getDriver();
        Instant end = Instant.now().plusSeconds(timeoutSeconds);

        while (Instant.now().isBefore(end)) {
            try {
                WebElement element = target.resolveFor(actor);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
                if (element.isDisplayed()) {
                    return;
                }
            } catch (Exception ignored) {
                // elemento aún no presente/resuelto; seguir intentándolo
            }
            try { Thread.sleep(250); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        // Si se prefiere, lanzar excepción aquí para fallar rápido:
        // throw new RuntimeException("No se encontró el elemento " + target.getName() + " en " + timeoutSeconds + "s");
    }
}