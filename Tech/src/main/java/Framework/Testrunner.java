package Framework;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
@RunWith(Cucumber.class)				
@CucumberOptions(features="src/test/java/Feature_Files/Verifying_BrokenLinks.feature",glue={"StepDefinitions"})	
public class Testrunner {

}
