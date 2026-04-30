# Vault Calculator

![Android](https://img.shields.io/badge/Android-native-3DDC84?logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-007396?logo=openjdk&logoColor=white)
![Encryption](https://img.shields.io/badge/Encryption-AES--GCM-blue)
![Storage](https://img.shields.io/badge/Storage-local--only-success)
![Status](https://img.shields.io/badge/status-MVP%20complete-brightgreen)

A native Android calculator that doubles as a private, local-only encrypted file vault.

Vault Calculator behaves like a clean everyday calculator. Behind a PIN unlock flow, it opens a user-controlled vault where selected files are encrypted into app-private storage using AES-GCM and an Android Keystore-backed key.

> Built as a practical Android privacy utility and portfolio project.

## Why It Stands Out

- Real calculator UI with basic arithmetic
- Hidden vault unlock by entering the PIN and pressing `=`
- First-run PIN setup and PIN change flow
- Multi-file import through Android's system file picker
- AES-GCM file encryption with per-file IVs
- Android Keystore-backed vault key
- App-private local storage
- Restore and delete vault files
- No server, account system, analytics, ads, or broad storage permission
- Modern AndroidX back handling with `OnBackPressedDispatcher`
- Modern activity result handling with `ActivityResultLauncher`

## Screenshots

Screenshots are intentionally kept out until they are captured from a real emulator or device build.

Recommended files:

```text
docs/screenshots/calculator.png
docs/screenshots/pin-setup.png
docs/screenshots/vault-empty.png
docs/screenshots/vault-files.png
```

After adding screenshots, place this in the section:

```md
![Calculator screen](docs/screenshots/calculator.png)
![Encrypted file vault](docs/screenshots/vault-files.png)
```

## Tech Stack

- Android SDK 36
- Java 17
- Gradle Wrapper 9.4.1
- Android Gradle Plugin 9.2.0
- AndroidX Activity
- AndroidX Core
- Android Keystore
- AES/GCM/NoPadding

## Project Structure

```text
.
|-- app/
|   `-- src/main/
|       |-- java/com/calcvault/privatefiles/MainActivity.java
|       `-- res/
|-- docs/
|   |-- GITHUB_SHOWCASE_GUIDE.md
|   |-- PLAY_STORE_AND_MONETIZATION.md
|   |-- PRIVACY_POLICY_TEMPLATE.md
|   `-- RUN_ON_PC_AND_GITHUB.md
|-- gradle/wrapper/
|-- build.gradle.kts
|-- settings.gradle.kts
`-- README.md
```

## How The Vault Works

1. The user chooses files through Android's system file picker.
2. The app streams each selected file through AES-GCM encryption.
3. Encrypted files are stored in the app-private directory.
4. Metadata is stored locally so the vault can list, restore, or delete files.
5. The vault key is generated and protected by Android Keystore.
6. The vault PIN is stored as a salted hash, not as plain text.

Vault contents stay on the device. Nothing is uploaded to a server.

## Run Locally

1. Install Android Studio.
2. Open this project folder.
3. Let Android Studio sync Gradle and install any missing SDK tools.
4. Start an emulator or connect an Android phone with USB debugging enabled.
5. Run the `app` configuration.

To open the vault:

1. Set a 4 to 8 digit PIN on first launch.
2. Type that PIN into the calculator.
3. Press `=`.

## Build From Command Line

On Windows PowerShell:

```powershell
.\gradlew.bat :app:assembleDebug
```

The project writes generated Gradle build output outside OneDrive to avoid Windows file-lock issues:

```text
C:\Users\harsh\AndroidBuilds\CalculatorVault
```

To override the build output location:

```powershell
.\gradlew.bat :app:assembleDebug -PexternalBuildRoot=C:\AndroidBuilds\CalculatorVault
```

## Privacy Position

This version is intentionally local-first:

- No cloud sync
- No account login
- No analytics
- No ads
- No data selling
- No broad all-files storage permission

If monetization, crash reporting, analytics, or cloud backup is added later, the privacy policy and Play Console Data safety declarations must be updated before release.

## Suggested GitHub Topics

```text
android
java
android-app
privacy
encryption
calculator
file-vault
android-keystore
aes-gcm
portfolio-project
```

## Roadmap Ideas

- Add polished screenshots and a short demo GIF
- Add biometric unlock as an optional convenience layer
- Add export-all flow for trusted backup locations
- Add instrumented tests for vault import, restore, and delete behavior
- Add a release workflow for debug and signed builds

## Documentation

- [Run on PC and GitHub guide](docs/RUN_ON_PC_AND_GITHUB.md)
- [Play Store and monetization notes](docs/PLAY_STORE_AND_MONETIZATION.md)
- [Privacy policy template](docs/PRIVACY_POLICY_TEMPLATE.md)
- [GitHub showcase checklist](docs/GITHUB_SHOWCASE_GUIDE.md)

## Status

MVP complete and ready for GitHub showcase. Before a production release, capture real screenshots, review Play Store policy requirements, and test on multiple Android versions.

## Disclaimer

This project is intended for legitimate personal privacy use. Users are responsible for complying with all applicable laws and platform policies.
