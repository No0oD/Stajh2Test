// index.js - Firebase Functions file
if (process.env.NODE_ENV === "development") {
  require("dotenv").config();

  console.log("Development mode: Environment variables loaded");
}

const functions = require("firebase-functions");
const express = require("express");
const admin = require("firebase-admin");
const nodemailer = require("nodemailer");
// const cors = require("cors");

// Initialization of Express application
const app = express();

// Middleware
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Firebase Admin Initialization
// For local development, check for service account presence
try {
  // Attempting to initialize with service account for local environment
  const serviceAccount = require("./serviceAccountKey.json");
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
  console.log("Firebase Admin SDK initialized with service account");
} catch (error) {
  // If service account is not found, use standard initialization
  admin.initializeApp();
  console.log("Firebase Admin SDK initialized in standard way");
}

console.log(process.env.NODEMAILER_EMAIL);

// Email service configuration
const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user:
      functions.config().email?.user ||
      process.env.NODEMAILER_EMAIL ||
      "your-app@gmail.com",
    pass:
      functions.config().email?.pass ||
      process.env.NODEMAILER_PASSWORD ||
      "your-app-password",
  },
});

// Base route
app.get("/", (req, res) => {
  res.send("API is working");
});

/**
 * Route for sending verification code to email
 */
app.post("/send-verification-email", async (req, res) => {
  try {
    // Input validation
    const email = req.body.email;
    if (!email) {
      return res.status(400).json({ error: "Email is required" });
    }

    // Check if user exists in Firebase Auth
    try {
      const userRecord = await admin.auth().getUserByEmail(email);
      if (!userRecord) {
        return res.status(404).json({ error: "User not found" });
      }
    } catch (error) {
      if (error.code === "auth/user-not-found") {
        return res.status(404).json({ error: "User not found" });
      }
      throw error;
    }

    // Generate 4-digit verification code
    const verificationCode = Math.floor(1000 + Math.random() * 9000).toString();

    // Save code to Firestore with expiration time (10 minutes)
    const expirationTime = Date.now() + 10 * 60 * 1000; // 10 minutes

    await admin.firestore().collection("verificationCodes").doc(email).set({
      code: verificationCode,
      email: email,
      expirationTime: expirationTime,
      verified: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    const mailFrom =
      functions.config().email?.user || process.env.NODEMAILER_EMAIL;

    // Send email
    const mailOptions = {
      from: `Your App <${mailFrom}>`,
      to: email,
      subject: "Password Reset Verification Code",
      html: `
          <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e9e9e9; border-radius: 5px;">
            <h2 style="color: #333; text-align: center;">Your Verification Code</h2>
            <p style="color: #666; font-size: 16px; line-height: 1.5;">We received a request to reset your password. Please use the following code to verify your identity:</p>
            <div style="text-align: center; margin: 30px 0;">
              <div style="display: inline-block; padding: 15px 30px; background-color: #f5f5f5; border-radius: 5px; letter-spacing: 8px; font-size: 28px; font-weight: bold; color: #333;">${verificationCode}</div>
            </div>
            <p style="color: #666; font-size: 14px; line-height: 1.5;">This code will expire in 10 minutes. If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>
            <p style="color: #999; font-size: 12px; text-align: center; margin-top: 30px;">This is an automated message, please do not reply.</p>
          </div>
        `,
    };

    await transporter.sendMail(mailOptions);

    // For security reasons, don't return the code itself
    res.status(200).json({ success: true });
  } catch (error) {
    console.error("Error sending verification email:", error);
    res.status(500).json({ error: error.message });
  }
});

/**
 * Route for verifying 4-digit code
 */
app.post("/verify-code", async (req, res) => {
  try {
    // Input validation
    const email = req.body.email;
    const code = req.body.code;

    if (!email || !code) {
      return res.status(400).json({ error: "Email and code are required" });
    }

    // Get verification code from Firestore
    const docSnapshot = await admin
      .firestore()
      .collection("verificationCodes")
      .doc(email)
      .get();

    if (!docSnapshot.exists) {
      return res.status(404).json({ error: "Verification code not found" });
    }

    const verificationData = docSnapshot.data();

    // Check code expiration
    if (Date.now() > verificationData.expirationTime) {
      return res.status(410).json({ error: "Verification code has expired" });
    }

    // Check if code is correct
    if (verificationData.code !== code) {
      return res.status(400).json({ error: "Invalid verification code" });
    }

    // Mark code as verified
    await admin.firestore().collection("verificationCodes").doc(email).update({
      verified: true,
      verifiedAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    res.status(200).json({ success: true });
  } catch (error) {
    console.error("Error verifying code:", error);
    res.status(500).json({ error: error.message });
  }
});

// Function to clean up expired codes
const cleanupExpiredCodes = async () => {
  const now = Date.now();

  // Query for expired codes
  const snapshot = await admin
    .firestore()
    .collection("verificationCodes")
    .where("expirationTime", "<", now)
    .get();

  // Batch delete expired codes
  const batch = admin.firestore().batch();
  snapshot.docs.forEach((doc) => {
    batch.delete(doc.ref);
  });

  if (snapshot.docs.length > 0) {
    await batch.commit();
    console.log(`Deleted ${snapshot.docs.length} expired verification codes`);
  }
};

exports.api = functions.https.onRequest(app);

if (process.env.NODE_ENV !== "development") {
  /**
   * Separate function for cleaning up expired verification codes (runs daily)
   */
  // Export Express app as Firebase function
  exports.cleanupExpiredCodes = functions.pubsub
    .schedule("every 24 hours")
    .onRun(async (context) => {
      await cleanupExpiredCodes();
      return null;
    });
}

// For local running
exports.app = app;
