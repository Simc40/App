package com.android.simc40.errorHandling;

public class FirebaseDatabaseException extends Exception {

    String errorCode;

    public FirebaseDatabaseException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public FirebaseDatabaseException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
