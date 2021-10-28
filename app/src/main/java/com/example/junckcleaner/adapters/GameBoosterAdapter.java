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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class GameBoosterAdapter extends ListAdapter<String, GameBoosterAdapter.AppsHolder> {
    Context context;
    Set<String> list;
    Utils utils;
    View view;
    AppsHolder holder;
    boolean longClicked = false;

    SendData sendData;
    TrueFalse trueFalse;

    public GameBoosterAdapter(Context context, SendData sendData, TrueFalse trueFalse) {
        super(diffCallback);
        this.context = context;
        this.sendData = sendData;
        this.trueFalse = trueFalse;
        utils = new Utils(context);
        list = new HashSet<>();
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


    @NonNull
    @Override
    public AppsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_game_booster,
                parent, false);
        holder = new AppsHolder(view);
        return new AppsHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AppsHolder holder, int position) {
        String appPackage = getItem(position);

        if (getItem(position).equals("add")) {
            holder.textView_add.setVisibility(View.VISIBLE);
            Glide.with(context).load(ContextCompat.getDrawable(context,
                    R.drawable.ic_add)).into(holder.ImageView_icon2);
        } else {
            holder.textView_add.setVisibility(View.GONE);
            Glide.with(context).load((Drawable) utils.appInfo(appPackage,
                    MyAnnotations.APP_ICON)).into(holder.ImageView_icon2);

        }

//        String name = getItem(position).getApp_name();


        if (!getItem(position).equals("add")) {
            if (list.contains(getItem(position))) {
                Glide.with(context).load(context.getDrawable(R.drawable.ic_blue_checked)).into(holder.selection_iv);
            } else {
                if (!longClicked) {
                    holder.selection_iv.setImageBitmap(null);
                } else {
                    Glide.with(context).load(context.getDrawable(R.drawable.ic_undone_rectangle)).into(holder.selection_iv);

                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (!getItem(position).equals("add")) {
                if (longClicked) {
                    if (!list.contains(getItem(position))) {
                        list.add(getItem(position));
                        Glide.with(context).load(context.getDrawable(R.drawable.ic_blue_checked)).into(holder.selection_iv);
                    } else {

                        list.remove(getItem(position));
                        Glide.with(context).load(context.getDrawable(R.drawable.ic_undone_rectangle)).into(holder.selection_iv);
                    }
                    notifyItemChanged(position);
                    //
                    //select un select
                } else {
                    //Launch app
                    sendData.data(getItem(position));

                }
            } else {
                trueFalse.isTrue(true);
            }

        });


    }


    static class AppsHolder extends RecyclerView.ViewHolder {

        ImageView ImageView_icon2, selection_iv;
        TextView textView_add;

        public AppsHolder(@NonNull View itemView) {
            super(itemView);

            ImageView_icon2 = itemView.findViewById(R.id.ImageView_icon2);
            selection_iv = itemView.findViewById(R.id.checkbox);
            textView_add = itemView.findViewById(R.id.textView_add);

        }
    }
}
