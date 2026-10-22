import { test, expect } from "@fixtures/ui";
import {
  alertBar,
  bySel,
  bySelLike,
  fillTransactionForm,
  loginByXstate,
  searchUsersInput,
  selectContact,
  startNewTransaction,
  waitForApi,
} from "@ui";
import { stubServerError } from "@mocks";

const createTransaction = { method: "POST", path: "/transactions" } as const;

test.describe("KI-31: failed payment displays an error", () => {
  test("shows an error when POST /transactions returns 500", async ({ page, users }) => {
    test.fail(true, "KI-31: the UI reports success before the transaction API responds");

    const [sender, recipient] = users;
    await loginByXstate(page, sender.username);
    await startNewTransaction(page);

    const search = waitForApi(page, { method: "GET", path: "/users/search" });
    await searchUsersInput(page).fill(recipient.firstName);
    await search;
    await selectContact(page, recipient.firstName);
    await fillTransactionForm(page, "25", "Test payment that will fail");

    await stubServerError(page, createTransaction, 500);
    const failedPayment = waitForApi(page, createTransaction);
    await bySelLike(page, "submit-payment").click();
    const response = await failedPayment;

    expect.soft(response.status()).toBe(500);
    await expect.soft(alertBar(page, "error")).toBeVisible();
    await expect.soft(alertBar(page, "success")).toHaveCount(0);
    await expect.soft(bySel(page, "new-transaction-create-another-transaction")).toHaveCount(0);
  });
});
