package com.jacdemanec.parole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImageActvity extends AppCompatActivity {

    @BindView(R.id.image_container)
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_actvity);
        ButterKnife.bind(this);
        String photoUrl = getIntent().getStringExtra(getString(R.string.extra_image));
        Glide.with(this)
                .load(photoUrl)
                .into(imageView);
    }
}
