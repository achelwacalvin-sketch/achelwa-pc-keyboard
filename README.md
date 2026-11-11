# Achelwa PC Keyboard

A custom PC-style Android keyboard.

A lightweight Android Input Method Editor (IME), written in Kotlin with no third-party runtime libraries. The input service supports Android 6.0 and newer and targets Android 14 (API 34).

## Build an APK on GitHub

1. Create a GitHub repository and push this project to its default branch. Keep `.github/workflows/android.yml` in the repository.
2. Open **Actions** in the repository. If prompted, enable GitHub Actions for the repository.
3. A push, pull request, or manual **Android APK** run builds `app-debug.apk` using JDK 17 and Android Gradle Plugin. No signing secret is needed for this debug APK.
4. Open the successful workflow run, download the `achelwa-pc-keyboard-debug` artifact, unzip it, and install `app-debug.apk` on the Android phone. Approve installation from that file source if Android asks.

The APK is unsigned for Play Store release, but Android debug APKs can be installed directly. GitHub keeps workflow artifacts according to the repository's Actions artifact retention settings.

## Enable the keyboard

After installation, open **Settings → System → Keyboard → On-screen keyboard** (wording varies by phone), enable **Achelwa PC Keyboard**, then select it as the current/default keyboard. Android may show its standard warning that a keyboard can receive text entered into fields.

## Key behavior

The keyboard includes a PC-style QWERTY and symbol layout, function row, modifiers, editing/navigation keys, arrows, and CTRL+C/V/X/A buttons. The dedicated shortcuts use Android's copy, paste, cut, and select-all editor actions. Function and navigation keys are sent as Android key events; support depends on the app receiving text input. SHIFT and CAPS affect typed letters and symbol pairs. Some Android apps ignore PC-only keys such as Insert or function keys.
