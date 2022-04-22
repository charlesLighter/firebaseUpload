package com.android.firebaseupload;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowImagesActivity extends AppCompatActivity {
    //View for recyclerview
    private RecyclerView recyclerView;

    //Array list containing the urls for images that have been uploaded to firebase
    private ArrayList<Model> myurlList;

    //Instance of adapter class
    private RetrieveAdapter retrieveAdapter;

    //instance of database reference
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_images);

        //Setting properties for the recycler view
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ShowImagesActivity.this));

        //Initializing the database
        databaseReference = FirebaseDatabase.getInstance().getReference("uploaded_images"); //Reference to the path where urls are stored in the database
        //Initializing the array list of urls
        myurlList = new ArrayList<>();
        //Initializing the adapter class
        retrieveAdapter = new RetrieveAdapter(myurlList, this);

        //setting the adapter to the recycler view
        recyclerView.setAdapter(retrieveAdapter);

        //Getting changes on the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Checking whether a photo has been added or deleted from the database
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    Model model = dataSnapshot.getValue(Model.class);
                    //updating the download url list
                    myurlList.add(model);
                }
                //Notifying the adapter class about the changes to add or remove a photo from the list that is displaying
                retrieveAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}