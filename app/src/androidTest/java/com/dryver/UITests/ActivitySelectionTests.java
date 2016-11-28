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

import com.dryver.Activities.ActivityDryverMain;
import com.dryver.Activities.ActivityEditProfile;
import com.dryver.Activities.ActivityRyderMain;
import com.dryver.Activities.ActivityRydeOrDryve;
import com.dryver.Controllers.UserController;
import com.dryver.Models.User;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests the UI for the Role Selection Activity using Espresso
 * @see ActivityRydeOrDryve
 */

public class ActivitySelectionTests {
    private UserController userController = UserController.getInstance();
    private User user = new User("SuperCoolGuy312", "Hugh", "Mungus", "780-912-9045", "guy@place.xxx");

    @Rule
    public IntentsTestRule<ActivityRydeOrDryve> OPActivityRule = new IntentsTestRule<ActivityRydeOrDryve>(
            ActivityRydeOrDryve.class);

    @Test
    public void TestOpenUserProfile(){
        userController.setActiveUser(user);
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText("View Profile")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityEditProfile.class)));
    }

    //TODO: These tests fail... Not sure why...
    @Test
    public void TestSelectDryver(){
        onView(withText("Dryve")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityDryverMain.class)));
    }

    @Test
    public void TestSelectRyder(){
        onView(withText("Ryde")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRyderMain.class)));
    }

}
