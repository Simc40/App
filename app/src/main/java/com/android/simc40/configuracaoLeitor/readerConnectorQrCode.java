package com.android.simc40.configuracaoLeitor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.simc40.R;
import com.android.simc40.alerts.errorDialog;
import com.android.simc40.configuracaoLeitor.qrCode.QrCodeModel;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class readerConnectorQrCode {
    Activity myActivity;
    LinearLayout tagItemLayout;
    HashMap<String, View> tagMap = new HashMap<>();
    Button read, clear;
    long lastClickTime = 0;
    public final static int QRcodeWidth = 350 ;


    public readerConnectorQrCode(Activity myActivity, LinearLayout tagItemLayout, Button read, Button clear){
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
            System.out.println("Resukt QR Code");
            System.out.println(result);
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
//            Intent broadcastServer = new Intent("rfid_server");
//            broadcastServer.putExtra("message", "readTag");
//            myActivity.sendBroadcast(broadcastServer);
            read();
        });

        clear.setOnClickListener(view -> {
        tagItemLayout.removeAllViews();
        tagMap.clear();
    });
}

    public void addTagToLayout(String tag){
        if(detectedDoubleAction()) return;
        if(tagMap.containsKey(tag)) return;
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

    void read(){
        IntentIntegrator integrator = new IntentIntegrator(myActivity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }


    Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        myActivity.getResources().getColor(R.color.black):myActivity.getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
