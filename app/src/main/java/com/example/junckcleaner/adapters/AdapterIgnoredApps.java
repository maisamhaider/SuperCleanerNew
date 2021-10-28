package com.example.junckcleaner.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
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
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterIgnoredApps extends ListAdapter<String, AdapterIgnoredApps.FileHolder> {
    private final Context context;
    private Set<String> set;
    private final List<String> unChecked;
    private final Utils utils;
    private TrueFalse trueFalse;

    public AdapterIgnoredApps(Context context) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        set = new HashSet<>();
        unChecked = new ArrayList<>();
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
        return set;
    }

    public void setList(Set<String> set) {
        this.set = set;
    }


    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_uninstall,
                parent, false);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        String name = utils.appInfo(getItem(position), MyAnnotations.APP_NAME);
        String pack = getItem(position);
        Drawable drawable = utils.appInfo(pack, MyAnnotations.APP_ICON);
        long size = 0;
        if (set.contains(pack)) {
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_checked))
                    .into(holder.imageView_select);

        } else {
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                    .into(holder.imageView_select);

        }
        try {
            size = utils.appSize(pack);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.textView_size.setText(utils.getDataSizeWithPrefix(size));
        if (name == null) {
            holder.textView_app_name.setText(pack);
        } else {
            holder.textView_app_name.setText(name);
        }
        Glide.with(context).load(drawable)
                .placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.imageView_icon);


        holder.itemView.setOnClickListener(v -> {

            if (set.contains(getItem(position))) {

                set.remove(getItem(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                        .into(holder.imageView_select);
                if (!unChecked.contains(getItem(position))) {
                    unChecked.add(getItem(position));

                }

            } else {
                trueFalse.isTrue(true);
                set.add(getItem(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_checked))
                        .into(holder.imageView_select);
                if (unChecked.contains(getItem(position))) {
                    unChecked.add(getItem(position));
                    unChecked.remove(getItem(position));
                }
            }
            if (set.isEmpty()) {
                trueFalse.isTrue(false);
            }
            notifyItemChanged(position);

        });

    }


    static class FileHolder extends RecyclerView.ViewHolder {

        ImageView imageView_icon;
        ImageView imageView_select;
        TextView textView_app_name,
                textView_size;


        public FileHolder(@NonNull View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            imageView_select = itemView.findViewById(R.id.checkbox);

            textView_app_name = itemView.findViewById(R.id.textView_app_name);
            textView_size = itemView.findViewById(R.id.textView_size);

        }
    }

    public void setListener(TrueFalse trueFalse) {
        this.trueFalse = trueFalse;
    }

}
