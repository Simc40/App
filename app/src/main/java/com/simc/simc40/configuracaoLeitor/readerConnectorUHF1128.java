package com.simc.simc40.configuracaoLeitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simc.simc40.R;
import com.simc.simc40.alerts.errorDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class readerConnectorUHF1128 {

    Activity myActivity;
    LinearLayout tagItemLayout;
    HashMap<String, View> tagMap = new HashMap<>();
    Button read, clear;
    long lastClickTime = 0;

    public readerConnectorUHF1128(Activity myActivity, LinearLayout tagItemLayout, Button read, Button clear){
        if (myActivity == null){
            return;
        }
        this.myActivity = myActivity;
        this.tagItemLayout = tagItemLayout;
        this.read = read;
        this.clear = clear;

        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("tagResult");
                String error = intent.getStringExtra("error");
                if(error != null){
                    errorDialog.showError(myActivity, error);
                }
                if(result==null || result.length()<3){
                    return;
                }else if(countRegisteredTags().contains(result)){
                    return;
                }else{
                    addTagToLayout(result);
                }
                Log.d("mMessageReceiver", "Got message: " + result);
            }
        };

        myActivity.registerReceiver(mMessageReceiver, new IntentFilter("rfid_server"));

        read.setOnClickListener(view -> {
            Intent broadcastServer = new Intent("rfid_server");
            broadcastServer.putExtra("message", "readTag");
            myActivity.sendBroadcast(broadcastServer);
        });

        clear.setOnClickListener(view -> {
            tagItemLayout.removeAllViews();
            tagMap.clear();
        });
    }

    void addTagToLayout(String tag){
        if(detectedDoubleAction()) return;
        View obj = LayoutInflater.from(myActivity).inflate(R.layout.custom_tag_layout, tagItemLayout ,false);
        TextView item1 = obj.findViewById(R.id.item1);
        ImageView item2 = obj.findViewById(R.id.item2);
        item1.setText(tag);
        item2.setOnClickListener(view -> {
            tagItemLayout.removeView(obj);
            tagMap.remove(tag);
        });
        tagMap.put(tag, obj);
        tagItemLayout.addView(obj);
    }

    public List<String> countRegisteredTags(){
        if (myActivity == null){
            return null;
        }
        return new ArrayList<>(tagMap.keySet());
    }

    public HashMap<String, View> getTagMap() {
        return tagMap;
    }

    private boolean detectedDoubleAction(){
        if (SystemClock.elapsedRealtime() - this.lastClickTime < 1000){
            return true;
        }
        this.lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }
}

