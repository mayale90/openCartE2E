package openCart.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import net.serenitybdd.screenplay.Actor;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.screenplay.abilities.BrowsingTheWeb;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import io.cucumber.java.en.*;
import java.util.List;
import java.util.Map;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static openCart.enums.openCartWeb.OPEN_CART_WEB_URL;
import static openCart.utils.PropertiesUtils.getPropertiesValue;
import static openCart.utils.ExcelReadWrite.testReadWriteExcel;

import openCart.driver.NavegadorWebDriver;
import openCart.models.CompraModel;
import openCart.tasks.agregaProductosTask;
import openCart.tasks.confirmaCompraTask;
import openCart.tasks.validaCompraTask;

@Slf4j
public class procesoCompraSteps {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    String url = getPropertiesValue(OPEN_CART_WEB_URL.getPathProperties())
            .andKey(OPEN_CART_WEB_URL.getKey());


    @Before
    public void preparaEscenario() {
        OnStage.setTheStage(new OnlineCast());
    }

    @Given("El usuario abre la página openCart con los datos de prueba {string} y el caso {string}")
    public void abrirPaginaOpenCart(String data, String idCaso, List<Map<String, Object>> datosprueba ) {

        Actor cliente = OnStage.theActorCalled("cliente");
        testReadWriteExcel(data,idCaso,datosprueba, cliente);
        theActorInTheSpotlight().can(BrowsingTheWeb.with(NavegadorWebDriver.chromeHisBrowserWeb().on(url)));
    }

    @When("realiza la compra de dos productos y realiza checkout con la informacion requerida")
    public void realizaCompraProductos() {
        theActorInTheSpotlight().attemptsTo(
                agregaProductosTask.conInformacion(obtenerData().getProductos()),
                confirmaCompraTask.exitosa(obtenerData())
        );
    }

    @Then("Deberia ver el mensaje de confirmacion de la compra exitosa")
    public void confirmaCompra() {
        theActorInTheSpotlight().attemptsTo(
                validaCompraTask.exitosa()
        );
    }

    private CompraModel obtenerData() {
        Map<String, String> datos = theActorInTheSpotlight().recall("data");
        return MAPPER.convertValue(datos, CompraModel.class);
    }
}

