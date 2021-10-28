package com.example.junckcleaner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.models.AppModel;
import com.example.junckcleaner.models.ModelMoreApps;
import com.example.junckcleaner.utils.Internet;
import com.example.junckcleaner.utils.Utils;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class AdapterMoreApps extends ListAdapter<ModelMoreApps, AdapterMoreApps.AppsHolder> {
    Context context;
    List<AppModel> list;
    Utils utils;
    View view;
    AppsHolder holder;
    FirebaseStorage storage;
    int i = 1;

    public AdapterMoreApps(Context context) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        list = new ArrayList<>();
        storage = FirebaseStorage.getInstance();
//
    }

    public static final DiffUtil.ItemCallback<ModelMoreApps> diffCallback = new DiffUtil.ItemCallback<ModelMoreApps>() {
        @Override
        public boolean areItemsTheSame(@NonNull ModelMoreApps oldItem, @NonNull ModelMoreApps newItem) {
            return oldItem.getApp_packege().equals(newItem.getApp_packege());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ModelMoreApps oldItem, @NonNull ModelMoreApps newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getApp_packege().equals(newItem.getApp_packege()) &&
                    oldItem.getBody().equals(newItem.getBody());
        }
    };


    public List<AppModel> getList() {
        return list;
    }


    @NonNull
    @Override
    public AppsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_more_apps,
                parent, false);
        holder = new AppsHolder(view);
        return new AppsHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AppsHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelMoreApps modelMoreApps = getItem(position);
        String appName = modelMoreApps.getName();
        String appBody = modelMoreApps.getBody();
        String logo = "/storage/emulated/0/Android/data/com.example.junckcleaner/files/more_apps/"
                + "*app_" + i + "logo.png";
        String picture1 = "/storage/emulated/0/Android/data/com.example.junckcleaner/files/more_apps/"
                + "*app_" + i + "picture_1.png";
        String picture2 = "/storage/emulated/0/Android/data/com.example.junckcleaner/files/more_apps/"
                + "*app_" + i + "picture_2.png";
        String picture3 = "/storage/emulated/0/Android/data/com.example.junckcleaner/files/more_apps/"
                + "*app_" + i + "picture_3.png";


        holder.textView_app_name.setText(appName);
        holder.textView_app_body.setText(appBody);
        Glide.with(context).load(logo).into(holder.ImageView_icon_app);
        Glide.with(context).load(picture1).into(holder.imageView_screen_1);
        Glide.with(context).load(picture2).into(holder.imageView_screen_2);
        Glide.with(context).load(picture3).into(holder.imageView_screen_3);


        holder.textView_install.setOnClickListener(view -> {
            if (new Internet(context).isConnected()) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" +
                                getItem(position).getApp_packege())));

            } else {
                Toast.makeText(context, "Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });

        i++;

    }


    static class AppsHolder extends RecyclerView.ViewHolder {

        ImageView ImageView_icon_app, imageView_screen_1, imageView_screen_2, imageView_screen_3;
        TextView textView_app_name, textView_app_body, textView_install;


        public AppsHolder(@NonNull View itemView) {
            super(itemView);
            ImageView_icon_app = (itemView).findViewById(R.id.ImageView_icon_app);
            imageView_screen_1 = (itemView).findViewById(R.id.imageView_screen_1);
            imageView_screen_2 = (itemView).findViewById(R.id.imageView_screen_2);
            imageView_screen_3 = (itemView).findViewById(R.id.imageView_screen_3);

            textView_app_name = (itemView).findViewById(R.id.textView_app_name);
            textView_app_body = (itemView).findViewById(R.id.textView_app_body);
            textView_install = (itemView).findViewById(R.id.textView_install);
        }
    }

}
