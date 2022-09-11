package com.android.simc40.errorHandling;

public class QualidadeException extends Exception{
    String errorCode;

    public QualidadeException(String errorCode){
        super("e");
        this.errorCode = errorCode;
    }

    public QualidadeException(){
        super("e");
    }

    public String getErrorCode() {
        return errorCode;
    }
}
