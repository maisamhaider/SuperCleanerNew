package com.example.junckcleaner.adapters;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.interfaces.DeleteVirusInterface;
import com.example.junckcleaner.interfaces.SelectAll;
import com.example.junckcleaner.utils.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterAntivirus extends ListAdapter<String, AdapterAntivirus.FileHolder> {
    Context context;
    Utils utils;
    View view;
    FileHolder holder;

    SelectAll selectAll;
    List<String> deleteVirus;
    List<String> allVirus;
    DeleteVirusInterface deleteVirusInterface;

    public AdapterAntivirus(Context context, DeleteVirusInterface deleteVirusInterface) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.deleteVirusInterface = deleteVirusInterface;
        allVirus = new ArrayList<>();
        deleteVirus = new ArrayList<>();
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


    public void setAllVirus(List<String> allVirus) {
        this.allVirus = allVirus;
    }

    public List<String> getDeleteVirus() {
        return deleteVirus;
    }

    public void setDeleteVirus(List<String> deleteVirus) {
        this.deleteVirus = deleteVirus;
    }

    public void setSelectAll(SelectAll selectAll) {
        this.selectAll = selectAll;
    }

    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_2,
                parent, false);

        return holder = new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        utils = new Utils(context);
        File file = new File(getItem(position));
        holder.textView_app_name.setText(file.getName());
        holder.textView_size.setText(Formatter.formatFileSize(context, file.length()));

        Glide.with(context).load(R.drawable.ic_virus).
                into(holder.imageView_icon);


        holder.checkbox.setChecked(deleteVirus.contains(getItem(position)));

        holder.checkbox.setOnClickListener(v -> {
            if (deleteVirus.contains(getItem(position))) {
                deleteVirus.remove(getItem(position));

                deleteVirusInterface.listener(!deleteVirus.isEmpty());
                selectAll.selectAll(false, String.valueOf(deleteVirus.size()));

            } else {
                deleteVirus.add(getItem(position));
                deleteVirusInterface.listener(true);
                if (deleteVirus.size() == allVirus.size()) {
                    selectAll.selectAll(true, String.valueOf(deleteVirus.size()));
                }
            }
         });

        holder.itemView.setOnClickListener(v -> {
            if (deleteVirus.contains(getItem(position))) {
                deleteVirus.remove(getItem(position));

                deleteVirusInterface.listener(!deleteVirus.isEmpty());
                selectAll.selectAll(false, String.valueOf(deleteVirus.size()));

                holder.checkbox.setChecked(false);
            } else {
                deleteVirus.add(getItem(position));
                holder.checkbox.setChecked(true);
                deleteVirusInterface.listener(true);
                if (deleteVirus.size() == allVirus.size()) {
                    selectAll.selectAll(true, String.valueOf(deleteVirus.size()));
                }
            }
            notifyItemChanged(position);
        });

    }

    public void checkAll() {
        if (!deleteVirus.isEmpty()) {
            deleteVirus.clear();
        }
        deleteVirus.addAll(allVirus);
        selectAll.selectAll(true, String.valueOf(deleteVirus.size()));
        notifyDataSetChanged();

    }

    public void unCheckAll() {
        if (!deleteVirus.isEmpty()) {
            deleteVirus.clear();
        }
        selectAll.selectAll(false, String.valueOf(deleteVirus.size()));
        notifyDataSetChanged();
    }

    static class FileHolder extends RecyclerView.ViewHolder {

        CircularImageView imageView_icon;
        TextView textView_app_name, textView_size;
        CheckBox checkbox;

        public FileHolder(@NonNull View itemView) {
            super(itemView);

            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            checkbox = itemView.findViewById(R.id.checkbox);
            textView_app_name = itemView.findViewById(R.id.textView_app_name);
            textView_size = itemView.findViewById(R.id.textView_size);

        }
    }

}
