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
