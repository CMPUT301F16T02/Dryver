/*
 * Copyright (C) 2016
 *  Created by: usenka, jwu5, cdmacken, jvogel, asanche
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU General Public License as published by the Free Software Foundation; either version 2 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
 *  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.dryver.UITests;

/**
 * UI tests for the OpeningPage Activity using Espressp
 * @see com.dryver.Activities.ActivityOpeningPage
 */

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import android.content.ComponentName;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.dryver.Activities.ActivityOpeningPage;
import com.dryver.Activities.ActivityRegistration;
import com.dryver.Activities.ActivityRydeOrDryve;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Models.User;
import com.dryver.R;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class ActivityOpeningPageTests{
    private ElasticSearchController ES = ElasticSearchController.getInstance();
    private String testUserName = "TestyMcTesterton";
    private User testUser = new User(testUserName, "fTest", "lTest", "5555555555", "Test@Test.com");

    @Rule
    public IntentsTestRule<ActivityOpeningPage> OPActivityRule = new IntentsTestRule<ActivityOpeningPage>(
            ActivityOpeningPage.class);

    @Before
    public void addUserToES(){
        ES.addUser(testUser);
    }

    /**
     * Tests selecting the registration button whcich goes to the registration Activity
     * @see ActivityRegistration
     */
    @Test
    public void TestSelectRegistration() {
        onView(withText("New user?")).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Sign Up")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRegistration.class)));
    }


    /**
     * Tests selecting the login button which goes to the Login Activity
     * @see ActivityRydeOrDryve
     */
    @Test

    public void TestLogin() {
        onView(withText("Login")).check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.username_edittext)).perform(typeText(testUserName));
        onView(withText(testUserName)).check(ViewAssertions.matches(isDisplayed()));

        onView(withText("Login")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRydeOrDryve.class)));
    }
}
