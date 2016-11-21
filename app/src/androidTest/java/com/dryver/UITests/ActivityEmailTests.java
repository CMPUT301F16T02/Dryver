package com.dryver.UITests;

import android.content.ComponentName;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.dryver.Activities.ActivityEmail;
import com.dryver.Activities.ActivityLogin;

import com.dryver.Models.User;



import org.junit.Rule;




/**
 * TODO: Figure out how the f to login for the gmail app requirement of sending
 */

public class ActivityEmailTests
{
    @Rule
    public IntentsTestRule<ActivityEmail> OPActivityRule = new IntentsTestRule<ActivityEmail>(
            ActivityEmail.class);


}
