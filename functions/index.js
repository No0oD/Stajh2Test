const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

admin.initializeApp();

// Configure the email service
// IMPORTANT: For production, use environment variables instead of hardcoded credentials
const transporter = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: functions.config().email?.user || 'your-app@gmail.com',
    pass: functions.config().email?.pass || 'your-app-password'
  }
});

/**
 * Cloud Function to send verification emails with a 4-digit code
 */
exports.sendVerificationEmail = functions.https.onCall(async (data, context) => {
  try {
    // Validate input
    const email = data.email;
    if (!email) {
      throw new functions.https.HttpsError('invalid-argument', 'Email is required');
    }
    
    // Check if the email exists in Firebase Auth
    try {
      const userRecord = await admin.auth().getUserByEmail(email);
      if (!userRecord) {
        throw new functions.https.HttpsError('not-found', 'User not found');
      }
    } catch (error) {
      if (error.code === 'auth/user-not-found') {
        throw new functions.https.HttpsError('not-found', 'User not found');
      }
      throw error;
    }
    
    // Generate a 4-digit verification code
    const verificationCode = Math.floor(1000 + Math.random() * 9000).toString();
    
    // Store the code in Firestore with expiration timestamp (10 minutes)
    const expirationTime = Date.now() + (10 * 60 * 1000); // 10 minutes
    
    await admin.firestore().collection('verificationCodes').doc(email).set({
      code: verificationCode,
      email: email,
      expirationTime: expirationTime,
      verified: false,
      createdAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    // Send the email
    const mailOptions = {
      from: `Your App <${functions.config().email?.user || 'your-app@gmail.com'}>`,
      to: email,
      subject: 'Password Reset Verification Code',
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
      `
    };
    
    await transporter.sendMail(mailOptions);
    
    // For security, we don't return the code
    return { success: true };
    
  } catch (error) {
    console.error('Error sending verification email:', error);
    throw new functions.https.HttpsError('internal', error.message);
  }
});

/**
 * Cloud Function to verify a 4-digit code
 */
exports.verifyCode = functions.https.onCall(async (data, context) => {
  try {
    // Validate input
    const email = data.email;
    const code = data.code;
    
    if (!email || !code) {
      throw new functions.https.HttpsError('invalid-argument', 'Email and code are required');
    }
    
    // Get the verification code from Firestore
    const docSnapshot = await admin.firestore().collection('verificationCodes').doc(email).get();
    
    if (!docSnapshot.exists) {
      throw new functions.https.HttpsError('not-found', 'Verification code not found');
    }
    
    const verificationData = docSnapshot.data();
    
    // Check if the code has expired
    if (Date.now() > verificationData.expirationTime) {
      throw new functions.https.HttpsError('deadline-exceeded', 'Verification code has expired');
    }
    
    // Check if the code matches
    if (verificationData.code !== code) {
      throw new functions.https.HttpsError('invalid-argument', 'Invalid verification code');
    }
    
    // Mark the code as verified
    await admin.firestore().collection('verificationCodes').doc(email).update({
      verified: true,
      verifiedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    return { success: true };
    
  } catch (error) {
    console.error('Error verifying code:', error);
    throw new functions.https.HttpsError('internal', error.message);
  }
});

/**
 * Cleanup job to remove expired verification codes (runs daily)
 */
exports.cleanupExpiredCodes = functions.pubsub.schedule('every 24 hours').onRun(async (context) => {
  const now = Date.now();
  
  // Query for expired codes
  const snapshot = await admin.firestore()
    .collection('verificationCodes')
    .where('expirationTime', '<', now)
    .get();
  
  // Batch delete expired codes
  const batch = admin.firestore().batch();
  snapshot.docs.forEach(doc => {
    batch.delete(doc.ref);
  });
  
  if (snapshot.docs.length > 0) {
    await batch.commit();
    console.log(`Deleted ${snapshot.docs.length} expired verification codes`);
  }
  
  return null;
});