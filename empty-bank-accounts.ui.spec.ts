import { test, expect } from "@fixtures/ui";
import { bySel, loginByXstate, stubApi, waitForApi } from "@ui";
import { buildBankAccount, buildList, listBankAccountsMatcher } from "@mocks";

test.describe("Bank accounts empty state", () => {
  test("shows the empty state and onboarding when the API returns no accounts", async ({ page, users }) => {
    // Build a deterministic empty list and stub the GraphQL response; no real account data is used.
    const accounts = buildList(buildBankAccount, 0, (index) => ({ userId: users[0].id }));
    await stubApi(page, listBankAccountsMatcher, { data: { listBankAccount: accounts } });

    await loginByXstate(page, users[0].username);
    const listed = waitForApi(page, listBankAccountsMatcher);
    await page.goto("/bankaccounts");
    const response = await listed;

    expect(response.status()).toBe(200);
    await expect(bySel(page, "empty-list-header")).toContainText("No Bank Accounts");
    await expect(bySel(page, "bankaccount-list")).toHaveCount(0);
    await expect(bySel(page, "user-onboarding-dialog")).toBeVisible();
  });
});
