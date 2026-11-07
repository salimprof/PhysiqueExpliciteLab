# GUIDE DE COMPILATION, DE SIGNATURE ET D'EXPORTATION (APK & AAB)
## PHYSIQUE EXPLICITE LAB — Edited by Salimprof

---

### 1. Identité technique de l'application

- **Nom officiel affiché** : `Physique Explicite Lab`
- **Signature officielle** : `Edited by Salimprof`
- **Responsable de la personnalisation** : Salim El Mokhtar (`Salimprof`)
- **Identifiant Android (`applicationId`)** : `com.salimprof.physiqueexplicitelab`
  *(Distinct de `de.rwth_aachen.phyphox` : les deux applications peuvent être installées simultanément sur le même smartphone sans conflit de package ni de `FileProvider`).*
- **Version** : `versionName "1.0.0-pe (base phyphox 1.2.1)"` (`versionCode 1020105`)
- **SDK cible / compilation** : `compileSdk 37`, `targetSdkVersion 35`, `minSdkVersion 24`
- **Licence** : GNU General Public License v3.0 (GPL v3), dérivé du projet open source [phyphox-android](https://github.com/phyphox/phyphox-android).

---

### 2. Prérequis de compilation

1. **JDK 21** (ex. OpenJDK Temurin 21).
2. **Android SDK** avec :
   - `platforms;android-37.0`
   - `build-tools;36.0.0`
   - `ndk;28.0.13004108`
   - `cmake;3.22.1`
3. Fichier `local.properties` à la racine du projet pointant vers le SDK :
   ```properties
   sdk.dir=C\:\\Android\\Sdk
   ```

---

### 3. Générer l'APK Debug installable

Depuis la racine du projet `PhysiqueExpliciteLab` :

```powershell
.\gradlew.bat assembleRegularDebug
```

L'APK généré se trouve dans :
- `app/build/outputs/apk/regular/debug/app-regular-debug.apk`
- et une copie prête à installer est placée dans : `dist/PhysiqueExpliciteLab-debug.apk`

Pour l'installer sur un smartphone connecté en USB (avec le débogage USB activé) :
```powershell
adb install -r dist/PhysiqueExpliciteLab-debug.apk
```

---

### 4. Procédure de génération de l'APK Release signé

> **Sécurité** : Aucune clé privée de production n'est générée ni stockée dans le dépôt Git. Créez et conservez votre keystore de production hors du dépôt.

#### Étape 4.1 : Créer votre keystore de production (une seule fois)
```powershell
keytool -genkeypair -v `
  -keystore C:\chemin\securise\salimprof-release.jks `
  -keyalg RSA -keysize 4096 -validity 10000 `
  -alias salimprof_pelab
```

#### Étape 4.2 : Compiler l'APK Release non signé puis le signer avec `apksigner`
```powershell
.\gradlew.bat assembleRegularRelease

# Alignement 4 octets (zipalign)
C:\Android\Sdk\build-tools\36.0.0\zipalign.exe -v -p 4 `
  app\build\outputs\apk\regular\release\app-regular-release-unsigned.apk `
  dist\PhysiqueExpliciteLab-release-aligned.apk

# Signature cryptographique v2/v3 avec votre keystore
C:\Android\Sdk\build-tools\36.0.0\apksigner.bat sign `
  --ks C:\chemin\securise\salimprof-release.jks `
  --ks-key-alias salimprof_pelab `
  --out dist\PhysiqueExpliciteLab-release-signed.apk `
  dist\PhysiqueExpliciteLab-release-aligned.apk

# Vérification de la signature
C:\Android\Sdk\build-tools\36.0.0\apksigner.bat verify --verbose dist\PhysiqueExpliciteLab-release-signed.apk
```

---

### 5. Générer un Android App Bundle (AAB) pour Google Play

```powershell
.\gradlew.bat bundleRegularRelease
```

Le fichier `.aab` est généré dans :
- `app/build/outputs/bundle/regularRelease/app-regular-release.aab`

Pour signer l'AAB avec votre keystore avant soumission à la Google Play Console :
```powershell
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 `
  -keystore C:\chemin\securise\salimprof-release.jks `
  app\build\outputs\bundle\regularRelease\app-regular-release.aab `
  salimprof_pelab
```

---

### 6. Conformité GNU GPL v3 lors de la distribution

Conformément à la licence GNU GPL v3 :
- Conservez le fichier `LICENSE` et les notices des auteurs originaux (RWTH Aachen University / Sebastian Staacks et contributeurs) accessibles dans `Paramètres → À propos de l'application` et `Licences open source`.
- Lors de toute distribution de l'APK ou de l'AAB, mettez à disposition le code source complet correspondant de `PhysiqueExpliciteLab` (incluant les sous-modules `experiments` et `remote`) sous la même licence GNU GPL v3.
