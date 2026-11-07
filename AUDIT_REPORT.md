# Rapport d'Audit Technique, de Stabilisation des Tests et de Préparation Google Play
## Projet : **Physique Explicite Lab (« Edited by Salimprof »)**
- **Dépôt GitHub audité** : https://github.com/salimprof/PhysiqueExpliciteLab
- **Branche de travail dédiée** : `fix/stabilize-tests-and-release` *(branche `main` conservée intacte en attente de validation)*
- **Identifiant Android (`applicationId`)** : `com.salimprof.physiqueexplicitelab`
- **Version commerciale de l'application** : `versionName "1.0.0-pe (base phyphox 1.2.1)"` • `versionCode 1020105`
- **Version du moteur et du format `.phyphox` supporté** : `PhyphoxFile.phyphoxFileVersion = "1.20"` (base `phyphox-android` v1.2.1, commit `45fa55a0`)
- **SDK et chaîne d'outils** : `minSdkVersion 24` • `targetSdkVersion 35` (Android 15) • `compileSdk 37` • JDK 21 • Gradle 9.6.0 • AGP 9.2.1 • NDK `28.0.13004108` • CMake `3.22.1`
- **Révision de référence `phyphox-docs` épinglée** : `5d26b823e3738bf5a006cf677e42d461f53a804a`
- **Date de mise à jour de l'audit** : `2026-11-07`

---

## 1. Analyse et correction des échecs de tests automatisés (GitHub Actions run `41557967396`)

L'exécution CI initiale (`https://github.com/salimprof/PhysiqueExpliciteLab/actions/runs/41557967396`) présentait **469 tests exécutés, 34 échecs et 56 ignorés** (ainsi que 2 échecs supplémentaires sur Windows liés aux séparateurs de chemin dans `ContainerIntakeTest`). Toutes les causes racines ont été reproduites localement avec le corpus `phyphox-docs` et corrigées sans désactiver aucun test ni affaiblir les contrôles :

| Famille de tests | Cause racine identifiée | Correction appliquée |
|---|---|---|
| **A1. 26 échecs `ViewSnapshotTest` (configurations LTR, thèmes clair/sombre, tablette, grande police, `info-separator-image`)** | Dans [`app/src/main/java/de/rwth_aachen/phyphox/helper/RGB.java`](app/src/main/java/de/rwth_aachen/phyphox/helper/RGB.java), la couleur nommée sémantique `color="orange"` du format `.phyphox` pointe vers `@color/phyphox_primary` (`R.color.phyphox_primary`). Or [`app/src/main/res/values/colors.xml`](app/src/main/res/values/colors.xml) avait remplacé `phyphox_primary` (`#ffff7e22`) par du turquoise (`#ff16A6A2`), de sorte que les éléments `.phyphox` demandant explicitement `color="orange"` s'affichaient en turquoise au lieu d'orange. | Restauration de `@color/phyphox_primary` (`#ffff7e22`) et `@color/phyphox_primary_weak` (`#fffeae76`) dans [`colors.xml`](app/src/main/res/values/colors.xml) pour le moteur scientifique, tandis que l'interface personnalisée `PEMainActivity` conserve intégralement sa palette bleue/turquoise dédiée dans [`colors_pe.xml`](app/src/main/res/values/colors_pe.xml) et [`themes_pe.xml`](app/src/main/res/values/themes_pe.xml). Les 26 snapshots non-RTL passent immédiatement **sans modifier aucune image de référence LTR/thème**. |
| **A2. 5 échecs `ViewSnapshotTest` (`rtl-phone`) & défaut réel d'interface RTL dans `ExpView`** | L'activation de `android:supportsRtl="true"` dans `AndroidManifest.xml` (requise pour l'arabe) a révélé deux vrais défauts de mise en page RTL dans [`ExpView.java`](app/src/main/java/de/rwth_aachen/phyphox/ExpView.java) : (1) dans `editElement`, `AppCompatEditText` activait le défilement horizontal interne (`VERY_WIDE` = `1048576 px`), rendant le texte numérique (`1.5`, `42.0`, etc.) **invisible** en RTL ; (2) `valueElement`, `editElement`, `toggleElement`, `dropDownElement` et `sliderElement` utilisaient `setPadding` physique au lieu de `setPaddingRelative` et n'appliquaient pas `TEXT_ALIGNMENT_VIEW_START` / `TEXT_ALIGNMENT_VIEW_END`, provoquant l'écrasement ou le rejet aux bords externes des libellés et valeurs en RTL. | Correction dans [`ExpView.java`](app/src/main/java/de/rwth_aachen/phyphox/ExpView.java) : ajout de `et.setHorizontallyScrolling(false)`, remplacement des 10 appels `setPadding` par `setPaddingRelative`, et application de `TEXT_ALIGNMENT_VIEW_END` / `TEXT_ALIGNMENT_VIEW_START`. Après vérification visuelle image par image, seules les **22 images `rtl-phone.png`** impactées (`buttons-toggles`, `edits`, `sliders-dropdowns`, `values`) ont été actualisées, conformément au contrat documenté dans `ViewSnapshotTest.java` (lignes 99–106). |
| **B. 1 échec `AnalysisGoldenVectorTest` (`analysis/vectors/fft/length-one.phyphox`)** | Dans [`Analysis.java`](app/src/main/java/de/rwth_aachen/phyphox/Analysis.java), `FFT.prepare(int n)` et `fftAM.update()` ignoraient les signaux de taille `size == 1` via `if (size < 2) return;`, laissant les buffers de sortie `outre` et `outim` vides au lieu de produire la transformée de Fourier discrète à 1 point (transformée identité : `outre = [2.5]`, `outim = [0.0]`). | Dans [`Analysis.java`](app/src/main/java/de/rwth_aachen/phyphox/Analysis.java), acceptation de `size >= 1` (`if (size < 1) return;`) dans `fftAM.update()` (branches native et Java) et initialisation de `this.np2 = Math.max(0, n)` dans `FFT.prepare(int n)`. Les signaux vides (`N = 0`), de taille 1 (`N = 1`), puissances de 2 et tailles impaires (`non-power-of-two`) passent tous. |
| **C. 2 échecs `CorpusToleratedAttributesLoadTest` (`invalid/stack-with-spacing.phyphox`, `invalid/unit-reference-unknown.phyphox`)** | Ces deux fichiers du corpus déclarent `<phyphox version="1.21">`, alors que le moteur supporte `PhyphoxFile.phyphoxFileVersion = "1.20"`. Contrairement à `CorpusValidLoadTest` et `AnalysisGoldenVectorTest`, `CorpusToleratedAttributesLoadTest` avait omis la garde `CorpusTestEnvironment.versionAtMostSupported(declared)` prévue par le contrat du corpus (`phyphox-docs/corpus/README.md`). | Alignement de [`CorpusToleratedAttributesLoadTest.java`](app/src/test/java/de/rwth_aachen/phyphox/CorpusToleratedAttributesLoadTest.java) sur `CorpusValidLoadTest.java` via `declaredVersion` + `assumeTrue(..., versionAtMostSupported(declared))`, sans contourner `PhyphoxFile`, et épinglage de `phyphox/phyphox-docs` au commit `5d26b823e3738bf5a006cf677e42d461f53a804a` dans [`.github/workflows/test.yml`](.github/workflows/test.yml). |
| **D. 2 échecs `ContainerIntakeTest` sous Windows** | Sur Windows, `Uri.fromFile(file).getLastPathSegment()` renvoyait le chemin complet contenant des antislashs `\` au lieu du nom de fichier dans `aResourceZipDeliversItsImageToTheExperiment` et `aPartialZipIsRebuiltAndLoads`. | Extraction portable du nom de fichier via `new File(opened.getData().getPath()).getName()` dans [`ContainerIntakeTest.java`](app/src/test/java/de/rwth_aachen/phyphox/ContainerIntakeTest.java). |

---

## 2. Améliorations et contrôles du module `pelab`

1. **Externalisation des chaînes codées en dur** :
   - Le texte `"9 étapes"` codé en dur dans [`PEPedagogyManager.java`](app/src/main/java/de/rwth_aachen/phyphox/pelab/PEPedagogyManager.java) a été remplacé par la ressource traduisible `@string/pe_lab_steps_badge` définie dans :
     - [`app/src/main/res/values/strings_pe.xml`](app/src/main/res/values/strings_pe.xml) : `9 étapes`
     - [`app/src/main/res/values-en/strings_pe.xml`](app/src/main/res/values-en/strings_pe.xml) : `9 steps`
     - [`app/src/main/res/values-ar/strings_pe.xml`](app/src/main/res/values-ar/strings_pe.xml) : `9 مراحل`
2. **Gestion explicite des capteurs absents** :
   - Ajout de [`PEDialogs.showSensorUnavailableDialog`](app/src/main/java/de/rwth_aachen/phyphox/pelab/PEDialogs.java) affichant un message localisé clair (`sensorNotAvailableWarningTitle` / `sensorNotAvailableWarningText1` + nom du capteur + `sensorNotAvailableWarningText2`) lorsqu'un capteur matériel est absent de l'appareil.
   - Vérification active de `unavailableSensor` avant lancement dans [`PEMainActivity.launchExperiment`](app/src/main/java/de/rwth_aachen/phyphox/pelab/PEMainActivity.java) et dans [`PEPedagogyManager.launchRealAssetExperiment`](app/src/main/java/de/rwth_aachen/phyphox/pelab/PEPedagogyManager.java) (étape 4 du module « J'expérimente »).

---

## 3. État de validation par catégorie (Distinction stricte)

### 3.1 Vérifications automatisées réussies (Exécutées localement sous Windows 10/11, JDK 21.0.12, Gradle 9.6.0, Robolectric SDK 35)
- **Suite complète de tests unitaires JVM (`.\gradlew.bat testRegularDebugUnitTest`)** :
  - **474 tests exécutés, 416 réussis, 0 échec, 58 ignorés** (les 58 tests ignorés via `assumeTrue` sont les fichiers du corpus `phyphox-docs` déclarant `<phyphox version="1.21">`, supérieure à la version `1.20` du moteur).
  - Détail des suites exécutées et validées :
    - `ViewSnapshotTest` : **37 / 37 réussis** (0 échec).
    - `AnalysisGoldenVectorTest` : **38 / 38 vecteurs `1.20` réussis** (incluant `fft/length-one.phyphox`), 1 vecteur `1.21` ignoré.
    - `CorpusValidLoadTest` : **257 exécutés**, 214 réussis (`1.20`), 43 ignorés (`1.21`), 0 échec.
    - `CorpusToleratedAttributesLoadTest` : **8 exécutés**, 6 réussis (`1.20`), 2 ignorés (`1.21`), 0 échec.
    - `CorpusInvalidRejectTest` : **54 / 54 réussis**, 0 échec.
    - `ContainerIntakeTest` : **9 / 9 réussis**, 0 échec.
    - `PELabVerificationTest` : **3 / 3 réussis** (structure des 3 activités pédagogiques et quiz, conformité GNU GPL v3, sous-modules et ressources FR/EN/AR).
    - `PELabUiAndFlowTest` : **5 / 5 réussis** (navigation entre les 5 onglets, soumission et correction des 3 quiz, persistance des favoris, locales FR/EN/AR + direction RTL en arabe, boîtes de dialogue « Edited by Salimprof », Crédits/Sources, licence GNU GPL v3 hors ligne et capteur indisponible).
- **Contrôle Android Lint (`.\gradlew.bat lintRegularDebug`)** :
  - Exécuté avec succès (`BUILD SUCCESSFUL`, **0 erreur fatale**).
- **Compilations Debug & Release (`.\gradlew.bat assembleRegularDebug bundleRegularRelease`)** :
  - `assembleRegularDebug` : **`BUILD SUCCESSFUL`** → APK généré dans `app/build/outputs/apk/regular/debug/app-regular-debug.apk` et copié dans `dist/PhysiqueExpliciteLab-debug.apk`.
  - `bundleRegularRelease` : **`BUILD SUCCESSFUL`** → AAB généré dans `app/build/outputs/bundle/regularRelease/app-regular-release.aab` et signé dans `GooglePlay_Publication/PhysiqueExpliciteLab-GooglePlay-SIGNED.aab` (`jarsigner -verify` : **`jar verified`**).
- **Compatibilité Google Play avec les pages mémoire de 16 Ko (`llvm-readelf -l`)** :
  - Vérifiée sur toutes les bibliothèques natives C++/JNI compilées avec le NDK `28.0.13004108` (`libanalysis.so` et `libfftw3f.so`) : tous les segments ELF `LOAD` des architectures 64 bits (`arm64-v8a` et `x86_64`) possèdent un alignement de **`0x4000` (16 384 octets = 16 Ko)**.

### 3.2 Tests sur émulateur Android (`connectedAndroidTest`)
- **Non exécutés dans cette session** : aucun émulateur Android (`adb devices`) n'était démarré dans l'environnement d'exécution local lors de cet audit. Les tests d'interface et de rendu ont été exécutés via Robolectric (SDK 35, moteur graphique natif Robolectric) dans `testRegularDebugUnitTest`.

### 3.3 Tests sur appareil physique Android
- **Non exécutés dans cette session** : aucun smartphone physique n'était connecté en USB (`adb devices` vide) sur la machine de build. Les capteurs matériels réels (accéléromètre, gyroscope, magnétomètre, microphone, GPS, baromètre, capteur de lumière, caméra, Bluetooth Low Energy) nécessitent une validation manuelle sur smartphone physique avant publication.

### 3.4 Vérifications manuelles restantes avant mise en ligne sur Google Play
1. **Installation et test sur smartphone physique Android** :
   - Installer `dist/PhysiqueExpliciteLab-debug.apk` (ou `PhysiqueExpliciteLab-Salimprof.apk`).
   - Parcourir les 5 onglets (*Accueil*, *Expériences*, *J'expérimente*, *Données*, *Paramètres*) en **Français**, **Arabe (RTL)** et **Anglais**, en thème **Clair** et **Sombre**.
   - Ouvrir les 3 activités de *J'expérimente*, lancer la mesure réelle depuis l'étape 4 (*Accéléromètre*, *Magnétomètre*, *Autocorrélation audio*), enregistrer un état de mesure, le retrouver dans l'onglet *Données*, et tester l'exportation CSV / Excel.
   - Vérifier le comportement lors du refus d'une permission (`RECORD_AUDIO` ou `ACCESS_FINE_LOCATION`) et sur une expérience dont le capteur est absent de l'appareil (ex. baromètre ou capteur de pression).
2. **Examen et fusion de la branche Git `fix/stabilize-tests-and-release`** :
   - Examiner le diff sur la branche `fix/stabilize-tests-and-release`, la pousser sur GitHub pour vérifier le passage au vert du workflow GitHub Actions (`tests`), puis la fusionner dans `main`.
3. **Soumission finale dans Google Play Console** :
   - Renseigner l'URL publique de la politique de confidentialité (`PRIVACY_POLICY.md` ou `privacy-policy.html`) et le questionnaire *Data safety*.
   - Importer le bundle signé `GooglePlay_Publication/PhysiqueExpliciteLab-GooglePlay-SIGNED.aab` (`versionCode 1020105`, `targetSdkVersion 35`).
