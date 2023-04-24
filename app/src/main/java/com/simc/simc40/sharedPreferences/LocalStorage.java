package com.simc.simc40.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.simc.simc40.classes.Usuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.gson.Gson;

public class LocalStorage {

    public static void salvarUsuario(Context context, Usuario usuario, int MODE_PRIVATE, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro) {
        try{
            Gson gson = new Gson();
            String json = gson.toJson(usuario);

            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user", json);
            editor.apply();
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    public static Usuario getUsuario(Context context, int MODE_PRIVATE, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro) {
        try{
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String json = sharedPreferences.getString("user", "");
            return gson.fromJson(json, Usuario.class);
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, modalDeCarregamento, modalAlertaDeErro);
        }
        return null;
    }

    public static void salvarLeitor(Context context, String reader, int MODE_PRIVATE, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro) {
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("reader", reader);
            editor.apply();
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, modalDeCarregamento, modalAlertaDeErro);
        }
    }

    public static String getLeitor(Context context, int MODE_PRIVATE, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro) {
        try{
            System.out.println("getReader");
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String reader = sharedPreferences.getString("reader", "");
            if(reader != null && !reader.equals("")) return reader;
        }catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, modalDeCarregamento, modalAlertaDeErro);
        }
        return null;
    }
}
