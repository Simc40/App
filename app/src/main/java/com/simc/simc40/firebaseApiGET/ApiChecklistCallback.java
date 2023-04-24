package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Checklist;

import java.util.Hashtable;

public interface ApiChecklistCallback {
    void onCallback(Hashtable<String, Checklist> response) throws Exception;
}
