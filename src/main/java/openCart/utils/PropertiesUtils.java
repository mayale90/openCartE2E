package openCart.utils;


import java.util.ResourceBundle;

public class PropertiesUtils {

    private String pathProperties;

    public PropertiesUtils(String pathProperties) {
        this.pathProperties = pathProperties;
    }

    public static PropertiesUtils getPropertiesValue (String pathValueProperties){
        return new PropertiesUtils(pathValueProperties);
    }

    public String andKey(String key){
        return ResourceBundle.getBundle(this.pathProperties).getString(key);
    }
}

