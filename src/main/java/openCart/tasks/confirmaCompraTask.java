package openCart.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Step;
import openCart.actions.RegistraAction;
import openCart.actions.Scroll;
import openCart.actions.SeleccionaListaAction;
import openCart.models.CompraModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static openCart.pages.ProductoPage.*;
import static openCart.pages.CarroPage.*;
import static openCart.pages.CheckoutPage.*;

public class confirmaCompraTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(agregaProductosTask.class);
    private final CompraModel compraModel;

    public confirmaCompraTask(CompraModel compraModel) {
        this.compraModel = compraModel;
    }

    @Override
    @Step("{0} Agrega productos al carrito")
    public <T extends Actor> void performAs(T actor) {
        LOGGER.info("Visualiza carrito de compras");
        actor.attemptsTo(
                WaitUntil.the(BTN_CARRITO,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(BTN_CARRITO ),
                WaitUntil.the(LBL_VERCARRITO,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(LBL_VERCARRITO ),
                WaitUntil.the(BTN_CHECKOUT,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(BTN_CHECKOUT )
        );
        LOGGER.info("Realiza Checkout");
        actor.attemptsTo(
                WaitUntil.the(LABEL_GUEST_CHECKOUT,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(LABEL_GUEST_CHECKOUT ),
                WaitUntil.the(BTN_CONTINUAR,isVisible()).forNoMoreThan(10).seconds(),
                Click.on(BTN_CONTINUAR ),
                RegistraAction.inTargetAndText(INPUT_NOMBRE,compraModel.getNombre()),
                RegistraAction.inTargetAndText(INPUT_APELLIDO,compraModel.getApellido()),
                RegistraAction.inTargetAndText(INPUT_EMAIL,compraModel.getEmail()),
                RegistraAction.inTargetAndText(INPUT_TELEFONO,compraModel.getTelefono()),
                RegistraAction.inTargetAndText(INPUT_DIRECCION,compraModel.getDireccion()),
                RegistraAction.inTargetAndText(INPUT_CIUDAD,compraModel.getCiudad()),
                RegistraAction.inTargetAndText(INPUT_CODPOSTAL,compraModel.getCodPostal())
        );

        actor.attemptsTo(
                Scroll.to(BTN_CONTINUARFORM,30),
                SeleccionaListaAction.inTargetAndText(SELECT_PAIS,compraModel.getPais()),
                SeleccionaListaAction.inTargetAndText(SELECT_REGION,compraModel.getRegion()),
                Click.on(BTN_CONTINUARFORM ),
                RegistraAction.inTargetAndText(INPUT_COMENTARIO,compraModel.getDelivery()),
                Click.on(BTN_CONFCOMENTARIO ),
                RegistraAction.inTargetAndText(INPUT_COMENTARIO,compraModel.getPago()),
                Click.on(CHK_TERMINOSYCONDICIONES ),
                WaitUntil.the(BTN_CONFMETODOPAGO,isVisible()).forNoMoreThan(30).seconds(),
                Click.on(BTN_CONFMETODOPAGO ),
                WaitUntil.the(BTN_CONFIRMAORDEN,isVisible()).forNoMoreThan(60).seconds(),
                Click.on(BTN_CONFIRMAORDEN )
        );
    }

    public static confirmaCompraTask exitosa(CompraModel compraModel) {
        return instrumented(confirmaCompraTask.class, compraModel);
    }
}
