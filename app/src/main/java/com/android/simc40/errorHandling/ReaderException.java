package com.android.simc40.errorHandling;

public class ReaderException extends Exception{

    String errorCode;

    public ReaderException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public ReaderException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
