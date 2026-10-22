import { test, expect } from "@fixtures/api";
import { social } from "@api";

test.describe("KI-01: deleting an unknown contact must preserve all contacts", () => {
  test("DELETE /contacts/:contactId returns 404 and changes no contacts", async ({ api, db }) => {
    // This is a known defect. Keep the test marked as an expected failure until KI-01 is fixed.
    test.fail(true, "KI-01: an unknown contact id currently deletes every contact");

    const contactsBefore = await db.all("contacts");
    const response = await social.deleteContact(api, "abc123XYZ");
    const contactsAfter = await db.all("contacts");

    // Soft assertions report both the wrong HTTP status and the destructive data change.
    expect.soft(response.status).toBe(404);
    expect.soft(contactsAfter).toEqual(contactsBefore);
  });
});
