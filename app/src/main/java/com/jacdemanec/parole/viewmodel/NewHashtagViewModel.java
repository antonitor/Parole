package com.jacdemanec.parole.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;
import android.net.Uri;

public class NewHashtagViewModel extends ViewModel {

    private boolean imageSelected;
    private boolean isBitmap;
    private Uri uri;
    private Bitmap bitmap;
    private MutableLiveData<String> imageUrl = new MutableLiveData<>();


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        isBitmap = true;
        this.bitmap = bitmap;
    }

    public boolean isBitmap() {
        return isBitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        isBitmap = false;
        this.uri = uri;
    }

    public boolean isImageSelected() {
        return imageSelected;
    }

    public void setImageSelected(boolean imageSelected) {
        this.imageSelected = imageSelected;
    }

    public MutableLiveData<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl.setValue(imageUrl);
    }
}
