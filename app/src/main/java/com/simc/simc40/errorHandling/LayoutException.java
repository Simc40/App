package com.simc.simc40.errorHandling;

public class LayoutException extends Exception{

    String errorCode;

    public LayoutException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public LayoutException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
