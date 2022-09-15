package desktop.pages;

import abstractclasses.page.AbstractPage;
import driver.SingletonDriver;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Map;

public class CheckoutPage extends AbstractPage {
    @FindBy(xpath = "//div[@id='deliveryAddress']//div[@class='error-block']")
    public List<WebElement> deliveryAddressErrorBlocks;
    @FindBy(xpath = "//div[@class='buynow-error-msg']")
    public WebElement paymentErrorBlocks;
    @FindBy(xpath="//input[@name='emailAddress']")
    public WebElement emailAddress;
    @FindBy(xpath="//input[@name='delivery-fullName']")
    public WebElement fullName;
    @FindBy(xpath="//select[@name='deliveryCountry']")
    public WebElement deliveryCountry;
    @FindBy(xpath="//input[@name='delivery-addressLine1']")
    public WebElement addressLine1;
    @FindBy(xpath="//input[@name='delivery-addressLine2']")
    public WebElement addressLine2;
    @FindBy(xpath="//input[@name='delivery-city']")
    public WebElement city;
    @FindBy(xpath="//input[@name='delivery-county']")
    public WebElement state;
    @FindBy(xpath="//input[@name='delivery-postCode']")
    public WebElement postcode;
    @FindBy(xpath="//input[@name='cvv']")
    public WebElement cvv;
    @FindBy(xpath = "//input[@name='expiration']")
    public WebElement expiration;
    @FindBy(xpath = "//input[@name='credit-card-number']")
    public WebElement ccNumber;
    @FindBy(xpath = "//div[@class='wrapper']/div[2]/dl/dd")
    public WebElement subTotal;
    @FindBy(xpath = "//div[@class='wrapper']/div[3]/dl/dd")
    public WebElement delivery;
    @FindBy(xpath = "//dd[@class='text-right total-tax']")
    public WebElement vatTax;
    @FindBy(xpath = "//dd[@class='text-right total-price']")
    public WebElement totalPrice;
    @FindBy(xpath="//button[@type='submit']")
    public WebElement buyNowButton;
    @FindBy(xpath="//div[@id='emailAddress']//div[@class='error-block']")
    public WebElement emailAddressError;
    @FindBy(xpath="//div[@id='delivery-fullName-errors'][@class='error-block']")
    public WebElement fullNameError;
    @FindBy(xpath="//div[@id='delivery-addressLine1-errors'][@class='error-block']")
    public WebElement addressLine1Error;
    @FindBy(xpath="//div[@id='delivery-city-errors'][@class='error-block']")
    public WebElement cityError;
    @FindBy(xpath="//div[@id='delivery-postCode-errors'][@class='error-block']")
    public WebElement postcodeError;
    @FindBy(xpath = "//iframe[@name='braintree-hosted-field-number']")
    public WebElement iFrameCC;
    @FindBy(xpath = "//iframe[@name='braintree-hosted-field-expirationDate']")
    public WebElement iFrameExpiration;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        SingletonDriver.getInstance();
    }

    public String pageURL() {
        return getPageUrl();
    }

    public WebElement buyNowButton() {
        return buyNowButton;
    }

    public List<WebElement> getErrorMessageAddressForm() {
        return deliveryAddressErrorBlocks;
    }

    public WebElement getErrorMessagePaymentForm() {
        return paymentErrorBlocks;
    }

    public void fillInUserEmail(String email) {
        emailAddress.sendKeys(email);
    }

    public void fillAddressFields(Map<String, String> deliveryAddress){
        Select countrySelect = new Select(deliveryCountry);
        fullName.sendKeys(deliveryAddress.get("Full name"));
        countrySelect.selectByVisibleText(deliveryAddress.get("Delivery country"));
        addressLine1.sendKeys(deliveryAddress.get("Address line 1"));
        addressLine2.sendKeys(deliveryAddress.get("Address line 2"));
        city.sendKeys(deliveryAddress.get("Town/City"));
        state.sendKeys(deliveryAddress.get("County/State"));
        postcode.sendKeys(deliveryAddress.get("Postcode"));
    }

    public void checkOrderSummary(Map<String, String> orderDetails){
        Assertions.assertAll("Error on checkout",
                () -> Assertions.assertEquals(orderDetails.get("Sub-total"), subTotal.getText()),
                () -> Assertions.assertEquals(orderDetails.get("Delivery"), delivery.getText()),
                () -> Assertions.assertEquals(orderDetails.get("VAT"), vatTax.getText()),
                () -> Assertions.assertEquals(orderDetails.get("Total"), totalPrice.getText()));
    }

    public void checkErrorMessage(List<Map<String, String>> expectedErrors){
        Assertions.assertAll("Error on checkout",
                () -> Assertions.assertEquals(expectedErrors.get(0).get("validaton error message"), emailAddressError.getText()),
                () -> Assertions.assertEquals(expectedErrors.get(1).get("validaton error message"), fullNameError.getText()),
                () -> Assertions.assertEquals(expectedErrors.get(2).get("validaton error message"), addressLine1Error.getText()),
                () -> Assertions.assertEquals(expectedErrors.get(3).get("validaton error message"), cityError.getText()),
                () -> Assertions.assertEquals(expectedErrors.get(4).get("validaton error message"), postcodeError.getText())
                );
    }

    public void enterCardDetails(Map<String, String> cardDetails) {
        driver.switchTo().frame(iFrameCC);
        ccNumber.sendKeys(cardDetails.get("cardNumber"));
        driver.switchTo().defaultContent();
        driver.switchTo().frame(iFrameExpiration);
        expiration.sendKeys(cardDetails.get("Expiry date"));
        cvv.sendKeys(cardDetails.get("Cvv"));
    }
}
