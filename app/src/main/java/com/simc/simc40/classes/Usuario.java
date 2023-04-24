package com.simc.simc40.classes;

import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseClientePaths;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Usuario implements FirebaseClientePaths, FirebaseUserPaths, DefaultErrorMessage, FirebaseDatabaseExceptionErrorList, AccessLevel, Serializable {
    String uid;
    String nome;
    String email;
    Cliente cliente;
    String accessLevel;
    String appPermission;
    String reportPermission;
    String matricula;
    String telefone;
    String imgUrl;

    public Usuario(
        String uid,
        String nome,
        String email,
        Cliente cliente,
        String accessLevel,
        String appPermission,
        String reportPermission,
        String matricula,
        String telefone,
        String imgUrl
    ) throws FirebaseDatabaseException {
        List<String> attributes = Arrays.asList(uid, nome, email, accessLevel, appPermission, reportPermission, matricula, telefone);
        for (String attribute : attributes) if (attribute == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        if(cliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        if(!accessLevelMap.containsKey(accessLevel)) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        this.uid = uid;
        this.nome = nome;
        this.email = email;
        this.cliente = cliente;
        this.accessLevel = accessLevel;
        this.appPermission = appPermission;
        this.reportPermission = reportPermission;
        this.matricula = matricula;
        this.telefone = telefone;
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "User{" + "\n" + "uid='" + uid + '\'' + "\n" + ", nome='" + nome + '\'' + "\n" + ", email='" + email + '\'' + "\n" + ", cliente=" + cliente + "\n" + ", accessLevel='" + accessLevel + '\'' + "\n" + ", appPermission='" + appPermission + '\'' + "\n" + ", reportPermission='" + reportPermission + '\'' + "\n" + ", matricula='" + matricula + '\'' + "\n" + ", telefone='" + telefone + '\'' + "\n" + ", imgUrl='" + imgUrl + '\'' + "\n" + '}';
    }
    public void setCliente(Cliente cliente) throws FirebaseDatabaseException {
        if(cliente == null) throw new FirebaseDatabaseException(EXCEPTION_RETURNED_NULL_VALUE);
        this.cliente = cliente;
    }
    public String getUid() {return uid;}
    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public Cliente getCliente() {return cliente;}
    public String getAccessLevel() {return accessLevel;}
    public String getAppPermission() {return appPermission;}
    public String getReportPermission() {return reportPermission;}
    public String getMatricula() {return matricula;}
    public String getTelefone() {return telefone;}
    public String getImgUrl() {return imgUrl;}
}
