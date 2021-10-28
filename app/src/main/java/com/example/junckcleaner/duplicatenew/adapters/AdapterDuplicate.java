package com.example.junckcleaner.duplicatenew.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.junckcleaner.R;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.DuplicateListener;

import java.util.ArrayList;
import java.util.List;

public class AdapterDuplicate extends RecyclerView.Adapter<AdapterDuplicate.ContactsViewHolder> {
    private List<ArrayList<FileDetails>> duplicateListDetail;
    private final Context individualOtherAdapterContext;
    private final DuplicateListener duplicateListener;
    String scanType;

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        final RecyclerView recyclerView;
        final TextView textView;

        ContactsViewHolder(View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.tv_grp_name);
            this.recyclerView = itemView.findViewById(R.id.rv_documents);
            this.recyclerView.setNestedScrollingEnabled(false);
        }
    }

    public AdapterDuplicate(Context context, DuplicateListener duplicateListener,
                            List<ArrayList<FileDetails>> duplicateListDetail, String scanType) {
        this.individualOtherAdapterContext = context;
        this.duplicateListener = duplicateListener;
        this.duplicateListDetail = duplicateListDetail;
        this.scanType = scanType;
    }

    @NonNull
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_similar_document, parent, false);
        return new ContactsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull final ContactsViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.textView.setText("Group: " + position);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(individualOtherAdapterContext);
        AdapterGroup duplicateListOthersAdapter = new AdapterGroup(
                individualOtherAdapterContext,
                duplicateListDetail.get(position),
                duplicateListener, scanType);
        holder.recyclerView.setLayoutManager(mLayoutManager);
        holder.recyclerView.setAdapter(duplicateListOthersAdapter);
    }

    public int getItemCount() {
        return this.duplicateListDetail.size();
    }

}
