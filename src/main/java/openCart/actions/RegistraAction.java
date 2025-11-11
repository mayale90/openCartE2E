package openCart.actions;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.Enter;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;
import openCart.tasks.agregaProductosTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isEnabled;

public class RegistraAction implements Interaction {
    private static final Logger LOGGER = LoggerFactory.getLogger(agregaProductosTask.class);
    private Target elemento;
    private String texto;

    public RegistraAction(Target elemento, String texto) {
        this.elemento = elemento;
        this.texto = texto;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        if (texto != null && !texto.trim().isEmpty()) {
            LOGGER.info("Registra: {}", texto.trim());
            actor.attemptsTo(
                    WaitUntil.the(elemento, isEnabled()).forNoMoreThan(60).seconds(),
                    Enter.theValue(texto.trim()).into(elemento)

            );
        } else {
            LOGGER.info("Texto nulo o vacío, no se realiza ninguna acción sobre el elemento");
        }
    }

    public static Performable inTargetAndText(Target elemento, String texto) {
        return instrumented(RegistraAction.class, elemento, texto);
    }
}
