# Play Store and monetization steps

Checked against Google documentation on April 25, 2026.

## 1. Prepare the app

1. Test on real Android devices, not only an emulator.
2. Confirm the app name and package name. The current package is `com.calcvault.privatefiles`; package names are permanent on Google Play.
3. Build a signed Android App Bundle (`.aab`) from Android Studio.
4. Create screenshots for phone, 7-inch tablet, and 10-inch tablet if you want broader listing coverage.
5. Write a clear store description that says the app is both a calculator and a local encrypted file vault. Do not market it as invisible, untraceable, or for hiding illegal content.

## 2. Create a Play Console account

1. Go to the Google Play Console and sign up with a Google account.
2. You must be at least 18.
3. Accept the Developer Distribution Agreement.
4. Pay Google's one-time developer registration fee. Google's Help Center currently lists it as `US$25`.
5. Complete identity and developer profile requirements.

Source: [Get started with Play Console](https://support.google.com/googleplay/android-developer/answer/9859062?hl=en)

## 3. Create the app in Play Console

1. In Play Console, choose `Home > Create app`.
2. Select default language.
3. Enter the app name.
4. Choose app, not game.
5. Choose free or paid. You can change some monetization later, but choose carefully.
6. Add a support email.
7. Accept the policy declarations and Play App Signing terms.

Source: [Create and set up your app](https://support.google.com/googleplay/android-developer/answer/9859152?hl=en)

## 4. Complete app setup

Complete the dashboard tasks:

- Main store listing: name, short description, full description, screenshots, app icon, feature graphic.
- App category and contact details.
- Privacy policy URL.
- Data safety section.
- Content rating questionnaire.
- Target audience and content.
- Ads declaration, if ads are added.
- App access instructions if review needs a test PIN.

For this app, provide reviewers with a test PIN such as `1234` in the App access section.

Sources:

- [Set up your app on the app dashboard](https://support.google.com/googleplay/android-developer/answer/9859454?hl=en)
- [User Data policy](https://support.google.com/googleplay/android-developer/answer/10144311?hl=en)

## 5. Testing requirement

Google says personal developer accounts created after November 13, 2023 must meet testing requirements before production release. Expect to create a closed test, invite testers, collect feedback, and then request production access.

Source: [Create and set up your app](https://support.google.com/googleplay/android-developer/answer/9859152?hl=en)

## 6. Upload and release

1. Go to `Test and release`.
2. Start with internal testing or closed testing.
3. Upload the signed `.aab`.
4. Add release notes.
5. Roll out to testers.
6. Fix crashes, policy issues, and user feedback.
7. When eligible, create a production release.

Source: [Prepare and roll out a release](https://support.google.com/googleplay/android-developer/answer/9859348?hl=en-EN)

## 7. Target API requirement

The project targets SDK 36. Google Play currently requires new apps and updates to target Android 15 / API 35 or higher from August 31, 2025. Targeting SDK 36 keeps this project ahead of that listed requirement.

Source: [Target API level requirements](https://support.google.com/googleplay/android-developer/answer/11926878?hl=en-PH)

## 8. Ways to earn money

### Option A: Paid app

Charge upfront for downloads. To sell paid apps, you need a Play payments profile. This is simple, but paid apps usually get fewer installs than free apps.

Source: [Link a Play developer account to a payments profile](https://support.google.com/googleplay/android-developer/answer/3092739?hl=en)

### Option B: Free app with ads

Use AdMob banner, interstitial, or rewarded ads. For a privacy vault, keep ads out of the vault screen and avoid interrupting sensitive file actions. AdMob says ads work by matching ad spaces in your app with advertiser demand, and earnings vary.

Sources:

- [How AdMob works](https://support.google.com/admob/answer/7356092?hl=en)
- [Set up an app in AdMob](https://support.google.com/admob/answer/9989980?hl=en)

### Option C: Freemium with in-app purchase

Offer a free tier, then charge for premium features such as more themes, batch restore, decoy vault, or larger vault limits. Google Play Billing is required for digital in-app purchases in Play-distributed apps unless a policy exception applies.

Sources:

- [Create an in-app product](https://support.google.com/googleplay/android-developer/answer/1153481?hl=en)
- [Understanding Google Play Payments policy](https://support.google.com/googleplay/android-developer/answer/10281818?hl=en)

## 9. Recommended money plan for this app

Start free, no ads in version 1.0. Get approval and reviews first. Then add:

1. A small banner ad only on the calculator screen.
2. A one-time premium upgrade to remove ads and unlock convenience features.
3. No ads inside the vault, import flow, restore flow, or PIN screen.

This is more likely to feel trustworthy for a privacy app. There is no guaranteed income; real money depends on installs, retention, reviews, country mix, ad rates, and conversion rate.
