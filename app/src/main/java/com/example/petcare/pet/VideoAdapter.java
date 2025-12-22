package com.example.petcare.pet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petcare.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<Uri> videoUris;

    public VideoAdapter(Context context, List<Uri> videoUris) {
        this.context = context;
        this.videoUris = videoUris;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Uri videoUri = videoUris.get(position);

        // 🎬 Generate thumbnail
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoUri);

            Bitmap thumbnail = retriever.getFrameAtTime(
                    1_000_000,
                    MediaMetadataRetriever.OPTION_CLOSEST
            );

            if (thumbnail != null) {
                holder.thumbnail.setImageBitmap(thumbnail);
            }

            retriever.release();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ▶️ Click → fullscreen player
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("video_uri", videoUri.toString());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoUris.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.videoThumbnail);
        }
    }
}
