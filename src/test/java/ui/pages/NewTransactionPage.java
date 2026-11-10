package ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class NewTransactionPage {

    private Page page;

    private Locator recipientSearchInput;
    private Locator amountInput;
    private Locator descriptionInput;
    private Locator submitPaymentButton;
    private Locator returnToTransactionsButton;
    private Locator createAnotherTransactionButton;

    public void init(Page page) {
        this.page = page;
        recipientSearchInput = page.locator("[data-test='user-list-search-input']");
        amountInput = page.locator("[data-test='transaction-create-amount-input'] input");
        descriptionInput = page.locator("[data-test='transaction-create-description-input'] input");
        submitPaymentButton = page.locator("[data-test='transaction-create-submit-payment']");
        returnToTransactionsButton = page.locator("[data-test='new-transaction-return-to-transactions']");
        createAnotherTransactionButton = page.locator("[data-test='new-transaction-create-another-transaction']");
    }

    public void openNewTransactionPage(String baseUrl) {
        page.navigate(baseUrl + "/transaction/new");
    }

    public void selectRecipient(String recipientName, String recipientId) {
        recipientSearchInput.fill(recipientName);
        page.locator("[data-test='user-list-item-" + recipientId + "']").click();
    }

    public void enterAmount(String amount) {
        amountInput.fill(amount);
    }

    public void enterDescription(String description) {
        descriptionInput.fill(description);
    }

    public void submitPayment() {
        submitPaymentButton.click();
    }

    public void waitForReturnToTransactions() {
        returnToTransactionsButton.waitFor();
    }

    public boolean isReturnToTransactionsVisible() {
        return returnToTransactionsButton.isVisible();
    }

    public void waitForCreateAnotherTransaction() {
        createAnotherTransactionButton.waitFor();
    }

    public boolean isCreateAnotherTransactionVisible() {
        return createAnotherTransactionButton.isVisible();
    }
}