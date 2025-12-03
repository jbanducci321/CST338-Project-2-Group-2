package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.QuestionsActivity;
import com.example.TriviaBattler.MainActivity;
import com.example.TriviaBattler.AddQuestionsAdminActivity;
import com.example.TriviaBattler.Statistics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class IntentFactoryTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void testMainActivityIntentFactory() {
        // Testing Main Activity's Intent Factory
        int userId = 123;
        Intent i = MainActivity.mainActivityIntentFactory(context, userId);

        // Shouldn't be null, Class should match, extras should be correct
        assertNotNull(i);
        assertEquals(MainActivity.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.MAIN_ACTIVITY_USER_ID", -1));
    }

    @Test
    public void testLoginActivityIntentFactory() {
        // Testing Login Activity's Intent Factory
        Intent i = LoginActivity.loginIntentFactory(context);

        // Shouldn't be null, Class should match
        assertNotNull(i);
        assertEquals(LoginActivity.class.getName(), i.getComponent().getClassName());
    }

    @Test
    public void testCreateAccountActivityIntentFactory() {
        // Testing Create Account Activity's Intent Factory
        Intent i = CreateAccountActivity.createAccountIntentFactory(context);

        // Shouldn't be null, Class should match
        assertNotNull(i);
        assertEquals(CreateAccountActivity.class.getName(), i.getComponent().getClassName());
    }

    @Test
    public void testQuestionsIntentFactory() {
        //  Testing Question Activity's Intent Factory
        int userId = 88;
        String diff = "easy";
        Intent i = QuestionsActivity.questionsIntentFactory(context, userId, diff);

        // Shouldn't be null, Class should match, extras should be correct
        assertNotNull(i);
        assertEquals(QuestionsActivity.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("USER_ID", -1));
        assertEquals(diff, i.getStringExtra("EXTRA_DIFFICULTY"));
    }

    @Test
    public void testStatisticsIntentFactory() {
        // Testing Stats Activity's Intent Factory
        int userId = 42;
        Intent i = Statistics.statsIntentFactory(context, userId);

        // Shouldn't be null, Class should match, extras should be correct
        assertNotNull(i);
        assertEquals(Statistics.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.STATS_ACTIVITY_USER_ID", -1));
    }

    @Test
    public void testAddAdminIntentFactory() {
        // Testing Add Admin Activity's Intent Factory
        int userId = 44;
        boolean isAdmin = true;
        Intent i = AddAdmin.addAdminIntentFactory(context, userId, isAdmin);

        // Shouldn't be null, Class should match, Extras should be correct
        assertNotNull(i);
        assertEquals(AddAdmin.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.ADD_ADMIN_ACTIVITY_USER_ID", -1));
        assertEquals(isAdmin, i.getBooleanExtra("com.example.labandroiddemo.MODIFY_ADMIN_ACTIVITY_USER_ID", false));
    }

    @Test
    public void testAddQuestionsAdminIntentFactory() {
        // Testing Add Questions Activity's Intent Factory
        int userId = 14;
        Intent i = AddQuestionsAdminActivity.addQuestionIntentFactory(context, userId);

        // Shouldn't be null, Class should match
        assertNotNull(i);
        assertEquals(AddQuestionsAdminActivity.class.getName(), i.getComponent().getClassName());
    }

    @Test
    public void testAdminLandingIntentFactory() {
        // Testing Admin Landing Activity's Intent Factory
        int userId = 52;
        Intent i = AdminLanding.adminLandingIntentFactory(context, userId);

        // Shouldn't be null, Class should match, Extras should be correct
        assertNotNull(i);
        assertEquals(AdminLanding.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.ADMIN_ACTIVITY_USER_ID", -1));
    }

    @Test
    public void testAdminStatsIntentFactory() {
        // Testing Admin Stats Activity's Intent Factory
        int userId = 53;
        Intent i = AdminStats.adminStatsIntentFactory(context, userId);

        // Shouldn't be null, Class should match, Extras should be correct
        assertNotNull(i);
        assertEquals(AdminStats.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.ADMIN_STATS_ACTIVITY_USER_ID", -1));
    }
}