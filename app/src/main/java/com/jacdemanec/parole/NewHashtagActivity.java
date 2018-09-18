package com.jacdemanec.parole;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jacdemanec.parole.viewmodel.NewHashtagViewModel;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewHashtagActivity extends AppCompatActivity {

    private static final int RC_PHOTO_PICKER = 1;
    private static final int RC_CAMERA_ACTION = 2;
    private static final String LOG_TAG = NewHashtagActivity.class.getSimpleName();
    private NewHashtagViewModel mViewModel;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mHashtagImageStorageReference;

    @BindView(R.id.new_hashtag_et)
    EditText mNewHashtagEditText;
    @BindView(R.id.new_text_et)
    EditText mNewHastagTextEditText;
    @BindView(R.id.add_picture_button)
    ImageButton mAddPictureButton;
    @BindView(R.id.take_picture_button)
    ImageButton mTakePictureButton;
    @BindView(R.id.new_hashtag_iv)
    ImageView mNewHashtagImageImageView;
    @BindView(R.id.add_button)
    Button mAddButton;
    @BindView(R.id.cancel_button)
    Button mCancelButton;
    @BindView(R.id.new_hashtag_pb)
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_hashtag);

        mViewModel = ViewModelProviders.of(this).get(NewHashtagViewModel.class);
        mViewModel.setImageSelected(false);

        mFirebaseStorage = FirebaseStorage.getInstance();
        mHashtagImageStorageReference = mFirebaseStorage.getReference("hashtag_images");

        ButterKnife.bind(this);
        mNewHashtagEditText.setText("#");
        mNewHashtagEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                if (!editable.toString().startsWith("#")) {
                    mNewHashtagEditText.setText("#");
                    Selection.setSelection(mNewHashtagEditText.getText(), mNewHashtagEditText.getText().length());
                }
            }
        });
        mAddPictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                startActivityForResult(chooserIntent, RC_PHOTO_PICKER);
            }
        });
        mTakePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RC_CAMERA_ACTION);
                }
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mNewHashtagEditText.getText().toString().trim().equals("#") && !mNewHastagTextEditText.getText().toString().trim().equals("")) {
                    Log.d(NewHashtagActivity.class.getSimpleName(), "NO ESTAN VACIOS LOS CAMPOS DE TEXTO SEGúN ESTO");
                    final Intent data = new Intent();
                    final String hashtag = mNewHashtagEditText.getText().toString().substring(1);
                    final String text = mNewHastagTextEditText.getText().toString();
                    if (mViewModel.isImageSelected()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        Observer<String> imageUrlObserver = new Observer<String>() {
                            @Override
                            public void onChanged(@Nullable String s) {
                                data.putExtra(getString(R.string.extra_new_image), s);
                                data.putExtra(getString(R.string.extra_new_hashtag), hashtag);
                                data.putExtra(getString(R.string.extra_new_hashtag_text), text);
                                setResult(RESULT_OK, data);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                finish();
                            }
                        };
                        mViewModel.getImageUrl().observe(NewHashtagActivity.this, imageUrlObserver);
                        storeImage();
                    } else {
                        data.putExtra(getString(R.string.extra_new_hashtag), hashtag);
                        data.putExtra(getString(R.string.extra_new_hashtag_text), text);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } else {
                    Toast.makeText(NewHashtagActivity.this, "Imprescindible añadir un #Hashtag y una descripcion!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeImage() {
        if (mViewModel.isBitmap()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mViewModel.getBitmap().compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final String randomImageName = "image-" + (new Date().getTime());
            final StorageReference photoRef = mHashtagImageStorageReference.child(randomImageName + ".jpg");
            UploadTask uploadTask = photoRef.putBytes(baos.toByteArray());
            uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    mViewModel.setImageUrl(downloadUrl.toString());
                }
            });
        } else {
            StorageReference photoRef = mHashtagImageStorageReference.child(mViewModel.getUri().getLastPathSegment());
            photoRef.putFile(mViewModel.getUri()).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    mViewModel.setImageUrl(downloadUrl.toString());
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PHOTO_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    mViewModel.setUri(selectedImageUri);
                    mViewModel.setImageSelected(true);
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(mNewHashtagImageImageView);
                }
                break;
            case RC_CAMERA_ACTION:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    mViewModel.setBitmap(imageBitmap);
                    mViewModel.setImageSelected(true);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    Glide.with(this)
                            .asBitmap()
                            .load(baos.toByteArray())
                            .into(mNewHashtagImageImageView);
                }
        }
    }
}
