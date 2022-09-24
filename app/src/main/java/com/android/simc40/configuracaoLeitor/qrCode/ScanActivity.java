package com.android.simc40.configuracaoLeitor.qrCode;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.android.simc40.R;
import com.android.simc40.configuracaoLeitor.ServiceRfidReader;
import com.android.simc40.moduloQualidade.moduloProducao.Cadastro;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class ScanActivity extends AppCompatActivity {

    ServiceRfidReader serviceRfidReader;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracao_leitor_qrcode_scan_activity);

        Intent sIntent = new Intent(this, ServiceRfidReader.class);
        bindService(sIntent, serviceConnection, 0);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Service Disconnected");
            mBounded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service is connected");
            mBounded = true;
            ServiceRfidReader.LocalBinder mLocalBinder = (ServiceRfidReader.LocalBinder)service;
            serviceRfidReader = mLocalBinder.getService();
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            options.setPrompt("QR Code Scan");
            options.setCameraId(0);  // Use a specific camera of the device
            options.setBeepEnabled(false);
            options.setBarcodeImageEnabled(true);
            barcodeLauncher.launch(options);
        }
    };

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
    result -> {
        if(result.getContents() != null) {
            String qrCode = result.getContents();
            serviceRfidReader.giveQrCodeScanResult(qrCode);
        }
        finish();
    });
}