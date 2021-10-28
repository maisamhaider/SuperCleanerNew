package com.example.junckcleaner.duplicatenew.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.junckcleaner.R;
import com.example.junckcleaner.annotations.MyAnnotations;
import com.example.junckcleaner.duplicatenew.models.FileDetails;
import com.example.junckcleaner.duplicatenew.utils.DuplicateListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdapterGroup extends RecyclerView.Adapter<AdapterGroup.ContactViewHolder> {
    private final Context context;
    private ArrayList<FileDetails> fileDetailsArrayList;
    DuplicateListener duplicateListener;
    String scanType = "";

    class ContactViewHolder extends RecyclerView.ViewHolder {
        final CheckBox chcekbox;
        final TextView textViewTitle;
        final ImageView imageViewIcon, imageViewPlay;

        ContactViewHolder(View itemView) {
            super(itemView);
            this.textViewTitle = itemView.findViewById(R.id.tvTitle);
            this.imageViewIcon = itemView.findViewById(R.id.duplicate_video);
            this.imageViewPlay = itemView.findViewById(R.id.imageViewPlay);
            this.chcekbox = itemView.findViewById(R.id.checkbox_video);
        }
    }

    public AdapterGroup(Context context,
                        ArrayList<FileDetails> fileDetailsArrayList,
                        DuplicateListener duplicateListener, String scanType) {
        this.context = context;
        this.fileDetailsArrayList = fileDetailsArrayList;
        this.duplicateListener = duplicateListener;
        this.scanType = scanType;

    }

    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_video, parent, false));
    }

    public void onBindViewHolder(final ContactViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final FileDetails fileDetails = this.fileDetailsArrayList.get(position);
        holder.textViewTitle.setText(fileDetails.getFileName());
        holder.chcekbox.setChecked(fileDetails.isChecked());
        switch (scanType) {
            case MyAnnotations.IMAGES:
                Glide.with(context).load("file:///" + Uri.decode(fileDetails.getFilePath()))
                        .into(holder.imageViewIcon);
                break;
            case MyAnnotations.VIDEOS:
                holder.imageViewPlay.setVisibility(View.VISIBLE);
                Glide.with(context).load("file:///" +
                        Uri.decode(fileDetails.getFilePath())).into(holder.imageViewIcon);
                break;
            case MyAnnotations.AUDIOS:
                Glide.with(context).load(R.drawable.ic_audio_icon).into(holder.imageViewIcon);
                break;
            case MyAnnotations.DOCUMENTS:
                Glide.with(context).load(R.drawable.ic_document_icon).into(holder.imageViewIcon);
                break;
            case MyAnnotations.ALL_SCAN:
                String str = fileDetails.getFilePath();
                if (str.contains(".")) {
                    String extension = str.substring(str.lastIndexOf(".") + 1);
                    extension = "." + extension;
                    Log.e("NewGroupImagesAdapter", extension);
                    if (imagesExtensions().contains(extension.toLowerCase())) {
                        Glide.with(context).load("file:///" + Uri.decode(fileDetails.getFilePath()))
                                .into(holder.imageViewIcon);
                    } else if (videosExtensions().contains(extension.toLowerCase())) {
                        holder.imageViewPlay.setVisibility(View.VISIBLE);
                        Glide.with(context).load("file:///" +
                                Uri.decode(fileDetails.getFilePath())).into(holder.imageViewIcon);
                    } else if (audiosExtensions().contains(extension.toLowerCase())) {
                        Glide.with(context).load(R.drawable.ic_audio_icon).into(holder.imageViewIcon);
                    } else if (documentsExtensions().contains(extension.toLowerCase())) {
                        Glide.with(context).load(R.drawable.ic_document_icon).into(holder.imageViewIcon);

                    } else {
                        Glide.with(context).load(R.drawable.ic_document_icon).
                                into(holder.imageViewIcon);
                    }
                } else {
                    Glide.with(context).load(R.drawable.ic_document_icon).
                            into(holder.imageViewIcon);
                }

                break;
        }


        holder.chcekbox.setOnClickListener(view -> {
            fileDetails.setChecked(!fileDetails.isChecked());
            if (duplicateListener != null) {
                duplicateListener.duplicateListener();
            }
            holder.chcekbox.setChecked(fileDetails.isChecked());
        });


        holder.itemView.setOnClickListener(view -> {
            if (scanType.equals(MyAnnotations.IMAGES)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() +
                                ".fileprovider", new File(fileDetails.getFilePath()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "image/*");
                context.startActivity(Intent.createChooser(intent, "View image"));
                return;
            }
            if (scanType.equals(MyAnnotations.VIDEOS)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() +
                                ".fileprovider", new File(fileDetails.getFilePath()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "video/*");
                context.startActivity(Intent.createChooser(intent, "Play video"));
                return;
            }
            if (scanType.equals(MyAnnotations.AUDIOS)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() +
                                ".fileprovider", new File(fileDetails.getFilePath()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "audio/*");
                context.startActivity(Intent.createChooser(intent, "Play audio"));
                return;
            } else if (scanType.equals(MyAnnotations.DOCUMENTS)) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(context,
                        context.getApplicationContext().getPackageName() +
                                ".fileprovider", new File(fileDetails.getFilePath()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(uri, "application/*");
                context.startActivity(Intent.createChooser(intent, "Open document"));
                return;

            } else if (scanType.equals(MyAnnotations.ALL_SCAN)) {
                String str = fileDetails.getFilePath();
                if (str.contains(".")) {
                    String extension = str.substring(str.lastIndexOf(".") + 1);
                    extension = "." + extension;
                    Log.e("NewGroupImagesAdapter", extension);
                    if (imagesExtensions().contains(extension.toLowerCase())) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() +
                                        ".fileprovider", new File(fileDetails.getFilePath()));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "image/*");
                        context.startActivity(Intent.createChooser(intent, "View image"));

                    } else if (videosExtensions().contains(extension.toLowerCase())) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() +
                                        ".fileprovider", new File(fileDetails.getFilePath()));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "video/*");
                        context.startActivity(Intent.createChooser(intent, "Play video"));

                    } else if (audiosExtensions().contains(extension.toLowerCase())) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() +
                                        ".fileprovider", new File(fileDetails.getFilePath()));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "audio/*");
                        context.startActivity(Intent.createChooser(intent, "Play audio"));

                    } else if (documentsExtensions().contains(extension.toLowerCase())) {

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() +
                                        ".fileprovider", new File(fileDetails.getFilePath()));
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(uri, "application/*");
                        context.startActivity(Intent.createChooser(intent, "Open document"));

                    }
                }


                return;

            }
        });
    }

    public int getItemCount() {
        return fileDetailsArrayList.size();
    }

    public List<String> imagesExtensions() {
        List<String> list = new ArrayList<>();
        list.add(".jpg");
        list.add(".png");
        list.add(".gif");
        list.add(".webp");
        list.add(".tiff");
        list.add(".psd");
        list.add(".raw");
        list.add(".bmp");
        list.add(".heif");
        list.add(".indd");
        list.add(".jpeg");
        list.add(".svg");
        list.add(".arw");
        list.add(".cr2");
        list.add(".nrw");
        list.add(".k25");
        list.add(".dib");
        list.add(".heic");
        list.add(".ind");
        list.add(".indt");
        list.add(".jp2");
        list.add(".j2k");
        list.add(".jpf");
        list.add(".jpx");
        list.add(".jpm");
        list.add(".mj2");
        list.add(".svgz");
        list.add(".tif");

        return list;
    }

    public List<String> videosExtensions() {
        List<String> list = new ArrayList<>();
        list.add(".webm");
        list.add(".mkv");
        list.add(".flv");
        list.add(".vob");
        list.add(".ogv");
        list.add(".ogg");
        list.add(".rrc");
        list.add(".gifv");
        list.add(".mng");
        list.add(".mov");
        list.add(".avi");
        list.add(".qt");
        list.add(".wmv");
        list.add(".yuv");
        list.add(".rm");
        list.add(".asf");
        list.add(".amv");
        list.add(".mp4");
        list.add(".m4p");
        list.add(".m4v");
        list.add(".mpg");
        list.add(".mp2");
        list.add(".mpeg");
        list.add(".mpe");
        list.add(".mpv");
        list.add(".m4v");
        list.add(".svi");
        list.add(".3gp");
        list.add(".3g2");
        list.add(".mxf");
        list.add(".roq");
        list.add(".nsv");
        list.add(".flv");
        list.add(".f4v");
        list.add(".f4p");
        list.add(".f4a");
        list.add(".f4b");


        return list;
    }

    public List<String> audiosExtensions() {
        List<String> list = new ArrayList<>();
        list.add(".3gp");
        list.add(".aa");
        list.add(".aac");
        list.add(".aax");
        list.add(".act");
        list.add(".aiff");
        list.add(".alac");
        list.add(".amr");
        list.add(".ape");
        list.add(".au");
        list.add(".awb");
        list.add(".dss");
        list.add(".dvf");
        list.add(".flac");
        list.add(".gsm");
        list.add(".iklax");
        list.add(".ivs");
        list.add(".m4b");
        list.add(".mmf");
        list.add(".mp3");
        list.add(".mpc");
        list.add(".msv");
        list.add(".nmf");
        list.add(".wav");
        list.add(".wma");
        list.add(".wv");
        list.add(".webm");


        return list;
    }

    public List<String> documentsExtensions() {
        List<String> list = new ArrayList<>();
        list.add(".pdf");
        list.add(".docx");
        list.add(".dotx");
        list.add(".docm");
        list.add(".dot");
        list.add(".doc");
        list.add(".ppt");
        list.add(".snp");
        list.add(".ppsm");
        list.add(".pptm");
        list.add(".pdi");
        list.add(".pot");
        list.add(".adp");
        list.add(".1sp");
        list.add(".potx");
        list.add(".xsn");
        list.add(".vsd");
        list.add(".mpp");
        list.add(".pmx");
        list.add(".mpx");
        list.add(".js");
        list.add(".py");
        list.add(".csv");
        list.add(".a7z");
        list.add(".csv");
        list.add(".aql");
        list.add(".html");
        list.add(".php");
        list.add(".exi");
        list.add(".apk");
        list.add(".db");
        list.add(".log");
        list.add(".temp");

        return list;
    }
}