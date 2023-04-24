package com.simc.simc40.classes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Checklist implements Serializable {
    String uid;
    String etapa;
    Map<String, String> items;
    LinkedHashMap<String, Boolean> checkedList = new LinkedHashMap<>();
    String creation;
    String createdBy;

    public Checklist(){}

    public Checklist(Checklist checklist){
        this.uid = checklist.getUid();
        this.etapa = checklist.getEtapa();
        this.items = new HashMap<>(checklist.getItems());
        this.checkedList = new LinkedHashMap<>();
        this.creation = checklist.getCreation();
        this.createdBy = checklist.getCreatedBy();
    }

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

    public void createCheckedList(){
        checkedList.clear();
        for(String item : items.values()){
            checkedList.put(item, false);
        }
    }

    public Boolean markCheckedListItem(String item){
        if(checkedList.containsKey(item)) {
            checkedList.put(item, !checkedList.get(item));
            return checkedList.get(item);
        }
        return null;
    }

    public String getOrderOfItem(String item){
        for(String key: items.keySet()){
            if(items.get(key).equals(item)) return key;
        }
        return "0";
    }

    public String getUid() {return (uid != null) ? uid : "";}
    public String getEtapa() {return (etapa != null) ? etapa : "";}
    public Map<String, String> getItems() {return (items != null) ? items : new HashMap<>();}
    public String getCreation() {return (creation != null) ? creation : "";}
    public String getCreatedBy() {return (createdBy != null) ? createdBy : "";}
    public ArrayList<Boolean> getCheckedList() {return new ArrayList<>(checkedList.values());}

    @Override
    public String toString() {
        return "Checklist{" + "\n" + "uid='" + uid + '\'' + "\n" + ", etapa='" + etapa + '\'' + "\n" + ", items=" + items + "\n" + ", creation='" + creation + '\'' + "\n" + ", createdBy='" + createdBy + '\'' + "\n" + '}';
    }
}





