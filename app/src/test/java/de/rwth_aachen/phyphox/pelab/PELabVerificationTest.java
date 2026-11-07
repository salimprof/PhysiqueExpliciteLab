package de.rwth_aachen.phyphox.pelab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import de.rwth_aachen.phyphox.BuildConfig;

/**
 * Automated verification of:
 * 1. The Physique Explicite Lab pedagogical module ("J'expérimente") & interactive Quiz definitions.
 * 2. GNU GPL v3 compliance artifacts (bundled full GPL v3 text, original repo URL, modification date,
 *    configured modified source URL, and Section 5a modification headers).
 * 3. Repository consistency (.gitmodules, submodules presence, bilingual README.md, PRIVACY_POLICY.md).
 */
public class PELabVerificationTest {

    private static File resolveAppFile(String relativeToApp) {
        File f = new File(relativeToApp);
        if (!f.exists()) {
            f = new File("app/" + relativeToApp);
        }
        return f;
    }

    private static File resolveRepoRootFile(String relativeToRoot) {
        File f = new File("../" + relativeToRoot);
        if (!f.exists()) {
            f = new File(relativeToRoot);
        }
        return f;
    }

    @Test
    public void testPilotActivitiesStructureAndQuiz() {
        List<PEPedagogyManager.PedagogicalActivity> activities = PEPedagogyManager.getActivities();
        assertEquals("Three pilot activities must be present", 3, activities.size());

        for (PEPedagogyManager.PedagogicalActivity act : activities) {
            assertNotNull(act.id);
            assertFalse(act.id.isEmpty());
            assertNotNull(act.assetExperimentXml);
            assertTrue(act.assetExperimentXml.endsWith(".phyphox"));

            // Verify the 8 text steps + 9th quiz step
            assertEquals("Each activity must define 8 text steps + step 9 quiz", 8, act.stepTextRes.length);
            assertNotNull(act.quizQuestions);
            assertFalse(act.quizQuestions.isEmpty());

            for (PEPedagogyManager.QuizQuestion q : act.quizQuestions) {
                assertTrue("Correct answer index must be 0, 1, or 2 (3 choices)", q.correctIndex >= 0 && q.correctIndex < 3);
                assertTrue(q.questionRes != 0);
                assertTrue(q.optionARes != 0);
                assertTrue(q.optionBRes != 0);
                assertTrue(q.optionCRes != 0);
                assertTrue(q.explanationRes != 0);
            }

            // Verify that the linked real .phyphox experiment file exists in assets/experiments
            File assetFile = resolveAppFile("src/main/assets/experiments/" + act.assetExperimentXml);
            assertTrue("Real sensor experiment file must exist: " + act.assetExperimentXml, assetFile.exists());
        }
    }

    @Test
    public void testGplV3ComplianceRequirements() throws Exception {
        // 1. Original phyphox GitHub link preserved
        assertEquals("https://github.com/phyphox/phyphox-android", PEDialogs.PHYPHOX_REPO_URL);

        // 2. Modification date and configurable modified source URL present in BuildConfig
        assertEquals("2026-11-02", BuildConfig.PE_MODIFICATION_DATE);
        assertEquals("https://github.com/salimprof/PhysiqueExpliciteLab", BuildConfig.PE_MODIFIED_SOURCE_URL);

        // 3. Full GNU GPL v3 license text bundled offline in assets/gpl-3.0.txt
        File gplAsset = resolveAppFile("src/main/assets/gpl-3.0.txt");
        assertTrue("Full GNU GPL v3 text must be bundled in assets/gpl-3.0.txt", gplAsset.exists());
        String gplContent = new String(Files.readAllBytes(gplAsset.toPath()), StandardCharsets.UTF_8);
        assertTrue(gplContent.contains("GNU GENERAL PUBLIC LICENSE"));
        assertTrue(gplContent.contains("Version 3, 29 June 2007"));
        assertTrue("GPL v3 text must be complete (>34,000 chars)", gplContent.length() > 34000);

        // 4. Modified upstream files carry prominent GPL v3 Section 5a modification notices
        String[] modifiedFiles = new String[]{
                "src/main/java/de/rwth_aachen/phyphox/Experiment.java",
                "src/main/java/de/rwth_aachen/phyphox/ExperimentList/datasource/AssetExperimentLoader.java",
                "src/main/java/de/rwth_aachen/phyphox/ExperimentList/model/ExperimentShortInfo.java",
                "src/main/java/de/rwth_aachen/phyphox/pelab/PEDialogs.java",
                "src/main/java/de/rwth_aachen/phyphox/pelab/PEMainActivity.java"
        };
        for (String relPath : modifiedFiles) {
            File f = resolveAppFile(relPath);
            assertTrue("File must exist: " + relPath, f.exists());
            String src = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            assertTrue("File must identify Salimprof: " + relPath, src.contains("Salimprof"));
            assertTrue("File must carry modification date 2026-11-02: " + relPath, src.contains("2026-11-02"));
        }
    }

    @Test
    public void testRepositoryDocumentationAndSubmodulesConsistency() throws Exception {
        // 1. Submodules (.gitmodules + populated experiments & remote directories)
        File gitmodules = resolveRepoRootFile(".gitmodules");
        assertTrue(".gitmodules must exist", gitmodules.exists());
        String gmText = new String(Files.readAllBytes(gitmodules.toPath()), StandardCharsets.UTF_8);
        assertTrue(gmText.contains("app/src/main/assets/experiments"));
        assertTrue(gmText.contains("app/src/main/assets/remote"));
        assertFalse("Orphaned paho.mqtt.android submodule must be removed from .gitmodules",
                gmText.contains("paho.mqtt.android"));

        File remoteIndex = resolveAppFile("src/main/assets/remote/index.html");
        assertTrue("Remote interface submodule (index.html) must be populated", remoteIndex.exists());

        // 2. Bilingual README.md
        File readme = resolveRepoRootFile("README.md");
        assertTrue("README.md must exist", readme.exists());
        String readmeText = new String(Files.readAllBytes(readme.toPath()), StandardCharsets.UTF_8);
        assertTrue(readmeText.contains("Physique Explicite Lab"));
        assertTrue(readmeText.contains("Edited by Salimprof"));
        assertTrue(readmeText.contains("Version Française"));
        assertTrue(readmeText.contains("English Version"));
        assertTrue(readmeText.contains("https://github.com/phyphox/phyphox-android"));
        assertTrue(readmeText.contains("https://github.com/salimprof/PhysiqueExpliciteLab"));

        // 3. Privacy Policy & Notice consistency
        File privacy = resolveRepoRootFile("PRIVACY_POLICY.md");
        assertTrue("PRIVACY_POLICY.md must exist", privacy.exists());
        String privacyText = new String(Files.readAllBytes(privacy.toPath()), StandardCharsets.UTF_8);
        assertTrue(privacyText.contains("com.salimprof.physiqueexplicitelab"));
        assertTrue(privacyText.contains("1020105"));

        // 4. Translatable string resources for "9 étapes" (FR, EN, AR) and no hardcoded "9 étapes" in PEPedagogyManager
        File pedagogySrc = resolveAppFile("src/main/java/de/rwth_aachen/phyphox/pelab/PEPedagogyManager.java");
        String pedagogyCode = new String(Files.readAllBytes(pedagogySrc.toPath()), StandardCharsets.UTF_8);
        assertFalse("PEPedagogyManager.java must not hardcode \"9 étapes\"", pedagogyCode.contains("\"9 étapes\""));
        assertTrue("PEPedagogyManager.java must reference R.string.pe_lab_steps_badge", pedagogyCode.contains("R.string.pe_lab_steps_badge"));

        for (String resPath : new String[]{
                "src/main/res/values/strings_pe.xml",
                "src/main/res/values-en/strings_pe.xml",
                "src/main/res/values-ar/strings_pe.xml"
        }) {
            File resFile = resolveAppFile(resPath);
            assertTrue("Resource file must exist: " + resPath, resFile.exists());
            String xml = new String(Files.readAllBytes(resFile.toPath()), StandardCharsets.UTF_8);
            assertTrue(resPath + " must define pe_lab_steps_badge", xml.contains("name=\"pe_lab_steps_badge\""));
        }
    }
}
