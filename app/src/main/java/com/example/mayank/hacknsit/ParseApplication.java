package com.example.mayank.hacknsit;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Mayank on 09-04-2016.
 */
public class ParseApplication extends Application {

    public static final String YOUR_APPLICATION_ID = "O6H2V7pJzoOWntRT9hFqpxxHHdJTCLtA7xmnhHZ5" ;
    public static final String YOUR_CLIENT_KEY = "mwTO3w2Ties5sJrOhkgbJ9sw9sqIiyaneZuryls4" ;

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);
    }
}
