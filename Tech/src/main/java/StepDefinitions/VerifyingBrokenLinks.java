package StepDefinitions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.google.common.base.Predicate;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.openqa.selenium.JavascriptExecutor;

public class VerifyingBrokenLinks {

	static WebDriver driver;
	static String homePage = "https://developer.here.com/";
	static By Documentation=By.xpath("//span[@data-title='Documentation']");
	static By DocumentationLinks=By.tagName("a");

	@Given("^I am launching an application$") 
	public static void launchingAnApplication() throws InterruptedException{
		String projectpath=System.getProperty("user.dir");
		String ChromePath = projectpath+"/src/main/java/Framework/"+"chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", ChromePath);
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.get(homePage);
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		Thread.sleep(5000);
		untilAngularFinishHttpCalls(homePage);

	}


	public static  void untilAngularFinishHttpCalls(String Url) {

		try{
			final String javaScriptToLoadAngular =
					"var injector = window.angular.element('body').injector();" + 
							"var $http = injector.get('$http');" + 
							"return ($http.pendingRequests.length === 0)";

			ExpectedCondition<Boolean> pendingHttpCallsCondition = new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript(javaScriptToLoadAngular).equals(true);
				}
			};
			WebDriverWait wait = new WebDriverWait(driver, 30); 
			wait.until(pendingHttpCallsCondition);
		}
		catch(Exception ex){
			System.out.println(Url+" is not loaded properly");
		}
	}
	@Then("^Verifying broken links are available$")
	public static void verifyingBrokenLinks() throws InterruptedException{
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		String url = "";
		HttpURLConnection huc = null;
		int respCode = 200;
		Actions act=new Actions(driver);

		act.moveToElement(new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@data-title='Documentation'])[2]")))).click().build().perform();
		driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
		Thread.sleep(5000);
		List<WebElement> links = driver.findElements(By.xpath("//a[contains(@class,'cta')]|//a[@target='_self']|//a[@data-utag-section='documentation-index-top']"));
		System.out.println("Total Number of links is "+links.size());



		for(int i=0;i<links.size();i++){
			//act.moveToElement(new WebDriverWait(driver, 20).until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@data-title='Documentation'])[2]")))).click().build().perform();
			/*Documentaion=driver.findElement(By.xpath("//span[@data-title='Documentation']"));
			act.moveToElement(Documentaion).click().build().perform();;*/
			links = driver.findElements(By.xpath("//a[contains(@class,'cta')]|//a[@target='_self']|//a[@data-utag-section='documentation-index-top']"));
			

			String href=links.get(i).getAttribute("href");
			if(href.contains("#")){
				url="https://developer.here.com/"+"documentation#";
			}
			else if(href.contains("(")||href.contains("0")){
				System.out.println("This is not a valid url "+"url="+href+"LinkName="+links.get(i).getText());
				continue;
			}
			else if(href.contains("https://developer.here.com/")){
				url=href;
			}else if(href.contains("https")){
				url=href;
			}
			else{
				url="https://developer.here.com/"+href;
			}

			System.out.println("Successfully The page is opened and loaded");
			System.out.println("Opened page is="+driver.getCurrentUrl());
			if(url == null || url.isEmpty()){
				System.out.println("URL is either not configured for anchor tag or it is empty");
				continue;
			}

			if(!url.startsWith(homePage)){
				System.out.println("URL belongs to another domain "+url);

			}

			try {
				huc = (HttpURLConnection)(new URL(url).openConnection());

				huc.setRequestMethod("HEAD");

				huc.connect();

				respCode = huc.getResponseCode();

				if(respCode >= 400){
					System.out.println(url+" is a broken link");
				}
				else{
					System.out.println(url+" is a valid link");
				}


				System.out.println("OpeningTheUrl "+url);
				JavascriptExecutor js = (JavascriptExecutor)driver;
				js.executeScript("arguments[0].click();", links.get(i));
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				Thread.sleep(5000);
				untilAngularFinishHttpCalls(url);
				driver.navigate().back();
				driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
				Thread.sleep(5000);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}







		}



		driver.quit();





	}


	public static void main(String[] args) {
		//launchingAnApplication();
		//verifyingBrokenLinks();

	}

}
