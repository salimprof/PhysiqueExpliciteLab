/*
 * Physique Explicite Lab ("Edited by Salimprof")
 * Copyright (C) 2026 Salimprof (Salim El Mokhtar) — Custom user interface & navigation
 * Based on phyphox (https://phyphox.org / https://github.com/phyphox/phyphox-android)
 * Copyright (C) 2017 RWTH Aachen University, 2nd Institute of Physics (Sebastian Staacks et al.)
 *
 * MODIFICATION NOTICE (GNU GPL v3 Section 5a):
 * Added on 2026-11-02 by Salimprof (Salim El Mokhtar) for "Physique Explicite Lab, Edited by Salimprof".
 * This is an independent customized version and is NOT an official RWTH Aachen University or phyphox product.
 * Distributed under the GNU General Public License v3.0 (see assets/gpl-3.0.txt).
 */
package de.rwth_aachen.phyphox.pelab;

import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_ISASSET;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_ISTEMP;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_UNAVAILABLESENSOR;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_XML;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.os.LocaleListCompat;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.rwth_aachen.phyphox.BuildConfig;
import de.rwth_aachen.phyphox.Experiment;
import de.rwth_aachen.phyphox.ExperimentList.ExperimentListActivity;
import de.rwth_aachen.phyphox.ExperimentList.datasource.AssetExperimentLoader;
import de.rwth_aachen.phyphox.ExperimentList.model.ExperimentListEnvironment;
import de.rwth_aachen.phyphox.ExperimentList.model.ExperimentLoadInfoData;
import de.rwth_aachen.phyphox.ExperimentList.model.ExperimentShortInfo;
import de.rwth_aachen.phyphox.R;
import de.rwth_aachen.phyphox.SettingsActivity.SettingsActivity;
import de.rwth_aachen.phyphox.SettingsActivity.SettingsFragment;

/**
 * Main launcher activity of Physique Explicite Lab (Edited by Salimprof).
 * Provides the custom scientific interface while preserving the real phyphox engine.
 */
public class PEMainActivity extends AppCompatActivity {

    public static final String CAT_ALL = "all";
    public static final String CAT_MOTION = "motion";
    public static final String CAT_ROTATION = "rotation";
    public static final String CAT_ACOUSTICS = "acoustics";
    public static final String CAT_MAGNETISM = "magnetism";
    public static final String CAT_LIGHT = "light";
    public static final String CAT_PRESSURE = "pressure";
    public static final String CAT_LOCATION = "location";
    public static final String CAT_CUSTOM = "custom";
    public static final String CAT_FAVORITES = "favorites";
    public static final String CAT_PEDAGOGY = "pedagogy";

    private static final String PREF_FIRST_LOCALE_DONE = "pe_first_locale_initialized";

    private View tabHome;
    private View tabExperiments;
    private View tabLab;
    private View tabData;
    private View tabSettings;
    private BottomNavigationView bottomNav;

    private LinearLayout categoriesContainer;
    private LinearLayout expListContainer;
    private LinearLayout labContainer;
    private LinearLayout dataContainer;
    private LinearLayout settingsContainer;

    private TextView expCategoryHeader;
    private TextView expCountLabel;
    private EditText homeSearchInput;
    private EditText expSearchInput;

    private final List<ExperimentShortInfo> allExperiments = new ArrayList<>();
    private String activeCategoryFilter = CAT_ALL;
    private String activeSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ensureDefaultFrenchLocaleOnce();
        applySavedTheme();
        setTheme(R.style.Theme_PELab);
        super.onCreate(savedInstanceState);

        // Forward deep-links / file intents (.phyphox, .zip, phyphox://) directly to ExperimentListActivity
        Intent incoming = getIntent();
        if (incoming != null && Intent.ACTION_VIEW.equals(incoming.getAction()) && incoming.getData() != null) {
            Intent forward = new Intent(incoming);
            forward.setClass(this, ExperimentListActivity.class);
            startActivity(forward);
        }

        setContentView(R.layout.activity_pe_main);

        tabHome = findViewById(R.id.pe_tab_home);
        tabExperiments = findViewById(R.id.pe_tab_experiments);
        tabLab = findViewById(R.id.pe_tab_lab);
        tabData = findViewById(R.id.pe_tab_data);
        tabSettings = findViewById(R.id.pe_tab_settings);
        bottomNav = findViewById(R.id.pe_bottom_nav);

        categoriesContainer = findViewById(R.id.pe_categories_container);
        expListContainer = findViewById(R.id.pe_exp_list_container);
        labContainer = findViewById(R.id.pe_lab_container);
        dataContainer = findViewById(R.id.pe_data_container);
        settingsContainer = findViewById(R.id.pe_settings_container);

        expCategoryHeader = findViewById(R.id.pe_exp_category_header);
        expCountLabel = findViewById(R.id.pe_exp_count_label);
        homeSearchInput = findViewById(R.id.pe_home_search);
        expSearchInput = findViewById(R.id.pe_exp_search);

        // Header actions
        findViewById(R.id.pe_header_signature).setOnClickListener(v -> PEDialogs.showSalimprofDialog(this));
        findViewById(R.id.pe_header_logo).setOnClickListener(v -> PEDialogs.showSalimprofDialog(this));
        findViewById(R.id.pe_header_settings_btn).setOnClickListener(v -> bottomNav.setSelectedItemId(R.id.pe_nav_settings));

        // Experiments tab header actions
        findViewById(R.id.pe_exp_show_all_btn).setOnClickListener(v -> {
            activeCategoryFilter = CAT_ALL;
            activeSearchQuery = "";
            expSearchInput.setText("");
            expCategoryHeader.setText(R.string.pe_exp_title);
            renderExperimentsList();
        });

        findViewById(R.id.pe_exp_classic_btn).setOnClickListener(v -> {
            startActivity(new Intent(this, ExperimentListActivity.class));
        });

        setupSearchBars();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllExperiments();
        buildHomeCategories();
        renderExperimentsList();
        PEPedagogyManager.populateLabTab(this, labContainer);
        populateDataTab();
        populateSettingsTab();
    }

    private void ensureDefaultFrenchLocaleOnce() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean(PREF_FIRST_LOCALE_DONE, false)) {
            prefs.edit().putBoolean(PREF_FIRST_LOCALE_DONE, true).apply();
            if (AppCompatDelegate.getApplicationLocales().isEmpty()) {
                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"));
            }
        }
    }

    private void applySavedTheme() {
        String themePref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.setting_dark_mode_key), SettingsFragment.DARK_MODE_SYSTEM);
        SettingsFragment.setApplicationTheme(themePref);
    }

    private void setupSearchBars() {
        homeSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            String q = homeSearchInput.getText().toString().trim();
            activeCategoryFilter = CAT_ALL;
            activeSearchQuery = q;
            expSearchInput.setText(q);
            expCategoryHeader.setText(R.string.pe_exp_title);
            bottomNav.setSelectedItemId(R.id.pe_nav_experiments);
            return true;
        });

        homeSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String q = s.toString().trim();
                if (q.length() >= 2 && tabHome.getVisibility() == View.VISIBLE) {
                    activeCategoryFilter = CAT_ALL;
                    activeSearchQuery = q;
                    expSearchInput.setText(q);
                    expSearchInput.setSelection(q.length());
                    expCategoryHeader.setText(R.string.pe_exp_title);
                    bottomNav.setSelectedItemId(R.id.pe_nav_experiments);
                    expSearchInput.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        expSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activeSearchQuery = s.toString().trim();
                renderExperimentsList();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            tabHome.setVisibility(id == R.id.pe_nav_home ? View.VISIBLE : View.GONE);
            tabExperiments.setVisibility(id == R.id.pe_nav_experiments ? View.VISIBLE : View.GONE);
            tabLab.setVisibility(id == R.id.pe_nav_lab ? View.VISIBLE : View.GONE);
            tabData.setVisibility(id == R.id.pe_nav_data ? View.VISIBLE : View.GONE);
            tabSettings.setVisibility(id == R.id.pe_nav_settings ? View.VISIBLE : View.GONE);
            return true;
        });
    }

    private void loadAllExperiments() {
        allExperiments.clear();
        ExperimentListEnvironment env = new ExperimentListEnvironment(this);

        // 1. Local files (saved states & custom experiments)
        try {
            File[] files = env.getFilesDir().listFiles((dir, name) -> name.endsWith(".phyphox"));
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) continue;
                    try (InputStream in = openFileInput(f.getName())) {
                        ExperimentLoadInfoData data = new ExperimentLoadInfoData(in, f.getName(), null, false);
                        ExperimentShortInfo info = AssetExperimentLoader.loadExperimentShortInfo(data, env);
                        if (info != null && info.title != null) {
                            allExperiments.add(info);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // 2. Bundled asset experiments
        try {
            String[] assets = env.assetManager.list("experiments");
            if (assets != null) {
                for (String xml : assets) {
                    if (!xml.endsWith(".phyphox")) continue;
                    // Exclude phyphox.org promotional links from the student experiment list
                    if ("support.phyphox".equals(xml) || "sensordb.phyphox".equals(xml)) continue;
                    try (InputStream in = env.assetManager.open("experiments/" + xml)) {
                        ExperimentLoadInfoData data = new ExperimentLoadInfoData(in, xml, null, true);
                        ExperimentShortInfo info = AssetExperimentLoader.loadExperimentShortInfo(data, env);
                        if (info != null && info.title != null) {
                            allExperiments.add(info);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception ignored) {
        }

        Collections.sort(allExperiments, (a, b) -> {
            String ta = a.title != null ? a.title : "";
            String tb = b.title != null ? b.title : "";
            return ta.compareToIgnoreCase(tb);
        });
    }

    private boolean matchesCategory(@NonNull ExperimentShortInfo exp, @NonNull String cat) {
        Set<String> s = exp.sensorTypes;
        String xml = exp.xmlFile != null ? exp.xmlFile.toLowerCase(Locale.ROOT) : "";
        switch (cat) {
            case CAT_ALL:
                return true;
            case CAT_MOTION:
                return s.contains("accelerometer") || s.contains("linear_acceleration") || s.contains("gravity")
                        || xml.contains("accel") || xml.contains("spring") || xml.contains("collision");
            case CAT_ROTATION:
                return s.contains("gyroscope") || s.contains("attitude") || s.contains("rotation_vector")
                        || xml.contains("gyro") || xml.contains("inclination") || xml.contains("pendulum") || xml.contains("roll");
            case CAT_ACOUSTICS:
                return s.contains("audio") || xml.contains("audio") || xml.contains("acoustic")
                        || xml.contains("sonar") || xml.contains("doppler") || xml.contains("tone") || xml.contains("applause");
            case CAT_MAGNETISM:
                return s.contains("magnetic_field") || xml.contains("mag");
            case CAT_LIGHT:
                return s.contains("light") || s.contains("camera") || s.contains("proximity")
                        || xml.contains("light") || xml.contains("camera") || xml.contains("optical");
            case CAT_PRESSURE:
                return s.contains("pressure") || xml.contains("pressure") || xml.contains("elevator");
            case CAT_LOCATION:
                return s.contains("location") || xml.contains("gps");
            case CAT_CUSTOM:
                return !exp.isAsset;
            case CAT_FAVORITES:
                return PEFavoritesManager.isFavorite(this, exp.xmlFile);
            default:
                return true;
        }
    }

    private int countForCategory(String cat) {
        if (CAT_PEDAGOGY.equals(cat)) {
            return PEPedagogyManager.getActivities().size();
        }
        int c = 0;
        for (ExperimentShortInfo e : allExperiments) {
            if (matchesCategory(e, cat)) c++;
        }
        return c;
    }

    private void buildHomeCategories() {
        categoriesContainer.removeAllViews();
        Object[][] cats = new Object[][]{
                {CAT_MOTION, R.drawable.ic_pe_cat_motion, R.string.pe_cat_motion_title, R.string.pe_cat_motion_desc},
                {CAT_ROTATION, R.drawable.ic_pe_cat_rotation, R.string.pe_cat_rotation_title, R.string.pe_cat_rotation_desc},
                {CAT_ACOUSTICS, R.drawable.ic_pe_cat_acoustics, R.string.pe_cat_acoustics_title, R.string.pe_cat_acoustics_desc},
                {CAT_MAGNETISM, R.drawable.ic_pe_cat_magnetism, R.string.pe_cat_magnetism_title, R.string.pe_cat_magnetism_desc},
                {CAT_LIGHT, R.drawable.ic_pe_cat_light, R.string.pe_cat_light_title, R.string.pe_cat_light_desc},
                {CAT_PRESSURE, R.drawable.ic_pe_cat_pressure, R.string.pe_cat_pressure_title, R.string.pe_cat_pressure_desc},
                {CAT_LOCATION, R.drawable.ic_pe_cat_location, R.string.pe_cat_location_title, R.string.pe_cat_location_desc},
                {CAT_CUSTOM, R.drawable.ic_pe_cat_custom, R.string.pe_cat_custom_title, R.string.pe_cat_custom_desc},
                {CAT_FAVORITES, R.drawable.ic_pe_cat_favorites, R.string.pe_cat_favorites_title, R.string.pe_cat_favorites_desc},
                {CAT_PEDAGOGY, R.drawable.ic_pe_cat_pedagogy, R.string.pe_cat_pedagogy_title, R.string.pe_cat_pedagogy_desc}
        };

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Object[] row : cats) {
            final String key = (String) row[0];
            final int iconRes = (Integer) row[1];
            final int titleRes = (Integer) row[2];
            final int descRes = (Integer) row[3];

            View card = inflater.inflate(R.layout.item_pe_category_card, categoriesContainer, false);
            ImageView icon = card.findViewById(R.id.pe_cat_icon);
            TextView tvTitle = card.findViewById(R.id.pe_cat_title);
            TextView tvDesc = card.findViewById(R.id.pe_cat_desc);
            TextView tvBadge = card.findViewById(R.id.pe_cat_badge);

            icon.setImageResource(iconRes);
            tvTitle.setText(titleRes);
            tvDesc.setText(descRes);
            tvBadge.setText(String.valueOf(countForCategory(key)));

            card.setContentDescription(getString(R.string.pe_cat_open_card, getString(titleRes), getString(descRes)));
            card.setOnClickListener(v -> {
                if (CAT_PEDAGOGY.equals(key)) {
                    bottomNav.setSelectedItemId(R.id.pe_nav_lab);
                } else {
                    activeCategoryFilter = key;
                    activeSearchQuery = "";
                    expSearchInput.setText("");
                    expCategoryHeader.setText(titleRes);
                    renderExperimentsList();
                    bottomNav.setSelectedItemId(R.id.pe_nav_experiments);
                }
            });

            categoriesContainer.addView(card);
        }
    }

    private void renderExperimentsList() {
        expListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        String qLower = activeSearchQuery.toLowerCase(Locale.ROOT);

        List<ExperimentShortInfo> filtered = new ArrayList<>();
        for (ExperimentShortInfo exp : allExperiments) {
            if (!matchesCategory(exp, activeCategoryFilter)) continue;
            if (!qLower.isEmpty()) {
                String t = exp.title != null ? exp.title.toLowerCase(Locale.ROOT) : "";
                String d = exp.description != null ? exp.description.toLowerCase(Locale.ROOT) : "";
                if (!t.contains(qLower) && !d.contains(qLower)) continue;
            }
            filtered.add(exp);
        }

        expCountLabel.setText(getString(R.string.pe_exp_count, filtered.size()));

        if (filtered.isEmpty()) {
            TextView empty = new TextView(this);
            int msgRes = R.string.pe_exp_empty;
            if (CAT_FAVORITES.equals(activeCategoryFilter)) msgRes = R.string.pe_exp_empty_favorites;
            else if (CAT_CUSTOM.equals(activeCategoryFilter)) msgRes = R.string.pe_exp_empty_custom;
            empty.setText(msgRes);
            empty.setTextSize(15f);
            empty.setTextColor(ContextCompat.getColor(this, R.color.pe_on_bg_variant));
            int pad = (int) (16 * getResources().getDisplayMetrics().density);
            empty.setPadding(pad, pad, pad, pad);
            expListContainer.addView(empty);
            return;
        }

        for (final ExperimentShortInfo exp : filtered) {
            expListContainer.addView(createExperimentRowView(inflater, expListContainer, exp));
        }
    }

    private View createExperimentRowView(LayoutInflater inflater, ViewGroup parent, final ExperimentShortInfo exp) {
        View row = inflater.inflate(R.layout.item_pe_experiment_row, parent, false);
        ImageView icon = row.findViewById(R.id.pe_exp_icon);
        TextView title = row.findViewById(R.id.pe_exp_row_title);
        TextView desc = row.findViewById(R.id.pe_exp_row_desc);
        TextView warning = row.findViewById(R.id.pe_exp_row_warning);
        final ImageButton favBtn = row.findViewById(R.id.pe_exp_fav_btn);

        if (exp.icon != null) {
            icon.setImageDrawable(exp.icon);
        } else {
            icon.setImageResource(R.drawable.ic_pe_atom);
        }
        title.setText(exp.title);
        desc.setText(exp.description);

        if (exp.unavailableSensor > 0) {
            warning.setVisibility(View.VISIBLE);
            warning.setText(getString(R.string.pe_sensor_missing, getString(exp.unavailableSensor)));
        } else {
            warning.setVisibility(View.GONE);
        }

        updateStarIcon(favBtn, PEFavoritesManager.isFavorite(this, exp.xmlFile));
        favBtn.setOnClickListener(v -> {
            boolean isFav = PEFavoritesManager.toggleFavorite(this, exp.xmlFile);
            updateStarIcon(favBtn, isFav);
            buildHomeCategories();
            if (CAT_FAVORITES.equals(activeCategoryFilter)) {
                renderExperimentsList();
            }
        });

        row.setOnClickListener(v -> launchExperiment(exp));
        return row;
    }

    private void updateStarIcon(ImageButton btn, boolean isFav) {
        btn.setImageResource(isFav ? R.drawable.ic_pe_cat_favorites : R.drawable.ic_pe_star_outline);
        btn.setContentDescription(getString(isFav ? R.string.pe_fav_remove : R.string.pe_fav_add));
    }

    private void launchExperiment(ExperimentShortInfo exp) {
        if (exp.isLink != null) {
            PEDialogs.openUrlSafely(this, exp.isLink);
            return;
        }
        Intent intent = new Intent(this, Experiment.class);
        intent.putExtra(EXPERIMENT_XML, exp.xmlFile);
        intent.putExtra(EXPERIMENT_ISTEMP, exp.isTemp);
        intent.putExtra(EXPERIMENT_ISASSET, exp.isAsset);
        intent.putExtra(EXPERIMENT_UNAVAILABLESENSOR, exp.unavailableSensor);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    private void populateDataTab() {
        dataContainer.removeAllViews();
        float d = getResources().getDisplayMetrics().density;

        TextView title = new TextView(this);
        title.setText(R.string.pe_data_title);
        title.setTextSize(22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(this, R.color.pe_on_bg));
        dataContainer.addView(title);

        TextView intro = new TextView(this);
        intro.setText(R.string.pe_data_intro);
        intro.setTextSize(14f);
        intro.setTextColor(ContextCompat.getColor(this, R.color.pe_on_bg_variant));
        intro.setPadding(0, (int) (4 * d), 0, (int) (14 * d));
        dataContainer.addView(intro);

        List<ExperimentShortInfo> localExps = new ArrayList<>();
        for (ExperimentShortInfo e : allExperiments) {
            if (!e.isAsset) localExps.add(e);
        }

        if (localExps.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(R.string.pe_data_empty);
            empty.setTextSize(15f);
            empty.setTextColor(ContextCompat.getColor(this, R.color.pe_on_bg_variant));
            empty.setPadding(0, (int) (8 * d), 0, (int) (16 * d));
            dataContainer.addView(empty);
        } else {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (ExperimentShortInfo exp : localExps) {
                dataContainer.addView(createExperimentRowView(inflater, dataContainer, exp));
            }
        }

        MaterialButton manageBtn = new MaterialButton(this);
        manageBtn.setText(R.string.pe_exp_add);
        manageBtn.setOnClickListener(v -> startActivity(new Intent(this, ExperimentListActivity.class)));
        dataContainer.addView(manageBtn);
    }

    private void populateSettingsTab() {
        settingsContainer.removeAllViews();
        float d = getResources().getDisplayMetrics().density;

        TextView title = new TextView(this);
        title.setText(R.string.pe_set_title);
        title.setTextSize(22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(this, R.color.pe_on_bg));
        settingsContainer.addView(title);

        // 1. Appearance section
        addSectionTitle(settingsContainer, getString(R.string.pe_set_appearance));
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_cat_light,
                getString(R.string.pe_set_appearance),
                getString(R.string.pe_set_theme_light) + " / " + getString(R.string.pe_set_theme_dark) + " / " + getString(R.string.pe_set_theme_system),
                this::showThemeChooserDialog
        );

        // 2. Language section (French default, Arabic RTL, English)
        addSectionTitle(settingsContainer, getString(R.string.pe_set_language));
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_info,
                getString(R.string.pe_set_language),
                getString(R.string.pe_set_lang_fr) + " • " + getString(R.string.pe_set_lang_ar) + " • " + getString(R.string.pe_set_lang_en),
                this::showLanguageChooserDialog
        );

        // 3. Experiments section
        addSectionTitle(settingsContainer, getString(R.string.pe_set_experiments));
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_nav_settings,
                getString(R.string.pe_set_engine),
                getString(R.string.pe_set_engine_desc),
                () -> startActivity(new Intent(this, SettingsActivity.class))
        );
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_nav_data,
                getString(R.string.pe_set_data),
                getString(R.string.pe_set_data_desc),
                () -> bottomNav.setSelectedItemId(R.id.pe_nav_data)
        );

        // 4. Information & Legal section
        addSectionTitle(settingsContainer, getString(R.string.pe_set_info));
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_cat_favorites,
                getString(R.string.pe_signature),
                getString(R.string.pe_dlg_person) + " — " + getString(R.string.pe_dlg_role),
                () -> PEDialogs.showSalimprofDialog(this)
        );
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_info,
                getString(R.string.pe_set_about),
                getString(R.string.pe_set_version) + " : " + BuildConfig.VERSION_NAME,
                () -> PEDialogs.showAboutDialog(this)
        );
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_nav_experiments,
                getString(R.string.pe_set_licenses),
                "GNU GPL v3 — phyphox (RWTH Aachen)",
                () -> PEDialogs.showLicensesDialog(this)
        );
        addSettingsItem(
                settingsContainer,
                R.drawable.ic_pe_cat_location,
                getString(R.string.pe_set_privacy),
                getString(R.string.pe_data_intro),
                () -> PEDialogs.showPrivacyDialog(this)
        );
    }

    private void addSectionTitle(LinearLayout parent, String text) {
        float d = getResources().getDisplayMetrics().density;
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(15f);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(ContextCompat.getColor(this, R.color.pe_accent));
        tv.setPadding(0, (int) (14 * d), 0, (int) (6 * d));
        parent.addView(tv);
    }

    private void addSettingsItem(
            LinearLayout parent,
            int iconRes,
            String title,
            String subtitle,
            Runnable onClick
    ) {
        View card = LayoutInflater.from(this).inflate(R.layout.item_pe_category_card, parent, false);
        ImageView icon = card.findViewById(R.id.pe_cat_icon);
        TextView tvTitle = card.findViewById(R.id.pe_cat_title);
        TextView tvDesc = card.findViewById(R.id.pe_cat_desc);
        TextView tvBadge = card.findViewById(R.id.pe_cat_badge);

        icon.setImageResource(iconRes);
        tvTitle.setText(title);
        tvDesc.setText(subtitle);
        tvBadge.setVisibility(View.GONE);
        card.setOnClickListener(v -> onClick.run());
        parent.addView(card);
    }

    private void showThemeChooserDialog() {
        final String[] labels = new String[]{
                getString(R.string.pe_set_theme_light),
                getString(R.string.pe_set_theme_dark),
                getString(R.string.pe_set_theme_system)
        };
        final String[] values = new String[]{
                SettingsFragment.DARK_MODE_OFF,
                SettingsFragment.DARK_MODE_ON,
                SettingsFragment.DARK_MODE_SYSTEM
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.pe_set_appearance)
                .setItems(labels, (d, which) -> {
                    String chosen = values[which];
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putString(getString(R.string.setting_dark_mode_key), chosen)
                            .apply();
                    SettingsFragment.setApplicationTheme(chosen);
                })
                .show();
    }

    private void showLanguageChooserDialog() {
        final String[] labels = new String[]{
                getString(R.string.pe_set_lang_fr),
                getString(R.string.pe_set_lang_ar),
                getString(R.string.pe_set_lang_en),
                getString(R.string.pe_set_lang_system)
        };
        final String[] tags = new String[]{"fr", "ar", "en", "*"};

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.pe_set_language)
                .setItems(labels, (d, which) -> {
                    String tag = tags[which];
                    if ("*".equals(tag)) {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList());
                    } else {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag));
                    }
                })
                .show();
    }
}
