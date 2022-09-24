package com.android.simc40.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.activityStatus.ActivityStatus;
import com.android.simc40.authentication.Login;
import com.android.simc40.authentication.sessionManagement;
import com.android.simc40.classes.User;
import com.android.simc40.configuracaoLeitor.ListaLeitores;
import com.android.simc40.configuracaoLeitor.ServiceRfidReader;
import com.android.simc40.configuracaoLeitor.ServiceRfidReader.LocalBinder;
import com.android.simc40.configuracaoLeitor.bluetooth_UHF_RFID_reader_1128.ReadWrite1128;
import com.android.simc40.configuracaoLeitor.qrCode.ReadWriteQrCode;
import com.android.simc40.configuracaoUsuario.ConfiguracaoUsuario;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.DefaultErrorMessage;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.ReaderException;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebasePaths.FirebaseUserPaths;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.selecaoListas.SelecaoListaLeitores;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Home extends AppCompatActivity implements SharedPrefsExceptionErrorList, DefaultErrorMessage, FirebaseUserPaths, AccessLevel, ListaLeitores {

    DoubleClick doubleClick = new DoubleClick();
    TextView userName, logout, clientName, changeReader;
    CardView card1, card2, card3, card4, card5, card6;
    ImageView profileImage;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    User user;
    DatabaseReference reference;
    ServiceRfidReader serviceRfidReader;
    boolean mBounded = false;

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service is connected");
            mBounded = true;
            LocalBinder mLocalBinder = (LocalBinder) service;
            serviceRfidReader = mLocalBinder.getService();
            serviceRfidReader.configureErrorHandling(loadingDialog, errorDialog);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            System.out.println("Service Disconnected");
            mBounded = false;
            serviceRfidReader = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        startService(new Intent(this, ServiceRfidReader.class));

        Intent sIntent = new Intent(this, ServiceRfidReader.class);
        bindService(sIntent, serviceConnection, 0);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);

        errorDialog.getButton().setOnClickListener(null);
        errorDialog.getButton().setOnClickListener(buttonView1 -> {
            errorDialog.endErrorDialog();
            logOut();
        });

        userName = findViewById(R.id.userName);
        clientName = findViewById(R.id.clientName);
        profileImage = findViewById(R.id.avatar);
        changeReader = findViewById(R.id.changeReader);
        logout = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);

        try{
            user = sharedPrefsDatabase.getUser(this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            userName.setText(user.getNome());
            clientName.setText(user.getCliente().getNome());
            DownloadImage.fromUrl(profileImage, user.getImgUrl());
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        changeReader.setOnClickListener(view -> {
            Intent intent = new Intent(this, SelecaoListaLeitores.class);
            selectLeitorFromList.launch(intent);
        });

        card1.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(this, ConfiguracaoUsuario.class));
        });
        card2.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                String selectedReader = serviceRfidReader.getReader();
                if(selectedReader == null){
                    Intent intent = new Intent(this, SelecaoListaLeitores.class);
                    selectLeitorFromList.launch(intent);
                }
                else if(selectedReader.equals(UHF_RFID_Reader_1128)) startActivity(new Intent(this, ReadWrite1128.class));
                else if(selectedReader.equals(QR_CODE)) startActivity(new Intent(this, ReadWriteQrCode.class));
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        card3.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(this, ModuloDeGerenciamento.class));
        });
        card4.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(this, ModuloDeLogistica.class));
        });
        card5.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            startActivity(new Intent(this, ModuloDeQualidade.class));
        });
        card6.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            if (loadingDialog.isVisible) return;
            loadingDialog.showLoadingDialog(3);
            try{
                loadingDialog.tick(); // 1
                if(!ErrorHandling.deviceIsConnected(this)) throw new FirebaseNetworkException(defaultErrorMessage);
                reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(user.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loadingDialog.tick(); // 2
                        try{
                            if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                            String accessLevel = dataSnapshot.child(firebaseUserPathAccessLevelKey).getValue(String.class);
                            if(accessLevel == null) throw new FirebaseDatabaseException();
                            if(!accessLevel.equals(accessLevelUser)){
                                loadingDialog.finalTick(); // 3
                                startActivity(new Intent(Home.this, SupervisaoRelatorio.class));
                                return;
                            }
                            String permission = dataSnapshot.child(firebaseUserPathThirdKey).child(firebaseUserPathReportsPermissionKey).getValue(String.class);
                            if(permission == null) throw new FirebaseDatabaseException();
                            if(permission.equals("ativo")){
                                loadingDialog.finalTick(); // 3
                                startActivity(new Intent(Home.this, SupervisaoRelatorio.class));
                            }else{
                                throw new FirebaseAuthException("ERROR_ACTIVITY_ACCESS_DENIED", defaultErrorMessage);
                            }
                        }catch (Exception e){
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                            reference.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Exception e = new Exception(databaseError.getMessage());
                        if(ActivityStatus.activityIsRunning(Home.this)) ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                        reference.removeEventListener(this);
                    }
                });
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        logout.setOnClickListener(v -> logOut());
    }

    private void logOut(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
        exit();
    }

    private void exit(){
        sessionManagement session = new sessionManagement(this);
        session.removeSession();
        Intent i = new Intent(this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (doubleClick.detected()) return;
        this.moveTaskToBack(true);
    }

    ActivityResultLauncher<Intent> selectLeitorFromList = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if(data == null) throw new ReaderException();
                String selectedReader = data.getStringExtra("result");
                serviceRfidReader.setReader(selectedReader, loadingDialog, errorDialog);
                if(selectedReader.equals(UHF_RFID_Reader_1128)) {
                    startActivity(new Intent(this, ReadWrite1128.class));
                }else if(selectedReader.equals(QR_CODE)){
                    startActivity(new Intent(this, ReadWriteQrCode.class));
                }
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        if(mBounded) {
            unbindService(serviceConnection);
            mBounded = false;
        }
    }
}

//    private void copyRecord(DatabaseReference fromPath, final DatabaseReference toPath) {
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(task -> {
//                    if (task.isComplete()) {
//                        System.out.println("Success!");
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {}
//        };
//        fromPath.addListenerForSingleValueEvent(valueEventListener);
//    }