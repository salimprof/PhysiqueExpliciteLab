package de.rwth_aachen.phyphox.pelab;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Automated verification of the Physique Explicite Lab pedagogical module ("J'expérimente")
 * and interactive Quiz definitions.
 */
public class PELabVerificationTest {

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
            File assetFile = new File("src/main/assets/experiments/" + act.assetExperimentXml);
            if (!assetFile.exists()) {
                assetFile = new File("app/src/main/assets/experiments/" + act.assetExperimentXml);
            }
            assertTrue("Real sensor experiment file must exist: " + act.assetExperimentXml, assetFile.exists());
        }
    }
}
