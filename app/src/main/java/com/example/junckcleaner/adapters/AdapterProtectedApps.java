package com.example.junckcleaner.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
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

import java.util.HashSet;
import java.util.Set;

public class AdapterProtectedApps extends ListAdapter<String, AdapterProtectedApps.FileHolder> {
    private final Context context;
    private Set<String> list;
    private final Utils utils;
    private TrueFalse trueFalse;

    public AdapterProtectedApps(Context context) {
        super(diffCallback);
        this.context = context;
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

    public void setList(Set<String> list) {
        this.list = list;
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
        long size = 0;
        if (list != null && !list.isEmpty()) {
            if (list.contains(pack)) {
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_checked))
                        .into(holder.imageView_select);

            } else {
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                        .into(holder.imageView_select);

            }
        } else {
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                    .into(holder.imageView_select);
        }
        try {
            size = utils.appSize(pack);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        holder.textView_size.setText(Formatter.formatFileSize(context, size));
        if (name == null) {
            holder.textView_app_name.setText(pack);
        } else {
            holder.textView_app_name.setText(name);
        }
        Glide.with(context)
                .load((Drawable) utils.appInfo(pack, MyAnnotations.APP_ICON))
                .placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.imageView_icon);


        holder.itemView.setOnClickListener(v -> {

            if (list.contains(getItem(position))) {
                list.remove(getItem(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                        .into(holder.imageView_select);


            } else {
                trueFalse.isTrue(true);
                list.add(getItem(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_checked))
                        .into(holder.imageView_select);

            }
            if (list.isEmpty()) {
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
