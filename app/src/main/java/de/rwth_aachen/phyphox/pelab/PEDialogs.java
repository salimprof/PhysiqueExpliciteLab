package de.rwth_aachen.phyphox.pelab;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.rwth_aachen.phyphox.BuildConfig;
import de.rwth_aachen.phyphox.R;

/**
 * Custom modal windows for Physique Explicite Lab:
 * - "Edited by Salimprof" signature window
 * - "About the app" & legal notices window
 * - GPL v3 licence & Privacy policy windows
 */
public final class PEDialogs {

    public static final String PHYPHOX_REPO_URL = "https://github.com/phyphox/phyphox-android";
    public static final String GPL_URL = "https://www.gnu.org/licenses/gpl-3.0.html";

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
     * Opens the "About the application" and legal notices modal window.
     */
    public static void showAboutDialog(final Activity activity) {
        View content = LayoutInflater.from(activity).inflate(R.layout.dialog_pe_about, null, false);

        TextView versionTv = content.findViewById(R.id.pe_about_version_tv);
        versionTv.setText(activity.getString(R.string.pe_set_version) + " : " + BuildConfig.VERSION_NAME);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(content)
                .setCancelable(true)
                .create();

        content.findViewById(R.id.pe_about_repo_btn).setOnClickListener(v -> openUrlSafely(activity, PHYPHOX_REPO_URL));

        content.findViewById(R.id.pe_about_engine_credits_btn).setOnClickListener(v -> {
            dialog.dismiss();
            showLicensesDialog(activity);
        });

        content.findViewById(R.id.pe_about_close_btn).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    /**
     * Opens the Open Source Licences (GNU GPL v3 + component notices) dialog.
     */
    public static void showLicensesDialog(final Activity activity) {
        String text = activity.getString(R.string.pe_about_license)
                + "\n\n" + activity.getString(R.string.creditsRWTH)
                + "\n\n" + activity.getString(R.string.creditsNames)
                + "\n\n" + activity.getString(R.string.creditsLibs)
                + "\n\n" + PHYPHOX_REPO_URL
                + "\n" + GPL_URL;

        showScrollableTextDialog(
                activity,
                activity.getString(R.string.pe_licenses_title),
                text,
                activity.getString(R.string.pe_about_full_license),
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
                null,
                null
        );
    }

    private static void showScrollableTextDialog(
            final Activity activity,
            String title,
            String body,
            String neutralText,
            final Runnable neutralAction
    ) {
        int pad = (int) (20 * activity.getResources().getDisplayMetrics().density);
        ScrollView scroll = new ScrollView(activity);
        TextView tv = new TextView(activity);
        tv.setPadding(pad, pad / 2, pad, pad / 2);
        tv.setText(body);
        tv.setTextSize(14f);
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
