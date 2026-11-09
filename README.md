# VocaPro
Kostenlose Android-Vokabeltrainer-App mit Material 3, Dark Mode, Karteikarten, Quiz, Aussprache per Android Text-to-Speech, Foto-Texterkennung (ML Kit) und 13 englischen Zeitformen.

## APK bauen
Das GitHub-Action-Workflow startet automatisch bei Push auf `main` oder `master`. Nach erfolgreichem Lauf kann die Debug-APK unter Actions → Build VocaPro APK → Artifacts heruntergeladen werden.

## Lokal bauen
Voraussetzung: JDK 17, Android SDK und Gradle 8.9. Dann im Projektordner `gradle assembleDebug` ausführen.
