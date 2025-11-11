package openCart.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;


public class InicioPage extends PageObject {

    private InicioPage() {
    }

    public static final Target LBL_TITLE = Target.the("Titulo")
            .locatedBy("//a[normalize-space()='Your Store']");

    public static final Target LBL_PRODUCTO = Target.the("Enlace de producto {0}")
            .locatedBy("//div[@class='caption']//a[contains(normalize-space(),'{0}')]");
}

