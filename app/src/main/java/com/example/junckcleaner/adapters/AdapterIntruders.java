package com.example.junckcleaner.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.junckcleaner.models.ModelIntruder;
import com.example.junckcleaner.utils.Utils;
import com.example.junckcleaner.views.activities.ActivityIntruderDetail;

import java.util.ArrayList;
import java.util.List;

public class AdapterIntruders extends ListAdapter<ModelIntruder, AdapterIntruders.FileHolder> {
    private final Context context;
    private List<String> list;
    private final Utils utils;

    public AdapterIntruders(Context context) {
        super(diffCallback);
        this.context = context;
        utils = new Utils(context);
        list = new ArrayList<>();

    }

    public static final DiffUtil.ItemCallback<ModelIntruder> diffCallback = new DiffUtil.ItemCallback<ModelIntruder>() {
        @Override
        public boolean areItemsTheSame(@NonNull ModelIntruder oldItem, @NonNull ModelIntruder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ModelIntruder oldItem, @NonNull ModelIntruder newItem) {
            return oldItem.getApp_name().equals(newItem.getApp_name()) &&
                    oldItem.getImagePath().equals(newItem.getImagePath()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.getAttempts().equals(newItem.getAttempts()) &&
                    oldItem.getTime().equals(newItem.getTime()) &&
                    oldItem.getId() == newItem.getId();
        }
    };


    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public FileHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_intrudors_item,
                parent, false);

        return new FileHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileHolder holder, int position) {
        String name = getItem(position).getApp_name();
        String path = getItem(position).getImagePath();
        String attempts = getItem(position).getAttempts();
        String date = getItem(position).getDate();
        String time = getItem(position).getTime();

        holder.textView_name.setText(utils.appInfo(name, MyAnnotations.APP_NAME));
        holder.textView_time.setText(time);
        holder.textView_date.setText(date);

        Glide.with(context).load(path).into(holder.imageView_icon);


        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, ActivityIntruderDetail.class).
                putExtra(MyAnnotations.INTRUDER_APP, name)
                .putExtra(MyAnnotations.INTRUDER_PATH, path)
                .putExtra(MyAnnotations.INTRUDER_DATE, date)
                .putExtra(MyAnnotations.INTRUDER_TIME, time)
                .putExtra(MyAnnotations.INTRUDER_ATTEMPTS, attempts)));

    }


    static class FileHolder extends RecyclerView.ViewHolder {

        ImageView imageView_icon;
        ImageView imageView_next;
        TextView textView_name,
                textView_time, textView_date;


        public FileHolder(@NonNull View itemView) {
            super(itemView);
            imageView_icon = itemView.findViewById(R.id.imageView_icon);
            imageView_next = itemView.findViewById(R.id.imageView_next);

            textView_name = itemView.findViewById(R.id.textView_name);
            textView_date = itemView.findViewById(R.id.textView_date);
            textView_time = itemView.findViewById(R.id.textView_time);

        }
    }
}
