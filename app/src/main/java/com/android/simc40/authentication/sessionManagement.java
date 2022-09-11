package com.android.simc40.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.simc40.classes.User;

public class sessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public sessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(User user){
        //save session of user whenever user is logged in
        String uid = user.getUid();
        editor.putString(SESSION_KEY,uid).commit();
    }

    public String getSession(){
        //return user whose session is saved
        return sharedPreferences.getString(SESSION_KEY, "-1");
    }

    public void removeSession(){
        editor.putString(SESSION_KEY,"-1").commit();
    }
}
