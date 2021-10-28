package com.example.junckcleaner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.services.AppLockService;
import com.example.junckcleaner.utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashSet;
import java.util.Set;

public class AppLockerAdapter extends ListAdapter<String, AppLockerAdapter.AppsHolder> {
    private final Context context;
    private final Set<String> list;
    private final Utils utils;
    private final AppPreferences preferences;
    private final SendData sendData;

    public AppLockerAdapter(Context context, SendData sendData) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        list = new HashSet<>();
        preferences = new AppPreferences(context);
        this.sendData = sendData;
    }

    public static final DiffUtil.ItemCallback<String> diffCallback =
            new DiffUtil.ItemCallback<String>() {
                @Override
                public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }

                @Override
                public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                    return oldItem.equals(newItem);
                }
            };


    @NonNull
    @Override
    public AppsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_app_locker, parent, false);
        return new AppsHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AppsHolder holder, int position) {

        String appPackage = getItem(position);
        String name = utils.appInfo(appPackage, MyAnnotations.APP_NAME);
        Drawable drawable = utils.appInfo(appPackage, MyAnnotations.APP_ICON);

        Glide.with(context).load(drawable).placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.imageView_icon1);
        if (name == null) {
            holder.textView_name.setText(appPackage);
        } else {
            holder.textView_name.setText(name);

        }

        if (preferences.getStringSet(MyAnnotations.APPS_SET) != null) {
            list.addAll(preferences.getStringSet(MyAnnotations.APPS_SET));
            if (preferences.getStringSet(MyAnnotations.APPS_SET).contains(appPackage)) {
                Glide.with(context).load(context.getDrawable(R.drawable.ic_locked))
                        .into(holder.selection_iv);
            } else {
                Glide.with(context).load(context.getDrawable(R.drawable.ic_unlock_icon))
                        .into(holder.selection_iv);
            }
        } else {
            Glide.with(context).load(context.getDrawable(R.drawable.ic_unlock_icon))
                    .into(holder.selection_iv);
        }

        holder.itemView.setOnClickListener(v -> {
            if (preferences.getStringSet(MyAnnotations.APPS_SET) == null) {
                list.add(getItem(position));
                preferences.adStringSet(MyAnnotations.APPS_SET, list);
                Glide.with(context).load(context.getDrawable(R.drawable.ic_locked))
                        .into(holder.selection_iv);
            } else {
                if (!preferences.getStringSet(MyAnnotations.APPS_SET).contains(getItem(position))) {
                    list.add(getItem(position));
                    preferences.adStringSet(MyAnnotations.APPS_SET, list);
                    Glide.with(context).load(context.getDrawable(R.drawable.ic_locked))
                            .into(holder.selection_iv);
                } else {
                    list.remove(getItem(position));
                    preferences.adStringSet(MyAnnotations.APPS_SET, list);
                    Glide.with(context).load(context.getDrawable(R.drawable.ic_unlock_icon))
                            .into(holder.selection_iv);
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, AppLockService.class));

                } else {
                    context.startService(new Intent(context, AppLockService.class));
                }

                notifyItemChanged(position);
            }
            if (preferences.getStringSet(MyAnnotations.APPS_SET) != null) {
                sendData.data(String.valueOf(preferences.getStringSet(MyAnnotations.APPS_SET)
                        .size()));
            } else if (preferences.getStringSet(MyAnnotations.APPS_SET) == null) {
                sendData.data("0");
            } else if (preferences.getStringSet(MyAnnotations.APPS_SET).isEmpty()) {
                sendData.data("0");
            }
        });

    }


    static class AppsHolder extends RecyclerView.ViewHolder {

        CircularImageView imageView_icon1;
        ImageView selection_iv;
        TextView textView_name;

        public AppsHolder(@NonNull View itemView) {
            super(itemView);
            imageView_icon1 = itemView.findViewById(R.id.imageView_icon1);
            textView_name = itemView.findViewById(R.id.textView_name);
            selection_iv = itemView.findViewById(R.id.checkbox);

        }
    }
}
