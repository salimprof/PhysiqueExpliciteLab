package de.rwth_aachen.phyphox.pelab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.rwth_aachen.phyphox.Experiment;
import de.rwth_aachen.phyphox.R;

/**
 * End-to-end Robolectric verification of Physique Explicite Lab's custom UI and pedagogical flows:
 * - 5 navigation tabs (Accueil, Expériences, J'expérimente, Données, Paramètres)
 * - 3 pedagogical activities in "J'expérimente", step 4 measurement launch & missing-sensor dialog
 * - Interactive quiz submission, correction feedback, score calculation & reset
 * - Favorites persistence (PEFavoritesManager)
 * - French, English, and Arabic (with RTL layout direction) localization
 * - Credits, source links & offline GNU GPL v3 license dialog
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35)
public class PELabUiAndFlowTest {

    @Test
    public void testFiveTabsNavigationAndBranding() {
        try (ActivityController<PEMainActivity> controller = Robolectric.buildActivity(PEMainActivity.class).setup()) {
            PEMainActivity activity = controller.get();

            // Verify branding and signature in top header
            TextView signature = activity.findViewById(R.id.pe_header_signature);
            assertNotNull(signature);
            assertEquals("Edited by Salimprof", signature.getText().toString());

            BottomNavigationView nav = activity.findViewById(R.id.pe_bottom_nav);
            View tabHome = activity.findViewById(R.id.pe_tab_home);
            View tabExp = activity.findViewById(R.id.pe_tab_experiments);
            View tabLab = activity.findViewById(R.id.pe_tab_lab);
            View tabData = activity.findViewById(R.id.pe_tab_data);
            View tabSettings = activity.findViewById(R.id.pe_tab_settings);

            // Initial state: Home tab visible
            assertEquals(View.VISIBLE, tabHome.getVisibility());
            assertEquals(View.GONE, tabExp.getVisibility());

            // Switch to Tab 2: Expériences
            nav.setSelectedItemId(R.id.pe_nav_experiments);
            assertEquals(View.VISIBLE, tabExp.getVisibility());
            assertEquals(View.GONE, tabHome.getVisibility());

            // Switch to Tab 3: J'expérimente
            nav.setSelectedItemId(R.id.pe_nav_lab);
            assertEquals(View.VISIBLE, tabLab.getVisibility());
            assertEquals(View.GONE, tabExp.getVisibility());

            // Switch to Tab 4: Données
            nav.setSelectedItemId(R.id.pe_nav_data);
            assertEquals(View.VISIBLE, tabData.getVisibility());
            assertEquals(View.GONE, tabLab.getVisibility());

            // Switch to Tab 5: Paramètres
            nav.setSelectedItemId(R.id.pe_nav_settings);
            assertEquals(View.VISIBLE, tabSettings.getVisibility());
            assertEquals(View.GONE, tabData.getVisibility());

            // Switch back to Tab 1: Accueil
            nav.setSelectedItemId(R.id.pe_nav_home);
            assertEquals(View.VISIBLE, tabHome.getVisibility());
        }
    }

    @Test
    public void testFavoritesPersistence() {
        Context context = RuntimeEnvironment.getApplication();
        String sampleXml = "accelerometer.phyphox";

        assertFalse(PEFavoritesManager.isFavorite(context, sampleXml));
        assertTrue(PEFavoritesManager.toggleFavorite(context, sampleXml));
        assertTrue(PEFavoritesManager.isFavorite(context, sampleXml));
        assertTrue(PEFavoritesManager.getFavorites(context).contains(sampleXml));

        assertFalse(PEFavoritesManager.toggleFavorite(context, sampleXml));
        assertFalse(PEFavoritesManager.isFavorite(context, sampleXml));
    }

    @Test
    public void testThreeActivitiesQuizCorrectionAndSensorHandling() {
        try (ActivityController<PEMainActivity> controller = Robolectric.buildActivity(PEMainActivity.class).setup()) {
            PEMainActivity activity = controller.get();
            List<PEPedagogyManager.PedagogicalActivity> activities = PEPedagogyManager.getActivities();
            assertEquals(3, activities.size());

            for (PEPedagogyManager.PedagogicalActivity act : activities) {
                PEPedagogyManager.showActivityDialog(activity, act);
                AlertDialog dialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
                assertNotNull("Activity dialog should be shown for " + act.id, dialog);

                LinearLayout root = dialog.findViewById(R.id.pe_act_dialog_root);
                assertNotNull(root);

                // Find all RadioGroups (quiz questions) and MaterialButtons inside the dialog
                List<RadioGroup> groups = new ArrayList<>();
                List<MaterialButton> buttons = new ArrayList<>();
                collectViews(root, groups, buttons);

                assertEquals("Quiz must render all questions for " + act.id,
                        act.quizQuestions.size(), groups.size());
                // Expect: 1 button at step 4 ("Lancer la mesure réelle"), 1 "Vérifier mes réponses", 1 "Recommencer le quiz"
                assertTrue("Dialog must include Step 4 measurement button and Quiz action buttons",
                        buttons.size() >= 3);

                MaterialButton checkBtn = buttons.get(buttons.size() - 2);
                MaterialButton retryBtn = buttons.get(buttons.size() - 1);

                // Select all correct answers and verify score = N / N
                for (int i = 0; i < groups.size(); i++) {
                    RadioGroup rg = groups.get(i);
                    int correctIdx = act.quizQuestions.get(i).correctIndex;
                    RadioButton rb = (RadioButton) rg.getChildAt(correctIdx);
                    rb.setChecked(true);
                }
                checkBtn.performClick();

                String expectedScore = activity.getString(R.string.pe_quiz_score,
                        act.quizQuestions.size(), act.quizQuestions.size());
                assertTrue("Dialog must display full score after selecting correct answers",
                        containsText(root, expectedScore));

                // Click retry and verify selections are cleared
                retryBtn.performClick();
                for (RadioGroup rg : groups) {
                    assertEquals(-1, rg.getCheckedRadioButtonId());
                }

                dialog.dismiss();
            }
        }
    }

    @Test
    public void testTrilingualResourcesAndArabicRtlDirection() {
        Context base = RuntimeEnvironment.getApplication();

        // 1. French (default)
        Configuration frConfig = new Configuration(base.getResources().getConfiguration());
        frConfig.setLocale(Locale.FRENCH);
        Context frCtx = base.createConfigurationContext(frConfig);
        assertEquals("Accueil", frCtx.getString(R.string.pe_nav_home));
        assertEquals("9 étapes", frCtx.getString(R.string.pe_lab_steps_badge));
        assertEquals(View.LAYOUT_DIRECTION_LTR, frConfig.getLayoutDirection());

        // 2. English
        Configuration enConfig = new Configuration(base.getResources().getConfiguration());
        enConfig.setLocale(Locale.ENGLISH);
        Context enCtx = base.createConfigurationContext(enConfig);
        assertEquals("Home", enCtx.getString(R.string.pe_nav_home));
        assertEquals("9 steps", enCtx.getString(R.string.pe_lab_steps_badge));
        assertEquals(View.LAYOUT_DIRECTION_LTR, enConfig.getLayoutDirection());

        // 3. Arabic + RTL layout direction
        Configuration arConfig = new Configuration(base.getResources().getConfiguration());
        arConfig.setLocale(new Locale("ar"));
        Context arCtx = base.createConfigurationContext(arConfig);
        assertEquals("الرئيسية", arCtx.getString(R.string.pe_nav_home));
        assertEquals("9 مراحل", arCtx.getString(R.string.pe_lab_steps_badge));
        assertEquals(View.LAYOUT_DIRECTION_RTL, arConfig.getLayoutDirection());
    }

    @Test
    public void testLegalDialogsAndSensorUnavailableDialog() {
        try (ActivityController<PEMainActivity> controller = Robolectric.buildActivity(PEMainActivity.class).setup()) {
            PEMainActivity activity = controller.get();

            // 1. "Edited by Salimprof" dialog
            PEDialogs.showSalimprofDialog(activity);
            AlertDialog salimDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
            assertNotNull(salimDialog);
            salimDialog.dismiss();

            // 2. "À propos / Crédits GPL v3" dialog with source links
            PEDialogs.showAboutDialog(activity);
            AlertDialog aboutDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
            assertNotNull(aboutDialog);
            View modBtn = aboutDialog.findViewById(R.id.pe_about_modified_repo_btn);
            View origBtn = aboutDialog.findViewById(R.id.pe_about_repo_btn);
            assertNotNull(modBtn);
            assertNotNull(origBtn);
            assertEquals(View.VISIBLE, modBtn.getVisibility());
            origBtn.performClick();
            Intent started = Shadows.shadowOf(activity).getNextStartedActivity();
            assertNotNull(started);
            assertEquals(PEDialogs.PHYPHOX_REPO_URL, started.getDataString());
            aboutDialog.dismiss();

            // 3. Offline GNU GPL v3 full text dialog
            PEDialogs.showFullGplLicenseDialog(activity);
            AlertDialog gplDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
            assertNotNull(gplDialog);
            gplDialog.dismiss();

            // 4. Sensor unavailable warning dialog
            PEDialogs.showSensorUnavailableDialog(activity, R.string.sensorAccelerometer);
            AlertDialog sensorDialog = (AlertDialog) ShadowAlertDialog.getLatestDialog();
            assertNotNull(sensorDialog);
            sensorDialog.dismiss();
        }
    }

    private static void collectViews(View v, List<RadioGroup> groups, List<MaterialButton> buttons) {
        if (v instanceof RadioGroup) {
            groups.add((RadioGroup) v);
            return;
        }
        if (v instanceof MaterialButton) {
            buttons.add((MaterialButton) v);
            return;
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                collectViews(vg.getChildAt(i), groups, buttons);
            }
        }
    }

    private static boolean containsText(View v, String needle) {
        if (v instanceof TextView) {
            CharSequence cs = ((TextView) v).getText();
            if (cs != null && cs.toString().contains(needle)) {
                return true;
            }
        }
        if (v instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                if (containsText(vg.getChildAt(i), needle)) {
                    return true;
                }
            }
        }
        return false;
    }
}
