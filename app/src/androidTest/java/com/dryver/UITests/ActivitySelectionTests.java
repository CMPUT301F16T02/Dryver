package com.dryver.UITests;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.dryver.Activities.ActivityDriver;
import com.dryver.Activities.ActivityRequestList;
import com.dryver.Activities.ActivitySelection;
import com.dryver.Activities.ActivityUserProfile;
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
 * @see ActivitySelection
 */

public class ActivitySelectionTests {
    private UserController userController = UserController.getInstance();
    private User user = new User("SuperCoolGuy312", "Hugh", "Mungus", "780-912-9045", "guy@place.xxx");

    @Rule
    public IntentsTestRule<ActivitySelection> OPActivityRule = new IntentsTestRule<ActivitySelection>(
            ActivitySelection.class);

    @Test
    public void TestOpenUserProfile(){
        userController.setActiveUser(user);
        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText("View Profile")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityUserProfile.class)));
    }

    //TODO: These tests fail... Not sure why...
    @Test
    public void TestSelectDryver(){
        onView(withText("Dryve")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityDriver.class)));
    }

    @Test
    public void TestSelectRyder(){
        onView(withText("Ryde")).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), ActivityRequestList.class)));
    }

}
