package ui.tests;

import com.microsoft.playwright.Locator;
import framework.config.ConfigManager;
import org.junit.jupiter.api.Test;
import ui.base.BaseTest;
import ui.pages.HomePage;
import ui.pages.LoginPage;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Module4ScenarioTest extends BaseTest {

    @Test
    void module4ScenarioTests() {
        String note = "Module 4 payment " + UUID.randomUUID();

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


        page.locator("[data-test*='user-list-item']")
                .first()
                .click();


        page.getByPlaceholder("Amount").fill("25");
        page.getByPlaceholder("Add a note").fill(note);
        page.locator("[data-test*='submit-payment']").click();

        Locator successMessage =
                page.locator("[data-test*='alert-bar-success']");
        successMessage.waitFor();

        assertTrue(successMessage.innerText().contains("Transaction Submitted!"),
                "A success message should confirm the payment");


        page.locator("[data-test*='return-to-transactions']").click();
        page.locator("[data-test='sidenav-home']").click();


        page.locator("[data-test*='personal-tab']").click();


        Locator createdTransaction = page
                .locator("[data-test*='transaction-item']")
                .filter(new Locator.FilterOptions().setHasText(note))
                .first();

        createdTransaction.waitFor();

        assertTrue(createdTransaction.isVisible(),
                "The created transaction should appear in Mine");


        createdTransaction.click();


        Locator transactionNote = page.getByText(note);
        transactionNote.waitFor();

        assertTrue(transactionNote.isVisible(),
                "Transaction details should show the note");
    }
}