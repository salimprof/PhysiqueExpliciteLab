/*
 * Physique Explicite Lab ("Edited by Salimprof")
 * Copyright (C) 2026 Salimprof (Salim El Mokhtar) — Custom user interface, pedagogical module & legal notices
 * Based on phyphox (https://phyphox.org / https://github.com/phyphox/phyphox-android)
 * Copyright (C) 2017 RWTH Aachen University, 2nd Institute of Physics (Sebastian Staacks et al.)
 *
 * MODIFICATION NOTICE (GNU GPL v3 Section 5a):
 * Added on 2026-11-02 by Salimprof (Salim El Mokhtar) for "Physique Explicite Lab, Edited by Salimprof".
 * This is an independent customized version and is NOT an official RWTH Aachen University or phyphox product.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program (see assets/gpl-3.0.txt). If not, see <https://www.gnu.org/licenses/>.
 */
package de.rwth_aachen.phyphox.pelab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.rwth_aachen.phyphox.BuildConfig;
import de.rwth_aachen.phyphox.R;

/**
 * Custom modal windows for Physique Explicite Lab:
 * - "Edited by Salimprof" signature window
 * - "About the app" & GPL v3 compliance notices window
 * - Original copyright & third-party open-source licences window
 * - Full offline GNU GPL v3 license text viewer (from assets/gpl-3.0.txt)
 * - Privacy policy window
 */
public final class PEDialogs {

    public static final String PHYPHOX_REPO_URL = "https://github.com/phyphox/phyphox-android";
    public static final String GPL_URL = "https://www.gnu.org/licenses/gpl-3.0.html";
    private static final String GPL_ASSET_FILE = "gpl-3.0.txt";

    private PEDialogs() {
    }

    /**
     * Opens the mandatory "Edited by Salimprof" modal window with a subtle fade/scale animation.
     */
    public static void showSalimprofDialog(final Activity activity) {
        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_salimprof, null, false);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(content)
                .setCancelable(true)
                .create();

        View closeBtn = content.findViewById(R.id.pe_dlg_close_btn);
        closeBtn.setOnClickListener(v -> dialog.dismiss());

        View creditsBtn = content.findViewById(R.id.pe_dlg_credits_btn);
        creditsBtn.setOnClickListener(v -> {
            dialog.dismiss();
            showAboutDialog(activity);
        });

        content.setAlpha(0f);
        content.setScaleX(0.95f);
        content.setScaleY(0.95f);
        dialog.setOnShowListener(d -> content.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(180L)
                .setInterpolator(new DecelerateInterpolator())
                .start());

        dialog.show();
    }

    /**
     * Opens the "About the application" and GPL v3 legal notices modal window.
     */
    public static void showAboutDialog(final Activity activity) {
        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_pe_about, null, false);

        TextView versionTv = content.findViewById(R.id.pe_about_version_tv);
        versionTv.setText(activity.getString(R.string.pe_set_version) + " : " + BuildConfig.VERSION_NAME
                + " (mod. " + BuildConfig.PE_MODIFICATION_DATE + ")");

        TextView modSourceTv = content.findViewById(R.id.pe_about_modified_source_tv);
        View modRepoBtn = content.findViewById(R.id.pe_about_modified_repo_btn);

        final String configuredUrl = BuildConfig.PE_MODIFIED_SOURCE_URL != null
                ? BuildConfig.PE_MODIFIED_SOURCE_URL.trim()
                : "";

        if (configuredUrl.startsWith("https://")) {
            modSourceTv.setText(activity.getString(R.string.pe_about_modified_source_configured, configuredUrl));
            modRepoBtn.setVisibility(View.VISIBLE);
            modRepoBtn.setOnClickListener(v -> openUrlSafely(activity, configuredUrl));
        } else {
            modSourceTv.setText(R.string.pe_about_modified_source_unconfigured);
            modRepoBtn.setVisibility(View.GONE);
        }

        final AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(content)
                .setCancelable(true)
                .create();

        content.findViewById(R.id.pe_about_repo_btn).setOnClickListener(v -> openUrlSafely(activity, PHYPHOX_REPO_URL));

        content.findViewById(R.id.pe_about_full_gpl_btn).setOnClickListener(v -> {
            dialog.dismiss();
            showFullGplLicenseDialog(activity);
        });

        content.findViewById(R.id.pe_about_engine_credits_btn).setOnClickListener(v -> {
            dialog.dismiss();
            showLicensesDialog(activity);
        });

        content.findViewById(R.id.pe_about_close_btn).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Opens the Open Source Licences dialog preserving ALL original copyright notices
     * and ALL third-party component license notices.
     */
    public static void showLicensesDialog(final Activity activity) {
        String apachePlain = HtmlCompat.fromHtml(
                activity.getString(R.string.creditsApache),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString().trim();

        String zxingPlain = HtmlCompat.fromHtml(
                activity.getString(R.string.creditsZxing),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString().trim();

        String pahoPlain = HtmlCompat.fromHtml(
                activity.getString(R.string.creditsPahoMQTT),
                HtmlCompat.FROM_HTML_MODE_LEGACY
        ).toString().trim();

        String additionalThirdParty =
                "• AndroidSVG (com.caverock:androidsvg-aar:1.4)\n"
                + "  Copyright © 2013–2019 Paul LeBeau / CaveRock Software Ltd.\n"
                + "  Licensed under the Apache License, Version 2.0.\n\n"
                + "• Apache Commons IO (org.apache.commons:commons-io:1.3.2)\n"
                + "  Copyright © 2002–2007 The Apache Software Foundation.\n"
                + "  Licensed under the Apache License, Version 2.0.\n\n"
                + "• AndroidX Libraries & Material Components for Android (com.google.android.material / androidx.*)\n"
                + "  Copyright © The Android Open Source Project / Google LLC.\n"
                + "  Licensed under the Apache License, Version 2.0 (https://www.apache.org/licenses/LICENSE-2.0).\n\n"
                + "• Web Remote Interface Assets (Chart.js MIT License, Normalize.css MIT License, Open Sans OFL/Apache 2.0)\n"
                + "  Copyright © respective authors in app/src/main/assets/remote.";

        String text = "=== MODIFICATION & CUSTOMIZATION NOTICE (GNU GPL v3 § 5a) ===\n"
                + activity.getString(R.string.pe_about_customization)
                + "\n\n=== ORIGINAL PHYPHOX COPYRIGHT & AUTHORS ===\n"
                + activity.getString(R.string.creditsRWTH)
                + "\n\n" + activity.getString(R.string.creditsNames)
                + "\n\n=== GNU GENERAL PUBLIC LICENSE v3 NOTICE ===\n"
                + activity.getString(R.string.gpl)
                + "\n\n=== THIRD-PARTY LIBRARIES & LICENSES ===\n"
                + activity.getString(R.string.creditsLibs)
                + "\n\n--- Apache License 2.0 Notice (Chart.js / AndroidSVG / Commons IO / AndroidX) ---\n"
                + apachePlain
                + "\n\n--- ZXing License Notice ---\n"
                + zxingPlain
                + "\n\n--- Eclipse Paho MQTT Notice ---\n"
                + pahoPlain
                + "\n\n--- Additional Bundled Libraries ---\n"
                + additionalThirdParty
                + "\n\nOriginal phyphox repository: " + PHYPHOX_REPO_URL
                + "\nGNU GPL v3 URL: " + GPL_URL;

        showScrollableTextDialog(
                activity,
                activity.getString(R.string.pe_licenses_title),
                text,
                false,
                activity.getString(R.string.pe_about_full_license),
                () -> showFullGplLicenseDialog(activity)
        );
    }

    /**
     * Displays the complete, verbatim text of the GNU General Public License v3
     * bundled offline inside assets/gpl-3.0.txt.
     */
    public static void showFullGplLicenseDialog(final Activity activity) {
        String fullGplText = loadAssetText(activity, GPL_ASSET_FILE);
        showScrollableTextDialog(
                activity,
                "GNU General Public License v3.0",
                fullGplText,
                true,
                "gnu.org (Web)",
                () -> openUrlSafely(activity, GPL_URL)
        );
    }

    /**
     * Opens the Privacy Policy dialog adapted to actual offline/local operation.
     */
    public static void showPrivacyDialog(final Activity activity) {
        showScrollableTextDialog(
                activity,
                activity.getString(R.string.pe_privacy_title),
                activity.getString(R.string.pe_privacy_text),
                false,
                null,
                null
        );
    }

    private static String loadAssetText(Activity activity, String assetName) {
        StringBuilder sb = new StringBuilder();
        try (InputStream is = activity.getAssets().open(assetName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (Exception e) {
            return activity.getString(R.string.gpl) + "\n\n" + GPL_URL;
        }
        return sb.toString();
    }

    private static void showScrollableTextDialog(
            final Activity activity,
            String title,
            String body,
            boolean monospace,
            String neutralText,
            final Runnable neutralAction
    ) {
        int pad = (int) (20 * activity.getResources().getDisplayMetrics().density);
        ScrollView scroll = new ScrollView(activity);
        TextView tv = new TextView(activity);
        tv.setPadding(pad, pad / 2, pad, pad / 2);
        tv.setText(body);
        tv.setTextSize(monospace ? 12f : 14f);
        if (monospace) {
            tv.setTypeface(Typeface.MONOSPACE);
        }
        tv.setTextIsSelectable(true);
        scroll.addView(tv);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                .setTitle(title)
                .setView(scroll)
                .setPositiveButton(R.string.pe_close, null);

        if (neutralText != null && neutralAction != null) {
            builder.setNeutralButton(neutralText, (d, w) -> neutralAction.run());
        }
        builder.show();
    }

    public static void openUrlSafely(Activity activity, String url) {
        try {
            Uri uri = Uri.parse(url);
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                return;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            activity.startActivity(intent);
        } catch (Exception ignored) {
        }
    }
}
