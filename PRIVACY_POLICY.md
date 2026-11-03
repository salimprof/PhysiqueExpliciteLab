# Politique de Confidentialité / Privacy Policy
## Physique Explicite Lab (« Edited by Salimprof »)
**Identifiant de l'application (`applicationId`)** : `com.salimprof.physiqueexplicitelab`  
**Éditeur / Responsable de la personnalisation** : Salim El Mokhtar (**Salimprof**) — Contact : `salimprof@gmail.com`  
**Dernière mise à jour** : 2 novembre 2026 (`2026-11-02`)

---

## 1. Principe général : Fonctionnement local et absence de collecte de données personnelles
**Physique Explicite Lab** fonctionne hors connexion pour toutes les expériences scientifiques et activités pédagogiques locales :
- **Aucune donnée personnelle n'est collectée, transmise, vendue ou partagée** avec l'éditeur (Salimprof) ni avec des tiers.
- L'application **ne nécessite aucun compte utilisateur**, ne contient **aucune publicité** et n'intègre **aucun outil de suivi, de télémétrie ou d'analyse statistique (analytics)**.
- Toutes les mesures effectuées à l'aide des capteurs de votre smartphone (accéléromètre, gyroscope, magnétomètre, luxmètre, baromètre, microphone, caméra, GPS) sont traitées **exclusivement en temps réel et localement sur votre appareil**.

---

## 2. Utilisation des autorisations (Permissions Android)
Certaines expériences scientifiques nécessitent l'accès à des capteurs soumis à une autorisation Android. Ces autorisations ne sont demandées qu'au moment où vous lancez une expérience qui en a besoin :
1. **Microphone (`android.permission.RECORD_AUDIO`)** : utilisé uniquement pendant l'exécution d'une expérience acoustique (oscilloscope sonore, spectre de fréquence FFT, autocorrélation/fréquencemètre, sonar). Aucun enregistrement audio n'est envoyé sur Internet.
2. **Caméra (`android.permission.CAMERA`)** : utilisée uniquement lorsque vous choisissez de scanner un code QR pour importer une expérience ou lorsque vous utilisez une expérience optique basée sur la caméra. Aucune image ou vidéo n'est transmise à l'extérieur de votre appareil.
3. **Localisation / GPS (`android.permission.ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`)** : utilisée uniquement au premier plan lorsque vous lancez l'expérience *Localisation (GPS)* afin d'afficher et tracer la position, l'altitude et la vitesse. Vos coordonnées GPS restent sur votre appareil.
4. **Bluetooth (`BLUETOOTH_SCAN`, `BLUETOOTH_CONNECT`)** : utilisé uniquement si vous choisissez de connecter un capteur externe Bluetooth Low Energy (BLE) à l'application.
5. **Réseau local / Internet (`INTERNET`, `ACCESS_LOCAL_NETWORK`)** : aucun échange réseau n'a lieu automatiquement. Le réseau n'est utilisé que si vous déclenchez manuellement l'une des fonctions suivantes :
   - **Accès à distance (Remote Access)** : permet d'afficher et contrôler une expérience depuis le navigateur web d'un ordinateur ou d'une tablette connecté(e) au même réseau Wi-Fi local (communication locale non chiffrée : à utiliser uniquement sur un réseau de confiance).
   - **Import d'expérience par URL** ou ouverture volontaire d'un lien externe (comme le dépôt du code source sur GitHub).

---

## 3. Stockage et suppression des données
- Vos mesures enregistrées, vos expériences personnalisées, vos favoris et vos préférences (langue, thème clair/sombre) sont stockés uniquement dans l'espace privé de l'application sur votre téléphone.
- La sauvegarde automatique dans le cloud Android est désactivée (`android:allowBackup="false"`) afin que vos mesures restent strictement sur votre appareil.
- L'exportation de mesures (CSV, Excel, capture d'écran) ne se produit que lorsque vous appuyez explicitement sur le bouton d'exportation/partage et choisissez vous-même l'application destinataire via le menu de partage d'Android.
- **Suppression** : vous pouvez effacer toutes vos données locales à tout moment en supprimant les mesures dans l'onglet *Données*, en vidant le stockage de l'application dans les paramètres Android, ou en désinstallant l'application.

---

## 4. Licence Open Source et Indépendance
**Physique Explicite Lab (« Edited by Salimprof »)** est une version éducative personnalisée indépendante dérivée du projet open source **phyphox** (créé au 2e Institut de Physique de la RWTH Aachen University, https://github.com/phyphox/phyphox-android) et distribuée sous licence **GNU General Public License v3.0 (GNU GPL v3)**. Il ne s'agit pas d'un produit officiel de la RWTH Aachen University ni de l'équipe phyphox.

---

## English Summary (Privacy Policy)
**Physique Explicite Lab ("Edited by Salimprof")** processes all sensor measurements locally on your device.
- **No personal data collection or sharing**: The app has no user accounts, no advertisements, and no analytics or tracking SDKs. No data is transmitted to the developer or third parties.
- **Permissions**: Microphone (`RECORD_AUDIO`), Camera (`CAMERA` for QR codes and optical experiments), Location (`ACCESS_FINE_LOCATION` for GPS experiments), and Bluetooth are requested only at runtime when you launch an experiment requiring that specific sensor. All recorded data remains on your device unless you explicitly export/share it via the Android Share sheet or enable local Wi-Fi Remote Access.
