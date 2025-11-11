package openCart.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static net.serenitybdd.screenplay.questions.WebElementQuestion.the;
import static openCart.pages.CheckoutPage.*;

public class validaCompraTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(agregaProductosTask.class);

    public validaCompraTask() {}

    @Step("{0} Valida compra realizada con éxito")
    public <T extends Actor> void performAs(T actor) {
        LOGGER.info("Confirma mensaje de confirmación de la orden");
        actor.attemptsTo(
                WaitUntil.the(LBL_MENSAJECONFIRMACIONORDEN,isVisible()).forNoMoreThan(10).seconds()
        );
        actor.should(seeThat(the(LBL_MENSAJECONFIRMACIONORDEN), isVisible()));
        actor.attemptsTo(
                WaitUntil.the(BTN_FINALIZA,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(BTN_FINALIZA)
        );
    }

    public static validaCompraTask exitosa() {
        return instrumented(validaCompraTask.class);
    }
}
