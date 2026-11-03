# Politique de Confidentialité / Privacy Policy
## Physique Explicite Lab (« Edited by Salimprof »)
- **Identifiant Android (`applicationId`)** : `com.salimprof.physiqueexplicitelab`
- **Version** : `1.0.0-pe (base phyphox 1.2.1)` (`versionCode` : `1020105`)
- **Éditeur / Responsable de la personnalisation** : Salim El Mokhtar (**Salimprof**) — Contact : `salimprof@gmail.com`
- **Dépôt du code source (GNU GPL v3)** : https://github.com/salimprof/PhysiqueExpliciteLab
- **Dernière mise à jour** : 2 novembre 2026 (`2026-11-02`)

---

## 1. Principe général : Fonctionnement local et absence de collecte par l'éditeur
**Physique Explicite Lab** fonctionne hors connexion pour toutes les expériences scientifiques intégrées et les activités pédagogiques locales (« J'expérimente » et Quiz) :
- **Aucune donnée personnelle n'est collectée, transmise, vendue ou partagée** avec l'éditeur (Salimprof) ni avec des régies publicitaires ou services d'analyse.
- L'application **ne nécessite aucun compte utilisateur**, ne contient **aucune publicité** et n'intègre **aucun SDK de télémétrie, de profilage ou de statistiques (analytics / crash reporting)**.
- Toutes les mesures effectuées à l'aide des capteurs de votre smartphone (accéléromètre, gyroscope, magnétomètre, luxmètre, baromètre, microphone, caméra, capteur de profondeur, GPS) sont traitées **en temps réel et localement dans la mémoire de votre appareil**.

---

## 2. Détail complet des autorisations Android (`AndroidManifest.xml`) et de leur utilisation réelle

Les autorisations sensibles d'exécution (*runtime permissions*) ne sont demandées par Android qu'au moment précis où vous ouvrez une expérience qui utilise le capteur correspondant :

| Autorisation Android | Utilisation réelle dans le code de l'application |
|---|---|
| `HIGH_SAMPLING_RATE_SENSORS` | Permet l'échantillonnage à haute fréquence (> 200 Hz sur Android 12+) des capteurs inertiels et magnétiques (`SensorInput.java`). |
| `RECORD_AUDIO` | Utilisée uniquement pendant l'exécution d'une expérience acoustique (oscilloscope sonore, spectre FFT, autocorrélation/fréquencemètre, sonar, doppler) via `AudioInput.java`. L'audio est traité en mémoire vive et n'est jamais transmis à l'éditeur. |
| `CAMERA` | Utilisée uniquement dans deux cas déclenchés par l'utilisateur : (1) le scan d'un code QR pour importer une expérience (`CaptureActivity`), ou (2) l'exécution d'une expérience optique/caméra/profondeur (`CameraInput.java`, `DepthInput.java`). |
| `ACCESS_FINE_LOCATION` & `ACCESS_COARSE_LOCATION` | Utilisées uniquement au premier plan lorsque vous lancez l'expérience *Localisation (GPS)* (`GpsInput.java`), ou sur Android 6 à 11 (API 23–30) lorsque le système Android l'exige pour rechercher des capteurs Bluetooth Low Energy à proximité. Aucune géolocalisation en arrière-plan (`ACCESS_BACKGROUND_LOCATION`) n'est demandée. |
| `BLUETOOTH_SCAN` (`neverForLocation`) & `BLUETOOTH_CONNECT` *(ainsi que `BLUETOOTH` / `BLUETOOTH_ADMIN` sur API ≤ 30)* | Utilisées uniquement lorsque vous recherchez et connectez volontairement un capteur externe Bluetooth Low Energy (BLE) (`Bluetooth.kt`). Sur Android 12+ (API 31+), l'attribut `neverForLocation` garantit que le scan Bluetooth n'est jamais utilisé pour déduire votre position. |
| `READ_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) & `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) | Utilisées uniquement sur les anciens appareils Android 5 à 9 (API 21–28) pour importer ou exporter des fichiers d'expérience. Sur Android 10+ (API 29+), l'application utilise le stockage isolé (*Scoped Storage*) et le `FileProvider` (`${applicationId}.exportProvider`). |
| `WAKE_LOCK` | Maintient l'acquisition active et empêche la mise en veille pendant qu'une mesure ou que l'accès à distance est en cours d'exécution. |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE`, `ACCESS_LOCAL_NETWORK` | Utilisées **exclusivement** lors d'actions réseau déclenchées par l'utilisateur (détaillées à la section 3 ci-dessous). |

---

## 3. Communications réseau, Accès à distance et trafic en clair (`usesCleartextTraffic="true"`)

Aucune connexion réseau n'est établie automatiquement au lancement ou pendant les expériences locales. Une communication réseau n'a lieu que dans les **4 cas suivants, toujours à l'initiative explicite de l'utilisateur** :

1. **Accès à distance sur le réseau local (`RemoteServer.java` / `jlhttp`)** :
   - Lorsque vous cochez manuellement « Autoriser l'accès à distance » dans le menu d'une expérience et validez l'avertissement de sécurité, l'application démarre un serveur web HTTP local sur votre smartphone (port par défaut `8080`).
   - Cela vous permet de visualiser les graphiques et de piloter l'expérience depuis le navigateur d'un ordinateur ou d'une tablette connecté(e) au même réseau local Wi-Fi ou partage de connexion.
   - **Avertissement de sécurité (`usesCleartextTraffic="true"`)** : cette communication locale utilise le protocole HTTP non chiffré afin de fonctionner sans certificat TLS sur des adresses IP locales (`http://192.168.x.x:8080`). **N'activez l'accès à distance que sur un réseau local de confiance (domicile, réseau de laboratoire scolaire fermé).**
2. **Expériences réseau personnalisées HTTP / MQTT (`NetworkConnection.java`, `Http.java`, `MqttClient.java`)** :
   - Certaines définitions d'expériences personnalisées (`.phyphox`) peuvent configurer l'envoi ou la réception de mesures vers un serveur HTTP/HTTPS ou un courtier MQTT 3.1.1 choisi par l'utilisateur (ex. serveur pédagogique local ou IoT).
   - Avant toute exécution d'une telle expérience, l'application affiche une fenêtre d'avertissement obligatoire (*Privacy Notice*) indiquant l'adresse exacte du serveur cible et la liste des capteurs concernés, et requiert votre accord explicite.
3. **Importation d'une expérience par URL ou code QR** :
   - Lorsque vous importez une expérience depuis une adresse web (`http://` ou `https://`) ou un code QR, l'application télécharge uniquement le fichier d'expérience demandé.
4. **Ouverture volontaire de liens externes** :
   - Lorsque vous appuyez sur un bouton ouvrant une page externe dans votre navigateur (dépôts GitHub du code source, texte GNU GPL sur `gnu.org`, ou liens d'aide documentaire / vidéos explicatives vers `phyphox.org` ou `youtube.com`).

---

## 4. Stockage local, sauvegarde et suppression des données
- Vos états d'expériences enregistrés, vos expériences personnalisées, vos favoris (`PEFavoritesManager`) et vos préférences (langue FR/AR/EN, thème Clair/Sombre) sont stockés uniquement dans l'espace interne privé de l'application.
- La sauvegarde automatique dans le cloud Android est désactivée (`android:allowBackup="false"`) afin qu'aucune mesure ou préférence locale ne soit copiée automatiquement sur des serveurs distants.
- L'exportation de données (CSV, TSV, Excel `.xlsx`, capture d'écran PNG, état `.phyphox`) s'effectue uniquement à votre demande via le menu de partage d'Android (`Intent.ACTION_SEND` / `FileProvider`).
- **Suppression** : vous pouvez supprimer vos données enregistrées à tout moment depuis l'onglet **Données** de l'application, en effaçant les données de l'application dans les paramètres Android, ou en désinstallant l'application.

---

## 5. English Summary — Privacy Policy (`Physique Explicite Lab, Edited by Salimprof`)
- **No Data Collection or Sharing by the Publisher**: **Physique Explicite Lab** (`com.salimprof.physiqueexplicitelab`) runs offline for all bundled experiments and educational activities. It has no user accounts, no advertisements, and no analytics, tracking, or telemetry SDKs. All sensor data (accelerometer, gyroscope, magnetometer, light, pressure, microphone, camera/depth, GPS) is processed locally on your device.
- **Runtime Permissions**:
  - `RECORD_AUDIO`: Used only during acoustic experiments.
  - `CAMERA`: Used only when scanning QR codes to import experiments or during optical/camera/depth experiments.
  - `ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`: Used only in the foreground during GPS experiments (or on Android 6–11 when required by the OS to scan for BLE sensors).
  - `BLUETOOTH_SCAN` (`neverForLocation`) / `BLUETOOTH_CONNECT`: Used only when connecting to external Bluetooth Low Energy sensors.
  - `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion="28"`): Used only on Android 9 and older for file import/export.
- **User-Initiated Network Activity**: Network access (`INTERNET`, `ACCESS_LOCAL_NETWORK`, `usesCleartextTraffic="true"`) only occurs when explicitly triggered by the user: (1) local Wi-Fi **Remote Access** web server (`jlhttp`, unencrypted HTTP — use only on trusted local networks), (2) custom **HTTP/MQTT network experiments** (which display a mandatory consent prompt disclosing the target server and sensors before running), (3) importing an experiment via URL/QR code, or (4) tapping an external link.
- **Data Storage & Deletion**: Saved measurements and preferences stay in app-private storage (`android:allowBackup="false"`). You can delete them at any time in the **Data** tab, via Android Settings, or by uninstalling the app.
