package com.simc.simc40.Images;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.simc.simc40.R;
import com.simc.simc40.classes.Image;

public class DisplayLoadedImage extends AppCompatActivity {

    Intent intent;
    ImageView imageView, check, uncheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_uploaded_image);

        intent = getIntent();
        Image image = (Image) intent.getSerializableExtra("dependency");

        imageView = findViewById(R.id.imageView);
        check = findViewById(R.id.check);
        uncheck = findViewById(R.id.uncheck);

        check.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult (DisplayLoadedImage.RESULT_OK, intent);
            finish();
        });

        uncheck.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult (DisplayLoadedImage.RESULT_CANCELED, intent);
            finish();
        });

        DownloadImage.fromPath(imageView, image.getPath());
    }
}