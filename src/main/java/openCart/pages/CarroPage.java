package openCart.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import net.thucydides.core.annotations.findby.By;

public class CarroPage extends PageObject {

    private CarroPage() {
    }

    public static final Target LBL_VERCARRITO = Target.the("Texto View Cart")
            .locatedBy("//strong[.//i[contains(@class,'fa-shopping-cart')] and contains(normalize-space(.),'View Cart')]");

    public static final Target BTN_CHECKOUT = Target.the("Botón Checkout")
            .locatedBy("//a[@class='btn btn-primary' and normalize-space() = 'Checkout']");

 }
