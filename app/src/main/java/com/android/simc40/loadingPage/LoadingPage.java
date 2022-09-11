package com.android.simc40.loadingPage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

import com.android.simc40.R;
import com.android.simc40.errorDialog.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;

import java.util.Timer;

public class LoadingPage implements FirebaseDatabaseExceptionErrorList {

    AlertDialog dialog;
    public Boolean isVisible = false;
    ErrorDialog errorDialog;
    Activity activity;
    CountDownTimer timeCounter;
    int TimeLimitMillisseconds = 10000;

    @SuppressLint("InflateParams")
    public LoadingPage(Activity activity, ErrorDialog errorDialog){
        this.activity = activity;
        this.errorDialog = errorDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_page, null));
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void showLoadingPage(){
        dialog.show();
        isVisible = true;
        timeCounter = new CountDownTimer(TimeLimitMillisseconds, 1000) {

            public void onTick(long millisUntilFinished) {
                System.out.println("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if(isVisible) try {
                    throw new FirebaseDatabaseException(EXCEPTION_TIME_LIMIT_REACHED);
                } catch (FirebaseDatabaseException e) {
                    errorDialog.getButton().setOnClickListener(view -> activity.finish());
                    ErrorHandling.handleError("LoadingPage", e, LoadingPage.this, errorDialog);
                }
            }
        }.start();

    }

    public void endLoadingPage(){
        if(timeCounter != null) timeCounter.cancel();
        dialog.dismiss();
        isVisible = false;
    }

    public void setTimeLimitMillisseconds(int timeLimitMillisseconds) {if(timeLimitMillisseconds >= 1000 && timeLimitMillisseconds <= 30000) this.TimeLimitMillisseconds = timeLimitMillisseconds;}
}
