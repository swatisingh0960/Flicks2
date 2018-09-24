package com.nanodegree.swatisingh.popularmoviesapp2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nanodegree.swatisingh.popularmoviesapp2.Model.Movie;
import com.nanodegree.swatisingh.popularmoviesapp2.R;
import com.squareup.picasso.Picasso;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
        private Movie[] moviesList;
        private Context context;

        public static final String TAG = "MovieAdapter";

        private final MovieItemClickListener mClickListener;

        public interface MovieItemClickListener{
            void onClick(int id);
        }

        public MovieAdapter(MovieItemClickListener clickListener){
            mClickListener = clickListener;
        }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.MovieViewHolder holder, int position) {
        String posterPath = moviesList[position].getPoster_path();
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/" + posterPath)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
            if (moviesList == null) return 0;
        return moviesList.length;
    }

    public void setMovieData(Movie[] moviesData) {
            moviesList = moviesData;
    }

    public Movie[] getMovieData(){
            return moviesList;
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

          ImageView posterImageView;

          public MovieViewHolder(View itemView){
              super(itemView);
              posterImageView = itemView.findViewById(R.id.iv_poster);
              itemView.setOnClickListener(this);
          }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
          int movieId = moviesList[adapterPosition].getId();
          mClickListener.onClick(movieId);
          }
    }
}