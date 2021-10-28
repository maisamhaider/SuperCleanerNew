package com.example.junckcleaner.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.interfaces.SelectAll;
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class JunkAdapter1 extends ListAdapter<String, JunkAdapter1.FileHolder> {
    Context context;
    Utils utils;
    View view;
    FileHolder holder;

    SendData sendData;
    SelectAll selectAll;

    List<String> killingApps;
    List<String> adapterApps;


    public JunkAdapter1(Context context, List<String> killingApps,
                        List<String> adapterApps) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.killingApps = killingApps;
        this.adapterApps = adapterApps;

    }

    public static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
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


    public void setAdapterApps(List<String> adapterApps) {
        this.adapterApps = adapterApps;
    }

    public void setKillingApps(List<String> killingApps) {
        this.killingApps = killingApps;
    }

    public List<String> getKillingApps() {
        return killingApps;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_1,
                parent, false);
        holder = new FileHolder(view);
        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        utils = new Utils(context);
        holder.textView_app_name.setText(utils.appInfo(getItem(position),
                MyAnnotations.APP_NAME));


        holder.checkbox.setBackground(ContextCompat.getDrawable(context,
                R.drawable.selector_custom_checkbox_red));


        holder.checkbox.setChecked(killingApps.contains(getItem(position)));
        holder.textView_size.setText(utils.getDataSizeWithPrefix((float)
                utils.getCache(getItem(position))));


        Glide.with(context).load((Drawable) utils.appInfo(getItem(position),
                MyAnnotations.APP_ICON)).into(holder.imageView_icon);
        holder.checkbox.setOnClickListener(v -> {
            if (killingApps.contains(getItem(position))) {
                killingApps.remove(getItem(position));
                selectAll.selectAll(false, "apk");
            } else {
                killingApps.add(getItem(position));
                if (killingApps.size() == getCurrentList().size()) {
                    selectAll.selectAll(true, "apk");
                }
            }
            sendData.data(String.valueOf(killingApps.size()));
            notifyItemChanged(position);
        });
        holder.itemView.setOnClickListener(v -> {
            if (killingApps.contains(getItem(position))) {
                killingApps.remove(getItem(position));
                selectAll.selectAll(false, "apk");
                holder.checkbox.setChecked(false);
            } else {
                holder.checkbox.setChecked(true);
                killingApps.add(getItem(position));
                if (killingApps.size() == getCurrentList().size()) {
                    selectAll.selectAll(true, "apk");
                }
            }
            sendData.data(String.valueOf(killingApps.size()));
            notifyItemChanged(position);
        });

    }


    public void selectAll() {
        if (!killingApps.isEmpty()) {
            killingApps.clear();
        }
//        CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
//        if (holder.checkbox != null) {
//            holder.checkbox.setChecked(true);
//        }
        killingApps.addAll(adapterApps);
        sendData.data(String.valueOf(killingApps.size()));
        notifyDataSetChanged();

    }

    public void clearList() {
        if (!killingApps.isEmpty()) {
            killingApps.clear();
        }
//        CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);
//        if (holder.checkbox != null) {
//            holder.checkbox.setChecked(false);
//        }
        sendData.data(String.valueOf(killingApps.size()));

        notifyDataSetChanged();
    }


    static class FileHolder extends RecyclerView.ViewHolder {

        CircularImageView imageView_icon;
        CheckBox checkbox;
        TextView textView_app_name, textView_size;

        public FileHolder(@NonNull View itemView) {
            super(itemView);

            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            checkbox = itemView.findViewById(R.id.checkbox);
            textView_app_name = itemView.findViewById(R.id.textView_app_name);
            textView_size = itemView.findViewById(R.id.textView_size);

        }
    }

    public void setSendData(SendData sendData) {
        this.sendData = sendData;
    }

    public void setSelectAll(SelectAll selectAll) {
        this.selectAll = selectAll;
    }

}
