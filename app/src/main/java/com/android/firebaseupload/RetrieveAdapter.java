/*Adapter class for attaching the cardView onto the recycler view
This class play an important role in the process of retrieving and displaying images
 */
package com.android.firebaseupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class RetrieveAdapter extends RecyclerView.Adapter<RetrieveAdapter.MyViewHolder> {

    //Variables
    private ArrayList<Model> urlList;
    private Context context;

    //Constructor
    public RetrieveAdapter(ArrayList<Model> urlList, Context context) {
        this.urlList = urlList;
        this.context = context;
    }

    /*Method for getting the CardView layout and inflating it onto the recycler view
     *The card view is inflated in a linear layout. This makes the images to appear horizontally below each other*/
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new MyViewHolder(view);
    }


    /*For showing/displaying the retrieved image from the database
     *Since the image is coming from the internet, it will be loaded onto the image view using Glide library*/
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       //Loading images
        Glide.with(context).load(urlList.get(position).getImageUrl()).into(holder.imageView);
    }

    /*Method for getting the number of images in the database
     *expanding the number of images in the recyclerview everytime a new image is added to the database*/
    @Override
    public int getItemCount() {
        return urlList.size();
    }


    //Class for fetching the image view in the xml card view layout
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.retrivedImage);

        }
    }
}


