package com.ubertapp.UITests;

import android.content.ComponentName;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.ubertapp.Activities.ActivityLogin;
import com.ubertapp.Activities.ActivityOpeningPage;
import com.ubertapp.Activities.ActivityRegistration;
import com.ubertapp.Activities.ActivitySelection;
import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Models.User;
import com.ubertapp.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Adam on 11/11/2016.
 */

public class ActivityLoginTests {

    private ElasticSearchController elasticSearchController = ElasticSearchController.getInstance();
    private String testUserName = "TestyMcTesterton";
    private User testUser = new User(testUserName, "fTest", "lTest", "5555555555", "Test@Test.com");

    @Rule
    public IntentsTestRule<ActivityLogin> OPActivityRule = new IntentsTestRule<ActivityLogin>(
            ActivityLogin.class);

    @Test
    //Note, this actually logs in. It makes a user, then logs in
    public void TestLogin() {
        onView(withText("Login")).check(ViewAssertions.matches(isDisplayed()));

        elasticSearchController.addUser(testUser);

        onView(withId(R.id.username_edittext)).perform(typeText(testUserName));
        onView(withText(testUserName)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Login")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivitySelection.class)));
    }
}
