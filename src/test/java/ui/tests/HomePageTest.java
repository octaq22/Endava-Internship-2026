package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;
import ui.pages.MyAccountPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HomePageTest extends BaseTest {

    @Test
    void userCanNavigateToMyAccount() {
        // Verifică autentificarea și navigarea la pagina My Account.
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);

        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        MyAccountPage myAccountPage = homePage.navigateToMyAccount();

        assertTrue( myAccountPage.isMyAccountPageVisible(),
                "My Account page should be visible after navigation" );
    }
    @Test
    void userCanCreateTransactionAndSeeItInMine() {
        // Deschide formularul New și afișează butoanele și linkurile găsite.
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);
        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );
        assertTrue(homePage.isHomePageVisible(),
                "Home page should be visible after login");
        homePage.clickNew();
        System.out.println("Butoane: " + page.locator("button").allTextContents());
        System.out.println("Linkuri: " + page.locator("a").allTextContents());
    }
    @Test
    void userCanCreateNewTransaction() {
        // Selectează primul contact și completează suma și nota tranzacției.
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);
        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        assertTrue(homePage.isHomePageVisible(),
                "Home page should be visible after login");

        homePage.clickNew();
        page.locator("[data-test*='user-list-item']").first().click();

        assertTrue(page.getByPlaceholder("Amount").isVisible(),
                "Payment form should appear after selecting the first contact");

        page.getByPlaceholder("Amount").fill("100");
        page.getByPlaceholder("Add a note").fill("imprumut");
    }
    @Test
    void userCanCommentTransaction() {
        // Creează o tranzacție, o deschide din Mine și adaugă un comentariu.
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);
        loginPage.openLoginPage(ConfigManager.get("base.url"));

        HomePage homePage = loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        assertTrue(homePage.isHomePageVisible(),
                "Home page should be visible after login");

        homePage.clickNew();
        page.locator("[data-test*='user-list-item']").first().click();

        page.getByPlaceholder("Amount").fill("100");
        page.getByPlaceholder("Add a note").fill("datorie");

        page.locator("[data-test*='submit-payment']").click();

        var successMessage = page.locator("[data-test*='alert-bar-success']");
        successMessage.waitFor();
        assertTrue(successMessage.textContent().contains("Transaction Submitted!"),
                "A success message should confirm the transaction");

        page.locator("[data-test*='return-to-transactions']").click();
        page.locator("[data-test='sidenav-home']").click();
        page.locator("[data-test*='personal-tab']").click();

        var latestTransaction = page.locator("[data-test*='transaction-item']").first();
        assertTrue(latestTransaction.innerText().contains("datorie"),
                "The newest transaction should be the one just created");
        latestTransaction.click();

        var commentInput = page.locator("[data-test*='comment-input']");
        commentInput.fill("Poate nu mi dai inapoi!");
        commentInput.press("Enter");

        var postedComment = page.getByText("Poate nu mi dai inapoi!");
        postedComment.waitFor();
        assertTrue(postedComment.isVisible(),
                "The comment should appear in transaction details");
    }
}