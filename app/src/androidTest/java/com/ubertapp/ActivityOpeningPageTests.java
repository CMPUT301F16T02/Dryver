package com.ubertapp;

/**
 * Created by Adam on 11/8/2016.
 */

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.ComponentName;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ubertapp.Activities.ActivityLogin;
import com.ubertapp.Activities.ActivityOpeningPage;
import com.ubertapp.Activities.ActivityRegistration;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ActivityOpeningPageTests{
    @Rule
    public IntentsTestRule<ActivityOpeningPage> OPActivityRule = new IntentsTestRule<ActivityOpeningPage>(
            ActivityOpeningPage.class);

    @Test
    public void TestSelectRegistration() {
        onView(withText("New user?")).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Get Started")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRegistration.class)));
    }



    @Test
    public void TestSelectLogin() {
        onView(withText("Existing user?")).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Sign In")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityLogin.class)));
    }
}
