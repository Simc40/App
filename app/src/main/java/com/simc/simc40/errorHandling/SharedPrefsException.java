package com.simc.simc40.errorHandling;

public class SharedPrefsException extends Exception{

    String errorCode;

    public SharedPrefsException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public SharedPrefsException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}

