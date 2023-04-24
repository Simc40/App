package com.simc.simc40.configuracaoLeitor.qrCode;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.configuracaoLeitor.ListaLeitores;
import com.simc.simc40.dialogs.SuccessDialog;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.simc.simc40.sharedPreferences.LocalStorage;


public class ReadWriteQrCode extends AppCompatActivity implements ListaLeitores {
    ImageView imageView;
    Button btnScan;
    EditText editText;
    BroadcastReceiver mMessageReceiver;
    TextView tv_qr_readTxt, goBack;
    SuccessDialog successDialog;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.configuracao_leitor_readwriteqrcode);

        goBack = findViewById(R.id.goBack);
        imageView = findViewById(R.id.imageView);
        btnScan = findViewById(R.id.btnScan);
        tv_qr_readTxt = findViewById(R.id.tv_qr_readTxt);

        LocalStorage.salvarLeitor(this, QR_CODE, MODE_PRIVATE, null, null);

        successDialog = new SuccessDialog(ReadWriteQrCode.this);
        successDialog.getButton2().setVisibility(View.VISIBLE);
        successDialog.getButton1().setText("Testar");
        successDialog.getButton2().setText("Voltar");
        successDialog.getButton2().setOnClickListener(view -> {
            successDialog.endSuccessDialog();
            finish();
        });
        successDialog.showSuccess("QR Code Configurado com Sucesso!", "Ao entrar no Aplicativo, o QR code será utilizado por padrão, para alterar o leitor, clique em Trocar Leitor na Página Inicial");

        goBack.setOnClickListener(view -> finish());
        btnScan.setOnClickListener(view -> read());

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                String message = intent.getStringExtra("message");
                if(message==null) return;
                if (message.equals("readTag")) {
                    read();
                }
                Log.d("mMessageReceiver", "Got message: " + message);
            }
        };

        IntentFilter mIntentFilter=new IntentFilter("rfid_server");
        registerReceiver(mMessageReceiver, mIntentFilter);
    }

    void read(){
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("QR Code Scan");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    public final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
    result -> {
        if(result.getContents() == null) {
            Toast.makeText(ReadWriteQrCode.this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            tv_qr_readTxt.setText(result.getContents());
            Toast.makeText(ReadWriteQrCode.this, "Scanned", Toast.LENGTH_LONG).show();
        }
    });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if(result != null) {
//            if(result.getContents() == null) {
////                Log.e("Scan*******", "Cancelled scan");
//
//            } else {
//                Log.e("Scan", "Scanned");
//                System.out.println(result);
//                tv_qr_readTxt.setText(result.getContents());
////                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
//                Intent broadcastServer = new Intent("rfid_server");
//                broadcastServer.putExtra("tagResult", result.getContents());
//                sendBroadcast(broadcastServer);
//            }
//        } else {
//            // This is important, otherwise the result will not be passed to the fragment
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
}

//        bitmap = TextToImageEncode(EditTextValue);
//        imageView.setImageBitmap(bitmap);
//    Bitmap TextToImageEncode(String Value) throws WriterException {
//        BitMatrix bitMatrix;
//        try {
//            bitMatrix = new MultiFormatWriter().encode(
//                    Value,
//                    BarcodeFormat.DATA_MATRIX.QR_CODE,
//                    QRcodeWidth, QRcodeWidth, null
//            );
//
//        } catch (IllegalArgumentException Illegalargumentexception) {
//
//            return null;
//        }
//        int bitMatrixWidth = bitMatrix.getWidth();
//
//        int bitMatrixHeight = bitMatrix.getHeight();
//
//        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
//
//        for (int y = 0; y < bitMatrixHeight; y++) {
//            int offset = y * bitMatrixWidth;
//
//            for (int x = 0; x < bitMatrixWidth; x++) {
//
//                pixels[offset + x] = bitMatrix.get(x, y) ?
//                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
//            }
//        }
//        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
//
//        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight);
//        return bitmap;
//    }