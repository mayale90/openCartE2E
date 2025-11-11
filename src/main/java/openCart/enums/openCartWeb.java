package openCart.enums;

import lombok.Getter;

@Getter
public enum openCartWeb {

    OPEN_CART_WEB_URL("properties/environments", "environment.opencart.url");
    private final String pathProperties;
    private final String key;


    openCartWeb(String pathProperties, String key) {
        this.pathProperties = pathProperties;
        this.key = key;
    }
}
