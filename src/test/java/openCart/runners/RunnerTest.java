package openCart.runners;

import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;
import io.cucumber.junit.CucumberOptions;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features",
        plugin = {"pretty", "html:target/cucumber"},
        glue = "openCart.steps",
        snippets = CucumberOptions.SnippetType.CAMELCASE,
        monochrome = true,
        tags = "@compraOpenCart"

)
public class RunnerTest {
}

