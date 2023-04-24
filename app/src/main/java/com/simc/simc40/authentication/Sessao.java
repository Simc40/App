package com.simc.simc40.authentication;

import android.content.Context;
import android.content.SharedPreferences;

import com.simc.simc40.classes.Usuario;

public class Sessao {
    public static final String USUARIO_NAO_ENCONTRADO = "-1";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String CHAVE_NO_SHARED_PREFERENCES = "session";
    String CHAVE_DE_SESSAO = "session_user";

    public Sessao(Context context){
        sharedPreferences = context.getSharedPreferences(CHAVE_NO_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void salvarSessao(Usuario usuario){
        //save session of user whenever user is logged in
        String uid = usuario.getUid();
        editor.putString(CHAVE_DE_SESSAO,uid).commit();
    }

    public String getUidUsuarioDaSessao(){
        //return user whose session is saved
        return sharedPreferences.getString(CHAVE_DE_SESSAO, USUARIO_NAO_ENCONTRADO);
    }

    public boolean existeInstanciaNoLocalStorage(){
        String uidDeUsuario = sharedPreferences.getString(CHAVE_DE_SESSAO, USUARIO_NAO_ENCONTRADO);
        return (uidDeUsuario != null && !uidDeUsuario.equals("-1"));
    }

    public void removerSessao(){
        editor.putString(CHAVE_DE_SESSAO, USUARIO_NAO_ENCONTRADO).commit();
    }
}
