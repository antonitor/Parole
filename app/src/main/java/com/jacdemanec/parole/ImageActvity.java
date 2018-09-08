package com.jacdemanec.parole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActvity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_actvity);
        imageView = findViewById(R.id.image_container);
        String photoUrl = getIntent().getStringExtra(getString(R.string.extra_image));
        Glide.with(this)
                .load(photoUrl)
                .into(imageView);
    }
}
