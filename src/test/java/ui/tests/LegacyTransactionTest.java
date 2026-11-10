package ui.tests;

import framework.config.ConfigManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ui.base.BaseTest;
import ui.pages.LoginPage;
import ui.pages.NewTransactionPage;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LegacyTransactionTest extends BaseTest {

    @ParameterizedTest
    @CsvFileSource(
            resources = "/testdata/legacy-transactions.csv",
            numLinesToSkip = 1
    )
    void userMakesTransaction(
            String firstName,
            String recipientId,
            String amount,
            String description,
            String expectedAction
    ) {
        LoginPage loginPage = new LoginPage();
        loginPage.init(page);
        loginPage.openLoginPage(ConfigManager.get("base.url"));
        loginPage.login(
                ConfigManager.get("login.username"),
                ConfigManager.get("login.password")
        );

        NewTransactionPage transactionPage = new NewTransactionPage();
        transactionPage.init(page);
        transactionPage.openNewTransactionPage(ConfigManager.get("base.url"));
        transactionPage.selectRecipient(firstName, recipientId);
        transactionPage.enterAmount(amount);
        transactionPage.enterDescription(description);
        transactionPage.submitPayment();

        if ("return".equals(expectedAction)) {
            transactionPage.waitForReturnToTransactions();
            assertTrue(
                    transactionPage.isReturnToTransactionsVisible(),
                    "Return to transactions action should be visible after payment"
            );
        } else if ("createAnother".equals(expectedAction)) {
            transactionPage.waitForCreateAnotherTransaction();
            assertTrue(
                    transactionPage.isCreateAnotherTransactionVisible(),
                    "Create another transaction action should be visible after payment"
            );
        } else {
            throw new IllegalArgumentException(
                    "Unknown expected action: " + expectedAction
            );
        }
    }
}