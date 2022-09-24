package com.android.simc40.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.android.simc40.R;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;

public class LoadingDialog implements FirebaseDatabaseExceptionErrorList {

    AlertDialog dialog;
    public Boolean isVisible = false;
    ErrorDialog errorDialog;
    Activity activity;
    View view;
    TextView percentageText;
    TextView loadingText;
    ConstraintLayout constraintLayout;
    ConstraintSet constraintSet;
    Animation anim;
    int totalSteps;
    float stepSize;
    int step;

    @SuppressLint("InflateParams")
    public LoadingDialog(Activity activity, ErrorDialog errorDialog){
        this.activity = activity;
        this.errorDialog = errorDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_loading, null);
        this.view = view;

        loadingText = view.findViewById(R.id.loadingText);

        anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);

        percentageText = view.findViewById(R.id.percentage);

        setLoadingBarPercentage(20);

        builder.setView(view);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.getWindow().getDecorView().setBackgroundResource(R.drawable.loading_page_background);
    }

    public void setLoadingBarPercentage(float percentage){
        if(percentage > 100 || percentage < 0) return;
        percentage = percentage/100;
        constraintLayout = view.findViewById(R.id.loadingBarConstraint);
        constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.constrainPercentWidth(R.id.loadingBar, percentage);
        constraintSet.applyTo(constraintLayout);

        int intPercentage = (int) (percentage * 100);
        String strPercentage = intPercentage + "%";
        percentageText.setText(strPercentage);
    }

    public void showLoadingDialog(int totalSteps){
        this.totalSteps = totalSteps;
        this.step = 0;
        setLoadingBarPercentage(0);
        this.stepSize = (float) 100/totalSteps;
        loadingText.startAnimation(anim);
        dialog.show();
        isVisible = true;
    }

    public void continueLoadingDialog(){
        loadingText.startAnimation(anim);
        dialog.show();
        isVisible = true;
    }

    public void tick(){
        if(!isVisible) return;
        if(step > totalSteps) return;
        step++;
        setLoadingBarPercentage(step*stepSize);
    }

    public void finalTick(){
        if(!isVisible) return;
        setLoadingBarPercentage(100);
    }

    public void endLoadingDialog(){
        loadingText.clearAnimation();
        dialog.dismiss();
        isVisible = false;
    }
}
