# Politique de Confidentialité / Privacy Policy
## Physique Explicite Lab (« Edited by Salimprof »)
- **Nom de l'application** : **Physique Explicite Lab** (signature : **Edited by Salimprof**)
- **Identifiant Android (`applicationId`)** : `com.salimprof.physiqueexplicitelab`
- **Version auditée** : `1.0.0-pe (base phyphox 1.2.1)` (`versionCode` : `1020106`, `minSdkVersion 24`, `targetSdkVersion 36`)
- **Éditeur / Responsable de la personnalisation** : Salim El Mokhtar (**Salimprof**) — Contact : `salimprof@gmail.com`
- **Dépôt public du code source (GNU GPL v3)** : https://github.com/salimprof/PhysiqueExpliciteLab
- **Dernière mise à jour** : 3 novembre 2026 (`2026-11-03`)

---

# 🇫🇷 POLITIQUE DE CONFIDENTIALITÉ (VERSION FRANÇAISE COMPLÈTE)

## 1. Principe général et absence de collecte par l'éditeur
**Physique Explicite Lab (« Edited by Salimprof »)** est une application scientifique et pédagogique dépourvue de serveur central de collecte :
- **Aucune donnée personnelle ou mesure capteur n'est collectée, transmise, stockée, vendue ou partagée par l'éditeur (Salimprof).**
- L'application **ne comporte aucun compte utilisateur**, **aucune publicité** et **aucun SDK tiers d'analyse statistique, de suivi publicitaire, de télémétrie ou de rapport de plantage** (vérifié dans `app/build.gradle` et dans le manifeste fusionné : absence totale de Firebase, Google Analytics, Crashlytics, Sentry ou SDK sociaux).
- Toutes les expériences scientifiques intégrées (`assets/experiments`) ainsi que le module pédagogique **« J'expérimente »** et ses **Quiz interactifs** fonctionnent **100 % hors connexion** : les signaux des capteurs sont acquis et traités exclusivement en mémoire vive locale sur votre smartphone.

---

## 2. Permissions Android (`AndroidManifest.xml`) et utilisation réelle vérifiée dans le code

L'application déclare 16 permissions dans `app/src/main/AndroidManifest.xml` (plus la permission interne de protection AndroidX `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` de niveau `signature`). Les permissions sensibles d'exécution (*runtime permissions*) ne sont sollicitées qu'au moment où vous lancez une expérience qui requiert le capteur correspondant :

| Permission Android | Utilisation réelle vérifiée dans le code source |
|---|---|
| `HIGH_SAMPLING_RATE_SENSORS` | Permet l'échantillonnage à haute fréquence (> 200 Hz sur Android 12+) des capteurs inertiels et magnétiques (`SensorInput.java`). |
| `RECORD_AUDIO` | Utilisée uniquement au premier plan pendant l'exécution d'une expérience acoustique (oscilloscope sonore, spectre FFT, autocorrélation/fréquencemètre, sonar, effet Doppler) via `AudioInput.java`. L'audio est traité dans un tampon en mémoire vive et s'arrête immédiatement dès la mise en pause ou la fermeture de l'expérience (`Experiment.onStop()` → `shutdownIO()`). |
| `CAMERA` | Utilisée uniquement au premier plan dans deux cas déclenchés par l'utilisateur : (1) le scan d'un code QR pour importer une expérience (`com.journeyapps.barcodescanner.CaptureActivity`), ou (2) l'exécution d'une expérience optique/colorimétrique (`CameraInput.java` via CameraX) ou de capteur de profondeur (`DepthInput.java` via Camera2). Aucune photo ou vidéo n'est enregistrée automatiquement sur le stockage permanent. |
| `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION` | Utilisées uniquement au premier plan lorsque vous lancez l'expérience *Localisation (GPS)* (`GpsInput.java`), ou sur Android 6 à 11 (API 23–30) lorsque le système Android impose cette permission pour rechercher des capteurs Bluetooth Low Energy (`BluetoothScanDialog.java`). L'application ne demande jamais la permission `ACCESS_BACKGROUND_LOCATION`. |
| `BLUETOOTH_SCAN` (`neverForLocation`) & `BLUETOOTH_CONNECT` *(et `BLUETOOTH` / `BLUETOOTH_ADMIN` avec `maxSdkVersion="30"`)* | Utilisées uniquement lorsque vous recherchez ou connectez volontairement un capteur externe Bluetooth Low Energy (BLE) (`Bluetooth.kt`). Sur Android 12+ (API 31+), l'attribut `android:usesPermissionFlags="neverForLocation"` garantit au niveau du système d'exploitation que le scan Bluetooth n'est jamais utilisé pour déduire votre localisation. |
| `READ_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) & `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) | Utilisées uniquement sur Android 5 à 9 (API 21–28) pour importer ou exporter des fichiers d'expérience. Sur Android 10+ (API 29+), l'application utilise le stockage isolé (*Scoped Storage*) et le `FileProvider` interne (`com.salimprof.physiqueexplicitelab.exportProvider`). |
| `WAKE_LOCK` | Utilisée dans `Experiment.java` (lignes 885–890 et 2272–2282) **uniquement si vous activez l'option « Verrouillage de proximité » (`proximityLock`)** dans les paramètres, afin d'acquérir un verrou `PROXIMITY_SCREEN_OFF_WAKE_LOCK` (`"phyphox:measuring"`) qui éteint l'écran lorsque le capteur de proximité est couvert pendant une mesure. *(Le maintien normal de l'écran allumé pendant une mesure utilise l'attribut de fenêtre `FLAG_KEEP_SCREEN_ON` sans `WakeLock` CPU en arrière-plan).* |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `ACCESS_LOCAL_NETWORK` | Utilisées **exclusivement** lors d'actions réseau déclenchées par l'utilisateur (voir Section 3). |

---

## 3. Appels réseau, Accès à distance par Wi-Fi et expériences réseau personnalisées

**Aucun appel réseau automatique n'est effectué au lancement de l'application ni pendant les expériences locales.** Une communication réseau n'a lieu que dans les **4 cas suivants, toujours déclenchés par l'utilisateur** :

### 3.1 Accès à distance par Wi-Fi (`RemoteServer.java` / `jlhttp`) — Fonctionnement et risques
- **Fonctionnement** : lorsque vous cochez manuellement « Autoriser l'accès à distance » dans le menu d'une expérience et validez l'avertissement de sécurité, l'application démarre un serveur web HTTP local (`net.freeutils.httpserver.HTTPServer`) sur l'interface réseau locale de votre téléphone (port par défaut `8080`, modifiable dans les paramètres).
- Ce serveur expose localement les points de terminaison `/`, `/style.css`, `/config`, `/get` (valeurs des capteurs en temps réel), `/control` (démarrer, arrêter, effacer ou modifier une valeur d'entrée), `/export` (téléchargement CSV/TSV/Excel), `/meta` (modèle de l'appareil et caractéristiques techniques des capteurs matériels, **en excluant** tout identifiant unique `uniqueID`), `/time` et `/res`.
- **Risques de sécurité sur le réseau local (`android:usesCleartextTraffic="true"`)** :
  1. **Absence de chiffrement (HTTP en clair)** : les navigateurs web ne permettant pas d'établir simplement une connexion HTTPS auto-signée vers une adresse IP locale dynamique (`http://192.168.x.x:8080`), les échanges sur le réseau local ne sont pas chiffrés.
  2. **Absence d'authentification par mot de passe** : toute personne connectée au **même sous-réseau Wi-Fi (ou partage de connexion)** qui connaît l'adresse IP locale et le port affichés à l'écran peut consulter les mesures en cours, télécharger les données de l'expérience (`/export`), lire les métadonnées techniques du modèle de téléphone (`/meta`) et piloter l'expérience (`/control`) tant que la fonction reste cochée.
  3. **Recommandation** : n'activez l'accès à distance que sur un réseau local privé de confiance (domicile, point d'accès de laboratoire scolaire fermé). Le serveur s'arrête immédiatement dès que vous décochez l'option ou que vous quittez l'écran de l'expérience (`Experiment.onStop()` → `stopRemoteServer()`).

### 3.2 Expériences réseau personnalisées HTTP / MQTT (`NetworkConnection.java`, `Http.java`, `MqttClient.java`)
- Un fichier d'expérience personnalisé (`.phyphox`) importé par l'utilisateur peut définir un bloc `<network>` configurant l'envoi ou la réception de données vers un serveur HTTP/HTTPS ou un courtier MQTT 3.1.1 tiers défini par l'auteur de l'expérience.
- **Consentement préalable obligatoire** : avant toute exécution d'une expérience contenant un bloc `<network>`, l'application affiche une fenêtre d'avertissement bloquante (`NetworkConnection.showDataPolicyInfo`) qui liste explicitement :
  - l'adresse exacte du serveur distant et le lien vers sa politique de confidentialité,
  - chaque type de donnée que l'expérience demande à transmettre (mesures de capteurs, microphone, localisation GPS, métadonnées techniques de l'appareil/capteurs, ou identifiant d'installation généré localement `NetworkMetadataUUID` haché par service dans `Metadata.java`).
- Aucune donnée n'est transmise tant que vous n'appuyez pas sur le bouton d'acceptation.

### 3.3 Importation d'expériences par URL ou code QR
- Lorsque vous importez une expérience via une adresse (`http://`, `https://`, `phyphox://`) ou un code QR (`ExperimentListActivity.java`), l'application effectue une requête réseau uniquement vers l'URL que vous avez fournie afin de télécharger le fichier `.phyphox` ou `.zip`.

### 3.4 Ouverture volontaire de liens externes (`Intent.ACTION_VIEW`)
- Lorsque vous touchez un bouton ouvrant une page web externe (dépôt GitHub `https://github.com/salimprof/PhysiqueExpliciteLab`, dépôt amont `https://github.com/phyphox/phyphox-android`, licence GNU GPL sur `gnu.org`, ou liens d'aide documentaire / vidéos vers `phyphox.org` ou `youtube.com`), le lien s'ouvre dans votre navigateur ou application externe.

---

## 4. Absence d'opérations en arrière-plan (`Experiment.onStop()`)
- L'application ne comporte **aucun service d'acquisition en arrière-plan** (`ForegroundService` ou `WorkManager`).
- Les deux entrées `<service>` présentes dans le manifeste fusionné (`AppLocalesMetadataHolderService` et `androidx.camera.core.impl.MetadataHolderService`) ont toutes deux `android:enabled="false"` et `android:exported="false"` (elles servent uniquement de conteneurs de métadonnées statiques pour AndroidX).
- Dès que l'écran de l'expérience passe en arrière-plan (`Experiment.onStop()`, lignes 414–434), l'application exécute immédiatement `stopRemoteServer()` (arrêt du serveur web distant) et `shutdownIO()` (arrêt immédiat de tous les capteurs, du microphone, de la caméra, du GPS, du Bluetooth et des connexions réseau). Lorsque vous revenez dans l'application (`onRestart()`), la mesure reste en pause jusqu'à ce que vous appuyiez à nouveau sur *Lecture*.

---

## 5. Stockage local, exportation et suppression des données
- **Stockage local privé** : vos états de mesures enregistrés, vos expériences importées, vos favoris (`pe_lab_prefs` via `PEFavoritesManager.java`) et vos préférences (`SharedPreferences`) sont stockés uniquement dans le répertoire interne privé de l'application sur votre appareil.
- **Sauvegarde cloud désactivée** : `android:allowBackup="false"` est défini dans `AndroidManifest.xml` afin d'empêcher toute copie automatique de vos mesures ou préférences vers des serveurs de sauvegarde cloud.
- **Exportation à la demande** : l'exportation de mesures (CSV, TSV, Excel `.xlsx`, capture d'écran PNG, état `.phyphox`) ne se déclenche que lorsque vous utilisez le menu *Exporter les données* ou *Capture d'écran*, qui transmet le fichier à l'application que vous choisissez via le sélecteur de partage d'Android (`FileProvider` non exporté `com.salimprof.physiqueexplicitelab.exportProvider`).
- **Suppression des données** :
  1. **Suppression individuelle d'une mesure enregistrée ou d'une expérience personnalisée** : depuis l'onglet **Données** (bouton *Ajouter ou importer des expériences*) ou depuis **Paramètres → Gestion des données**, ouvrez la vue de gestion complète (`ExperimentListActivity`) et utilisez le menu d'options de l'élément pour le supprimer définitivement du stockage interne (`deleteFile`).
  2. **Suppression globale** : vous pouvez effacer l'intégralité des données et préférences à tout moment via **Paramètres Android → Applications → Physique Explicite Lab → Stockage → Effacer les données**, ou en désinstallant l'application.

---
---

# 🇬🇧 PRIVACY POLICY (COMPLETE ENGLISH TRANSLATION)

## 1. General Principle & No Data Collection by the Publisher
**Physique Explicite Lab ("Edited by Salimprof")** (`com.salimprof.physiqueexplicitelab`) is a scientific and educational Android application with no central data-collection backend:
- **No personal data or sensor measurements are collected, transmitted, stored, sold, or shared by the publisher (Salimprof).**
- The application **has no user accounts**, **contains no advertisements**, and **includes no third-party analytics, advertising, telemetry, or crash-reporting SDKs** (verified in `app/build.gradle` and the merged Android manifest: no Firebase, Google Analytics, Crashlytics, Sentry, or social SDKs).
- All bundled scientific experiments (`assets/experiments`) as well as the **"Let's experiment" (`J'expérimente`)** educational module and its **interactive Quizzes** run **100% offline**: sensor signals are acquired and processed solely in local RAM on your smartphone.

---

## 2. Android Permissions (`AndroidManifest.xml`) and Verified Code Behavior

The application declares 16 permissions in `app/src/main/AndroidManifest.xml` (plus AndroidX's internal `signature`-level protection permission `DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION`). Sensitive runtime permissions are requested only when you open an experiment requiring the corresponding hardware sensor:

| Android Permission | Verified Behavior in the Source Code |
|---|---|
| `HIGH_SAMPLING_RATE_SENSORS` | Allows high-frequency sampling (> 200 Hz on Android 12+) of inertial and magnetic sensors (`SensorInput.java`). |
| `RECORD_AUDIO` | Used only in the foreground while an acoustic experiment (audio oscilloscope, FFT spectrum, autocorrelation/frequency, sonar, Doppler effect) is actively recording via `AudioInput.java`. Audio is processed in a volatile in-memory buffer and stops immediately when paused or when leaving the experiment (`Experiment.onStop()` → `shutdownIO()`). |
| `CAMERA` | Used only in the foreground in two user-triggered cases: (1) scanning a QR code to import an experiment (`com.journeyapps.barcodescanner.CaptureActivity`), or (2) running an optical/colorimetric experiment (`CameraInput.java` via CameraX) or depth-sensor experiment (`DepthInput.java` via Camera2). No photos or videos are saved to permanent storage. |
| `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION` | Used only in the foreground when you run the *Location (GPS)* experiment (`GpsInput.java`), or on Android 6 to 11 (API 23–30) when required by the Android OS to scan for nearby Bluetooth Low Energy sensors (`BluetoothScanDialog.java`). The app never requests `ACCESS_BACKGROUND_LOCATION`. |
| `BLUETOOTH_SCAN` (`neverForLocation`) & `BLUETOOTH_CONNECT` *(plus `BLUETOOTH` / `BLUETOOTH_ADMIN` with `maxSdkVersion="30"`)* | Used only when you manually scan for and connect to an external Bluetooth Low Energy (BLE) sensor (`Bluetooth.kt`). On Android 12+ (API 31+), `android:usesPermissionFlags="neverForLocation"` guarantees at the OS level that Bluetooth scanning is never used to derive your location. |
| `READ_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) & `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) | Used only on Android 5 to 9 (API 21–28) to import or export experiment files. On Android 10+ (API 29+), the app uses Scoped Storage and its internal `FileProvider` (`com.salimprof.physiqueexplicitelab.exportProvider`). |
| `WAKE_LOCK` | Used in `Experiment.java` (lines 885–890 and 2272–2282) **only if you enable the "Proximity lock" (`proximityLock`) setting**, acquiring a `PROXIMITY_SCREEN_OFF_WAKE_LOCK` (`"phyphox:measuring"`) to turn off the screen when the proximity sensor is covered during a measurement. *(Keeping the screen on during normal measurements uses the window flag `FLAG_KEEP_SCREEN_ON` without a background CPU WakeLock).* |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `ACCESS_LOCAL_NETWORK` | Used **exclusively** for user-initiated network actions (see Section 3). |

---

## 3. Network Calls, Wi-Fi Remote Access, and Custom Network Experiments

**No automatic network calls are made on app startup or during local experiments.** Network communication occurs only in the following **4 user-initiated scenarios**:

### 3.1 Wi-Fi Remote Access (`RemoteServer.java` / `jlhttp`) — Operation & Security Risks
- **Operation**: When you manually enable "Allow remote access" in an experiment's menu and confirm the security prompt, the app starts a local HTTP web server (`net.freeutils.httpserver.HTTPServer`) on your phone's local network interface (default port `8080`, configurable in settings).
- This local server exposes `/`, `/style.css`, `/config`, `/get` (real-time sensor buffer values), `/control` (start, stop, clear, or set input parameters), `/export` (download CSV/TSV/Excel data), `/meta` (device model and hardware sensor specifications, **excluding** `uniqueID`), `/time`, and `/res`.
- **Local Network Security Risks (`android:usesCleartextTraffic="true"`)**:
  1. **Unencrypted HTTP (Cleartext)**: Because web browsers cannot easily validate TLS certificates for dynamic local IP addresses (`http://192.168.x.x:8080`), local traffic is unencrypted.
  2. **No Password Authentication**: Anyone connected to the **same Wi-Fi subnet (or mobile hotspot)** who knows the displayed local IP address and port can view live sensor measurements, download exported measurement data (`/export`), read device/sensor hardware metadata (`/meta`), and start/stop the experiment (`/control`) while remote access is active.
  3. **Recommendation**: Enable Remote Access only on a trusted private local network (home or closed classroom/lab Wi-Fi). The server stops immediately when you uncheck the option or leave the experiment screen (`Experiment.onStop()` → `stopRemoteServer()`).

### 3.2 Custom HTTP / MQTT Network Experiments (`NetworkConnection.java`, `Http.java`, `MqttClient.java`)
- A custom `.phyphox` experiment file imported by the user may contain a `<network>` block configured to send or receive data to a third-party HTTP/HTTPS endpoint or MQTT 3.1.1 broker.
- **Mandatory Prior Consent**: Before any experiment with a `<network>` block can run, the app displays a blocking consent dialog (`NetworkConnection.showDataPolicyInfo`) disclosing:
  - the exact target server address and a link to its privacy policy,
  - every data item requested for transmission (sensor readings, microphone, GPS location, device/sensor hardware metadata, or a locally generated installation UUID `NetworkMetadataUUID` hashed per target service in `Metadata.java`).
- No data is transmitted unless you explicitly accept the prompt.

### 3.3 Importing Experiments via URL or QR Code
- When you import an experiment from a URL (`http://`, `https://`, `phyphox://`) or QR code (`ExperimentListActivity.java`), the app connects only to the user-specified URL to download the `.phyphox` or `.zip` file.

### 3.4 Opening External Web Links (`Intent.ACTION_VIEW`)
- When you tap an external link button (source code repositories on GitHub, GNU GPL license on `gnu.org`, or experiment help/wiki/video links to `phyphox.org` or `youtube.com`), Android opens the URL in your external browser.

---

## 4. No Background Execution (`Experiment.onStop()`)
- The app runs **no background data-collection services** (`ForegroundService` or `WorkManager`).
- Both `<service>` entries in the merged manifest (`AppLocalesMetadataHolderService` and `androidx.camera.core.impl.MetadataHolderService`) are static metadata holders with `android:enabled="false"` and `android:exported="false"`.
- As soon as the experiment screen leaves the foreground (`Experiment.onStop()`, lines 414–434), the app immediately calls `stopRemoteServer()` and `shutdownIO()`, halting all sensors, microphone, camera, GPS, Bluetooth, and network connections. Returning to the app (`onRestart()`) does not resume recording automatically.

---

## 5. Local Storage, Data Export, and Deletion
- **App-Private Local Storage**: Saved experiment states, custom experiments, favourites (`pe_lab_prefs` via `PEFavoritesManager.java`), and preferences (`SharedPreferences`) are stored exclusively in the app's private internal storage directory.
- **Cloud Backup Disabled**: `android:allowBackup="false"` is set in `AndroidManifest.xml` so measurement files and preferences are never automatically uploaded to cloud backup servers.
- **User-Initiated Export**: Exporting measurements (CSV, TSV, Excel `.xlsx`, PNG screenshot, `.phyphox` state) happens only when you explicitly invoke *Export data* or *Share screenshot* via the Android Share sheet (`com.salimprof.physiqueexplicitelab.exportProvider`).
- **Deleting Data**:
  1. **Individual Deletion**: From the **Data** tab (tap *Add or import experiments*) or **Settings → Data management**, open the full management view (`ExperimentListActivity`) and use the item menu on any saved state or custom experiment to delete it permanently (`deleteFile`).
  2. **Complete Deletion**: You can erase all local data and preferences at any time via **Android Settings → Apps → Physique Explicite Lab → Storage → Clear storage**, or by uninstalling the application.

---

## 6. Contact & Open-Source Attribution
- **Publisher / Customization Author**: **Salim El Mokhtar (Salimprof)** — Email: `salimprof@gmail.com`
- **Source Code of this Modified Version (GNU GPL v3)**: https://github.com/salimprof/PhysiqueExpliciteLab
- **Original Upstream Project**: Derived from **phyphox** (Copyright © 2016–2024 Dr. Sebastian Staacks, 2nd Institute of Physics, RWTH Aachen University, https://github.com/phyphox/phyphox-android), distributed under the **GNU General Public License v3.0 (GNU GPL v3)**. **Physique Explicite Lab ("Edited by Salimprof") is an independent customized version and is NOT an official product of RWTH Aachen University or of the phyphox team.**
