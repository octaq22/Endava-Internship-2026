import { test, expect } from "@fixtures/api";
import { transactions } from "@api";

test.describe("G-01: reject must not move money", () => {
  test("rejecting a pending request leaves both balances unchanged (KI-05)", async ({ api, db, me }) => {
    test.fail(true, "KI-05: rejecting a request currently transfers its amount");

    const pendingRequest = await db.find("transactions", {
      receiverId: me.id,
      requestStatus: "pending",
    });
    const senderBefore = await db.find("users", { id: pendingRequest.senderId });
    const receiverBefore = await db.find("users", { id: pendingRequest.receiverId });

    const response = await transactions.updateTransaction(api, pendingRequest.id, {
      requestStatus: "rejected",
    });
    expect(response.status).toBe(204);

    const [senderAfter, receiverAfter, rejectedRequest] = await Promise.all([
      db.find("users", { id: pendingRequest.senderId }),
      db.find("users", { id: pendingRequest.receiverId }),
      db.find("transactions", { id: pendingRequest.id }),
    ]);

    expect(rejectedRequest.requestStatus).toBe("rejected");
    expect(senderAfter.balance).toBe(senderBefore.balance);
    expect(receiverAfter.balance).toBe(receiverBefore.balance);
  });
});
