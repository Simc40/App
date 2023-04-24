package com.simc.simc40.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simc.simc40.Images.DownloadImage;
import com.simc.simc40.MainActivity;
import com.simc.simc40.R;
import com.simc.simc40.accessLevel.AccessLevel;
import com.simc.simc40.activityStatus.ActivityStatus;
import com.simc.simc40.authentication.Login;
import com.simc.simc40.authentication.Sessao;
import com.simc.simc40.classes.Usuario;
import com.simc.simc40.configuracaoLeitor.ListaLeitores;
import com.simc.simc40.configuracaoLeitor.ServiceRfidReader;
import com.simc.simc40.configuracaoUsuario.ConfiguracaoUsuario;
import com.simc.simc40.dialogs.ModalAlertaDeErro;
import com.simc.simc40.dialogs.ModalDeCarregamento;
import com.simc.simc40.doubleClick.DuploClique;
import com.simc.simc40.errorHandling.DefaultErrorMessage;
import com.simc.simc40.errorHandling.ErrorHandling;
import com.simc.simc40.errorHandling.FirebaseDatabaseException;
import com.simc.simc40.errorHandling.SharedPrefsException;
import com.simc.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.simc.simc40.firebasePaths.FirebaseUserPaths;
import com.simc.simc40.sharedPreferences.LocalStorage;

public class Home extends Fragment implements SharedPrefsExceptionErrorList, DefaultErrorMessage, FirebaseUserPaths, AccessLevel, ListaLeitores {


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

    public Home() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setViews(){
        userName = requireView().findViewById(R.id.userName);
        clientName = requireView().findViewById(R.id.clientName);
        profileImage = requireView().findViewById(R.id.avatar);
        logout = requireView().findViewById(R.id.goBack);
        card1 = requireView().findViewById(R.id.card1);
        card2 = requireView().findViewById(R.id.card2);
        card3 = requireView().findViewById(R.id.card3);
        card4 = requireView().findViewById(R.id.card4);
        card5 = requireView().findViewById(R.id.card5);
        card6 = requireView().findViewById(R.id.card6);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setViews();

        LocalStorage.salvarLeitor(this.requireActivity(), "", MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);

        modalAlertaDeErro = new ModalAlertaDeErro(this.getActivity());
        modalDeCarregamento = new ModalDeCarregamento(this.getActivity(), modalAlertaDeErro);

        modalAlertaDeErro.getBotao().setOnClickListener(null);
        modalAlertaDeErro.getBotao().setOnClickListener(buttonView1 -> {
            modalAlertaDeErro.fecharModal();
            logOut();
        });

        try{
            usuario = LocalStorage.getUsuario(requireActivity(), MODE_PRIVATE, modalDeCarregamento, modalAlertaDeErro);
            if (usuario == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            userName.setText(usuario.getNome());
            clientName.setText(usuario.getCliente().getNome());
            DownloadImage.fromUrl(profileImage, usuario.getImgUrl());
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
        }

        card1.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(requireActivity(), ConfiguracaoUsuario.class));
        });

        card2.setOnClickListener(view1 -> {
//            if(doubleClick.detected()) return;
//            try{
//                String selectedReader = serviceRfidReader.getReader();
//                if(selectedReader == null){
//                    Intent intent = new Intent(this, SelecaoListaLeitores.class);
//                    selectLeitorFromList.launch(intent);
//                }
//                else if(selectedReader.equals(UHF_RFID_Reader_1128)) startActivity(new Intent(this, ReadWrite1128.class));
//                else if(selectedReader.equals(QR_CODE)) startActivity(new Intent(this, ReadWriteQrCode.class));
//            } catch (Exception e){
//                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
//            }
        });

        card3.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(requireActivity(), ModuloDeGerenciamento.class));
        });
        card4.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            startActivity(new Intent(requireActivity(), ModuloDeLogistica.class));
        });
        card5.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            ((MainActivity) getActivity()).replaceFragments(ModuloDeQualidade.class);
//            startActivity(new Intent(requireActivity(), ModuloDeQualidadeLegacy.class));
        });
        card6.setOnClickListener(view1 -> {
            if(duploClique.detectado()) return;
            if (modalDeCarregamento.estaVisivel) return;
//            loadingDialog.showLoadingDialog(3);
            try{
                modalDeCarregamento.avancarPasso(); // 1
                if(!ErrorHandling.deviceIsConnected(requireActivity())) throw new FirebaseNetworkException(defaultErrorMessage);
                reference = FirebaseDatabase.getInstance().getReference().child(firebaseUserPathFirstKey).child(usuario.getUid());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        loadingDialog.tick(); // 2
                        try{
                            if(dataSnapshot.getValue() == null) throw new FirebaseDatabaseException();
                            String accessLevel = dataSnapshot.child(firebaseUserPathAccessLevelKey).getValue(String.class);
                            if(accessLevel == null) throw new FirebaseDatabaseException();
                            if(!accessLevel.equals(accessLevelUser)){
//                                loadingDialog.finalTick(); // 3
                                startActivity(new Intent(requireActivity(), SupervisaoRelatorio.class));
                                return;
                            }
                            String permission = dataSnapshot.child(firebaseUserPathThirdKey).child(firebaseUserPathReportsPermissionKey).getValue(String.class);
                            if(permission == null) throw new FirebaseDatabaseException();
                            if(permission.equals("ativo")){
//                                loadingDialog.finalTick(); // 3
                                startActivity(new Intent(requireActivity(), SupervisaoRelatorio.class));
                            }else{
                                throw new FirebaseAuthException("ERROR_ACTIVITY_ACCESS_DENIED", defaultErrorMessage);
                            }
                        } catch (Exception e){
                            ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                            reference.removeEventListener(this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Exception e = new Exception(databaseError.getMessage());
                        if(ActivityStatus.activityIsRunning(requireActivity())) ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
                        reference.removeEventListener(this);
                    }
                });
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, modalDeCarregamento, modalAlertaDeErro);
            }
        });

        logout.setOnClickListener(v -> logOut());

//        BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String result = intent.getStringExtra("tagResult");
////                String error = intent.getStringExtra("error");
////                if(error != null){
////                    com.simc.simc40.alerts.errorDialog.showError(myActivity, error);
////                }
//                if(result==null || result.length()<3){
//                    return;
//                }else{
//                    Toast.makeText(requireActivity(), result, Toast.LENGTH_LONG).show();
//                }
//                Log.d("mMessageReceiver", "Got message: " + result);
//            }
//        };
//
//        requireActivity().registerReceiver(mMessageReceiver, new IntentFilter("rfid_server"));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void logOut(){
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseAuth.getInstance().signOut();
        }
        exit();
    }

    private void exit(){
        Sessao session = new Sessao(this.getActivity());
        session.removerSessao();
        Intent i = new Intent(this.getActivity(), Login.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}