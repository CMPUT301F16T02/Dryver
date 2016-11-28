package com;

import android.app.Application;
import android.content.Context;

/**
 * Class that is used to get the context of the application at any time.
 * http://stackoverflow.com/questions/2002288/static-way-to-get-context-on-android
 * Created by colemackenzie on 2016-11-27.
 */
public class Dryver extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        Dryver.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Dryver.context;
    }

}
