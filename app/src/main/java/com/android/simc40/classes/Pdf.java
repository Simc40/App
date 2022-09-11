package com.android.simc40.classes;

import java.io.Serializable;

public class Pdf implements Serializable {
    String title;
    String pdfUrl;

    public Pdf(String title, String pdfUrl){
        this.title = title;
        this.pdfUrl = pdfUrl;
    }

    @Override
    public String toString() {
        return "Pdf{" + "\n" + "title='" + title + '\'' + "\n" + ", pdfUrl='" + pdfUrl + '\'' + "\n" + '}';
    }
    public String getTitle() {return title;}
    public String getPdfUrl() {return pdfUrl;}
}
