package com.android.simc40.errorHandling;

public class PermissionException extends Exception{

    String errorCode;

    public PermissionException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public PermissionException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
