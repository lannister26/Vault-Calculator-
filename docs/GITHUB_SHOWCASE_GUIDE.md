# GitHub Showcase Guide

Use this checklist to publish the project cleanly and make it look polished on your GitHub profile.

## 1. Create the repository

1. Go to `https://github.com/new`.
2. Repository name idea: `vault-calculator`.
3. Description idea:

   `A native Android calculator with a private encrypted file vault.`

4. Choose `Public` if you want it visible on your profile.
5. Do not add a README, `.gitignore`, or license on GitHub. This project already has local files.
6. Click `Create repository`.

## 2. Push the local project

Install Git for Windows first: `https://git-scm.com/download/win`

Open PowerShell in:

```text
C:\Users\harsh\OneDrive\Documents\New project
```

Run:

```powershell
git init
git add .
git commit -m "Initial Vault Calculator app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/vault-calculator.git
git push -u origin main
```

Replace `YOUR_USERNAME` with your GitHub username.

## 3. Add repository topics

On GitHub, open the repository page and add topics:

```text
android
java
android-app
privacy
encryption
calculator
file-vault
android-keystore
portfolio-project
```

## 4. Add screenshots

1. Run the app in Android Studio.
2. Open the emulator.
3. Use the emulator screenshot button.
4. Save images into:

   `docs/screenshots/`

5. Recommended filenames:

```text
calculator.png
pin-setup.png
vault-empty.png
vault-files.png
```

6. Update the `Screenshots` section in `README.md`.

## 5. Pin it to your profile

1. Go to your GitHub profile.
2. Click `Customize your pins`.
3. Select `vault-calculator`.
4. Save.

## 6. Profile wording

Use this short project description on your resume, LinkedIn, or GitHub profile:

```text
Built a native Android calculator app with a hidden encrypted file vault using Java, Android Keystore, AES-GCM encryption, and Android's system file picker.
```

Use this longer description:

```text
Vault Calculator is a native Android privacy utility that combines a clean calculator UI with a PIN-protected encrypted file vault. It uses AES-GCM encryption, Android Keystore-backed keys, local app-private storage, AndroidX back handling, and modern activity result APIs.
```
