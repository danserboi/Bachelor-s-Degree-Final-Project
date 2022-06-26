package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.Config.EXTRA_ID;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.onclicklisteners.AddToCollectionOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ChangeBackgroundOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ChangeFontButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.CopyButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ExportImageOnClickListenerDailyQuote;
import ro.danserboi.quotesformindandsoul.onclicklisteners.LikeButtonOnClickListenerDailyQuote;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ShareButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class QuoteOfTheDayActivity extends AppCompatActivity {
    Quote dailyQuote;

    Button mCopyButton;
    ToggleButton mFavoriteButton;
    Button mShareButton;
    Button mChangeFontButton;
    Button mChangeBackgroundButton;
    Button mExportImageButton;
    ImageButton addToCollectionButtonSingle;

    TextView username;
    TextView numberOfReviews;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_quote);

        TextView mWordsTextView = findViewById(R.id.wordsSingle);
        TextView mAuthorTextView = findViewById(R.id.authorSingle);

        mCopyButton = findViewById(R.id.copyButtonSingle);
        mFavoriteButton = findViewById(R.id.favoriteButtonSingle);
        mShareButton = findViewById(R.id.shareButtonSingle);
        mChangeFontButton = findViewById(R.id.changeFontButtonSingle);
        mChangeBackgroundButton = findViewById(R.id.changeBackgroundButtonSingle);
        mExportImageButton = findViewById(R.id.exportImageButtonSingle);
        addToCollectionButtonSingle = findViewById(R.id.addToCollectionButtonSingle);

        username = findViewById(R.id.username);
        numberOfReviews = findViewById(R.id.numberOfReviews);
        ratingBar = findViewById(R.id.ratingBar);

        // We set OnClickListeners to buttons
        mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mFavoriteButton.setOnClickListener(new LikeButtonOnClickListenerDailyQuote(this));
        mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
        mExportImageButton.setOnClickListener(new ExportImageOnClickListenerDailyQuote(this));

        // get quote
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<Quote> call = api.getQuote("Bearer " + userPrefs.getJWT(), userPrefs.getDailyQuoteId());
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    dailyQuote = response.body();

                    addToCollectionButtonSingle.setOnClickListener(new AddToCollectionOnClickListener(QuoteOfTheDayActivity.this, dailyQuote.getId()));
                    numberOfReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.buttonAnimation(view);

                            Intent detailIntent = new Intent(QuoteOfTheDayActivity.this, ReviewsActivity.class);
                            detailIntent.putExtra(EXTRA_ID, dailyQuote.getId());
                            startActivityForResult(detailIntent, Config.REVIEW_REQUEST);
                        }
                    });

                    mWordsTextView.setText(dailyQuote.getText());
                    mAuthorTextView.setText(dailyQuote.getAuthor());

                    mFavoriteButton.setChecked(dailyQuote.getLiked());

                    username.setText(dailyQuote.getOwnerName());
                    numberOfReviews.setText(dailyQuote.getReviewsNo().toString() + " Reviews");
                    ratingBar.setRating(dailyQuote.getReviewsAvg());
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });

    }

    public Quote getDailyQuote() {
        return dailyQuote;
    }

    public Button getCopyButton() {
        return mCopyButton;
    }

    public ToggleButton getFavoriteButton() {
        return mFavoriteButton;
    }

    public Button getShareButton() {
        return mShareButton;
    }

    public Button getChangeFontButton() {
        return mChangeFontButton;
    }

    public Button getChangeBackgroundButton() {
        return mChangeBackgroundButton;
    }

    public Button getExportImageButton() {
        return mExportImageButton;
    }

    public ImageButton getAddToCollectionButtonSingle() {
        return addToCollectionButtonSingle;
    }

    public TextView getUsername() {
        return username;
    }

    public TextView getNumberOfReviews() {
        return numberOfReviews;
    }

    public RatingBar getRatingBar() {
        return ratingBar;
    }
}