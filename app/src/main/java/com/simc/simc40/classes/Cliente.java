package com.simc.simc40.classes;

import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseClientePaths;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Cliente implements FirebaseClientePaths, FirebaseDatabaseExceptionErrorList, Serializable {
    String uid;
    String nome;
    String uf;
    String database;
    String storage;
    String status;

    public Cliente(
        String uid,
        String nome,
        String uf,
        String database,
        String storage,
        String status
    ) throws FirebaseDatabaseException {
        List<String> attributes = Arrays.asList(uid, nome, uf, database, storage, status);
        for (String attribute : attributes) if (attribute == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        this.uid = uid;
        this.nome = nome;
        this.uf = uf;
        this.database = database;
        this.storage = storage;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Cliente{" + "\n" + "uid='" + uid + '\'' + "\n" + ", nome='" + nome + '\'' + "\n" + ", uf='" + uf + '\'' + "\n" + ", database='" + database + '\'' + "\n" + ", storage='" + storage + '\'' + "\n" + ", status='" + status + '\'' + "\n" + '}';
    }
    public String getUid() {return uid;}
    public String getNome() {return nome;}
    public String getUf() {return uf;}
    public String getDatabase() {return database;}
    public String getStorage() {return storage;}
    public String getStatus() {return status;}
}
