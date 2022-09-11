package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.User;

import java.util.TreeMap;

public interface ApiUsersCallback {
    void onCallback(TreeMap<String, User> response) throws Exception;
}
