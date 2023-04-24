package com.simc.simc40.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.simc.simc40.Images.DownloadImage;
import com.simc.simc40.R;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.authentication.Login;
import com.simc.simc40.authentication.Sessao;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.configuracaoLeitor.ListaLeitores;
import com.simc.simc40.configuracaoLeitor.ServiceRfidReader;
import com.simc.simc40.configuracaoLeitor.bluetooth_UHF_RFID_reader_1128.ReadWrite1128;
import com.simc.simc40.configuracaoLeitor.qrCode.ReadWriteQrCode;
import com.simc.simc40.configuracaoUsuario.ConfiguracaoUsuario;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.ReaderException;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.selecaoListas.SelecaoListaLeitores;
import com.simc.simc40.sharedPreferences.LocalStorage;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeLegacy extends AppCompatActivity implements SharedPrefsExceptionErrorList, DefaultErrorMessage, FirebaseUserPaths, AccessLevel, ListaLeitores {

    DuploClique duploClique = new DuploClique();
    TextView userName, logout, clientName;
    CardView card1, card2, card3, card4, card5, card6;
    ImageView profileImage;
    ModalDeCarregamento modalDeCarregamento;
    ModalAlertaDeErro modalAlertaDeErro;
    Usuario usuario;
    DatabaseReference reference;
    ServiceRfidReader serviceRfidReader;
    boolean mBounded = false;

//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            System.out.println("Service is connected");
//            mBounded = true;
//            LocalBinder mLocalBinder = (LocalBinder) service;
//            serviceRfidReader = mLocalBinder.getService();
//            serviceRfidReader.configureErrorHandling(loadingDialog, errorDialog);
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            System.out.println("Service Disconnected");
//            mBounded = false;
//            serviceRfidReader = null;
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        startService(new Intent(this, ServiceRfidReader.class));

//        Intent sIntent = new Intent(this, ServiceRfidReader.class);
//        bindService(sIntent, serviceConnection, 0);

        modalAlertaDeErro = new ModalAlertaDeErro(this);
        modalDeCarregamento = new ModalDeCarregamento(this, modalAlertaDeErro);

        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(buttonView1 -> {
            modalAlertaDeErro.fecharModal();
            logOut();
        });

        userName = findViewById(R.id.userName);
        clientName = findViewById(R.id.clientName);
        profileImage = findViewById(R.id.avatar);
        logout = findViewById(R.id.goBack);
        card1 = findViewById(R.id.card1);
        card2 = findViewById(R.id.card2);
        card3 = findViewById(R.id.card3);
        card4 = findViewById(R.id.card4);
        card5 = findViewById(R.id.card5);
        card6 = findViewById(R.id.card6);

        try{
            usuario = LocalStorage.getUsuario(this, MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            userName.setText(usuario.getNome());
            clientName.setText(usuario.getCliente().getNome());
            DownloadImage.fromUrl(profileImage, usuario.getImgUrl());
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        card1.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this, ConfiguracaoUsuario.class));
        });
        card2.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            try{
                String selectedReader = serviceRfidReader.getReader();
                if(selectedReader == null){
                    Intent intent = new Intent(this, SelecaoListaLeitores.class);
                    selectLeitorFromList.launch(intent);
                }
                else if(selectedReader.equals(UHF_RFID_Reader_1128)) startActivity(new Intent(this, ReadWrite1128.class));
                else if(selectedReader.equals(QR_CODE)) startActivity(new Intent(this, ReadWriteQrCode.class));
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        card3.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeGerenciamento.class));
        });
        card4.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeLogistica.class));
        });
        card5.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(this, ModuloDeQualidadeLegacy.class));
        });
        card6.setOnClickListener(view -> {
            if(duploClique.detectado()) return;
            if (modalDeCarregamento.estaVisivel) return;
            modalDeCarregamento.mostrarModalDeCarregamento(3);
            try{
                modalDeCarregamento.avancarPasso(); // 1
                if(!ErrorHandling.deviceIsConnected(this)) throw new FirebaseNetworkException(defaultErrorMessage);
                reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(usuario.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        modalDeCarregamento.avancarPasso(); // 2
                        try{
                            if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                            String accessLevel = dataSnapshot.child(firebaseUserPathAccessLevelKey).getValue(String.class);
                            if(accessLevel == null) throw new FirebaseDatabaseException();
                            if(!accessLevel.equals(accessLevelUser)){
                                modalDeCarregamento.passoFinal(); // 3
                                startActivity(new Intent(HomeLegacy.this, SupervisaoRelatorio.class));
                                return;
                            }
                            String permission = dataSnapshot.child(firebaseUserPathThirdKey).child(firebaseUserPathReportsPermissionKey).getValue(String.class);
                            if(permission == null) throw new FirebaseDatabaseException();
                            if(permission.equals("ativo")){
                                modalDeCarregamento.passoFinal(); // 3
                                startActivity(new Intent(HomeLegacy.this, SupervisaoRelatorio.class));
                            }else{
                                throw new FirebaseAuthException("ERROR_ACTIVITY_ACCESS_DENIED", defaultErrorMessage);
                            }
                        }catch (Exception e){
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                            reference.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Exception e = new Exception(databaseError.getMessage());
                        if(ActivityStatus.activityIsRunning(HomeLegacy.this)) ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                        reference.removeEventListener(this);
                    }
                });
            }catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
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
        Sessao session = new Sessao(this);
        session.removerSessao();
        Intent i = new Intent(this, Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (duploClique.detectado()) return;
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
                System.out.println(selectedReader);
                serviceRfidReader.setReader(selectedReader, modalDeCarregamento, modalAlertaDeErro);
                if(selectedReader.equals(UHF_RFID_Reader_1128)) {
                    startActivity(new Intent(this, ReadWrite1128.class));
                }else if(selectedReader.equals(QR_CODE)){
                    startActivity(new Intent(this, ReadWriteQrCode.class));
                }
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        modalDeCarregamento.fecharModal();
        modalAlertaDeErro.fecharModal();
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        loadingDialog.endLoadingDialog();
//        errorDialog.endErrorDialog();
//        if(mBounded) {
//            unbindService(serviceConnection);
//            mBounded = false;
//        }
//    }
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