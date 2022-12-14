package com.cucumber.junit.steps;

import desktop.pages.*;
import driver.SingletonDriver;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import static constants.Constants.*;

public class StepDefs {

    WebDriver driver = SingletonDriver.getInstance();
    HomePage homePageObject;
    SearchResultsPage searchResultsPageObject;
    AccountPage accountPageObject;
    BasketPage basketPageObject;
    CheckoutPage checkoutPageObject;

    @Given("^(?:Guest user|Customer) opens bookdepository site$")
    public void openSite() {
        homePageObject = new HomePage(driver);
    }


    @Given("I open the {string}")
    public void openPage(String pageName) {
        if(pageName.equals("Initial home page")){
        homePageObject = new HomePage(driver);
        }
    }

    @When("I search for {string}")
    public void searchForTerm(String searchTerm) {
        searchResultsPageObject = homePageObject.enterSearchTerm(searchTerm);
    }

    @Then("^the search results are displayed$")
    public void pageWithSearchResultsIsPresent() {
        Assertions.assertThat(searchResultsPageObject.isSearchResultsPresent())
                .overridingErrorMessage("Search results are not displayed")
                .isTrue();
    }

    @When("^clicks on the Sign (?:in|out) button on navigation bar$")
    public void clickSigninButton() {
        accountPageObject = homePageObject.navBarClick();
        accountPageObject.isLoginTitleDisplayed();
    }

    @When("^the user fills in the login and password$")
    public void fillInCredentials() {
        accountPageObject.fillInSignInFields("olsteshen@example.com", "Temp12345");
    }

    @Then("^the user is logged in$")
    public void advancedSearchPageIsDisplayed() {
        Assertions.assertThat(accountPageObject.pageURL().equals(SIGN_IN_PAGE_URL))
                .overridingErrorMessage("User is not logged in")
                .isTrue();
    }

    @Given("I am an anonymous customer with clear cookies")
    public void setAnonymousCustomer() {
        driver.manage().deleteAllCookies();
    }


    @And("Search results contain the following products")
    public void checkSearchResultsContainsProducts(List<String> expectedBookNames) {
       Assertions.assertThat(searchResultsPageObject.getBookTitleInResults())
               .extracting(WebElement::getText)
               .as("Some of the books are not shown")
               .containsAll(expectedBookNames);
    }

    @And("I apply the following search filters")
    public void applySearchFilters(DataTable filtersData) {
    searchResultsPageObject.applyFilters(filtersData);
    }

    @Then("Search results contain only the following products")
    public void checkSearchResultsContainOnlyProducts(List<String> expectedOnlyBookNames) {
        Assertions.assertThat(searchResultsPageObject.getBookTitleInResults())
                .extracting(WebElement::getText)
                .as("Search results are not as expected")
                .containsExactlyElementsOf(expectedOnlyBookNames);
    }

    @Then("I am redirected to a {string}")
    public void checkPageURL(String pageName) {
        switch (pageName) {
            case "Basket page" -> Assertions.assertThat(basketPageObject.pageURL().equals(BASKET_PAGE_URL))
                    .overridingErrorMessage("Wrong page url")
                    .isTrue();
            case "Checkout page" -> Assertions.assertThat(checkoutPageObject.pageURL().equals(CHECKOUT_PAGE_URL))
                    .overridingErrorMessage("Wrong page url")
                    .isTrue();
            case "Search page" -> Assertions.assertThat(searchResultsPageObject.pageURL().contains(SEARCH_RESULT_PAGE_URL))
                    .overridingErrorMessage("Wrong page url")
                    .isTrue();
        }
    }

    @When("I click 'Add to basket' button for product with name {string}")
    public void clickATBButton(String productName) {
        searchResultsPageObject.atbButton(productName).click();
    }

    @When("I click 'Checkout' button on 'Basket' page")
    public void clickCheckoutOnBasket() {
        checkoutPageObject = basketPageObject.buttonCheckoutOnBasket();
    }

    @When("I click 'Buy now' button")
    public void clickBuyButton() {
        checkoutPageObject.buyNowButton().click();
    }

    @Then("the following validation error messages are displayed on 'Delivery Address' form:")
    public void checkValidationErrorMessage(List<Map<String, String>> expectedErrors){
        checkoutPageObject.checkErrorMessage(expectedErrors);
    }

    @And("Checkout order summary is as following:")
    public void checkOrderSummary(@Transpose Map<String, String> orderSummary) {
        checkoutPageObject.checkOrderSummary(orderSummary);
    }

    @And("I checkout as a new customer with email {string}")
    public void fillUserDetails(String email) {
        checkoutPageObject.fillInUserEmail(email);
    }

    @When("I fill delivery address information manually:")
    public void fillDeliveryAddressFields(@Transpose Map<String, String> deliveryAddress) {
        checkoutPageObject.fillAddressFields(deliveryAddress);
    }

    @Then("the following validation error messages are displayed on 'Payment' form:")
    public void checkValidationErrorMessage(String expectedError) {
        String error = expectedError.replace(",", "");
        String actualError = checkoutPageObject.getErrorMessagePaymentForm().getText().replace("\n", " ");
        Assertions.assertThat(actualError.equals(error))
                .overridingErrorMessage("error message is not as expected")
                .isTrue();
    }

    @When("I enter my card details")
    public void fillCardDetails(Map<String, String> cardDetails) {
        checkoutPageObject.enterCardDetails(cardDetails);
    }

    @And("Basket order summary is as following:")
    public void checkBasketSummary(DataTable basketSummary) {
        basketPageObject.checkBasketOrderSummary(basketSummary);
    }

    @And("I select 'Basket Checkout' in basket pop-up")
    public void clickButtonContinue() {
        basketPageObject = searchResultsPageObject.clickButtonContinue();
    }

    @Then("there is no validation error messages displayed on 'Delivery Address' form")
    public void checkNoErrorInAddressForm() {
        Assertions.assertThat(checkoutPageObject.getErrorMessageAddressForm())
                .extracting(WebElement::getText)
                .as("Error message in Delivery Address form")
                .containsOnly("");
    }
}
