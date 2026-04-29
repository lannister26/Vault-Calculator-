# Run on your PC and upload to GitHub

## Run the app on your PC

1. Install Android Studio from `https://developer.android.com/studio`.
2. Open Android Studio.
3. Choose `Open`.
4. Select this folder:

   `C:\Users\harsh\OneDrive\Documents\New project`

5. Wait for Gradle sync to finish. If Android Studio asks to install SDK tools, accept it.
6. Create an emulator:
   - Open `Device Manager`.
   - Choose `Create device`.
   - Pick a phone such as Pixel 7 or Pixel 8.
   - Download a recent Android system image.
   - Start the emulator.
7. Press the green `Run` button in Android Studio.
8. On first launch, set a 4 to 8 digit PIN.
9. To open the hidden vault, type that PIN on the calculator and press `=`.

You can also run on a real Android phone:

1. Enable Developer Options on the phone.
2. Turn on USB debugging.
3. Connect the phone to the PC.
4. Accept the debugging prompt on the phone.
5. Select the phone in Android Studio and press `Run`.

## Upload this project to GitHub

Install Git first from `https://git-scm.com/download/win`.

Then open PowerShell in this project folder and run:

```powershell
git init
git add .
git commit -m "Initial Vault Calculator app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git push -u origin main
```

Replace `YOUR_USERNAME` and `YOUR_REPO_NAME` with your real GitHub username and repository name.

## Create the GitHub repo

1. Go to `https://github.com/new`.
2. Enter a repository name, for example `vault-calculator`.
3. Choose Public or Private.
4. Do not add a README, `.gitignore`, or license on GitHub because this project already has files.
5. Click `Create repository`.
6. Copy the HTTPS repo URL and use it in the `git remote add origin ...` command.

## Important

Do not upload signing keys, keystore files, passwords, or `local.properties` to GitHub. The `.gitignore` file already excludes the common sensitive Android files.
