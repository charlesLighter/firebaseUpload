package com.android.firebaseupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    //View for image
    ImageView imageView;
    //Views for buttons
    Button btn_choose, btn_upload;
    //Request code
    private static final int IMAGE_REQ_CODE = 101;
    //File path
    Uri imageUri;
    //progress bar
    ProgressBar progressBar;

    //Reference for database
    private DatabaseReference databaseReference;
    //Reference for storage
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.iv_photo);
        btn_choose = findViewById(R.id.btn_chooseImage);
        btn_upload = findViewById(R.id.btn_upload);
        progressBar = findViewById(R.id.progress);

        //Making the progress bar invisible when the app starts
        progressBar.setVisibility(View.INVISIBLE);

        //Initializing the database and storage references
        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploaded_images");
        storageReference = FirebaseStorage.getInstance().getReference();

        btn_choose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //selecting iMAGE
                chooseImage();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Upload Image
                uploadImage(imageUri);
            }
        });
    }


    //For selecting an image from the phones gallery
    private void chooseImage() {
        Intent imageIntent = new Intent();
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(imageIntent, IMAGE_REQ_CODE);
    }

    //For receiving the selected image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQ_CODE && resultCode == RESULT_OK && data!= null && data.getData() != null)
        {
            //Getting the image using file path and displaying it on the imageView
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    //For uploading the selected image to firebase database
    private void uploadImage(Uri uri) {

        //Checking if the image is available
        if (imageUri != null)
        {
            /*start uploading the image*/
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                    {
                        @Override
                        public void onSuccess(Uri uri) {
                            //image uploaded to firebase
                            /*generating a url that will be used to retrieve back the image. This url is stored in Realtime Database*/
                            Model retrieveModel = new Model(uri.toString());
                            String downloadUrl = databaseReference.push().getKey();
                            databaseReference.child(downloadUrl).setValue(retrieveModel);      //storing the url
                            progressBar.setVisibility(View.INVISIBLE);                         //Dismissing the progress bar
                            Toast.makeText(MainActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                { //When the upload is in progress
                    progressBar.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //if the upload fail
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();

                }
            });

        }
        else
        {
            Toast.makeText(this, "Please choose an image", Toast.LENGTH_SHORT).show();
        }


    }

    //For checking whether the image selected is jpg or png
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));

    }
}