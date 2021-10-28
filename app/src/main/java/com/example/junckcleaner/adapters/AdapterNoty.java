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
import com.example.junckcleaner.models.ModelNoty;
import com.example.junckcleaner.prefrences.AppPreferences;
import com.example.junckcleaner.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AdapterNoty extends ListAdapter<ModelNoty, AdapterNoty.NotyHolder> {
    private final Context context;
    private final AppPreferences preferences;
    private final Utils utils;
    private NotyHolder holder;

    private final List<ModelNoty> modelNotyList;
    private final List<ModelNoty> userList;

    private final SelectAll selectAll;

    public AdapterNoty(Context context, SelectAll selectAll, List<ModelNoty> userList) {
        super(diffCallback);
        this.context = context;
        this.selectAll = selectAll;
        this.userList = userList;
        this.preferences = new AppPreferences(context);
        this.utils = new Utils(context);
        modelNotyList = new ArrayList<>();
    }

    public static DiffUtil.ItemCallback<ModelNoty> diffCallback = new DiffUtil.ItemCallback<ModelNoty>() {
        @Override
        public boolean areItemsTheSame(@NonNull ModelNoty oldItem, @NonNull ModelNoty newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ModelNoty oldItem, @NonNull ModelNoty newItem) {
            return oldItem.getId() == newItem.getId() &&
                    oldItem.getNotyTitle().equals(newItem.getNotyTitle()) &&
                    oldItem.getNotyContent().equals(newItem.getNotyContent()) &&
                    oldItem.getAppIcon().equals(newItem.getAppIcon());

        }
    };

    @NonNull
    @Override
    public NotyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_noty_messages,
                parent, false);
        holder = new NotyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotyHolder holder, int position) {
        ModelNoty item = getItem(position);

        Glide.with(context).load((Drawable) utils.appInfo(item.getAppIcon(),
                MyAnnotations.APP_ICON)).into(holder.ImageViewApp);
        holder.textViewNotyTitle.setText(item.getNotyTitle());
        holder.textViewNotyContent.setText(item.getNotyContent());

        holder.checkBoxNoty.setChecked(getModelNotyList().contains(getItem(position)));

        holder.checkBoxNoty.setOnClickListener(view -> {

            if (!getModelNotyList().contains(getItem(position))) {
                modelNotyList.add(getItem(position));
                selectAll.selectAll(
                        modelNotyList.size() == preferences.getNoty(MyAnnotations.NOTIFICATIONS).size(),
                        String.valueOf(modelNotyList.size()));
            } else {
                modelNotyList.remove(getItem(position));
                selectAll.selectAll(false, String.valueOf(modelNotyList.size()));
            }


        });

    }

    public void selectAll() {
        if (!getModelNotyList().isEmpty()) {
            modelNotyList.clear();
        }

         modelNotyList.addAll(userList);
        selectAll.selectAll(true, String.valueOf(modelNotyList.size()));
        holder.checkBoxNoty.setChecked(true);
        notifyDataSetChanged();

    }

    public void unSelectAll() {
        if (!getModelNotyList().isEmpty()) {
            modelNotyList.clear();
        }

        selectAll.selectAll(false, "0");
        holder.checkBoxNoty.setChecked(false);
        notifyDataSetChanged();

    }

    public List<ModelNoty> getModelNotyList() {
        return modelNotyList;
    }


    static class NotyHolder extends RecyclerView.ViewHolder {
        ImageView ImageViewApp;
        CheckBox checkBoxNoty;
        TextView textViewNotyTitle;
        TextView textViewNotyContent;

        public NotyHolder(@NonNull View itemView) {
            super(itemView);
            ImageViewApp = itemView.findViewById(R.id.ImageViewApp);
            checkBoxNoty = itemView.findViewById(R.id.checkBoxNoty);
            textViewNotyTitle = itemView.findViewById(R.id.textViewNotyTitle);
            textViewNotyContent = itemView.findViewById(R.id.textViewNotyContent);
        }
    }
}
