package com.android.simc40.moduloQualidade.relatorioErros;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.simc40.Images.DownloadImage;
import com.android.simc40.Images.ImageActivity;
import com.android.simc40.R;
import com.android.simc40.checklist.Etapas;
import com.android.simc40.classes.Checklist;
import com.android.simc40.classes.Image;
import com.android.simc40.classes.Peca;
import com.android.simc40.classes.User;
import com.android.simc40.doubleClick.DoubleClick;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.FirebaseDatabaseException;
import com.android.simc40.errorHandling.FirebaseDatabaseExceptionErrorList;
import com.android.simc40.errorHandling.LayoutException;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.QualidadeException;
import com.android.simc40.errorHandling.QualidadeExceptionErrorList;
import com.android.simc40.errorHandling.SharedPrefsException;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.firebaseApiPOST.ImageUploadSuccessCallback;
import com.android.simc40.firebaseApiPOST.UploadErroPicture;
import com.android.simc40.firebasePaths.FirebaseErrosPaths;
import com.android.simc40.dialogs.ImageDialog;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.moduloGerenciamento.GerenciamentoElementos;
import com.android.simc40.dialogs.QuestionDialog;
import com.android.simc40.selecaoListas.SelecaoListaMultipleChoice;
import com.android.simc40.sharedPreferences.sharedPrefsDatabase;
import com.android.simc40.dialogs.SuccessDialog;
import com.android.simc40.touchListener.TouchListener;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

@SuppressLint("ClickableViewAccessibility")
public class RelatarErro extends AppCompatActivity implements QualidadeExceptionErrorList, Etapas, LayoutExceptionErrorList, SharedPrefsExceptionErrorList, FirebaseErrosPaths, FirebaseDatabaseExceptionErrorList {

    private class ItemChecklistLayout{
        View view;
        String item;
        EditText comentario;
        ImageView close;
        ImageView picture;
        Image image;

        public ItemChecklistLayout(TagErroLayout tagErroLayout, View view, String item, EditText comentario, ImageView close, ImageView picture) {
            this.view = view;
            this.item = item;
            this.comentario = comentario;
            this.close = close;
            this.picture = picture;
            close.setOnClickListener(view1 -> tagErroLayout.removeView(item));
            picture.setOnClickListener(view12 -> {
                if(doubleClick.detected()) return;
                if(image == null){
                    itemChecklistLayout = ItemChecklistLayout.this;
                    takePicture.launch(new Intent(RelatarErro.this, ImageActivity.class));
                    return;
                }
                if(imageDialog.getImageView().getTag() == ItemChecklistLayout.this){
                    imageDialog.showImageDialog();
                    return;
                }
                imageDialog.getImageView().setTag(ItemChecklistLayout.this);
                imageDialog.getImageView().setOnClickListener(view13 -> {
                    questionDialog.getButton1().setOnClickListener(view14 -> questionDialog.endQuestionDialog());
                    questionDialog.getButton2().setOnClickListener(view14 -> {
                        questionDialog.endQuestionDialog();
                        itemChecklistLayout = ItemChecklistLayout.this;
                        takePicture.launch(new Intent(RelatarErro.this, ImageActivity.class));
                        imageDialog.endImageDialog();
                    });
                    questionDialog.showQuestion("Trocar de foto?", "Cancelar", "Sim");
                });
                imageDialog.showImageDialog(image.getPath());
            });

            picture.setOnTouchListener(TouchListener.getTouch());
            close.setOnTouchListener(TouchListener.getTouch());
        }

        public void setImage(Image image){
            this.image = image;
            DownloadImage.fromPath(picture, image.getPath());
        }

        public Image getImage() {return image;}
        public View getView() {return view;}
        public String getItem() {return item;}
        public EditText getComentario() {return comentario;}
    }

    private static class TagErroLayout{
        String tag;
        String name;
        CheckBox checkBox;
        LinearLayout layoutItems;
        LinkedHashMap<String, ItemChecklistLayout> items = new LinkedHashMap<>();

        public TagErroLayout(LinearLayout layoutItems, String tag, String name, CheckBox checkBox) {
            this.tag = tag;
            this.name = name;
            this.checkBox = checkBox;
            this.layoutItems = layoutItems;
        }

        public String getTag() {return tag;}
        public String getName() {return name;}
        public CheckBox getCheckBox() {return checkBox;}
        public void addView(String item, ItemChecklistLayout itemChecklistLayout){items.put(item, itemChecklistLayout);}
        public void removeView(String item){
            if(!items.containsKey(item)) return;
            ItemChecklistLayout itemObject = items.get(item);
            layoutItems.removeView(itemObject.getView());
            items.remove(item);
        }
        public boolean viewIsEmpty(){return (items.size() == 0);}
        public boolean hasView(String item){return (items.containsKey(item));}

        public ArrayList<ItemChecklistLayout> getViews(){
            return new ArrayList<>(items.values());
        }
    }


    ArrayList<Boolean> postResponse = new ArrayList<>();
    int numItems = 0;
    User user;
    String database;
    Intent intent;
    CardView informacoesProjeto, submitForm;
    TextView goBack, etapaTextView, obraTextView, elementoTextView;
    LinearLayout layoutTags;
    ArrayList<TagErroLayout> layoutTagsList = new ArrayList<>();
    DoubleClick doubleClick = new DoubleClick();
    Checklist checklist;
    HashMap<String, Peca> pecasMap;
    Peca peca;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    LinearLayout checklistItemLayout;
    ItemChecklistLayout itemChecklistLayout;
    QuestionDialog questionDialog;
    ImageDialog imageDialog;
    DatabaseReference reference;

    @Override
    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.relatorioerros_relatar_erro);

        intent = getIntent();
        Object[] dependency = (Object[]) intent.getSerializableExtra("dependency");
        pecasMap = (HashMap<String, Peca>) dependency[0];
        checklist = (Checklist) dependency[1];
        peca = pecasMap.values().iterator().next();

        try{
            user = sharedPrefsDatabase.getUser(this, MODE_PRIVATE, loadingDialog, errorDialog);
            if (user == null) throw new SharedPrefsException(EXCEPTION_USER_NULL);
            database = user.getCliente().getDatabase();
        } catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }

        goBack = findViewById(R.id.goBack);
        etapaTextView = findViewById(R.id.etapa);
        obraTextView = findViewById(R.id.obra);
        elementoTextView = findViewById(R.id.elemento);
        informacoesProjeto = findViewById(R.id.informacoesProjeto);
        layoutTags = findViewById(R.id.tags);
        submitForm = findViewById(R.id.submitForm);

        layoutTags.setTag(layoutTagsList);

        errorDialog = new ErrorDialog(this);
        loadingDialog = new LoadingDialog(this, errorDialog);
        questionDialog = new QuestionDialog(this);
        imageDialog = new ImageDialog(this);
        successDialog = new SuccessDialog(this);

        goBack.setOnClickListener(view -> finish());

        etapaTextView.setText(prettyEtapas.get(checklist.getEtapa()));
        obraTextView.setText(peca.getObra().getNomeObra());
        elementoTextView.setText(peca.getElemento().getNome());

        informacoesProjeto.setOnClickListener(view -> {
            if(doubleClick.detected()) return;
            try{
                if(peca.getElemento() == null) throw new QualidadeException(EXCEPTION_ELEMENTO_NULL);
                Intent ipIntent = new Intent(RelatarErro.this, GerenciamentoElementos.class);
                ipIntent.putExtra("dependency", peca.getElemento());
                startActivity(ipIntent);
            } catch (Exception e){
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            }
        });

        for(Peca peca : pecasMap.values()){
            View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.relatorioerros_relatar_erro_tag_layout, layoutTags, false);
            TextView tag = obj.findViewById(R.id.tag);
            TextView nome = obj.findViewById(R.id.nome);
            TextView tagText = obj.findViewById(R.id.tagText);
            TextView nameText = obj.findViewById(R.id.nameText);
            ImageView addItem = obj.findViewById(R.id.add);
            CheckBox lockPeca = obj.findViewById(R.id.lockPeca);
            if(checklist.getEtapa().equals(planejamentoKey)) lockPeca.setVisibility(View.INVISIBLE);
            LinearLayout checklistItemLayout = obj.findViewById(R.id.checkList);
            TagErroLayout tagErroLayout = new TagErroLayout(checklistItemLayout, (checklist.getEtapa().equals(planejamentoKey)) ? peca.getElemento().getNome() : peca.getTag(), peca.getNomePeca(), lockPeca);
            checklistItemLayout.setTag(tagErroLayout);
            layoutTagsList.add(tagErroLayout);
            tag.setText(tagErroLayout.getTag());
            nome.setText(tagErroLayout.getName());
            if(checklist.getEtapa().equals(planejamentoKey)){
                String tagTextString = "Elemento: ";
                if(checklist.getEtapa().equals(planejamentoKey)) tagText.setText(tagTextString);
                nameText.setVisibility(View.GONE);
                nome.setVisibility(View.GONE);
            }

            addItem.setOnClickListener(view -> {
                if (doubleClick.detected()) return;
                this.checklistItemLayout = checklistItemLayout;
                Intent intent = new Intent(RelatarErro.this, SelecaoListaMultipleChoice.class);
                intent.putExtra("title", "Selecione um Item do Checklist");
                intent.putExtra("description", "Checklist");
                intent.putStringArrayListExtra("dependency", new ArrayList<>(checklist.getItems().values()));
                selectItemsFromChecklist.launch(intent);
            });
            layoutTags.addView(obj);
        }

        submitForm.setOnClickListener(view -> {
            if (doubleClick.detected()) return;
            if(loadingDialog.isVisible) return;
            int steps = 0;
            for(TagErroLayout tagErroLayout : layoutTagsList){
                for(ItemChecklistLayout itemLayout: tagErroLayout.getViews()){
                    if(itemLayout.getImage() != null) steps ++;
                    else steps += 2;
                }
            }
            loadingDialog.showLoadingDialog(steps + 1);
            for (TagErroLayout tagErroLayout : layoutTagsList){
                try{
                    if(tagErroLayout.viewIsEmpty()) throw new QualidadeException(EXCEPTION_REPORTAR_ERRO_TAG_NO_ITEM_REGISTERED);
                    numItems = 0;
                    checkPostResponse(0, true);
                    for(ItemChecklistLayout itemLayout: tagErroLayout.getViews()){
                        numItems++;
                        if(itemLayout.getComentario().getText().toString().trim().isEmpty()) throw new QualidadeException(EXCEPTION_REPORTAR_ERRO_ITEM_NO_COMENTARIO_REGISTERED);
                    }
                    if(!ErrorHandling.deviceIsConnected(RelatarErro.this)) throw new FirebaseNetworkException("message");
                    for(ItemChecklistLayout itemLayout: tagErroLayout.getViews()){
                        if(itemLayout.getImage() != null) uploadImageOnStorage(tagErroLayout, itemLayout);
                        else uploadDataOnDatabase(null, null, tagErroLayout, itemLayout);
                    }
                }catch (Exception e){
                    ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                    return;
                }
            }
        });
    }

    private void uploadDataOnDatabase(String uuid, String imgUrl, TagErroLayout tagErroLayout, ItemChecklistLayout itemLayout){
        if(uuid == null) uuid = UUID.randomUUID().toString();
        HashMap<String, String> firebaseObj = new HashMap<>();
        @SuppressLint("SimpleDateFormat") String data = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
        firebaseObj.put(firebaseErrosPathsItem, itemLayout.getItem());
        firebaseObj.put(firebaseErrosPathsChecklist, checklist.getUid());
        firebaseObj.put(firebaseErrosPathsComentarios, itemLayout.getComentario().getText().toString().trim());
        firebaseObj.put(firebaseErrosPathsCreatedBy, user.getUid());
        firebaseObj.put(firebaseErrosPathsCreation, data);
        if(imgUrl != null) firebaseObj.put(firebaseErrosPathsImgUrlDefeito, imgUrl);
        firebaseObj.put(firebaseErrosPathsLastModifiedBy, user.getUid());
        firebaseObj.put(firebaseErrosPathsLastModifiedOn, data);
        firebaseObj.put(firebaseErrosPathsEtapaDetectada, checklist.getEtapa());
        firebaseObj.put(firebaseErrosPathsStatus, "aberto");
        firebaseObj.put(firebaseErrosPathsStatusLocked, String.valueOf(tagErroLayout.getCheckBox().isChecked()));
        firebaseObj.put(firebaseErrosPathsUid, uuid);
        if(checklist.getEtapa().equals("planejamento")) reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(peca.getObra().getUid()).child(peca.getElemento().getUid()).child(uuid);
        else reference = FirebaseDatabase.getInstance(database).getReference().child(firebaseErrosPathsFirstKey).child(peca.getObra().getUid()).child(peca.getTag()).child(uuid);
        reference.setValue(firebaseObj)
        .addOnSuccessListener(unused -> checkPostResponse(numItems, true)).addOnFailureListener(e -> checkPostResponse(numItems, false));
    }

    private void uploadImageOnStorage(TagErroLayout tagErroLayout, ItemChecklistLayout itemLayout){
        String uuid = UUID.randomUUID().toString();
        ImageUploadSuccessCallback imageUploadSuccessCallback = response -> {
            loadingDialog.tick();
            if(response.isEmpty()) return;
            uploadDataOnDatabase(uuid, response, tagErroLayout, itemLayout);
        };
        UploadErroPicture.uploadErroPictureOnFirebaseStorage(uuid, user, itemLayout.getImage(), this.getClass().getSimpleName(), loadingDialog, errorDialog, imageUploadSuccessCallback);
    }

    private void checkPostResponse(int numItems, boolean success){
        if(numItems == 0) {
            postResponse = new ArrayList<>();
            return;
        }
        loadingDialog.tick();
        if(!success) try {
            throw new FirebaseDatabaseException(EXCEPTION_POST_FAILED);
        } catch (Exception e) {
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
            return;
        }
        postResponse.add(true);
        if(postResponse.size() == numItems){
            loadingDialog.finalTick();
            loadingDialog.endLoadingDialog();
            successDialog.getButton1().setOnClickListener(view -> {
                successDialog.endSuccessDialog();
                Intent intent = new Intent();
                setResult (RelatarErro.RESULT_OK, intent);
                finish();
            });
            successDialog.showSuccess("Inconformidade Relatada com Sucesso!");
        }
    }

    ActivityResultLauncher<Intent> selectItemsFromChecklist = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try{
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                TagErroLayout tagErroLayout = (TagErroLayout) checklistItemLayout.getTag();
                for(String item: data.getStringArrayListExtra("result")) {
                    if(tagErroLayout.hasView(item)) continue;
                    View obj = LayoutInflater.from(getApplicationContext()).inflate(R.layout.relatorioerros_relatar_erro_checklist_layout, checklistItemLayout, false);
                    TextView itemTextView = obj.findViewById(R.id.item);
                    itemTextView.setText(item);
                    EditText comentario = obj.findViewById(R.id.comentario);
                    ImageView picture = obj.findViewById(R.id.picture);
                    ImageView close = obj.findViewById(R.id.close);
                    ItemChecklistLayout itemChecklistLayout = new ItemChecklistLayout(tagErroLayout, obj, item, comentario, close, picture);
                    tagErroLayout.addView(item, itemChecklistLayout);
                    checklistItemLayout.addView(obj);
                }
            }catch (Exception e){
                ErrorHandling.handleError(RelatarErro.this.toString(), e, loadingDialog, errorDialog);
            }
        }
    });

    ActivityResultLauncher<Intent> takePicture = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if (data == null) throw new LayoutException(EXCEPTION_SERIALIZABLE_NULL);
                Image image = (Image) data.getSerializableExtra("result");
                itemChecklistLayout.setImage(image);
            } catch (Exception e) {
                ErrorHandling.handleError(RelatarErro.this.toString(), e, loadingDialog, errorDialog);
            }
        }
    });

    @Override
    public void onBackPressed() {
        questionDialog.getButton1().setOnClickListener(view -> questionDialog.endQuestionDialog());
        questionDialog.getButton2().setOnClickListener(view -> {questionDialog.endQuestionDialog();finish();});
        questionDialog.showQuestion("Sair da atividade?", "Cancelar", "Sair");
    }

    @Override
    protected void onDestroy() {
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        loadingDialog.endLoadingDialog();
        errorDialog.endErrorDialog();
        successDialog.endSuccessDialog();
        questionDialog.endQuestionDialog();
    }
}