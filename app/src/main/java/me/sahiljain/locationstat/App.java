package me.sahiljain.locationstat;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by sahil on 15/2/15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "g6RAVxcxermOczF7n8WEuN7nBTe7vTzADJTqMh6F", "v5zBzf0ZxefhdnLnRulZ8dSkUjsOn1sYuQAEb89Z");
    }
}
