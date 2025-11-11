package openCart.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NavegadorWebDriver {

    private static WebDriver driver;
    private static Path USER_DIR;   // <-- guarda el perfil para debug/cleanup
    private static Path CACHE_DIR;  // <--

    // ====== PRESETS DE DISPOSITIVOS (puedes agregar más) ======
    public static enum DevicePreset {
        // iPhones
        IPHONE_12(390, 844, 3.0,
                "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15A372 Safari/604.1"),
        IPHONE_13_PRO(390, 844, 3.0,
                "Mozilla/5.0 (iPhone; CPU iPhone OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15A372 Safari/604.1"),
        IPHONE_14_PRO_MAX(430, 932, 3.0,
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15A372 Safari/604.1"),

        // Samsung Galaxy
        PIXEL_7(412, 915, 2.625,
                "Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"),
        SAMSUNG_S21(384, 854, 2.75,
                "Mozilla/5.0 (Linux; Android 12; SM-G991B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"),
        SAMSUNG_S22_ULTRA(412, 915, 3.0,
                "Mozilla/5.0 (Linux; Android 13; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36"),

        // Tablets
        IPAD_AIR(820, 1180, 2.0,
                "Mozilla/5.0 (iPad; CPU OS 16_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16.0 Mobile/15A372 Safari/604.1"),
        TABLET_ANDROID(768, 1024, 2.0,
                "Mozilla/5.0 (Linux; Android 12; SM-T870) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        public final int width, height;
        public final double dpr;
        public final String userAgent;

        DevicePreset(int w, int h, double dpr, String ua) {
            this.width = w;
            this.height = h;
            this.dpr = dpr;
            this.userAgent = ua;
        }

        /**
         * Obtiene las dimensiones según la orientación especificada
         */
        public java.awt.Dimension getDimensions(Orientation orientation) {
            if (orientation == Orientation.LANDSCAPE) {
                return new java.awt.Dimension(height, width); // Intercambiamos para landscape
            }
            return new java.awt.Dimension(width, height);
        }
    }

    public static enum Orientation { PORTRAIT, LANDSCAPE }

    // ====== PREFS PARA APAGAR EL GESTOR DE CONTRASEÑAS/LEAK DETECTION ======
    private static Map<String, Object> passwordManagerPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        return prefs;
    }

    private static void applyMobileEmulation(ChromiumDriver chromium,
                                             DevicePreset device,
                                             Orientation orientation) {

        int w = device.width;
        int h = device.height;

        // Para landscape, intercambiamos las dimensiones
        if (orientation == Orientation.LANDSCAPE) {
            int temp = w;
            w = h;
            h = temp;
        }

        System.out.println("[Driver] Aplicando emulación móvil: " + device.name() +
                " (" + w + "x" + h + ") - DPR: " + device.dpr +
                " - Orientación: " + orientation);

        // Configurar métricas del dispositivo de forma simplificada
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("width", w);
        metrics.put("height", h);
        metrics.put("deviceScaleFactor", device.dpr);
        metrics.put("mobile", true);

        // Aplicar las métricas del dispositivo
        chromium.executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);

        // Habilitar emulación táctil
        Map<String, Object> touch = new HashMap<>();
        touch.put("enabled", true);
        chromium.executeCdpCommand("Emulation.setTouchEmulationEnabled", touch);


        // Configurar User Agent
        chromium.executeCdpCommand("Network.enable", Collections.emptyMap());
        Map<String, Object> ua = new HashMap<>();
        ua.put("userAgent", device.userAgent);
        chromium.executeCdpCommand("Network.setUserAgentOverride", ua);
    }

    // ====== INICIALIZADORES DE NAVEGADORES ======
    public static synchronized NavegadorWebDriver chromeHisBrowserWeb() {
        ChromeOptions options = new ChromeOptions();

        // Flags base simplificados y probados
        options.addArguments("--ignore-certificate-errors");
        options.addArguments("--disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        // ZOOM AL 80%
        options.addArguments("--force-device-scale-factor=0.8");

        // Headless si lo piden (propiedad o env)
        boolean headless =
                "true".equalsIgnoreCase(System.getProperty("webdriver.chrome.headless")) ||
                        "true".equalsIgnoreCase(System.getenv("webdriver.chrome.headless")) ||
                        "true".equalsIgnoreCase(System.getenv("CI_HEADLESS"));
        if (headless) {
            options.addArguments("--headless=new");
        }

        // Desactiva password manager básico
        //options.setExperimentalOption("prefs", passwordManagerPrefs());

        // ZOOM AL 80% - método 2: configurar prefs ANTES de usarlas
        Map<String, Object> prefs = passwordManagerPrefs();
        prefs.put("ui.scale_factor", 0.8);
        options.setExperimentalOption("prefs", prefs);

        // === PERFIL ÚNICO POR INSTANCIA ===
        String baseEnv = System.getenv("CHROME_USER_DATA_DIR");
        Path baseDir = Paths.get(
                (baseEnv != null && !baseEnv.isBlank()) ? baseEnv : System.getProperty("java.io.tmpdir")
        );

        String runId = System.getProperty("GITHUB_RUN_ID", String.valueOf(System.nanoTime()));
        String threadId = String.valueOf(Thread.currentThread().getId());
        String suffix = "ud-" + runId + "-t" + threadId + "-" + UUID.randomUUID();

        try {
            USER_DIR  = Files.createDirectories(baseDir.resolve(suffix));
            String cacheBaseEnv = System.getenv("CHROME_CACHE_DIR");
            Path cacheBase = Paths.get((cacheBaseEnv != null && !cacheBaseEnv.isBlank())
                    ? cacheBaseEnv : System.getProperty("java.io.tmpdir"));
            CACHE_DIR = Files.createDirectories(cacheBase.resolve("cd-" + runId + "-t" + threadId + "-" + UUID.randomUUID()));
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron crear directorios únicos para Chrome", e);
        }

        options.addArguments("--user-data-dir=" + USER_DIR);
        options.addArguments("--disk-cache-dir=" + CACHE_DIR);

        System.out.println("[Driver] user-data-dir: " + USER_DIR);
        System.out.println("[Driver] disk-cache-dir: " + CACHE_DIR);

        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver(options);

        // ZOOM AL 80% - método 3: aplicar DESPUÉS de crear el driver
        setPageZoom(driver, 0.8);

        // Hook extra por si el runner termina abruptamente
        Runtime.getRuntime().addShutdownHook(new Thread(NavegadorWebDriver::shutdown));

        return new NavegadorWebDriver();
    }

    /** Cierre y limpieza segura (llamar en @After) */
    public static synchronized void shutdown() {
        try {
            if (driver != null) {
                driver.quit();
                driver = null;
            }
        } catch (Exception ignore) {}
        // Nota: no borro USER_DIR/CACHE_DIR porque Chrome deja archivos en uso; el runner limpia /tmp
    }

    public static NavegadorWebDriver firefoxBrowser() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        return new NavegadorWebDriver();
    }

    public static NavegadorWebDriver edgeBrowser() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--incognito");
        options.addArguments("--inprivate");
        options.addArguments("--disable-dev-shm-usage");
//      options.addArguments("--headless=new");
        options.addArguments("--start-maximized");
        options.setExperimentalOption("prefs", passwordManagerPrefs());

        driver = new EdgeDriver(options);
        return new NavegadorWebDriver();
    }

    public static NavegadorWebDriver internetExplorerBrowser() {
        WebDriverManager.iedriver().setup();
        driver = new InternetExplorerDriver();
        return new NavegadorWebDriver();
    }

    public WebDriver on(String url) {
        if (driver == null) {
            throw new IllegalStateException("El WebDriver no ha sido inicializado correctamente.");
        }
        driver.get(normalizeUrl(url));

        // Aplicar zoom del 80% después de cargar la página
        setPageZoom(driver, 0.8);
        return driver;
    }

    // ====== EMULACIÓN MÓVIL EN UNA NUEVA PESTAÑA (USANDO EL DRIVER GLOBAL) ======
    public static WebDriver openMobileEmulatedTab(WebDriver targetDriver,
                                                  String url,
                                                  DevicePreset device) {
        return openMobileEmulatedTab(targetDriver, url, device, Orientation.PORTRAIT);
    }

    public static String normalizeUrl(String raw) {
        if (raw == null) throw new IllegalArgumentException("URL nula");
        String u = raw.trim();

        // Permite esquemas especiales si ya vienen completos
        if (u.matches("(?i)^(https?|about:blank|data:|file:|chrome://|edge://).*")) {
            // Reemplaza espacios sueltos por %20
            return u.replace(" ", "%20");
        }

        // Si no empieza por http/https, anteponer https://
        u = "https://" + u;

        // Último toque: quitar espacios
        return u.replace(" ", "%20");
    }

    // ====== EMULACIÓN MÓVIL EN UNA NUEVA PESTAÑA (USANDO EL DRIVER DEL ACTOR) ======
    public static WebDriver openMobileEmulatedTab(WebDriver targetDriver, String url, DevicePreset device, Orientation orientation) {
        if (targetDriver == null) {
            throw new IllegalStateException("targetDriver es null. Asegúrate de pasar el driver del Actor.");
        }

        // 1) Nueva pestaña
        targetDriver.switchTo().newWindow(WindowType.TAB);

        // 2) Emulación SOLO en Chrome/Edge (Chromium)
        if (targetDriver instanceof ChromiumDriver) {
            ChromiumDriver chromium = (ChromiumDriver) targetDriver;
            applyMobileEmulation(chromium, device, orientation);
        } else {
            throw new UnsupportedOperationException(
                    "La emulación móvil por pestaña requiere Chrome/Edge (Chromium) con Selenium 4+.");
        }

        // 3) Navegar a la URL
        targetDriver.get(normalizeUrl(url));
        return targetDriver;
    }

    /** Rotar a vertical (retrato) en la pestaña ACTUAL. */
    public static void rotateToPortrait(WebDriver targetDriver, DevicePreset device) {
        if (targetDriver instanceof ChromiumDriver) {
            applyMobileEmulation((ChromiumDriver) targetDriver, device, Orientation.PORTRAIT);
        }
    }

    /** Espera a que el documento cargue por completo. */
    private static void waitForPageReady(WebDriver driver) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(10))
                    .until(d -> "complete".equals(
                            ((JavascriptExecutor) d).executeScript("return document.readyState")));
        } catch (Exception ignored) {}
    }

    /** Calcula la altura total del contenido (máximo de varias métricas). */
    private static int getFullContentHeight(WebDriver driver) {
        Number h = (Number) ((JavascriptExecutor) driver).executeScript(
                "return Math.max(" +
                        "document.body.scrollHeight, document.documentElement.scrollHeight," +
                        "document.body.offsetHeight, document.documentElement.offsetHeight," +
                        "document.body.clientHeight, document.documentElement.clientHeight);"
        );
        // límite de seguridad para no pasar valores absurdos (ajusta si lo necesitas)
        return Math.min(h.intValue(), 20000);
    }

    /** Re-ajusta el viewport a la altura total del documento para 'ver todo'. */
    public static void fitToFullPage(WebDriver targetDriver, DevicePreset device) {
        if (!(targetDriver instanceof ChromiumDriver)) {
            throw new UnsupportedOperationException("Se requiere Chrome/Edge (Chromium).");
        }
        waitForPageReady(targetDriver);

        int fullHeight = getFullContentHeight(targetDriver);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("width", device.width);
        metrics.put("height", fullHeight);                 // <- altura total
        metrics.put("deviceScaleFactor", device.dpr);
        metrics.put("mobile", true);

        // Conserva orientación actual (si quieres forzarla, puedes añadir screenOrientation aquí)
        ChromiumDriver chromium = (ChromiumDriver) targetDriver;
        chromium.executeCdpCommand("Emulation.setDeviceMetricsOverride", metrics);
    }

    /** Rotar a horizontal (apaisado) en la pestaña ACTUAL. */
    public static void rotateToLandscape(WebDriver targetDriver, DevicePreset device) {
        if (targetDriver instanceof ChromiumDriver) {
            applyMobileEmulation((ChromiumDriver) targetDriver, device, Orientation.LANDSCAPE);
        }
    }

    // ====== (Opcional) LIMPIAR EMULACIÓN EN LA PESTAÑA ACTUAL ======
    public static void clearEmulationInCurrentTab(WebDriver targetDriver) {
        if (targetDriver instanceof ChromiumDriver) {
            ChromiumDriver chromium = (ChromiumDriver) targetDriver;
            chromium.executeCdpCommand("Emulation.clearDeviceMetricsOverride", Collections.<String, Object>emptyMap());

            Map<String, Object> touchOff = new HashMap<String, Object>();
            touchOff.put("enabled", false);
            chromium.executeCdpCommand("Emulation.setTouchEmulationEnabled", touchOff);

            // Para volver a un UA de escritorio, fija uno explícito de desktop con Network.setUserAgentOverride.
        }
    }

    // ====== MÉTODOS DE DIAGNÓSTICO Y UTILIDADES ======

    /**
     * Obtiene información detallada sobre las dimensiones actuales del navegador
     */
    public static Map<String, Object> getScreenInfo(WebDriver targetDriver) {
        if (!(targetDriver instanceof JavascriptExecutor)) {
            throw new UnsupportedOperationException("El driver no soporta JavaScript");
        }

        JavascriptExecutor js = (JavascriptExecutor) targetDriver;
        Map<String, Object> info = new HashMap<>();

        try {
            // Información de la ventana
            Number windowWidth = (Number) js.executeScript("return window.innerWidth;");
            Number windowHeight = (Number) js.executeScript("return window.innerHeight;");
            Number screenWidth = (Number) js.executeScript("return screen.width;");
            Number screenHeight = (Number) js.executeScript("return screen.height;");
            Number devicePixelRatio = (Number) js.executeScript("return window.devicePixelRatio || 1;");

            // Información del viewport
            Number viewportWidth = (Number) js.executeScript("return document.documentElement.clientWidth;");
            Number viewportHeight = (Number) js.executeScript("return document.documentElement.clientHeight;");

            // Información del documento
            Number scrollWidth = (Number) js.executeScript("return document.documentElement.scrollWidth;");
            Number scrollHeight = (Number) js.executeScript("return document.documentElement.scrollHeight;");

            info.put("window", Map.of("width", windowWidth, "height", windowHeight));
            info.put("screen", Map.of("width", screenWidth, "height", screenHeight));
            info.put("viewport", Map.of("width", viewportWidth, "height", viewportHeight));
            info.put("document", Map.of("width", scrollWidth, "height", scrollHeight));
            info.put("devicePixelRatio", devicePixelRatio);
            info.put("userAgent", js.executeScript("return navigator.userAgent;"));

        } catch (Exception e) {
            info.put("error", "No se pudo obtener información de pantalla: " + e.getMessage());
        }

        return info;
    }

    /**
     * Imprime información detallada sobre las dimensiones de pantalla
     */
    public static void printScreenInfo(WebDriver targetDriver) {
        Map<String, Object> info = getScreenInfo(targetDriver);
        System.out.println("=== INFORMACIÓN DE PANTALLA ===");
        info.forEach((key, value) -> System.out.println("[Driver] " + key + ": " + value));
        System.out.println("==============================");
    }

    /**
     * Valida que las dimensiones aplicadas coincidan con las esperadas
     */
    public static boolean validateDeviceDimensions(WebDriver targetDriver, DevicePreset device, Orientation orientation) {
        if (!(targetDriver instanceof JavascriptExecutor)) {
            return false;
        }

        try {
            JavascriptExecutor js = (JavascriptExecutor) targetDriver;
            Number actualWidth = (Number) js.executeScript("return window.innerWidth;");
            Number actualHeight = (Number) js.executeScript("return window.innerHeight;");
            Number actualDPR = (Number) js.executeScript("return window.devicePixelRatio || 1;");

            java.awt.Dimension expected = device.getDimensions(orientation);

            boolean widthMatch = Math.abs(actualWidth.intValue() - expected.width) <= 5; // Tolerancia de 5px
            boolean heightMatch = Math.abs(actualHeight.intValue() - expected.height) <= 5;
            boolean dprMatch = Math.abs(actualDPR.doubleValue() - device.dpr) <= 0.1;

            System.out.println("[Driver] Validación de dimensiones:");
            System.out.println("[Driver] Esperado: " + expected.width + "x" + expected.height + " (DPR: " + device.dpr + ")");
            System.out.println("[Driver] Actual: " + actualWidth + "x" + actualHeight + " (DPR: " + actualDPR + ")");
            System.out.println("[Driver] Coincidencia: W=" + widthMatch + ", H=" + heightMatch + ", DPR=" + dprMatch);

            return widthMatch && heightMatch && dprMatch;

        } catch (Exception e) {
            System.out.println("[Driver] Error validando dimensiones: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reajusta las dimensiones si no coinciden con las esperadas
     */
    public static boolean ensureCorrectDimensions(WebDriver targetDriver, DevicePreset device, Orientation orientation) {
        if (!validateDeviceDimensions(targetDriver, device, orientation)) {
            System.out.println("[Driver] Las dimensiones no coinciden, reaplicando emulación...");
            if (targetDriver instanceof ChromiumDriver) {
                applyMobileEmulation((ChromiumDriver) targetDriver, device, orientation);
                // Esperar un momento para que se apliquen los cambios
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return validateDeviceDimensions(targetDriver, device, orientation);
            }
        }
        return true;
    }

    /**
     * Método mejorado para abrir pestaña con emulación móvil con validación
     */
    public static WebDriver openMobileEmulatedTabWithValidation(WebDriver targetDriver, String url,
                                                                DevicePreset device, Orientation orientation) {
        WebDriver result = openMobileEmulatedTab(targetDriver, url, device, orientation);

        // Validar que las dimensiones se aplicaron correctamente
        if (!ensureCorrectDimensions(targetDriver, device, orientation)) {
            System.out.println("[Driver] Advertencia: No se pudieron aplicar las dimensiones correctas");
        }

        // Imprimir información de diagnóstico
        printScreenInfo(targetDriver);

        return result;
    }

    /**
     * Establece el nivel de zoom de la página actual
     * @param targetDriver el WebDriver
     * @param zoomLevel nivel de zoom (0.8 = 80%, 1.0 = 100%, 1.2 = 120%)
     */
    public static void setPageZoom(WebDriver targetDriver, double zoomLevel) {
        if (targetDriver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) targetDriver;
            js.executeScript("document.body.style.zoom = '" + zoomLevel + "'");
        }
    }
}
