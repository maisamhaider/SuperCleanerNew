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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdapterUninstallApps extends RecyclerView.Adapter<AdapterUninstallApps.FileHolder> {
    private final Context context;
    private List<String> app;
    private List<String> list;
    private final List<Integer> poses;
    private final List<String> unChecked;
    private final Utils utils;
    private TrueFalse trueFalse;

    public AdapterUninstallApps(Context context) {
        this.context = context;
        utils = new Utils(context);
        list = new ArrayList<>();
        poses = new ArrayList<>();
        unChecked = new ArrayList<>();
//
    }

    public void setApp(List<String> app) {
        this.app = app;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }


    public List<Integer> getPoses() {
        return poses;
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
        String pack = app.get(position);
        String name = utils.appInfo(pack, MyAnnotations.APP_NAME);
        Drawable drawable = utils.appInfo(pack, MyAnnotations.APP_ICON);

        long size = 0;
        if (list.contains(pack)) {
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
        holder.textView_size.setText(Formatter.formatFileSize(context, size));
        if (name == null) {
            holder.textView_app_name.setText(pack);
        } else {
            holder.textView_app_name.setText(name);
        }
        Glide.with(context).load(drawable)
                .placeholder(R.drawable.ic_apks_circle_full)
                .into(holder.imageView_icon);


        holder.itemView.setOnClickListener(v -> {
            if (list.contains(app.get(position))) {

                list.remove(app.get(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_unchecked))
                        .into(holder.imageView_select);

                if (!unChecked.contains(app.get(position))) {
                    unChecked.add(app.get(position));

                }

            } else {
                trueFalse.isTrue(true);
                list.add(app.get(position));
                Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_checked))
                        .into(holder.imageView_select);

                unChecked.remove(app.get(position));
            }
            //positions
            if (poses.contains(position)) {

                poses.remove((Integer) position);
            } else {
                poses.add(position);

            }
            if (list.isEmpty()) {
                trueFalse.isTrue(false);
            }
            notifyItemChanged(position);

        });

    }

    @Override
    public int getItemCount() {
        return app.size();
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
