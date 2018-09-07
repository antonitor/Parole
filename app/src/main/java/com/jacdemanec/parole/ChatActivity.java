package com.jacdemanec.parole;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.ui.phone.CountryListSpinner;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jacdemanec.parole.adapters.MessageAdapter;
import com.jacdemanec.parole.model.ChatMessage;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 160;
    private static final int RC_PHOTO_PICKER = 2;
    private static final int RC_CAMERA_ACTION = 3;

    private RecyclerView mMessageRecyclerView;
    private FirebaseRecyclerAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EmojiEditText mMessageEditText;
    private ImageButton mSendButton;
    private EmojiPopup mEmojiPopup;
    private ImageButton mEmojiButton;

    private String mUsername;
    private String mHashtag;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDbReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;

    private View.OnClickListener mSendMessageClickListener;
    private View.OnClickListener mSendPictureClickListener;
    private View.OnClickListener mTakePhotoClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        mUsername = intent.getStringExtra("EXTRA_USERNAME");
        mHashtag = intent.getStringExtra("EXTRA_HASHTAG");
        getSupportActionBar().setTitle("#" + mHashtag);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        mMessageDbReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference("chat_photos");

        // Initialize references to views
        mProgressBar =  findViewById(R.id.progressBar);
        mMessageRecyclerView = findViewById(R.id.messageListView);
        mPhotoPickerButton =  findViewById(R.id.photoPickerButton);
        mSendButton = findViewById(R.id.sendButton);
        mEmojiButton = findViewById(R.id.emoji_picker);
        RelativeLayout rootView = findViewById(R.id.root_view);
        mMessageEditText = findViewById(R.id.messageEditText);
        mEmojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(mMessageEditText);


        Query query = mFirebaseDatabase.getReference().child("messages").orderByChild("hashtag").equalTo(mHashtag);
        FirebaseRecyclerOptions<ChatMessage> options =
                new FirebaseRecyclerOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .build();

        // Initialize RecyclerView and its adapter
        mMessageAdapter = new MessageAdapter(options);
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(linearLayoutManager);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mSendMessageClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trimedMessage = mMessageEditText.getText().toString().replaceFirst("\\s+$", "").replaceFirst("^\\s+", "");
                ChatMessage message = new ChatMessage(trimedMessage, mUsername, null, mHashtag);
                mMessageDbReference.push().setValue(message);
                // Clear input box
                mMessageEditText.setText("");
                mMessageRecyclerView.smoothScrollToPosition(mMessageAdapter.getItemCount());
            }
        };

        mSendPictureClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        };

        mTakePhotoClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, RC_CAMERA_ACTION);
                }
            }
        };
        mPhotoPickerButton.setOnClickListener(mSendPictureClickListener);
        mSendButton.setOnClickListener(mTakePhotoClickListener);
        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setImageResource(R.drawable.ic_send_white_24dp);
                    mSendButton.setOnClickListener(mSendMessageClickListener);
                    mPhotoPickerButton.setVisibility(View.INVISIBLE);
                } else {
                    mSendButton.setImageResource(R.drawable.ic_camera_grey_24dp);
                    mSendButton.setOnClickListener(mTakePhotoClickListener);
                    mPhotoPickerButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        mEmojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmojiPopup.toggle();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_PHOTO_PICKER:
                if (resultCode == RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    final StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());
                    photoRef.putFile(selectedImageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            ChatMessage friendlyMessage = new ChatMessage(null, mUsername, downloadUrl.toString(), mHashtag);
                            mMessageDbReference.push().setValue(friendlyMessage);
                        }
                    });
                }
                break;
            case RC_CAMERA_ACTION:
                if (resultCode == RESULT_OK)  {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final String randomPhotoName = "photo-" + (new Date().getTime());
                    final StorageReference photoRef = mChatPhotosStorageReference.child(randomPhotoName+".jpg");
                    byte[] byteData = baos.toByteArray();
                    UploadTask uploadTask = photoRef.putBytes(byteData);
                    uploadTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            ChatMessage friendlyMessage = new ChatMessage(null, mUsername, downloadUrl.toString(), mHashtag);
                            mMessageDbReference.push().setValue(friendlyMessage);
                        }
                    });

                }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mMessageAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessageAdapter.stopListening();
    }
}
