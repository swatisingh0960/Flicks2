package com.nanodegree.swatisingh.popularmoviesapp2.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.swatisingh.popularmoviesapp2.Model.Review;
import com.nanodegree.swatisingh.popularmoviesapp2.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Review[] reviewsList;
    private Context context;

    public ReviewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_list_item,parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        holder.mAuthor.setText(reviewsList[position].getAuthor());
        holder.mContent.setText(reviewsList[position].getContent());
    }

    @Override
    public int getItemCount() {
        if (reviewsList == null) {
            return 0;
        }
        return reviewsList.length;
    }

    public void setReviewsList (Review[] reviews) {
        reviewsList = reviews;
    }

    public Review[] getReviewsList(){
        return reviewsList;
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView mAuthor, mContent;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            mAuthor = itemView.findViewById(R.id.author_review);
            mContent = itemView.findViewById(R.id.review_content);
        }
    }
}
