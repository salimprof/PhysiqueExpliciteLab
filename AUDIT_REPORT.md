# Rapport d'Audit Complet — Préparation à la Publication Google Play
## Projet : **Physique Explicite Lab (« Edited by Salimprof »)**
- **Dépôt GitHub audité** : https://github.com/salimprof/PhysiqueExpliciteLab
- **Branche de travail dédiée (Point 10)** : `audit/google-play-compliance` *(branche `main` conservée intacte en attente de validation)*
- **Identifiant Android (`applicationId`)** : `com.salimprof.physiqueexplicitelab`
- **Version alignée** : `versionName "1.0.0-pe (base phyphox 1.2.1)"` • `versionCode 1020105`
- **SDK alignés** : `minSdkVersion 21` • `targetSdkVersion 35` • `compileSdk 37`
- **Date de l'audit** : `2026-11-03`

---

## Catégorie 1 — Conforme et vérifié (`Conforme et vérifié`)

### 1.1 Moteur scientifique `phyphox` intact (Point 9)
- Aucune modification n'a été apportée aux modules d'acquisition matérielle (`SensorInput.java`, `AudioInput.java`, `GpsInput.java`, `CameraInput.java`, `DepthInput.java`, `Bluetooth/`), au moteur d'analyse C++/JNI (`Analysis.java`, `mathModules/`, `FFTW`), au rendu graphique (`GraphView.java`, `PlotRenderer.java`) ni au serveur d'accès à distance (`RemoteServer.java`).
- Les 3 seuls fichiers Java amont adaptés (`Experiment.java`, `AssetExperimentLoader.java`, `ExperimentShortInfo.java`) ne comportent que l'autorité dynamique `${applicationId}.exportProvider` et des accesseurs de lecture (`public`) pour l'interface `PEMainActivity`.

### 1.2 Sous-modules Git actifs et récupérables (Point 2)
- `app/src/main/assets/experiments` (`https://github.com/Staacks/phyphox-experiments`, commit `af5c1a52a888426f0acff0284bb6a6564e113afb`) : référencé dans l'index Git (`160000`), peuplé et fonctionnel.
- `app/src/main/assets/remote` (`https://github.com/Staacks/phyphox-webinterface`, commit `67c3ef3253cf3088e5f4079f82d3be2c90cbf47c`) : référencé dans l'index Git (`160000`), peuplé (`index.html` et scripts présents) et fonctionnel.

### 1.3 Conformité GNU GPL v3 (Point 5)
- **Avis de modification (Section 5a)** : en-têtes explicites identifiant **Salimprof (Salim El Mokhtar)** et la date **`2026-11-02`** présents dans tous les fichiers Java modifiés ou ajoutés, ainsi que dans [`NOTICE_MODIFICATIONS_GPLv3.md`](NOTICE_MODIFICATIONS_GPLv3.md).
- **Conservation des copyrights originaux et licences tierces (Section 4 & 5)** : crédits complets de la **RWTH Aachen University** (`Dr. Sebastian Staacks` et contributeurs) et notices des bibliothèques tierces (`FFTW`, `jlhttp`, `ZXing`, `Chart.js`, `AndroidSVG`, `Apache Commons IO`, `Kotlin Coroutines`, `AndroidX / CameraX / Material`) intégralement accessibles dans [`PEDialogs.showLicensesDialog`](app/src/main/java/de/rwth_aachen/phyphox/pelab/PEDialogs.java).
- **Texte intégral GNU GPL v3 hors ligne (Section 6)** : fichier [`app/src/main/assets/gpl-3.0.txt`](app/src/main/assets/gpl-3.0.txt) (`35 821 octets`) accessible sans connexion Internet depuis l'application.
- **Code source correspondant (Section 6)** : section dédiée **« Source code of this modified version »** configurée sur `https://github.com/salimprof/PhysiqueExpliciteLab` via `PE_MODIFIED_SOURCE_URL` dans [`gradle.properties`](gradle.properties), à côté du lien original `https://github.com/phyphox/phyphox-android`.

### 1.4 Compilation, tests unitaires et validations Android (Point 7)
- Exécution validée : `.\gradlew.bat testRegularDebugUnitTest lintVitalRegularRelease assembleRegularDebug bundleRegularRelease` → **`BUILD SUCCESSFUL`**.
- Tests unitaires automatisés ([`PELabVerificationTest.java`](app/src/test/java/de/rwth_aachen/phyphox/pelab/PELabVerificationTest.java) + suite de tests unitaires du moteur) : **100 % réussis**.
- Signature cryptographique du bundle Release (`PhysiqueExpliciteLab-GooglePlay-SIGNED.aab`) vérifiée avec `jarsigner -verify` (`jar verified`).

---

## Catégorie 2 — Écarts détectés et corrigés sur la branche `audit/google-play-compliance` (`À corriger`)

| # | Point d'audit | Écart détecté sur `main` | Correction appliquée sur `audit/google-play-compliance` |
|---|---|---|---|
| **2.1** | **Point 1 : `README.md` obsolète** | Le `README.md` était encore celui d'origine de `phyphox: Android` (liens Google Play/F-Droid vers `de.rwth_aachen.phyphox`, mention obsolète de Paho MQTT, aucune présentation de *Physique Explicite Lab* ni du module *J'expérimente*). | Remplacé par un **[`README.md`](README.md) bilingue Français / Anglais** complet avec bannière, présentation de *Physique Explicite Lab (Edited by Salimprof)*, fonctionnalités, 4 captures d'écran dans [`docs/screenshots/`](docs/screenshots/), instructions de compilation reproductible, liens vers `phyphox` et crédits complets. |
| **2.2** | **Point 2 : Sous-module fantôme dans `.gitmodules`** | [`.gitmodules`](.gitmodules) contenait encore une 3e entrée `[submodule "paho.mqtt.android"]` supprimée de l'arbre Git en amont (remplacée par `MqttClient.java`), ce qui provoquait une incohérence lors d'un `git submodule status`. | Suppression de l'entrée orpheline `paho.mqtt.android` dans [`.gitmodules`](.gitmodules). Seuls les 2 sous-modules réels (`experiments` et `remote`) sont désormais déclarés. |
| **2.3** | **Point 3 : Écarts de versions SDK et `versionCode`** | `app/build.gradle` déclarait `targetSdkVersion 37` et `versionCode 1020105`, tandis que `NOTICE_MODIFICATIONS_GPLv3.md` mentionnait `targetSdk 35` et `versionCode 1020102`. De plus, `.gitignore` n'ignorait pas `*.apk`, `*.aab` et `*.jks`. | Alignement uniforme sur **`compileSdk 37`**, **`targetSdkVersion 35`** (cible stable Android 15 pour Google Play) et **`versionCode 1020105`** dans [`app/build.gradle`](app/build.gradle), [`README.md`](README.md), [`COMPILE_AND_SIGNING_GUIDE.md`](COMPILE_AND_SIGNING_GUIDE.md), [`NOTICE_MODIFICATIONS_GPLv3.md`](NOTICE_MODIFICATIONS_GPLv3.md) et [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md). Ajout de `*.apk`, `*.aab`, `*.jks`, `*.keystore` dans [`.gitignore`](.gitignore). |
| **2.4** | **Point 4 : Omissions dans `PRIVACY_POLICY.md`** | `PRIVACY_POLICY.md` ne mentionnait pas explicitement les expériences réseau personnalisées HTTP/MQTT (`NetworkConnection.java`, `Http.java`, `MqttClient.java`), ni `HIGH_SAMPLING_RATE_SENSORS`, `WAKE_LOCK`, `READ/WRITE_EXTERNAL_STORAGE (maxSdkVersion=28)` et l'attribut `usesCleartextTraffic="true"`. | Mise à jour complète de [`PRIVACY_POLICY.md`](PRIVACY_POLICY.md) (FR + EN) avec le tableau exhaustif des 16 permissions de [`AndroidManifest.xml`](app/src/main/AndroidManifest.xml) et des 4 cas d'utilisation réseau (Accès distant HTTP `jlhttp`, expériences réseau HTTP/MQTT avec boîte de consentement, import URL/QR, liens externes). |
| **2.5** | **Point 6 : Références à la marque `phyphox` dans `fastlane/` et `Screengrabfile`** | `fastlane/Appfile` et `Screengrabfile` utilisaient encore `de.rwth_aachen.phyphox`, et `fastlane/metadata/android/*` contenait les textes officiels `phyphox` et les icônes orange déposées `phyphox`. | Mise à jour de [`fastlane/Appfile`](fastlane/Appfile) et [`Screengrabfile`](Screengrabfile) vers `com.salimprof.physiqueexplicitelab`, suppression des métadonnées et icônes `phyphox` dans `fastlane/metadata/android/` et remplacement par les métadonnées `fr` et `en` de **Physique Explicite Lab** avec l'icône atomique personnalisée. |

---

## Catégorie 3 — À tester et valider manuellement avant publication finale (`À tester manuellement`)

1. **Fusion et poussée de la branche `audit/google-play-compliance` vers `main` sur GitHub** :
   - Après examen des modifications présentées sur `audit/google-play-compliance`, fusionner dans `main` et pousser vers `https://github.com/salimprof/PhysiqueExpliciteLab` afin que le `README.md` bilingue, `PRIVACY_POLICY.md` mis à jour et `.gitmodules` corrigé soient visibles publiquement sur la branche `main`.
2. **Test fonctionnel sur un appareil Android physique (ou émulateur)** :
   - Installer [`PhysiqueExpliciteLab-Salimprof.apk`](file:///c:/Users/SalimProf/Desktop/projetapps/PhysiqueExpliciteLab-Salimprof.apk).
   - Vérifier les 5 onglets (*Accueil*, *Expériences*, *J'expérimente*, *Données*, *Paramètres*), le basculement de langue (**FR / AR / EN**) et de thème (**Clair / Sombre**).
   - Ouvrir les 3 activités du module **J'expérimente**, lancer la mesure réelle à l'étape 4 (*Accéléromètre*, *Magnétomètre*, *Microphone*), et valider le **Quiz interactif** à l'étape 9.
   - Toucher le badge **« Edited by Salimprof »** → **« Crédits et licences »** et vérifier :
     - Le bouton **« Ouvrir le dépôt de cette version modifiée »** (`https://github.com/salimprof/PhysiqueExpliciteLab`),
     - Le bouton **« Ouvrir le dépôt original phyphox-android »** (`https://github.com/phyphox/phyphox-android`),
     - Le bouton **« Texte intégral de la licence GNU GPL v3 (hors ligne) »**.
3. **Vérification finale dans Google Play Console** :
   - Vérifier que l'URL de la politique de confidentialité (`https://github.com/salimprof/PhysiqueExpliciteLab/blob/main/PRIVACY_POLICY.md`) est bien enregistrée dans *App content → Privacy policy*.
   - Importer le fichier signé mis à jour [`PhysiqueExpliciteLab-GooglePlay-SIGNED.aab`](file:///c:/Users/SalimProf/Desktop/projetapps/GooglePlay_Publication/PhysiqueExpliciteLab-GooglePlay-SIGNED.aab) (`versionCode 1020105`, `targetSdkVersion 35`).
