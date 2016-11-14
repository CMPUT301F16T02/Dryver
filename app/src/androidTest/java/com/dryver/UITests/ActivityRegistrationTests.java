/*
 * Copyright (C) 2016
 * Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.dryver.UITests;

import android.content.ComponentName;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.dryver.Activities.ActivityRegistration;
import com.dryver.Activities.ActivitySelection;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Mock.MockElasticSeachController;
import com.dryver.R;

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
 * UI Tests for the Registration Activity using Espress
 * @see ActivityRegistration
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

    /**
     * Initializes the input strings for the editTexts available during registration
     */
    @Before
    public void initValidString() {
        username = "DopeD3aler666";
        firstname = "Osama";
        lastname = "Bin Laden";
        phoneNumber = "5555555555";
        email = "superman@Gmail.com";
    }

    /**
     * Simply inputs a presumably valid string for all EditTexts and attempts to select register.
     * Uses the mock ESController, so although the strings are validated, it is not actually pushed
     * to the ES server. Registration leads to the Selection Activity.
     * @see MockElasticSeachController
     * @see ActivitySelection
     */
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
