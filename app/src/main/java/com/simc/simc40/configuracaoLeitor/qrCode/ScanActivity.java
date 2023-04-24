package com.simc.simc40.configuracaoLeitor.qrCode;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.simc.simc40.R;
import com.simc.simc40.classes.Obra;
import com.simc.simc40.configuracaoLeitor.ServiceRfidReader;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.simc.simc40.selecaoListas.SelecaoListaObras;

public class ScanActivity extends AppCompatActivity {

    ServiceRfidReader serviceRfidReader;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_leitor_qrcode_scan_activity);
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("QR Code Scan");
        options.setCameraId(0);  // Use a specific camera of the device
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);

    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
    result -> {
        if(result.getContents() != null) {
            String qrCode = result.getContents();
            Intent broadcastServer = new Intent("rfid_server");
            broadcastServer.putExtra("scanResult", qrCode);
            ScanActivity.this.sendBroadcast(broadcastServer);
            finish();
        }
    });
}