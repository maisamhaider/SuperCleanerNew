package com.example.junckcleaner.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.SelectAll;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.HashSet;
import java.util.Set;

public class AdapterNotyApps extends ListAdapter<String, AdapterNotyApps.NotyHolder> {
    private final Context context;
    private final AppPreferences preferences;
    private final Utils utils;
    private final Set<String> userApps;
    private NotyHolder holder;

    private Set<String> notyApps;

    private final SelectAll selectAll;

    public AdapterNotyApps(Context context, AppPreferences preferences, Set<String> userApps, SelectAll
            selectAll) {
        super(diffCallback);
        this.context = context;
        this.preferences = preferences;
        this.userApps = userApps;
        this.selectAll = selectAll;
        utils = new Utils(context);
        notyApps = new HashSet<>();

    }

    public static DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
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
    public NotyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_noty_apps,
                parent, false);
        holder = new NotyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotyHolder holder, int position) {
        String item = getItem(position);
        String name = utils.appInfo(item, MyAnnotations.APP_NAME);
        Drawable drawable = utils.appInfo(item, MyAnnotations.APP_ICON);

        holder.checkBoxNoty.setChecked(preferences.getStringSet(MyAnnotations.NOTY_APPS) != null &&
                !preferences.getStringSet(MyAnnotations.NOTY_APPS).isEmpty() &&
                preferences.getStringSet(MyAnnotations.NOTY_APPS).contains(item));


        Glide.with(context).load(drawable).placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.ImageViewApp);
        if (name == null) {
            holder.textViewNotyTitle.setText(item);
        } else {
            holder.textViewNotyTitle.setText(name);
        }

        holder.checkBoxNoty.setOnClickListener(view -> {
            if (preferences.getStringSet(MyAnnotations.NOTY_APPS) == null) {
                notyApps.add(getItem(position));
                preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                selectAll.selectAll(
                        userApps.size() == preferences.getStringSet(MyAnnotations.NOTY_APPS).size(),
                        String.valueOf(notyApps.size()));
            } else {
                if (!preferences.getStringSet(MyAnnotations.NOTY_APPS).contains(getItem(position))) {
                    if (preferences.getStringSet(MyAnnotations.NOTY_APPS) != null) {

                        notyApps = preferences.getStringSet(MyAnnotations.NOTY_APPS);
                    }
                    notyApps.add(getItem(position));
                    preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                    selectAll.selectAll(
                            userApps.size() == preferences.getStringSet(MyAnnotations.NOTY_APPS).size(),
                            String.valueOf(notyApps.size()));
                } else {
                    notyApps = preferences.getStringSet(MyAnnotations.NOTY_APPS);
                    notyApps.remove(getItem(position));
                    preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                    selectAll.selectAll(false, String.valueOf(notyApps.size()));

                }
            }


        });
        holder.itemView.setOnClickListener(view -> {
            if (preferences.getStringSet(MyAnnotations.NOTY_APPS) == null) {
                notyApps.add(getItem(position));
                holder.checkBoxNoty.setChecked(true);
                preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                selectAll.selectAll(
                        userApps.size() == preferences.getStringSet(MyAnnotations.NOTY_APPS).size(),
                        String.valueOf(notyApps.size()));
            } else {
                if (!preferences.getStringSet(MyAnnotations.NOTY_APPS).contains(getItem(position))) {
                    if (preferences.getStringSet(MyAnnotations.NOTY_APPS) != null) {
                        notyApps = preferences.getStringSet(MyAnnotations.NOTY_APPS);
                    }
                    holder.checkBoxNoty.setChecked(true);
                    notyApps.add(getItem(position));
                    preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                    selectAll.selectAll(userApps.size() == preferences.getStringSet(MyAnnotations.NOTY_APPS).size(),
                            String.valueOf(notyApps.size()));
                } else {
                    notyApps = preferences.getStringSet(MyAnnotations.NOTY_APPS);
                    notyApps.remove(getItem(position));
                    preferences.adStringSet(MyAnnotations.NOTY_APPS, notyApps);
                    selectAll.selectAll(false, String.valueOf(notyApps.size()));
                    holder.checkBoxNoty.setChecked(false);


                }
            }


        });

    }

    public void selectAll() {
        holder.checkBoxNoty.setChecked(true);
        preferences.adStringSet(MyAnnotations.NOTY_APPS, userApps);

        selectAll.selectAll(true, String.valueOf(userApps.size()));
        notifyDataSetChanged();
    }

    public void unSelectAll() {
        selectAll.selectAll(false, "0");
        holder.checkBoxNoty.setChecked(false);
        Set<String> clearedSet = new HashSet<>(userApps);
        clearedSet.clear();
        preferences.adStringSet(MyAnnotations.NOTY_APPS, clearedSet);
        notifyDataSetChanged();
    }


    static class NotyHolder extends RecyclerView.ViewHolder {
        ImageView ImageViewApp;
        CheckBox checkBoxNoty;
        TextView textViewNotyTitle;

        public NotyHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewApp = itemView.findViewById(R.id.ImageViewApp);
            checkBoxNoty = itemView.findViewById(R.id.checkBoxNoty);
            textViewNotyTitle = itemView.findViewById(R.id.textViewNotyTitle);
        }
    }
}
