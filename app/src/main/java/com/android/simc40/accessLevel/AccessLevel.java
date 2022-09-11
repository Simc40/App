package com.android.simc40.accessLevel;

import java.util.HashMap;

public interface AccessLevel {

    String accessLevelAdmin = "685fe674-9d61-4946-8435-9cbc23a1fc8a";
    String accessLevelResponsable = "49481bb2-4aa9-4266-ba2b-3d2faa6258a0";
    String accessLevelUser = "497ed826-c29f-4dc3-964b-d491f54a2a2f";

    HashMap<String, String> accessLevelMap = new HashMap<String, String>(){{
        put(accessLevelAdmin, "Admin");
        put(accessLevelResponsable, "Responsável");
        put(accessLevelUser, "Usuário");
    }};
}
