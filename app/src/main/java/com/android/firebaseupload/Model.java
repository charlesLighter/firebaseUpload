package com.android.firebaseupload;

public class Model {
    private  String imageUrl;

    private Model(){
        //Empty constructor
    }

    public Model(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
