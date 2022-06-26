package ro.danserboi.quotesformindandsoul.adapters;

import static ro.danserboi.quotesformindandsoul.Config.COLLECTION_ID;
import static ro.danserboi.quotesformindandsoul.Config.COLLECTION_NAME;

import android.app.ActivityOptions;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.models.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    // Member variables.
    private final ArrayList<Review> mReviewsData;
    private final Context mContext;

    public ArrayList<Review> getReviewsData() {
        return mReviewsData;
    }

    public Context getContext() {
        return mContext;
    }

    public ReviewAdapter(Context context, ArrayList<Review> reviewsData) {
        this.mReviewsData = reviewsData;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.review_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder,
                                 int position) {
        // Get current review.
        Review currentReview = mReviewsData.get(position);

        // Populate text views with data.
        holder.bindTo(currentReview);
    }

    @Override
    public int getItemCount() {
        return mReviewsData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView name;
        private final TextView text;
        private final TextView txt_date;
        private final RatingBar ratingBar;

        ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            text = itemView.findViewById(R.id.text);
            txt_date = itemView.findViewById(R.id.txt_date);
            ratingBar = itemView.findViewById(R.id.ratingBar);

            itemView.setOnClickListener(this);
        }

        void bindTo(Review currentReview) {
            name.setText(currentReview.getUsername());
            text.setText(currentReview.getText());
            ratingBar.setRating(currentReview.getRating());
        }

        @Override
        public void onClick(View v) {
        }
    }
}
