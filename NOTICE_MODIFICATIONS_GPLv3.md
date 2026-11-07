# GNU GPL v3 Modification & Compliance Notice — Physique Explicite Lab ("Edited by Salimprof")

## 1. Identification of the Modified Version (GNU GPL v3, Section 5)
- **Modified Application Name**: **Physique Explicite Lab** (short name: **PE Lab**)
- **Customization Signature**: **Edited by Salimprof**
- **Person Responsible for Modifications**: **Salimprof (Salim El Mokhtar)** — Physics & Chemistry Teacher and Educational Technology Trainer
- **Modification Date**: **2026-11-02**
- **Modified Source Repository (GNU GPL v3)**: https://github.com/salimprof/PhysiqueExpliciteLab
- **Android Application ID**: `com.salimprof.physiqueexplicitelab`
- **Version Name**: `1.0.0-pe (base phyphox 1.2.1)` (`versionCode`: `1020105`)
- **SDK Configuration**: `compileSdk 37`, `targetSdkVersion 35`, `minSdkVersion 24`
- **Official Independence Disclaimer**: **Physique Explicite Lab ("Edited by Salimprof") is an independent customized version derived from phyphox. It is NOT an official product of RWTH Aachen University or of the phyphox team.**

---

## 2. Original Upstream Work & Copyright Notices (Preserved Intact)
- **Upstream Project**: **phyphox** (Physical Phone Experiments — https://phyphox.org)
- **Original Source Repository**: https://github.com/phyphox/phyphox-android (base commit `45fa55a0`, tag/version `1.2.1`)
- **Original Copyright**: Copyright © 2017–2024 **RWTH Aachen University**, 2nd Institute of Physics (**Sebastian Staacks** and contributors)
- **License**: **GNU General Public License version 3.0 (GNU GPL v3)** — full text included in [`LICENSE`](LICENSE) and bundled inside the APK/AAB at [`app/src/main/assets/gpl-3.0.txt`](app/src/main/assets/gpl-3.0.txt).
- **Scientific Engine Integrity**: All sensor acquisition, data buffers, mathematical analysis, graphing, Bluetooth Low Energy, audio, depth/LIDAR, and remote-access engine modules from upstream `phyphox` are preserved unmodified.

---

## 3. Summary of Modifications Made on 2026-11-02 by Salimprof (Salim El Mokhtar)

### A. Modified Upstream Files (Carry Explicit GPL v3 Section 5a Header Notices)
1. [`app/build.gradle`](app/build.gradle):
   - Set distinct `applicationId "com.salimprof.physiqueexplicitelab"` ( keeping `namespace "de.rwth_aachen.phyphox"` for engine compatibility).
   - Set `versionName "1.0.0-pe (base phyphox 1.2.1)"`.
   - Added `BuildConfig.PE_MODIFIED_SOURCE_URL` and `BuildConfig.PE_MODIFICATION_DATE` configured from [`gradle.properties`](gradle.properties).
   - Added `'ar'` (Arabic) to `defaultConfig.resConfigs`.
2. [`gradle.properties`](gradle.properties):
   - Added `PE_MODIFIED_SOURCE_URL` (configurable URL for the modified version's repository) and `PE_MODIFICATION_DATE=2026-11-02`.
3. [`app/src/main/AndroidManifest.xml`](app/src/main/AndroidManifest.xml):
   - Set `PEMainActivity` as the launcher activity while preserving `ExperimentListActivity` and `Experiment` intent filters.
   - Set `android:allowBackup="false"` and `android:supportsRtl="true"`.
   - Updated `FileProvider` authority to `${applicationId}.exportProvider`.
   - Added `android:maxSdkVersion="28"` to `READ_EXTERNAL_STORAGE` and removed unused `SCHEDULE_EXACT_ALARM` and `FLASHLIGHT` manifest declarations.
4. [`app/src/main/java/de/rwth_aachen/phyphox/Experiment.java`](app/src/main/java/de/rwth_aachen/phyphox/Experiment.java):
   - Updated export `FileProvider` authority reference from hardcoded string to `getPackageName() + ".exportProvider"`.
5. [`app/src/main/java/de/rwth_aachen/phyphox/ExperimentList/datasource/AssetExperimentLoader.java`](app/src/main/java/de/rwth_aachen/phyphox/ExperimentList/datasource/AssetExperimentLoader.java) & [`app/src/main/java/de/rwth_aachen/phyphox/ExperimentList/model/ExperimentShortInfo.java`](app/src/main/java/de/rwth_aachen/phyphox/ExperimentList/model/ExperimentShortInfo.java):
   - Added public accessors to allow `PEMainActivity` to list and launch bundled asset experiments and saved states without altering the sensor/measurement engine.
6. [`app/src/main/res/values*/strings.xml`](app/src/main/res/values/strings.xml), [`app/src/main/res/values/colors.xml`](app/src/main/res/values/colors.xml), [`app/src/main/res/values/styles.xml`](app/src/main/res/values/styles.xml), [`app/src/main/res/xml/locales_config.xml`](app/src/main/res/xml/locales_config.xml):
   - Updated `app_name` to `Physique Explicite Lab`, added custom palette colors and splash screen style, and enabled Arabic (`ar`) locale support.
7. [`app/src/main/java/de/rwth_aachen/phyphox/Analysis.java`](app/src/main/java/de/rwth_aachen/phyphox/Analysis.java) & [`app/src/main/java/de/rwth_aachen/phyphox/ExpView.java`](app/src/main/java/de/rwth_aachen/phyphox/ExpView.java):
   - Preserved 1-sample (`N = 1`) identity DFT output in `FFT` / `fftAM`, and fixed RTL layout alignment (`setPaddingRelative`, `TEXT_ALIGNMENT_VIEW_START/END`, `setHorizontallyScrolling(false)`) for Arabic RTL rendering.

### B. New Files Added for Physique Explicite Lab
- **Java classes (`app/src/main/java/de/rwth_aachen/phyphox/pelab/`)**:
  - `PEMainActivity.java`: 5-tab navigation (`Accueil`, `Expériences`, `J'expérimente`, `Données`, `Paramètres`), search, favorites filter, theme & language switcher (FR/AR/EN).
  - `PEPedagogyManager.java`: 9-step Moroccan middle-school (`1AC`, `2AC`, `3AC`) guided scientific inquiry module (`J'expérimente`) + interactive self-assessment Quiz with instant explanations.
  - `PEDialogs.java`: `"Edited by Salimprof"` modal, `"About the application"` modal with dedicated `"Source code of this modified version"` section, third-party licenses viewer, full offline GNU GPL v3 license viewer (`assets/gpl-3.0.txt`), and privacy policy viewer.
  - `PEFavoritesManager.java`: Local `SharedPreferences` favorites manager.
- **Layouts, Menus, Drawables & Strings**:
  - `res/layout/activity_pe_main.xml`, `item_pe_category_card.xml`, `item_pe_experiment_row.xml`, `dialog_salimprof.xml`, `dialog_pe_about.xml`, `dialog_pe_activity.xml`
  - `res/menu/pe_bottom_nav.xml`
  - `res/drawable/ic_pe_*.xml`, `pe_splash_window.xml`, launcher icons
  - `res/values/strings_pe.xml`, `res/values-en/strings_pe.xml`, `res/values-ar/strings_pe.xml`
  - `app/src/main/assets/gpl-3.0.txt`: Complete verbatim GNU GPL v3.0 license text for offline viewing inside the app.

---

## 4. Configuring the Public Source Code Repository URL (`PE_MODIFIED_SOURCE_URL`)
To comply with **GNU GPL v3 Section 6**, when distributing the APK or AAB publicly (e.g., on Google Play):
1. Publish this complete Git repository (including submodules `app/src/main/assets/experiments` and `app/src/main/assets/remote`) to a public repository (e.g., `https://github.com/<your-username>/PhysiqueExpliciteLab`).
2. Set `PE_MODIFIED_SOURCE_URL` in [`gradle.properties`](gradle.properties):
   ```properties
   PE_MODIFIED_SOURCE_URL=https://github.com/<your-username>/PhysiqueExpliciteLab
   ```
3. Rebuild the APK/AAB. Once `PE_MODIFIED_SOURCE_URL` is set to a valid `https://` URL, the **"Source code of this modified version"** section in the app's *About* dialog automatically displays the clickable repository link and button. When left empty, the app honestly informs the user that no online repository link is configured in the current build yet.

---

## 5. Reproducible Build Instructions (Complete Corresponding Source)
### Prerequisites
- **JDK**: Java 21 (e.g. Eclipse Temurin JDK 21)
- **Android SDK**:
  - `platforms;android-37.0` (`compileSdk 37`, `targetSdk 35`)
  - `build-tools;36.0.0`
  - `ndk;28.0.13004108`
  - `cmake;3.22.1`
- **Git Submodules**:
  - `app/src/main/assets/experiments` (commit `af5c1a52`)
  - `app/src/main/assets/remote` (commit `67c3ef32`)
  *(If cloning fresh, run: `git submodule update --init app/src/main/assets/experiments app/src/main/assets/remote`)*

### Build Commands (Windows PowerShell)
```powershell
# 1. Set JAVA_HOME and ANDROID_HOME
$env:JAVA_HOME = "C:\Path\To\jdk-21"
$env:ANDROID_HOME = "C:\Android\Sdk"

# 2. Run unit tests
.\gradlew.bat testRegularDebugUnitTest

# 3. Build Debug APK
.\gradlew.bat assembleRegularDebug
# Output: app\build\outputs\apk\regular\debug\app-regular-debug.apk

# 4. Build Release Android App Bundle (AAB) for Google Play
.\gradlew.bat bundleRegularRelease
# Output: app\build\outputs\bundle\regularRelease\app-regular-release.aab
```
