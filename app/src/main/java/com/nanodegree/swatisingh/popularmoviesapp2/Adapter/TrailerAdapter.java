package com.nanodegree.swatisingh.popularmoviesapp2.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nanodegree.swatisingh.popularmoviesapp2.Model.Trailer;
import com.nanodegree.swatisingh.popularmoviesapp2.MovieDetailActivity;
import com.nanodegree.swatisingh.popularmoviesapp2.R;

import static com.nanodegree.swatisingh.popularmoviesapp2.MovieDetailActivity.YOUTUBE_BASE_URL;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

//    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private Trailer[] trailersList;
    private Context context;

    public TrailerAdapter(Context context) {
        this.context=context;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_list_item,viewGroup,false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder trailerViewHolder, int i) {
        trailerViewHolder.trailerName.setText(trailersList[i].getName());

        final String youtube_url = YOUTUBE_BASE_URL + trailersList[i].getKey();

        trailerViewHolder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                context.startActivity(intent);
            }
        });

        trailerViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,youtube_url);
                context.startActivity(Intent.createChooser(sharingIntent,"Sharing with - "));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (trailersList == null) {
            return 0;
        }
        return trailersList.length;
    }

    public void setTrailersList(Trailer[] trailers) {
        trailersList = trailers;
    }

    public Trailer[] getTrailersList() {
        return trailersList;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {
        TextView trailerName;
        ImageButton playButton, shareButton;

        public TrailerViewHolder(View view) {
            super(view);
            trailerName = itemView.findViewById(R.id.tv_trailer_name);
            playButton = itemView.findViewById(R.id.ib_playButton);
            shareButton= itemView.findViewById(R.id.ib_shareButton);
        }
    }
}
