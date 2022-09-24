package com.android.simc40.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.simc40.classes.User;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.dialogs.LoadingDialog;
import com.google.gson.Gson;

public class sharedPrefsDatabase {


    public static void SaveUserOnSharedPreferences(Context context, User user, int MODE_PRIVATE, LoadingDialog loadingDialog, ErrorDialog errorDialog) {
        try{
            Gson gson = new Gson();
            String json = gson.toJson(user);

            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("user", json);
            editor.apply();
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, loadingDialog, errorDialog);
        }
    }

    public static User getUser(Context context, int MODE_PRIVATE, LoadingDialog loadingDialog, ErrorDialog errorDialog) {
        try{
            Gson gson = new Gson();
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String json = sharedPreferences.getString("user", "");
            return gson.fromJson(json, User.class);
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, loadingDialog, errorDialog);
        }
        return null;
    }

    public static void SaveReaderOnSharedPreferences(Context context, String reader, int MODE_PRIVATE, LoadingDialog loadingDialog, ErrorDialog errorDialog) {
        try{
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("reader", reader);
            editor.apply();
        } catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, loadingDialog, errorDialog);
        }
    }

    public static String getReader(Context context, int MODE_PRIVATE, LoadingDialog loadingDialog, ErrorDialog errorDialog) {
        try{
            System.out.println("getReader");
            SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", MODE_PRIVATE);
            String reader = sharedPreferences.getString("reader", "");
            if(reader != null && !reader.equals("")) return reader;
        }catch (Exception e){
            SharedPrefsException sharedPrefsException = new SharedPrefsException();
            ErrorHandling.handleError(SharedPrefsException.class.getSimpleName(), sharedPrefsException, loadingDialog, errorDialog);
        }
        return null;
    }
}
