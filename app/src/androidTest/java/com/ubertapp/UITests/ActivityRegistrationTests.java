package com.ubertapp.UITests;

import android.content.ComponentName;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import com.ubertapp.Activities.ActivityRegistration;
import com.ubertapp.Activities.ActivitySelection;
import com.ubertapp.Controllers.ElasticSearchController;
import com.ubertapp.Mock.MockElasticSeachController;
import com.ubertapp.R;

import org.junit.Before;
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
 * Created by Adam on 11/8/2016.
 */

public class ActivityRegistrationTests {
    private String username;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String email;

    @Rule
    public IntentsTestRule<ActivityRegistration> OPActivityRule = new IntentsTestRule<ActivityRegistration>(
            ActivityRegistration.class);

    @Before
    public void initValidString() {
        username = "DopeD3aler666";
        firstname = "Osama";
        lastname = "Bin Laden";
        phoneNumber = "5555555555";
        email = "superman@Gmail.com";
    }

    @Test
    public void TestRegister() {
        ElasticSearchController.setMock(MockElasticSeachController.getInstance());

        onView(withText("Registration")).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.username_edittext)).perform(typeText(username));
        onView(withText(username)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.firstname_edittext)).perform(typeText(firstname));
        onView(withText(firstname)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.lastname_edittext)).perform(typeText(lastname));
        onView(withText(lastname)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.phone_edittext)).perform(typeText(phoneNumber));
        onView(withText(phoneNumber)).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.email_edittext)).perform(typeText(email));
        onView(withText(email)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Done")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivitySelection.class)));
    }


    //TODO: Test Actually adding a user to ES. Duplicates. Etc...
    //TODO: Test Clicking register. Will probably need a teardown that delets the thing from ES, or a **mock
}
