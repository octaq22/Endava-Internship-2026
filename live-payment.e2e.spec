import { test, expect } from "@fixtures/ui";
import type { BrowserContext, Page } from "@playwright/test";
import { env } from "@config";
import {
  alertBar,
  bySel,
  bySelLike,
  exposeAppServices,
  fillTransactionForm,
  loginByXstate,
  openFeed,
  searchUsersInput,
  selectContact,
  startNewTransaction,
  transactionItems,
  waitForApi,
  waitForFeed,
  waitForListLoaded,
} from "@ui";
import { dollarsToCents } from "@utils/money";

test.describe("Multi-user payment journey", { tag: ["@e2e"] }, () => {
  test("Bob sees Alice's payment from a separate browser context", async ({ page: alicePage, browser, users, db }) => {
    const [alice, bob] = users;
    const amount = 25;
    const description = `Alice to Bob ${Date.now()}`;
    let bobContext: BrowserContext | undefined;

    try {
      bobContext = await browser.newContext({ baseURL: env.baseURL });
      const bobPage: Page = await bobContext.newPage();
      await exposeAppServices(bobPage);

      await test.step("Alice and Bob sign in in separate browser contexts", async () => {
        await Promise.all([
          loginByXstate(alicePage, alice.username),
          loginByXstate(bobPage, bob.username),
        ]);
      });

      await test.step("Alice sends Bob a payment", async () => {
        await startNewTransaction(alicePage);
        const searched = waitForApi(alicePage, { method: "GET", path: "/users/search" });
        await searchUsersInput(alicePage).fill(bob.firstName);
        await searched;
        await selectContact(alicePage, bob.firstName);
        await fillTransactionForm(alicePage, String(amount), description);

        const created = waitForApi(alicePage, { method: "POST", path: "/transactions" });
        await bySelLike(alicePage, "submit-payment").click();
        expect((await created).status()).toBe(200);
        await expect(alertBar(alicePage)).toHaveText("Transaction Submitted!");
      });

      await test.step("Bob refreshes his feed and sees the payment", async () => {
        await bobPage.reload();
        await waitForListLoaded(bobPage);
        const personal = waitForFeed(bobPage, "personal");
        await openFeed(bobPage, "personal");
        await personal;

        await expect(transactionItems(bobPage).filter({ hasText: description }).first()).toBeVisible();
        const bobAfter = await db.find("users", { id: bob.id });
        expect(bobAfter.balance).toBe(bob.balance + dollarsToCents(amount));
      });
    } finally {
      await bobContext?.close();
    }
  });
});
