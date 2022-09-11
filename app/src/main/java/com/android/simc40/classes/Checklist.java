package com.android.simc40.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Checklist {
    String uid;
    String etapa;
    Map<String, String> items;
    String creation;
    String createdBy;

    public Checklist(){}

    public Checklist(
        String uid,
        String etapa,
        Map<String, String> items,
        String creation,
        String createdBy
    ){
         this.uid = uid;
         this.etapa = etapa;
         this.items = items;
         this.creation = creation;
         this.createdBy = createdBy;
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getEtapa() {return (etapa != null) ? etapa : "";}
    public Map<String, String> getItems() {return (items != null) ? items : new HashMap<>();}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}

    @Override
    public String toString() {
        return "Checklist{" + "\n" + "uid='" + uid + '\'' + "\n" + ", etapa='" + etapa + '\'' + "\n" + ", items=" + items + "\n" + ", creation='" + creation + '\'' + "\n" + ", createdBy='" + createdBy + '\'' + "\n" + '}';
    }
}





