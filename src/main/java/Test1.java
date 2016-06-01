import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

class Test1 {
	
		/**
		* @param args
		*/
	static WebDriver driver;
	static Double orderCost;
	
		       public static void main(String[] args) {
		              
		// Start at the home page of "www.build.com"
		              driver = new FirefoxDriver();
		              String appUrl = "https://www.build.com";
		              
		// launch the firefox browser and open the application url
		              driver.get(appUrl);
		              		              
		// maximize the browser window and set default time out
		              driver.manage().window().maximize();
		              driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); 
		              		
		// Init order cost to zero
		              orderCost = 0.00;
		              
		// Add one Suede Kohler K-6626-6U to the cart
		              assertThat(SearchFor("Suede Kohler K-6626-6U")).contains("K-6626-6U");		              
		              orderCost += AddToCart();

		// continue Shopping
		              continueShopping();
		              
		// Add one Cashmere Kohler K-6626-6U to the cart
		              assertThat(SearchFor("Cashmere Kohler K-6626-6U")).contains("k-6626-6U-0");		              
		              orderCost += AddToCart();
		              
		// continue Shopping
		              continueShopping();
		              
		// Add two Kohler K-6066-ST to the cart
		              assertThat(SearchFor("Kohler K-6066-ST")).contains("K-6066-ST");
		              orderCost += AddToCart();
		              
		// Begin the Checkout flow and stop on the Review and Delivery page
		              Checkout();
		              
        // Fill in the necessary information
		              shippingAddress("Mickey", "Mouse", "1313 Disneyland Dr", "92802", 
	    			   			"Anaheim", "CA", "7147817277", "mmouse@disney.net");
	    	   
		              creditCardInfo("4111111111111111", "1", "2019", "Mickey Mouse", "123");
		              
		 // Assert that the CA Tax is correct
		              String subTotal = driver.findElement(By.id("subtotalamount")).getText().replace("$", "").replace(",", "");
			    	  String tax = driver.findElement(By.id("taxAmount")).getText().replace("$", "").replace(",", "");
			    	  String grandTotal = driver.findElement(By.id("grandtotalamount")).getText().replace("$", "").replace(",", "");
			    	 
			    	  Double salesTaxRate = 0.075;
			    	   
			    	  BigDecimal totalStateTax = new BigDecimal(Double.parseDouble(subTotal) * salesTaxRate);
			    	  // I don't know why we have to round down here?
			    	  totalStateTax = totalStateTax.setScale(2, BigDecimal.ROUND_DOWN);
			    	  
			    	  assertThat(orderCost).isEqualTo(Double.parseDouble(subTotal));
			    	  assertThat(totalStateTax.toString()).isEqualTo(tax);
			    	   
	     // Assert that the Grand Total is correct			    	   
			    	  assertThat(orderCost + totalStateTax.doubleValue()).isEqualTo(Double.parseDouble(grandTotal));
			    	   	              
		// close the web browser
		              driver.close();
		              System.out.println("Test was a failure, we did not find any bugs.");

		              // terminate the program
		              System.exit(0);

		       }
		       
		       //
		       public static String SearchFor(String item)
		       {
		    	   // Search for an item
		              WebElement searchbox = driver.findElement(By.id("search_txt"));
		              searchbox.clear();
		              searchbox.sendKeys(item);
		              
		              WebElement search = driver.findElement(By.className("search-site-search"));
		              search.click();
		              
		              WebElement results = driver.findElement(By.id("titleProdId"));
		              return results.getText();
		       }
		       
		       // Add to Cart
		       public static Double AddToCart(String ... howmany)
		       {
		    	   String howmany_;
		    	   if(howmany.length == 1)
		    		   howmany_ = howmany[0];
		    	   else howmany_ = "1";
		    	   		    	   
		    	   if (howmany_ != "1")
		    	   {
		    		   WebElement number = driver.findElement(By.id("qtyselected"));
		    		   number.clear();
		    		   number.sendKeys(howmany_);
		    	   }
		    	   
		    	   // Click on the Add to Cart button
		    	   WebElement add = driver.findElement(By.className("addToCart"));
		    	   add.click();		    	   
		    	   waitForPageLoad();
		    	   
		    	   assert(driver.findElement(By.className("successbox"))).isDisplayed();
		    	    	    	  
		    	   return Double.parseDouble(driver.findElement(By.className("item")).findElement(By.className("price")).getText().replace("$", ""));
		       }
		       
		       // Click on the Home button to Continue Shopping
		       
		       public static void continueShopping()
		       {
		              driver.findElement(By.className("caret")).click();
		              driver.findElement(By.linkText("Home")).click();
		       }
		       
		       public static void Checkout()
		       {
		    	   WebElement showCart = driver.findElement(By.className("cart-box"));
		    	   showCart.click();
		    	   waitForPageLoad();
		    	   
		    	   WebElement primaryCheckoutButton = driver.findElement(By.id("cartNav")).findElement(By.className("primary"));
		    	   primaryCheckoutButton.click();
		    	   waitForPageLoad();
		    	   
		    	   
		    	   WebElement guestCheckout = driver.findElement(By.id("guest-login")).findElement(By.className("primary"));
		    	   guestCheckout.click();
		    	   waitForPageLoad();	    	   
		    	  
		       }
		       
		       public static void shippingAddress(String first, String last, String addr1, String zip,
		    		   								String city, String state, String phone, String email)								
		       {
		    	   
		    	   driver.findElement(By.id("shippingfirstname")).clear();
		    	   driver.findElement(By.id("shippingfirstname")).sendKeys(first);
		    	   
		    	   driver.findElement(By.id("shippinglastname")).clear();
		    	   driver.findElement(By.id("shippinglastname")).sendKeys(last);
		    	   
		    	   driver.findElement(By.id("shippingaddress1")).clear();
		    	   driver.findElement(By.id("shippingaddress1")).sendKeys(addr1);
		    	   
		    	   driver.findElement(By.id("shippingphonenumber")).clear();
		    	   driver.findElement(By.id("shippingphonenumber")).sendKeys(phone);
		    	   
		    	   driver.findElement(By.id("emailAddress")).clear();
		    	   driver.findElement(By.id("emailAddress")).sendKeys(email);
		    	   
		    	   driver.findElement(By.id("shippingpostalcode")).clear();
		    	   driver.findElement(By.id("shippingpostalcode")).sendKeys(zip);
		    	   
		    	   // Have to wait here for city and state to be filled in
		    	   //if (!waitOnJQuery())
		    	   //{
		    		 driver.findElement(By.id("shippingcity")).clear();
			    	 driver.findElement(By.id("shippingcity")).sendKeys(city);
			    	   
			    	 Select stateSelect = new Select(driver.findElement(By.id("shippingstate_1")));
			    	 stateSelect.selectByValue(state);
		    	   //}
		    	   
		    	 
		    	   
		       }
		       
		       public static void creditCardInfo(String number, String mm, String yy, String name,
		    		   						String ccv)
		       {
		    	   
		    	   driver.findElement(By.id("creditCardNumber")).clear();
		    	   driver.findElement(By.id("creditCardNumber")).sendKeys(number);
		    	   
		    	   Select ccMonth = new Select(driver.findElement(By.id("creditCardMonth")));
		    	   ccMonth.selectByValue(mm);
		    	   
		    	   Select ccYear = new Select(driver.findElement(By.id("creditCardYear")));
		    	   ccYear.selectByValue(yy);
		    	   
		    	   driver.findElement(By.id("creditcardname")).clear();
		    	   driver.findElement(By.id("creditcardname")).sendKeys("Mickey Mouse");
		    	   
		    	   driver.findElement(By.id("creditCardCVV2")).clear();
		    	   driver.findElement(By.id("creditCardCVV2")).sendKeys(ccv);
		    	   
		    	   driver.findElement(By.id("delEditCard")).click();
		    	   
		    	   driver.findElement(By.id("creditcard")).findElement(By.className("primary")).click();
		    	   waitForPageLoad();
		    	   
		       }
		       
		       
		       public static void waitForPageLoad()
		       {
		    	   WebDriverWait wait = new WebDriverWait(driver, 15);
		    	   
		    	   wait.until(new ExpectedCondition<Boolean>() {
		    	        public Boolean apply(WebDriver wdriver) {
		    	            return ((JavascriptExecutor) driver).executeScript(
		    	                "return document.readyState"
		    	            ).equals("complete");
		    	        }
		    	   });
		    	}
		       
		       public static boolean waitOnJQuery()
		       {	
		    	   WebDriverWait wait = new WebDriverWait(driver, 15);
		    	   
		    	   try {
		    		   wait.until(new ExpectedCondition<Boolean>() {
		    	        public Boolean apply(WebDriver wdriver) {
		    	            return ((JavascriptExecutor) driver).executeScript(
		    	                "return jQuery.active"
		    	            ).toString().equals("0");
		    	        }
		    		   });
		    		   return true;
		    	   } catch (TimeoutException e) {
		    		   return false;
		    	   }
		    	       	    
		    	}
		       
		}

