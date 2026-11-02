package de.rwth_aachen.phyphox.pelab;

import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_ISASSET;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_UNAVAILABLESENSOR;
import static de.rwth_aachen.phyphox.ExperimentList.model.Const.EXPERIMENT_XML;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.rwth_aachen.phyphox.Experiment;
import de.rwth_aachen.phyphox.R;

/**
 * Extensible pedagogical module ("J'expérimente") and interactive Quiz engine
 * designed for Moroccan middle-school physics & chemistry (1AC, 2AC, 3AC).
 * Each activity has 9 structured pedagogical steps and launches the real sensor engine.
 */
public final class PEPedagogyManager {

    public static final class QuizQuestion {
        public final int questionRes;
        public final int optionARes;
        public final int optionBRes;
        public final int optionCRes;
        public final int correctIndex; // 0 = A, 1 = B, 2 = C
        public final int explanationRes;

        public QuizQuestion(int questionRes, int optionARes, int optionBRes, int optionCRes, int correctIndex, int explanationRes) {
            this.questionRes = questionRes;
            this.optionARes = optionARes;
            this.optionBRes = optionBRes;
            this.optionCRes = optionCRes;
            this.correctIndex = correctIndex;
            this.explanationRes = explanationRes;
        }
    }

    public static final class PedagogicalActivity {
        public final String id;
        public final int iconRes;
        public final int titleRes;
        public final int metaRes;
        public final int safetyRes;
        public final String assetExperimentXml;
        public final int[] stepTextRes; // 8 steps (1..8); step 9 is the interactive quiz
        public final List<QuizQuestion> quizQuestions;

        public PedagogicalActivity(
                String id,
                int iconRes,
                int titleRes,
                int metaRes,
                int safetyRes,
                String assetExperimentXml,
                int[] stepTextRes,
                List<QuizQuestion> quizQuestions
        ) {
            this.id = id;
            this.iconRes = iconRes;
            this.titleRes = titleRes;
            this.metaRes = metaRes;
            this.safetyRes = safetyRes;
            this.assetExperimentXml = assetExperimentXml;
            this.stepTextRes = stepTextRes;
            this.quizQuestions = quizQuestions;
        }
    }

    private PEPedagogyManager() {
    }

    public static List<PedagogicalActivity> getActivities() {
        List<PedagogicalActivity> list = new ArrayList<>();

        // Pilot Activity 1: Accelerometer
        list.add(new PedagogicalActivity(
                "act1_acceleration",
                R.drawable.ic_pe_cat_motion,
                R.string.pe_act1_title,
                R.string.pe_act1_meta,
                R.string.pe_act1_safety,
                "accelerometer.phyphox",
                new int[]{
                        R.string.pe_act1_s1,
                        R.string.pe_act1_s2,
                        R.string.pe_act1_s3,
                        R.string.pe_act1_s4,
                        R.string.pe_act1_s5,
                        R.string.pe_act1_s6,
                        R.string.pe_act1_s7,
                        R.string.pe_act1_s8
                },
                Arrays.asList(
                        new QuizQuestion(
                                R.string.pe_act1_q1,
                                R.string.pe_act1_q1_a,
                                R.string.pe_act1_q1_b,
                                R.string.pe_act1_q1_c,
                                0,
                                R.string.pe_act1_q1_exp
                        ),
                        new QuizQuestion(
                                R.string.pe_act1_q2,
                                R.string.pe_act1_q2_a,
                                R.string.pe_act1_q2_b,
                                R.string.pe_act1_q2_c,
                                0,
                                R.string.pe_act1_q2_exp
                        )
                )
        ));

        // Pilot Activity 2: Magnetometer
        list.add(new PedagogicalActivity(
                "act2_magnetic_field",
                R.drawable.ic_pe_cat_magnetism,
                R.string.pe_act2_title,
                R.string.pe_act2_meta,
                R.string.pe_act2_safety,
                "magnetometer.phyphox",
                new int[]{
                        R.string.pe_act2_s1,
                        R.string.pe_act2_s2,
                        R.string.pe_act2_s3,
                        R.string.pe_act2_s4,
                        R.string.pe_act2_s5,
                        R.string.pe_act2_s6,
                        R.string.pe_act2_s7,
                        R.string.pe_act2_s8
                },
                Arrays.asList(
                        new QuizQuestion(
                                R.string.pe_act2_q1,
                                R.string.pe_act2_q1_a,
                                R.string.pe_act2_q1_b,
                                R.string.pe_act2_q1_c,
                                0,
                                R.string.pe_act2_q1_exp
                        ),
                        new QuizQuestion(
                                R.string.pe_act2_q2,
                                R.string.pe_act2_q2_a,
                                R.string.pe_act2_q2_b,
                                R.string.pe_act2_q2_c,
                                0,
                                R.string.pe_act2_q2_exp
                        )
                )
        ));

        // Pilot Activity 3: Sound frequency
        list.add(new PedagogicalActivity(
                "act3_sound_frequency",
                R.drawable.ic_pe_cat_acoustics,
                R.string.pe_act3_title,
                R.string.pe_act3_meta,
                R.string.pe_act3_safety,
                "audio_autocorrelation.phyphox",
                new int[]{
                        R.string.pe_act3_s1,
                        R.string.pe_act3_s2,
                        R.string.pe_act3_s3,
                        R.string.pe_act3_s4,
                        R.string.pe_act3_s5,
                        R.string.pe_act3_s6,
                        R.string.pe_act3_s7,
                        R.string.pe_act3_s8
                },
                Arrays.asList(
                        new QuizQuestion(
                                R.string.pe_act3_q1,
                                R.string.pe_act3_q1_a,
                                R.string.pe_act3_q1_b,
                                R.string.pe_act3_q1_c,
                                0,
                                R.string.pe_act3_q1_exp
                        ),
                        new QuizQuestion(
                                R.string.pe_act3_q2,
                                R.string.pe_act3_q2_a,
                                R.string.pe_act3_q2_b,
                                R.string.pe_act3_q2_c,
                                0,
                                R.string.pe_act3_q2_exp
                        )
                )
        ));

        return list;
    }

    public static void populateLabTab(final Activity activity, LinearLayout container) {
        container.removeAllViews();
        float d = activity.getResources().getDisplayMetrics().density;

        TextView title = new TextView(activity);
        title.setText(R.string.pe_lab_title);
        title.setTextSize(22f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg));
        container.addView(title);

        TextView badge = new TextView(activity);
        badge.setText(R.string.pe_lab_levels_badge);
        badge.setTextSize(13f);
        badge.setTypeface(Typeface.DEFAULT_BOLD);
        badge.setTextColor(ContextCompat.getColor(activity, R.color.pe_orange));
        badge.setPadding(0, (int) (4 * d), 0, (int) (4 * d));
        container.addView(badge);

        TextView intro = new TextView(activity);
        intro.setText(R.string.pe_lab_intro);
        intro.setTextSize(14f);
        intro.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg_variant));
        intro.setPadding(0, 0, 0, (int) (14 * d));
        container.addView(intro);

        LayoutInflater inflater = LayoutInflater.from(activity);
        for (final PedagogicalActivity act : getActivities()) {
            View card = inflater.inflate(R.layout.item_pe_category_card, container, false);
            ImageView icon = card.findViewById(R.id.pe_cat_icon);
            TextView tvTitle = card.findViewById(R.id.pe_cat_title);
            TextView tvDesc = card.findViewById(R.id.pe_cat_desc);
            TextView tvBadge = card.findViewById(R.id.pe_cat_badge);

            icon.setImageResource(act.iconRes);
            tvTitle.setText(act.titleRes);
            tvDesc.setText(act.metaRes);
            tvBadge.setText("9 étapes");

            card.setOnClickListener(v -> showActivityDialog(activity, act));
            container.addView(card);
        }
    }

    public static void showActivityDialog(final Activity activity, final PedagogicalActivity act) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_pe_activity, null, false);
        LinearLayout root = view.findViewById(R.id.pe_act_dialog_root);
        float d = activity.getResources().getDisplayMetrics().density;

        // Title & metadata
        TextView title = new TextView(activity);
        title.setText(act.titleRes);
        title.setTextSize(19f);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg));
        root.addView(title);

        TextView meta = new TextView(activity);
        meta.setText(act.metaRes);
        meta.setTextSize(13f);
        meta.setTextColor(ContextCompat.getColor(activity, R.color.pe_accent));
        meta.setPadding(0, (int) (4 * d), 0, (int) (10 * d));
        root.addView(meta);

        // Safety warning card
        MaterialCardView safetyCard = new MaterialCardView(activity);
        safetyCard.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.pe_night));
        safetyCard.setStrokeColor(ContextCompat.getColor(activity, R.color.pe_orange));
        safetyCard.setStrokeWidth((int) (1.5f * d));
        safetyCard.setRadius(12 * d);
        LinearLayout safetyBox = new LinearLayout(activity);
        safetyBox.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (12 * d);
        safetyBox.setPadding(pad, pad, pad, pad);

        TextView safetyTitle = new TextView(activity);
        safetyTitle.setText("⚠ " + activity.getString(R.string.pe_lab_safety_title));
        safetyTitle.setTextColor(ContextCompat.getColor(activity, R.color.pe_orange));
        safetyTitle.setTypeface(Typeface.DEFAULT_BOLD);
        safetyTitle.setTextSize(13f);
        safetyBox.addView(safetyTitle);

        TextView safetyBody = new TextView(activity);
        safetyBody.setText(act.safetyRes);
        safetyBody.setTextColor(ContextCompat.getColor(activity, R.color.pe_white));
        safetyBody.setTextSize(13f);
        safetyBody.setPadding(0, (int) (4 * d), 0, 0);
        safetyBox.addView(safetyBody);

        safetyCard.addView(safetyBox);
        root.addView(safetyCard);

        // 8 Pedagogical sections
        int[] stepTitles = new int[]{
                R.string.pe_step_1,
                R.string.pe_step_2,
                R.string.pe_step_3,
                R.string.pe_step_4,
                R.string.pe_step_5,
                R.string.pe_step_6,
                R.string.pe_step_7,
                R.string.pe_step_8
        };

        for (int i = 0; i < stepTitles.length; i++) {
            TextView st = new TextView(activity);
            st.setText(stepTitles[i]);
            st.setTextSize(15f);
            st.setTypeface(Typeface.DEFAULT_BOLD);
            st.setTextColor(ContextCompat.getColor(activity, R.color.pe_accent));
            st.setPadding(0, (int) (14 * d), 0, (int) (4 * d));
            root.addView(st);

            TextView sb = new TextView(activity);
            sb.setText(act.stepTextRes[i]);
            sb.setTextSize(14f);
            sb.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg));
            root.addView(sb);

            // Right after step 4 ("Je réalise"), insert the button that launches the real experiment
            if (i == 3) {
                MaterialButton launchBtn = new MaterialButton(activity);
                launchBtn.setText(R.string.pe_lab_open_sensor);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                lp.topMargin = (int) (10 * d);
                launchBtn.setLayoutParams(lp);
                launchBtn.setOnClickListener(v -> launchRealAssetExperiment(activity, act.assetExperimentXml));
                root.addView(launchBtn);
            }
        }

        // Section 9: Interactive Quiz ("9. Je vérifie mes acquis")
        TextView quizHeader = new TextView(activity);
        quizHeader.setText(R.string.pe_step_9);
        quizHeader.setTextSize(16f);
        quizHeader.setTypeface(Typeface.DEFAULT_BOLD);
        quizHeader.setTextColor(ContextCompat.getColor(activity, R.color.pe_orange));
        quizHeader.setPadding(0, (int) (18 * d), 0, (int) (8 * d));
        root.addView(quizHeader);

        buildInteractiveQuiz(activity, root, act.quizQuestions);

        final AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(view)
                .setPositiveButton(R.string.pe_close, null)
                .create();
        dialog.show();
    }

    private static void buildInteractiveQuiz(
            final Activity activity,
            LinearLayout parent,
            final List<QuizQuestion> questions
    ) {
        final float d = activity.getResources().getDisplayMetrics().density;
        final List<RadioGroup> groups = new ArrayList<>();
        final List<TextView> feedbacks = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            QuizQuestion q = questions.get(i);

            TextView qTv = new TextView(activity);
            qTv.setText((i + 1) + ". " + activity.getString(q.questionRes));
            qTv.setTextSize(14f);
            qTv.setTypeface(Typeface.DEFAULT_BOLD);
            qTv.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg));
            qTv.setPadding(0, (int) (8 * d), 0, (int) (4 * d));
            parent.addView(qTv);

            RadioGroup rg = new RadioGroup(activity);
            rg.setOrientation(RadioGroup.VERTICAL);
            int[] opts = new int[]{q.optionARes, q.optionBRes, q.optionCRes};
            for (int j = 0; j < opts.length; j++) {
                RadioButton rb = new RadioButton(activity);
                rb.setId(View.generateViewId());
                rb.setTag(j);
                rb.setText(opts[j]);
                rb.setTextColor(ContextCompat.getColor(activity, R.color.pe_on_bg));
                rg.addView(rb);
            }
            groups.add(rg);
            parent.addView(rg);

            TextView fb = new TextView(activity);
            fb.setTextSize(13f);
            fb.setVisibility(View.GONE);
            fb.setPadding(0, (int) (4 * d), 0, (int) (8 * d));
            feedbacks.add(fb);
            parent.addView(fb);
        }

        final TextView scoreTv = new TextView(activity);
        scoreTv.setTextSize(16f);
        scoreTv.setTypeface(Typeface.DEFAULT_BOLD);
        scoreTv.setVisibility(View.GONE);
        scoreTv.setPadding(0, (int) (8 * d), 0, (int) (8 * d));
        parent.addView(scoreTv);

        LinearLayout btnRow = new LinearLayout(activity);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        MaterialButton validateBtn = new MaterialButton(activity);
        validateBtn.setText(R.string.pe_quiz_validate);

        MaterialButton retryBtn = new MaterialButton(activity, null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
        retryBtn.setText(R.string.pe_quiz_retry);
        LinearLayout.LayoutParams retryLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        retryLp.setMarginStart((int) (8 * d));
        retryBtn.setLayoutParams(retryLp);

        btnRow.addView(validateBtn);
        btnRow.addView(retryBtn);
        parent.addView(btnRow);

        validateBtn.setOnClickListener(v -> {
            for (RadioGroup rg : groups) {
                if (rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(activity, R.string.pe_quiz_select_all, Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            int score = 0;
            for (int i = 0; i < questions.size(); i++) {
                QuizQuestion q = questions.get(i);
                RadioGroup rg = groups.get(i);
                RadioButton checked = rg.findViewById(rg.getCheckedRadioButtonId());
                int chosen = (Integer) checked.getTag();
                TextView fb = feedbacks.get(i);
                fb.setVisibility(View.VISIBLE);

                if (chosen == q.correctIndex) {
                    score++;
                    fb.setText(activity.getString(R.string.pe_quiz_correct) + " " + activity.getString(q.explanationRes));
                    fb.setTextColor(ContextCompat.getColor(activity, R.color.pe_green));
                    fb.setScaleX(0.92f);
                    fb.setScaleY(0.92f);
                    fb.animate().scaleX(1f).scaleY(1f).setDuration(200L).setInterpolator(new OvershootInterpolator()).start();
                } else {
                    fb.setText(activity.getString(R.string.pe_quiz_wrong) + " " + activity.getString(q.explanationRes));
                    fb.setTextColor(ContextCompat.getColor(activity, R.color.pe_orange));
                }
            }
            scoreTv.setVisibility(View.VISIBLE);
            scoreTv.setText(activity.getString(R.string.pe_quiz_score, score, questions.size()));
            scoreTv.setTextColor(ContextCompat.getColor(activity, R.color.pe_accent));
        });

        retryBtn.setOnClickListener(v -> {
            for (int i = 0; i < groups.size(); i++) {
                groups.get(i).clearCheck();
                feedbacks.get(i).setVisibility(View.GONE);
            }
            scoreTv.setVisibility(View.GONE);
        });
    }

    public static void launchRealAssetExperiment(Activity activity, String assetXml) {
        Intent intent = new Intent(activity, Experiment.class);
        intent.putExtra(EXPERIMENT_XML, assetXml);
        intent.putExtra(EXPERIMENT_ISASSET, true);
        intent.putExtra(EXPERIMENT_UNAVAILABLESENSOR, -1);
        intent.setAction(Intent.ACTION_VIEW);
        activity.startActivity(intent);
    }
}
