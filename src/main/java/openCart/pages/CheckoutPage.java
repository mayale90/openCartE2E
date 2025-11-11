package openCart.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.findby.By;

public class CheckoutPage extends PageObject {

    private CheckoutPage() {
    }

    public static final Target LABEL_GUEST_CHECKOUT = Target.the("Label Guest Checkout")
            .locatedBy("//label[contains(normalize-space(.),'Guest Checkout')]");

    public static final Target BTN_CONTINUAR = Target.the("Continuar")
            .located(By.id("button-account"));

    public static final Target INPUT_NOMBRE = Target.the("NOMBRE")
            .located(By.id("input-payment-firstname"));

    public static final Target INPUT_APELLIDO = Target.the("APELLIDO")
            .located(By.id("input-payment-lastname"));

    public static final Target INPUT_EMAIL = Target.the("EMAIL")
            .located(By.id("input-payment-email"));

    public static final Target INPUT_TELEFONO = Target.the("TELEFONO")
            .located(By.id("input-payment-telephone"));

    public static final Target INPUT_DIRECCION = Target.the("DIRECCION ")
            .located(By.id("input-payment-address-1"));

    public static final Target INPUT_CIUDAD = Target.the("CIUDAD")
            .located(By.id("input-payment-city"));

    public static final Target INPUT_CODPOSTAL = Target.the("CODPOSTAL")
            .located(By.id("input-payment-postcode"));

    public static final Target SELECT_PAIS = Target.the("PAIS")
            .located(By.id("input-payment-country"));

    public static final Target SELECT_REGION = Target.the("REGION")
            .located(By.id("input-payment-zone"));

    public static final Target BTN_CONTINUARFORM = Target.the("Continuar")
            .located(By.id("button-guest"));

    public static final Target INPUT_COMENTARIO = Target.the("Comentario Pago")
            .located(By.name("comment"));

    public static final Target BTN_CONFCOMENTARIO = Target.the("Confirmar comentario compra")
            .located(By.id("button-shipping-method"));

    public static final Target BTN_CONFMETODOPAGO = Target.the("Confirmar método de pago")
            .located(By.id("button-payment-method"));

    public static final Target CHK_TERMINOSYCONDICIONES = Target.the("Aceptar terminos y condiciones")
            .located(By.name("agree"));

    public static final Target BTN_CONFIRMAORDEN = Target.the("Confirmar Orden")
            .located(By.id("button-confirm"));

    public static final Target LBL_MENSAJECONFIRMACIONORDEN = Target.the("Mensaje confirmación orden")
            .locatedBy("//h1[normalize-space() = 'Your order has been placed!']");

    public static final Target BTN_FINALIZA = Target.the("Botón Finaliza proceso Compra")
            .locatedBy("//a[contains(@class,'btn') and contains(@class,'btn-primary') and normalize-space() = 'Continue' and contains(@href,'route=common/home')]");
}
