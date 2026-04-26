# Vault Calculator

A native Android calculator with a private, local-only encrypted file vault.

Vault Calculator looks and behaves like a clean everyday calculator. Behind a PIN unlock flow, it provides a user-controlled vault where selected files are encrypted into app-private storage using AES-GCM with a key stored in the Android Keystore.

> Built as a practical Android privacy utility and portfolio project.

## Highlights

- Native Android app written in Java
- Calculator interface with basic arithmetic
- Hidden vault unlock by entering the PIN and pressing `=`
- First-run PIN setup and PIN change flow
- Multi-file import through Android's system file picker
- AES-GCM file encryption
- Android Keystore-backed encryption key
- App-private local storage
- Restore and delete vault files
- No server, account system, analytics, ads, or broad storage permission
- Modern Android back handling with `OnBackPressedDispatcher`
- Modern activity result handling with `ActivityResultLauncher`

## Screenshots

Add screenshots to `docs/screenshots/` after running the app on an emulator or phone.

Suggested images:

- `calculator.png`
- `pin-setup.png`
- `vault-empty.png`
- `vault-files.png`

Then update this section:

```md
![Calculator](docs/screenshots/calculator.png)
![Private vault](docs/screenshots/vault-files.png)
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
├── app/
│   └── src/main/
│       ├── java/com/calcvault/privatefiles/MainActivity.java
│       └── res/
├── docs/
│   ├── PLAY_STORE_AND_MONETIZATION.md
│   ├── PRIVACY_POLICY_TEMPLATE.md
│   └── RUN_ON_PC_AND_GITHUB.md
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## How It Works

The app uses Android's system file picker, so the user explicitly chooses the files they want to import. Imported files are encrypted and written into app-private storage. The app then attempts to delete the original selected file when Android grants that permission.

The vault PIN is stored as a salted hash. The encrypted file key is generated and protected by Android Keystore. Vault contents stay on the device and are not uploaded anywhere.

## Run Locally

1. Install Android Studio.
2. Open this project folder.
3. Let Android Studio sync Gradle and install any missing SDK tools.
4. Start an emulator or connect a real Android phone with USB debugging enabled.
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

## Privacy Position

This version is intentionally local-first:

- No cloud sync
- No account login
- No analytics
- No ads
- No data selling
- No broad all-files storage permission

If monetization, crash reporting, analytics, or cloud backup is added later, the privacy policy and Play Console Data safety declarations must be updated before release.

## Publishing Notes

Play Store and monetization guidance is included in:

[docs/PLAY_STORE_AND_MONETIZATION.md](docs/PLAY_STORE_AND_MONETIZATION.md)

## Status

MVP complete and tested on Android emulator/real-device workflow.

## Disclaimer

This project is intended for legitimate personal privacy use. Users are responsible for complying with all applicable laws and platform policies.
