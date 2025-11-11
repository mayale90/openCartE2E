package openCart.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.actions.Click;
import static net.serenitybdd.screenplay.Tasks.instrumented;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isVisible;
import static openCart.pages.InicioPage.*;
import static openCart.pages.ProductoPage.*;

import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Step;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class agregaProductosTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(agregaProductosTask.class);
    //private final String producto;
    private final List<String> productos;

    public agregaProductosTask(String producto) {
        this.productos = Arrays.stream(producto.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    @Step("{0} Agrega productos al carrito")
    public <T extends Actor> void performAs(T actor) {
        if (productos.isEmpty()) {
            LOGGER.warn("No hay productos para agregar (cadena vacía o sólo comas).");
            return;
        }

        LOGGER.info("Iniciando flujo para agregar {} producto(s): {}", productos.size(), productos);
        actor.attemptsTo(
                WaitUntil.the(LBL_TITLE,isVisible()).forNoMoreThan(10).seconds()
        );

        for (String prod : productos) {
            LOGGER.info("Agregando producto: {}", prod);
            actor.attemptsTo(
                    WaitUntil.the(LBL_PRODUCTO.of(prod),isVisible()).forNoMoreThan(10).seconds(),
                    Click.on(LBL_PRODUCTO.of(prod) ),
                    WaitUntil.the(BTN_AGREGAPROD,isVisible()).forNoMoreThan(10).seconds(),
                    Click.on(BTN_AGREGAPROD),
                    WaitUntil.the(LBLCONFIRMACION.of(prod),isVisible()).forNoMoreThan(10).seconds(),
                    WaitUntil.the(ICONO_INICIO,isVisible()).forNoMoreThan(10).seconds(),
                    Click.on(ICONO_INICIO)
            );
            LOGGER.info("Producto {} agregado exitosamente.", prod);
        }
    }

    public static agregaProductosTask conInformacion(String producto) {
        return instrumented(agregaProductosTask.class, producto);
    }
}
