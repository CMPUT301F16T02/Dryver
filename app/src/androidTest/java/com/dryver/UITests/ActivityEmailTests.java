package com.dryver.UITests;

import android.support.test.espresso.intent.rule.IntentsTestRule;


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
