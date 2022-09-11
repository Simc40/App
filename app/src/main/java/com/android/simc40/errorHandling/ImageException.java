package com.android.simc40.errorHandling;

public class ImageException extends Exception{
    String errorCode;

    public ImageException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public ImageException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
