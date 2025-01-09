package com.example.videoplayer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class videoAdapter extends RecyclerView.Adapter<videoAdapter.VideoviewHolder> {

    private static List<ControllerVideo> videoList;

    public videoAdapter(List<ControllerVideo> videos) {
        this.videoList = videos;
    }

    @NonNull
    @Override
    public VideoviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vidrow, parent, false);
        return new VideoviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoviewHolder holder, int position) {
        ControllerVideo video = videoList.get(position);
        holder.txtname.setText(video.getTitle());
        Glide.with(holder.itemView.getContext())
                .load(video.getThumbnailUri())
                .placeholder(R.drawable.baseline_arrow_circle_down_24)
                .centerCrop()
                .into(holder.vid);

        // Set an OnClickListener for the item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), MainActivity2.class);

            // Pass video details to the new activity
            intent.putExtra("videoTitle", video.getTitle());
            intent.putExtra("videoThumbnailUri", video.getThumbnailUri());
            intent.putExtra("videoPath",video.getPath());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }
    public static int getPositionByTitle(String title) {
        for (int i = 0; i < videoList.size(); i++) {
            if (videoList.get(i).getTitle().equalsIgnoreCase(title)) {
                return i;
            }
        }
        return -1; // Return -1 if the title is not found
    }


    static class VideoviewHolder extends RecyclerView.ViewHolder {
        TextView txtname;
        LinearLayout llrow;
        ImageView vid;

        public VideoviewHolder(@NonNull View itemView) {
            super(itemView);
            txtname = itemView.findViewById(R.id.txtname);
            vid = itemView.findViewById(R.id.vid);
        }
    }
}
