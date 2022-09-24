package com.android.simc40.Images;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.simc40.R;
import com.android.simc40.accessLevel.AccessLevel;
import com.android.simc40.classes.Image;
import com.android.simc40.dialogs.ErrorDialog;
import com.android.simc40.errorHandling.ErrorHandling;
import com.android.simc40.errorHandling.LayoutExceptionErrorList;
import com.android.simc40.errorHandling.PermissionException;
import com.android.simc40.errorHandling.PermissionExceptionList;
import com.android.simc40.errorHandling.SharedPrefsExceptionErrorList;
import com.android.simc40.dialogs.LoadingDialog;
import com.android.simc40.dialogs.SuccessDialog;

import java.io.File;
import java.io.IOException;

public class ImageActivity extends AppCompatActivity implements LayoutExceptionErrorList, SharedPrefsExceptionErrorList, PermissionExceptionList, AccessLevel, PermissionList, TextureView.SurfaceTextureListener, ActivityCompat.OnRequestPermissionsResultCallback{

    Uri outputUri;
    File outputFile;
    Image image;
    LoadingDialog loadingDialog;
    ErrorDialog errorDialog;
    SuccessDialog successDialog;
    IntentSelector intentSelector;

    private Camera mCamera;
    private TextureView mTextureView;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getSurfaceTexture());
        } else {
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        setContentView(mTextureView);

        intentSelector = new IntentSelector(this);
        errorDialog = new ErrorDialog(this);
        errorDialog.getButton().setOnClickListener(view -> {
            errorDialog.endErrorDialog();
            finish();
        });
        loadingDialog = new LoadingDialog(this, errorDialog);
        successDialog = new SuccessDialog(this);
        if(!PermissionsDenied()) loadPicture();

    }

    void loadPicture(){
        Object[] temp = intentSelector.getIntent();
        Intent takePic = (Intent) temp[0];  //For code clarity: temp[0] is not descriptive
        outputUri = (Uri) temp[1];
        outputFile = (File) temp[2];
        loadPicture.launch(takePic);
    }

    boolean PermissionsDenied(){
        intentSelector.checkPermissions();
        if(intentSelector.cameraPermissionDenied || intentSelector.galleryPermissionDenied){
            requestPermissionLauncher.launch(permissionList);
            return true;
        }
        return false;
    }

//    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    ActivityResultLauncher<String[]> requestPermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permissions -> {
        try{
            for(String permission : permissionList){

                System.out.println(permission + " " + permissions.get(permission) + " " + checkSelfPermission(permission));
                if(permissions.get(permission) == null) throw new PermissionException();
                if (!permissions.get(permission) && permission.equals(cameraPermission)) throw new PermissionException(EXCEPTION_CAMERA_PERMISSION_DENIED);
                else if (!permissions.get(permission) && permission.equals(galleryPermission)) throw new PermissionException(EXCEPTION_GALLERY_PERMISSION_DENIED);
                else if(!permissions.get(permission)) throw new PermissionException();
            }
            loadPicture();
        }catch (Exception e){
            ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
        }
    });

    ActivityResultLauncher<Intent> loadPicture = registerForActivityResult(
    new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            try {
                Intent data = result.getData();
                if(data != null){
                    image = new Image(ImageActivity.this, data, this.getClass().getSimpleName(), errorDialog);
                }else{
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(outputFile.getAbsolutePath());
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    ImageActivity.this.sendBroadcast(mediaScanIntent);
                    MediaStore.Images.Media.insertImage(getContentResolver(), f.getAbsolutePath(), f.getName(), null);
                    image = new Image(ImageActivity.this, f);
                }
                if(image.getImageFile() == null) throw new PermissionException(EXCEPTION_CAMERA_FILE_NULL);
                Intent intent = new Intent(ImageActivity.this, DisplayLoadedImage.class);
                intent.putExtra("dependency", image);
                this.PictureResult.launch(intent);
            } catch (Exception e) {
                ErrorHandling.handleError(this.getClass().getSimpleName(), e, loadingDialog, errorDialog);
                errorDialog.getButton().setOnClickListener(view -> {
                    errorDialog.endErrorDialog();
                    finish();
                });
            }
        }else{
            finish();
        }
    });

    ActivityResultLauncher<Intent> PictureResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = new Intent();
                    intent.putExtra("result", image);
                    setResult (ImageActivity.RESULT_OK, intent);
                    finish();
                }else{
                    finish();
                }
            });

    private void openCamera(SurfaceTexture surface) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewTexture(surface);
            int rotation = getWindowManager().getDefaultDisplay()
                    .getRotation();
            mCamera.setDisplayOrientation(ORIENTATIONS.get(rotation));
            Camera.Parameters params =  mCamera.getParameters();
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            mCamera.setParameters(params);
            mCamera.startPreview();
        } catch (IOException ioe) {
        }
    }

    private void closeCamera(){
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        openCamera(surface);
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }

        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void requestCameraPermission() {
        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }
}