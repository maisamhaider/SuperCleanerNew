package com.example.junckcleaner.adapters;

import android.content.Context;
import android.content.pm.PackageManager;
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
import com.example.junckcleaner.interfaces.SendData;
import com.example.junckcleaner.interfaces.TrueFalse;
import com.example.junckcleaner.utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class AppsAdapter extends ListAdapter<String, AppsAdapter.FileHolder> {
    Context context;
    Utils utils;
    View view;
    FileHolder holder;
    String type;

    SendData sendData;
    TrueFalse trueFalse;
    List<String> killingApps;
    List<String> adapterApps;


    public AppsAdapter(Context context, String type) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.type = type;
        adapterApps = new ArrayList<>();
        killingApps = new ArrayList<>();
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
        if (type.equals(MyAnnotations.ANTIVIRUS)) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_2,
                    parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_1,
                    parent, false);
        }

        return holder = new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        utils = new Utils(context);
        String name = utils.appInfo(getItem(position), MyAnnotations.APP_NAME);
        if (name == null) {
            holder.textView_app_name.setText(getItem(position));

        } else {
            holder.textView_app_name.setText(name);
        }
        switch (type) {
            case MyAnnotations.BOOST:
                holder.textView_size.setVisibility(View.INVISIBLE);
                holder.checkbox.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.selector_custom_checkbox_blue));

                break;
            case MyAnnotations.COOLER:
            case MyAnnotations.BATTERY_SAVER:
                holder.textView_size.setVisibility(View.INVISIBLE);
                holder.checkbox.setBackground(ContextCompat.getDrawable(context,
                        R.drawable.selector_custom_checkbox_red));
                break;

        }

        holder.checkbox.setChecked(killingApps.contains(getItem(position)));


        if (type.equals(MyAnnotations.ANTIVIRUS)) {
            holder.checkbox.setVisibility(View.GONE);
            try {
                holder.textView_size.setText(utils.getDataSizeWithPrefix((float)
                        utils.appSize(getItem(position))));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Glide.with(context).load((Drawable) utils.appInfo(getItem(position),
                MyAnnotations.APP_ICON)).placeholder(R.drawable.ic_apks_circle_full).
                into(holder.imageView_icon);
        holder.checkbox.setOnClickListener(v -> {
            switch (type) {
                case MyAnnotations.BOOST:
                case MyAnnotations.COOLER:
                case MyAnnotations.BATTERY_SAVER:
                    if (killingApps.contains(getItem(position))) {
                        killingApps.remove(getItem(position));
                        trueFalse.isTrue(false);
                    } else {
                        killingApps.add(getItem(position));
                        if (killingApps.size() == getCurrentList().size()) {
                            trueFalse.isTrue(true);
                        }
                    }

                    sendData.data(String.valueOf(killingApps.size()));
                    notifyItemChanged(position);
                    break;
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (MyAnnotations.ANTIVIRUS.equals(type)) {
                sendData.data(getItem(position));
            } else {
                if (killingApps.contains(getItem(position))) {
                    killingApps.remove(getItem(position));
                    trueFalse.isTrue(false);
                } else if (type.equals(MyAnnotations.BOOST) ||
                        type.equals(MyAnnotations.COOLER) ||
                        type.equals(MyAnnotations.BATTERY_SAVER)) {
                    killingApps.add(getItem(position));
                    if (killingApps.size() == getCurrentList().size()) {
                        trueFalse.isTrue(true);
                    }
                }

                sendData.data(String.valueOf(killingApps.size()));
                notifyItemChanged(position);
            }
        });

    }


    public void selectAll() {
        if (!killingApps.isEmpty()) {
            killingApps.clear();
        }
//        CheckBox checkBox = holder.itemView.findViewById(R.id.checkbox);

        holder.checkbox.setChecked(true);
        killingApps.addAll(adapterApps);
        sendData.data(String.valueOf(killingApps.size()));
        notifyDataSetChanged();

    }

    public void clearList() {
        if (!killingApps.isEmpty()) {
            killingApps.clear();
        }
        holder.checkbox.setChecked(false);
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

    public void setTrueFalse(TrueFalse trueFalse) {
        this.trueFalse = trueFalse;
    }

}
