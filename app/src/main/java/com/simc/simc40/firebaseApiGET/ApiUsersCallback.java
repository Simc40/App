package com.simc.simc40.firebaseApiGET;

import com.simc.simc40.classes.Usuario;

import java.util.TreeMap;

public interface ApiUsersCallback {
    void onCallback(TreeMap<String, Usuario> response) throws Exception;
}
