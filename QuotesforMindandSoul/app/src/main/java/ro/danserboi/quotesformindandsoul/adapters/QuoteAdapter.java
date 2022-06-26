package ro.danserboi.quotesformindandsoul.adapters;

import static ro.danserboi.quotesformindandsoul.Config.EXTRA_AUTHOR;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_ID;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_LIKED;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_LIKES_COUNT;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_OWNER_NAME;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_POSITION;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_REVIEWS_AVG;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_REVIEWS_NO;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_TEXT;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.activities.MyQuotesActivity;
import ro.danserboi.quotesformindandsoul.activities.SingleQuoteActivity;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.onclicklisteners.DeleteQuoteOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.LikeButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.CopyButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ShareButtonOnClickListener;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder>{
    // Member variables.
    private final Context mContext;
    private final ArrayList<Quote> mQuotesData;

    public QuoteAdapter(Context mContext, ArrayList<Quote> mQuotesData) {
        this.mContext = mContext;
        this.mQuotesData = mQuotesData;
    }

    public Context getContext() {
        return mContext;
    }

    public ArrayList<Quote> getQuotesData() {
        return mQuotesData;
    }

    @NonNull
    @Override
    public QuoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int quote_list_item = R.layout.quote_list_item;
        if(mContext instanceof MyQuotesActivity) {
            quote_list_item = R.layout.my_quote_list_item;
        }
        return new QuoteAdapter.ViewHolder(LayoutInflater.from(mContext).
                inflate(quote_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuoteAdapter.ViewHolder holder, int position) {
        // Get current Quote.
        Quote currentQuote = mQuotesData.get(position);

        // Populate the textviews with data.
        holder.bindTo(currentQuote);
    }

    @Override
    public int getItemCount() {
        return mQuotesData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Member Variables
        private final TextView mWordsText;
        private final TextView mAuthorText;
        private final ToggleButton mFavoriteButton;
        private final ImageButton deleteQuoteButton;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mWordsText = itemView.findViewById(R.id.words);
            mAuthorText = itemView.findViewById(R.id.author);
            Button mCopyButton = itemView.findViewById(R.id.copyButton);
            mFavoriteButton = itemView.findViewById(R.id.favoriteButton);
            Button mShareButton = itemView.findViewById(R.id.shareButton);
            if(mContext instanceof MyQuotesActivity) {
                deleteQuoteButton = itemView.findViewById(R.id.editQuote);
                deleteQuoteButton.setOnClickListener(new DeleteQuoteOnClickListener(QuoteAdapter.this, mWordsText));
            } else {
                deleteQuoteButton = null;
            }
            // Set OnClickListeners to buttons
            mCopyButton.setOnClickListener(new CopyButtonOnClickListener(mContext, mWordsText, mAuthorText));
            mShareButton.setOnClickListener(new ShareButtonOnClickListener(mContext, mWordsText, mAuthorText));
            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Quote currentQuote) {
            // Populate the textviews with data.
            mWordsText.setText(currentQuote.getText());
            mAuthorText.setText(currentQuote.getAuthor());

            mFavoriteButton.setChecked(currentQuote.getLiked());
            mFavoriteButton.setText(currentQuote.getLikesCount() + " Likes");
            mFavoriteButton.setOnClickListener(new LikeButtonOnClickListener(mContext, currentQuote));
        }

        @Override
        public void onClick(View v) {
            Quote currentQuote = mQuotesData.get(getBindingAdapterPosition());
            Intent detailIntent = new Intent(mContext, SingleQuoteActivity.class);

            detailIntent.putExtra(EXTRA_POSITION, getBindingAdapterPosition());
            detailIntent.putExtra(EXTRA_ID, currentQuote.getId());
            detailIntent.putExtra(EXTRA_AUTHOR, currentQuote.getAuthor());
            detailIntent.putExtra(EXTRA_TEXT, currentQuote.getText());
            detailIntent.putExtra(EXTRA_LIKED, currentQuote.getLiked());
            detailIntent.putExtra(EXTRA_LIKES_COUNT, currentQuote.getLikesCount());
            detailIntent.putExtra(EXTRA_OWNER_NAME, currentQuote.getOwnerName());
            detailIntent.putExtra(EXTRA_REVIEWS_NO, currentQuote.getReviewsNo());
            detailIntent.putExtra(EXTRA_REVIEWS_AVG, currentQuote.getReviewsAvg());

            ((Activity) mContext).startActivityForResult(detailIntent, Config.FAVORITE_STATE_REQUEST);
        }
    }
}
