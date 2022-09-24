package com.android.simc40.firebaseApiGET;

import com.android.simc40.classes.Peca;

import java.util.TreeMap;

public interface ApiPecasCallback {
    void onCallback(TreeMap<String, Peca> response) throws Exception;
}
