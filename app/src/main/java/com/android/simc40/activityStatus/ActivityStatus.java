package com.android.simc40.activityStatus;

import android.app.Activity;

public class ActivityStatus {

    public static boolean activityIsRunning(Activity activity){
        return !(activity.isFinishing() || activity.isDestroyed());
    }
}
