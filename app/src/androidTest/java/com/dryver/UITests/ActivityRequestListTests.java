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

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.dryver.Activities.ActivityEditProfile;
import com.dryver.Activities.ActivityRegistration;
import com.dryver.Activities.ActivityRequest;
import com.dryver.Activities.ActivityRyderMain;
import com.dryver.Controllers.ElasticSearchController;
import com.dryver.Controllers.UserController;
import com.dryver.Models.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests the finctionality of the RequestList Activity
 * @see ActivityRyderMain
 */

public class ActivityRequestListTests {

    private UserController userController = UserController.getInstance();
    private String testUserName = "TestyMcTesterton";
    private User testUser = new User(testUserName, "fTest", "lTest", "5555555555", "Test@Test.com");

    @Rule
    public IntentsTestRule<ActivityRegistration> OPActivityRule = new IntentsTestRule<ActivityRegistration>(
            ActivityRegistration.class);

    /**
     * Initializes the input strings for the editTexts available during registration
     */
    @Before
    public void addUserToESLogin() throws ExecutionException, InterruptedException {
        ElasticSearchController ES = ElasticSearchController.getInstance();
        ES.addUser(testUser);
        userController.login(testUserName);
    }

    /*
    * Tests US 03.01.01
    */
    @Test
    public void TestOpenUserProfile() throws InterruptedException {
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText("View Profile")).perform(click());

        Thread.sleep(1000);

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityEditProfile.class)));
    }

    @Test
    public void TestMakeRequest() throws InterruptedException {
        onView(withText("Make Request")).perform(click());
        Thread.sleep(1000);
        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRequest.class)));
    }

}
