package com.pt.zyfooai.ui.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.RecoverableSecurityException;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.DownloadItem;
import com.pt.zyfooai.ui.dialog.UniversalDialog;
import com.pt.zyfooai.utils.Util;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.MyViewHolder> {

    Activity context;
    List<DownloadItem> downloadItems;
    ClickListener<DownloadItem> listener;
    UniversalDialog universalDialog;
    DownloadItem downloadItem;
    int DELETE_REQUEST_URI_R = 11;
    int DELETE_REQUEST_URI_Q = 12;



    public SavedAdapter(Activity context, List<DownloadItem> downloadItems, ClickListener<DownloadItem> listener) {
        this.context = context;
        this.downloadItems = downloadItems;
        this.listener = listener;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        universalDialog = new UniversalDialog(context, false);

        if (downloadItems.get(position).isVideo) {
            Glide.with(context).load(downloadItems.get(position).uri.getPath()).into(holder.ivImage);
            holder.ivPlayVideo.setVisibility(View.VISIBLE);
        } else {
            Glide.with(context).load(downloadItems.get(position).uri).into(holder.ivImage);
            holder.ivPlayVideo.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(downloadItems.get(position)));

        holder.delete.setOnClickListener(v -> {
            downloadItem = downloadItems.get(position);


            showDeleteDialog();
        });
    }


    private void showDeleteDialog() {
        universalDialog.showDeleteDialog(context.getString(R.string.delete), context.getString(R.string.sure_delete), context.getString(R.string.delete), context.getString(R.string.cancel));
        universalDialog.show();

        universalDialog.okBtn.setOnClickListener(v -> {
            universalDialog.cancel();
            try {
                if (downloadItem.isVideo) {
                    File fdelete = new File(downloadItem.uri.getPath());
                    if (fdelete.exists()) {
                        if (fdelete.delete()) {
                            Util.showLog("file Deleted :" + downloadItem.uri.getPath());
                            Toast.makeText(context, context.getResources().getString(R.string.video_deleted), Toast.LENGTH_SHORT).show();
                            int delete = context.getContentResolver().delete(downloadItem.uri, null, null);

                        }
                    }
                } else {
                    int delete = context.getContentResolver().delete(downloadItem.uri, null, null);
                    if (delete == 1) {
                        Toast.makeText(context, context.getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception exception) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && exception instanceof RecoverableSecurityException) {
                    try {
                        ArrayList<Uri> arrayListUri = new ArrayList<>();
                        arrayListUri.add(downloadItem.uri);
                        PendingIntent editPendingIntent = MediaStore.createDeleteRequest(context.getContentResolver(), arrayListUri);
                        context.startIntentSenderForResult(editPendingIntent.getIntentSender(), DELETE_REQUEST_URI_R, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Util.showErrorLog(e.getMessage(), e);
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && exception instanceof RecoverableSecurityException) {
                    try {
                        context.startIntentSenderForResult(((RecoverableSecurityException) exception).getUserAction().getActionIntent().getIntentSender(), DELETE_REQUEST_URI_Q, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Util.showErrorLog(e.getMessage(), e);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return downloadItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cvBase;
        ImageView ivImage;
        ImageView ivPlayVideo;
        ImageView delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cvBase = itemView.findViewById(R.id.cv_base);
            ivImage = itemView.findViewById(R.id.iv_post);
            ivPlayVideo = itemView.findViewById(R.id.iv_play_video);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
