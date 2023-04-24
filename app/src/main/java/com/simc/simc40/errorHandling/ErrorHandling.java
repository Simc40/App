package com.simc.simc40.errorHandling;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorHandling  implements DefaultErrorMessage{

    public static Map<String, String> getError(Exception exception){
        Log.e("ErrorHandling" , exception.getMessage());
        if(exception instanceof FirebaseAuthException){
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
            return FirebaseAuthErrors.getError(errorCode);
        }else if(exception instanceof FirebaseNetworkException){
            return FirebaseNetworkExceptionErros.getError();
        }else if(exception instanceof FirebaseDatabaseException){
            String errorCode = ((FirebaseDatabaseException) exception).getErrorCode();
            return FirebaseDatabaseExceptionErrors.getError(errorCode);
        }else if(exception instanceof LayoutException){
            String errorCode = ((LayoutException) exception).getErrorCode();
            return LayoutExceptionErrors.getError(errorCode);
        }else if(exception instanceof PermissionException){
            String errorCode = ((PermissionException) exception).getErrorCode();
            return PermissionExceptonErrors.getError(errorCode);
        }else if(exception instanceof ImageException){
            String errorCode = ((ImageException) exception).getErrorCode();
            return ImageExceptionErrors.getError(errorCode);
        }else if(exception instanceof ReaderException){
            String errorCode = ((ReaderException) exception).getErrorCode();
            return ReaderExceptionErrors.getError(errorCode);
        }else if(exception instanceof QualidadeException){
            String errorCode = ((QualidadeException) exception).getErrorCode();
            return QualidadeExceptionErrors.getError(errorCode);
        }else{
            Map<String, String> response = new HashMap<>();
            response.put("errorCode", "Erro");
            response.put("message", "Ocorreu um erro inesperado, que não foi possível determinar a origem, contate o suporte!");
            return response;
        }
    }

    public static boolean deviceIsConnected(Activity activity){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(activity.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        return (nInfo != null) && nInfo.isConnected();
    }

    public static void printStackTrace(String TAG, StackTraceElement[] stackTrace){
        String stack = Arrays.toString(stackTrace);
        stack = stack.substring(1, stack.length() - 1).replaceAll(",", "\n");
        Log.e(TAG, stack);
    }

    public static void handleError(ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro, String title, String description){
        Map<String, String> response = new HashMap<>();
        response.put("errorCode", title);
        response.put("message", description);
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.showError(response);
    }

    public static void handleError(String contextException, Exception e, ModalDeCarregamento modalDeCarregamento, ModalAlertaDeErro modalAlertaDeErro){
        Log.e(contextException, ErrorHandling.getError(e).get(defaultErrorMessage));
        ErrorHandling.printStackTrace(contextException, e.getStackTrace());
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.showError(ErrorHandling.getError(e));
    }

    public static void handleError(String contextException, Exception e, ModalAlertaDeErro modalAlertaDeErro){
        Log.e(contextException, ErrorHandling.getError(e).get(defaultErrorMessage));
        ErrorHandling.printStackTrace(contextException, e.getStackTrace());
        modalAlertaDeErro.showError(ErrorHandling.getError(e));
    }

    public static void handleError(String contextException, Exception e){
        Log.e(contextException, ErrorHandling.getError(e).get(defaultErrorMessage));
        ErrorHandling.printStackTrace(contextException, e.getStackTrace());
    }

    public static void throwException(){
        int a = 1;
        int b = 2;
        int c = 0;
        List<Integer> lista = new ArrayList<>();
        lista.add(a);
        lista.add(b);
        lista.add(c);
        int d  = lista.get(0)/lista.get(2);
        System.out.println(d);
    }
}