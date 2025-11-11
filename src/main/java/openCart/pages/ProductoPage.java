package openCart.pages;

import net.serenitybdd.core.pages.PageObject;
import net.serenitybdd.screenplay.targets.Target;
import org.openqa.selenium.By;

public class ProductoPage  extends PageObject {

    private ProductoPage() {
    }

    public static final Target BTN_AGREGAPROD = Target.the("Agregar al carrito")
            .located(By.id("button-cart"));

    public static final Target LBLCONFIRMACION = Target.the("Producto en confirmación {0}")
            .locatedBy("//div[contains(@class,'alert-success')]//a[normalize-space()='{0}']");

    public static final Target ICONO_INICIO = Target.the("Icono Home (contains)")
            .located(By.xpath("//li[a[i[contains(@class,'fa-home')]]]"));

    public static final Target BTN_CARRITO = Target.the("Ver Carrito")
            .located(By.id("cart-total"));


}
