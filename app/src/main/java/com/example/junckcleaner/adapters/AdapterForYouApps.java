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
import com.example.junckcleaner.models.ModelForYouApps;
import com.example.junckcleaner.utils.Internet;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdapterForYouApps extends ListAdapter<ModelForYouApps, AdapterForYouApps.AppsHolder> {
    Context context;
    List<AppModel> list;
    Utils utils;
    View view;
    AppsHolder holder;
    int i = 1;

    public AdapterForYouApps(Context context) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        list = new ArrayList<>();
        //
    }

    public static final DiffUtil.ItemCallback<ModelForYouApps> diffCallback = new DiffUtil.ItemCallback<ModelForYouApps>() {
        @Override
        public boolean areItemsTheSame(@NonNull ModelForYouApps oldItem, @NonNull ModelForYouApps newItem) {
            return oldItem.getApp_package().equals(newItem.getApp_package());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ModelForYouApps oldItem, @NonNull ModelForYouApps newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getApp_package().equals(newItem.getApp_package()) &&
                    oldItem.getLogo_url().equals(newItem.getLogo_url());
        }
    };


    public List<AppModel> getList() {
        return list;
    }


    @NonNull
    @Override
    public AppsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_you_item,
                parent, false);
        holder = new AppsHolder(view);
        return new AppsHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AppsHolder holder, @SuppressLint("RecyclerView") int position) {
        ModelForYouApps modelMoreApps = getItem(position);
        String appName = modelMoreApps.getName();

        String logo = "/storage/emulated/0/Android/data/com.example.junckcleaner/files/cache/" + "*for_you_" + i + "logo.png";

        holder.title.setText(appName);
        Glide.with(context).load(logo).into(holder.imageViewLogo);

        holder.textViewInstall.setOnClickListener(view -> {
            if (new Internet(context).isConnected()) {
                context.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" +
                                getItem(position).getApp_package())));

            } else {
                Toast.makeText(context, "Check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        });

        i++;
    }


    static class AppsHolder extends RecyclerView.ViewHolder {

        ImageView imageViewLogo;
        TextView title, textViewInstall;

        public AppsHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLogo = (itemView).findViewById(R.id.imageViewLogo);

            title = (itemView).findViewById(R.id.title);
            textViewInstall = (itemView).findViewById(R.id.textViewInstall);

        }
    }

}
