package com.example.sev_user.myappt20.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.sev_user.myappt20.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class ImageBlurAdapter extends RecyclerView.Adapter<ImageBlurAdapter.BlurImage> {
    public interface Iclick {
        void itemClick(int pos);
    }

    List<String> imageBlurs;
    Context context;
    Iclick iclick;

    public ImageBlurAdapter(List<String> imageBlurs, Context context, Iclick iclick) {
        this.imageBlurs = imageBlurs;
        this.context = context;
        this.iclick = iclick;
    }

    @NonNull
    @Override
    public BlurImage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new BlurImage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlurImage holder, int position) {
        String link = imageBlurs.get(position);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.fromFile(new File(link))));
            holder.imgData.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return imageBlurs.size();
    }

    class BlurImage extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgData;
        LinearLayout btnData;

        public BlurImage(View itemView) {
            super(itemView);
            imgData = itemView.findViewById(R.id.img_image_blur);
            btnData = itemView.findViewById(R.id.btn_image_blur);
            btnData.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iclick.itemClick(getAdapterPosition());
        }
    }
}
