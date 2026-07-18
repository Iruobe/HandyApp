import {setGlobalOptions} from "firebase-functions";
import {onRequest} from "firebase-functions/https";
import {onDocumentCreated} from "firebase-functions/v2/firestore";
import * as logger from "firebase-functions/logger";
import * as admin from "firebase-admin";

setGlobalOptions({maxInstances: 10, region: "us-central1"});

admin.initializeApp();

export const helloWorld = onRequest((request, response) => {
  logger.info("Hello from Handy!", {structuredData: true});
  response.send("Hello from Handy — the deploy pipeline works!");
});

export const onNewMessageNotify = onDocumentCreated(
  {
    document: "conversations/{conversationId}/messages/{messageId}",
    region: "europe-west1",
  },
  async (event) => {
    const {conversationId} = event.params;

    const snapshot = event.data;
    if (!snapshot) {
      logger.warn("onNewMessageNotify: no snapshot data", {conversationId});
      return;
    }

    const message = snapshot.data();
    const senderId: string | undefined = message.senderId;
    const body: string | undefined = message.body;

    if (!senderId) {
      logger.warn("onNewMessageNotify: message missing senderId", {conversationId});
      return;
    }

    const db = admin.firestore();

    const conversationSnap = await db
      .collection("conversations")
      .doc(conversationId)
      .get();

    if (!conversationSnap.exists) {
      logger.warn("onNewMessageNotify: conversation not found", {conversationId});
      return;
    }

    const conversation = conversationSnap.data() ?? {};
    const participantIds: string[] = conversation.participantIds ?? [];
    const participantNames: Record<string, string> = conversation.participantNames ?? {};

    const recipientId = participantIds.find((id) => id !== senderId);
    if (!recipientId) {
      logger.warn("onNewMessageNotify: no recipient found", {conversationId, senderId});
      return;
    }

    const recipientSnap = await db.collection("users").doc(recipientId).get();
    if (!recipientSnap.exists) {
      logger.warn("onNewMessageNotify: recipient user not found", {recipientId});
      return;
    }

    const fcmToken: string | undefined = recipientSnap.data()?.fcmToken;
    if (!fcmToken) {
      logger.info("onNewMessageNotify: recipient has no fcmToken, skipping", {recipientId});
      return;
    }

    const senderName = participantNames[senderId] || "Handy";
    const notificationBody = body || "New message";

    try {
      await admin.messaging().send({
        token: fcmToken,
        notification: {
          title: senderName,
          body: notificationBody,
        },
        data: {
          conversationId,
        },
      });
      logger.info("onNewMessageNotify: push sent", {conversationId, recipientId});
    } catch (error) {
      logger.error("onNewMessageNotify: failed to send push", {conversationId, recipientId, error});
    }
  }
);
