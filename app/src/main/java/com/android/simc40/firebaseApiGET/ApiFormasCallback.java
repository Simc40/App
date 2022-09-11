package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Forma;

import java.util.HashMap;

public interface ApiFormasCallback {
    void onCallback(HashMap<String, Forma> response) throws Exception;
}
