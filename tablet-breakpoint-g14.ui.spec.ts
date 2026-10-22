import { test, expect } from "@fixtures/ui";
import { bySel, isMobile, loginByXstate } from "@ui";

test.describe("G-14: tablet-width responsive breakpoint", () => {
  test("the 500x800 mobile layout agrees with the test helper", async ({ page, users }, testInfo) => {
    test.fail(true, "G-14: between 414px and 599px the app is mobile but isMobile() reports desktop");

    expect.soft(page.viewportSize()).toEqual({ width: 500, height: 800 });
    await loginByXstate(page, users[0].username);

    const homeNav = bySel(page, "sidenav-home");
    const navToggle = bySel(page, "sidenav-toggle");
    const helperSaysMobile = isMobile(page);
    const appShowsMobileNavigation = !(await homeNav.isVisible()) && (await navToggle.isVisible());

    const diagnostics = [
      `viewport: ${JSON.stringify(page.viewportSize())}`,
      `isMobile(page): ${helperSaysMobile}`,
      `mobile navigation layout: ${appShowsMobileNavigation}`,
      `sidenav-home visible: ${await homeNav.isVisible()}`,
      `sidenav-toggle visible: ${await navToggle.isVisible()}`,
    ].join("\n");
    await testInfo.attach("g14-tablet-diagnostics.txt", { body: diagnostics, contentType: "text/plain" });
    await testInfo.attach("g14-tablet-home.png", { body: await page.screenshot({ fullPage: true }), contentType: "image/png" });

    // At 500px, MUI renders the narrow/mobile navigation, so these should be true/hidden/visible.
    // The first assertion currently exposes the mismatch: the helper uses a 414px cutoff.
    expect.soft(helperSaysMobile, diagnostics).toBe(true);
    await expect.soft(homeNav).toBeHidden();
    await expect.soft(navToggle).toBeVisible();
  });
});
