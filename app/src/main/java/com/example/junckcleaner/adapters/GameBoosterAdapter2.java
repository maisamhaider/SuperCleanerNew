package com.example.junckcleaner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class GameBoosterAdapter2 extends ListAdapter<String, GameBoosterAdapter2.AppsHolder> {
    Context context;
    Set<String> list;
    Utils utils;
    View view;
    AppsHolder holder;
    boolean selectAll = true;

    public GameBoosterAdapter2(Context context) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        list = new HashSet<>();
//
    }

    public static final DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);

        }
    };


    public Set<String> getList() {
        return list;
    }

    public void setList(Set<String> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public AppsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_game_booster2,
                parent, false);
        holder = new AppsHolder(view);
        return new AppsHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AppsHolder holder, int position) {

        String appPackage = getItem(position);
        String name = utils.appInfo(appPackage, MyAnnotations.APP_NAME);

        Glide.with(context).load((Drawable) utils.appInfo(appPackage, MyAnnotations.APP_ICON))
                .placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.imageView_icon);
        if (name == null) {
            holder.textView_app_name.setText(appPackage);
        } else {
            holder.textView_app_name.setText(name);
        }

        if (list.contains(appPackage)) {
            Glide.with(context).load(context.getDrawable(R.drawable.ic_checked)).into(holder.imageView_select);
        } else {

            Glide.with(context).load(context.getDrawable(R.drawable.ic_unchecked)).into(holder.imageView_select);
        }

        holder.itemView.setOnClickListener(v -> {
            if (!list.contains(getItem(position))) {
                list.add(getItem(position));
                Glide.with(context).load(context.getDrawable(R.drawable.ic_checked)).into(holder.imageView_select);
            } else {
                list.remove(getItem(position));
                Glide.with(context).load(context.getDrawable(R.drawable.ic_unchecked)).into(holder.imageView_select);
            }
            notifyItemChanged(position);
        });


    }


    static class AppsHolder extends RecyclerView.ViewHolder {

        ImageView imageView_icon, imageView_select;
        TextView textView_app_name;

        public AppsHolder(@NonNull View itemView) {
            super(itemView);

            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            textView_app_name = itemView.findViewById(R.id.textView_app_name);
            imageView_select = itemView.findViewById(R.id.checkbox);

        }
    }
}
