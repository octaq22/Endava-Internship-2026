import { test, expect } from "@fixtures/ui";
import { bySel, loginByXstate, scanA11y } from "@ui";

test.describe("A11Y-02: notification links have accessible names", () => {
  test("audits the notifications screen for serious link-name violations", async ({ page, users }, testInfo) => {
    test.fail(true, "A11Y-02: icon-only links may not have accessible names");

    await loginByXstate(page, users[0].username);
    await page.goto("/notifications");
    await expect(bySel(page, "notifications-list")).toBeVisible();

    const results = await scanA11y(page, testInfo, "notifications");
    const violations = results.violations
      .filter((violation) => violation.id === "link-name" && violation.impact === "serious")
      .map((violation) => ({
        help: violation.help,
        nodes: violation.nodes.map((node) => ({ target: node.target, summary: node.failureSummary })),
      }));

    // Keep the report readable and retain the exact elements flagged by axe in the attachment.
    expect.soft(violations, "Notifications page should not contain serious link-name violations").toEqual([]);
  });
});
