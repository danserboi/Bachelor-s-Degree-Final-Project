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
import ro.danserboi.quotesformindandsoul.onclicklisteners.ExportImageOnClickListenerRandom;
import ro.danserboi.quotesformindandsoul.onclicklisteners.LikeButtonOnClickListenerRandom;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ShareButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class RandomQuoteActivity extends AppCompatActivity{
    private Quote currentQuote;
    TextView mWordsTextView;
    TextView mAuthorTextView;

    Button mCopyButton;
    ToggleButton mFavoriteButton;
    Button mShareButton;
    Button mChangeFontButton;
    Button mChangeBackgroundButton;
    Button mExportImageButton;
    Button mAddToCollectionButton;
    ImageButton mGetAnotherQuoteButton;

    TextView username;
    TextView numberOfReviews;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_quote);

        mWordsTextView = findViewById(R.id.wordsRandom);
        mAuthorTextView = findViewById(R.id.authorRandom);
        mCopyButton = findViewById(R.id.copyButtonRandom);
        mFavoriteButton = findViewById(R.id.favoriteButtonRandom);
        mShareButton = findViewById(R.id.shareButtonRandom);
        mChangeFontButton = findViewById(R.id.changeFontButtonRandom);
        mChangeBackgroundButton = findViewById(R.id.changeBackgroundButtonRandom);
        mExportImageButton = findViewById(R.id.exportImageButtonRandom);
        mAddToCollectionButton = findViewById(R.id.addToCollectionButtonRandom);
        mGetAnotherQuoteButton = findViewById(R.id.getAnotherQuote);

        username = findViewById(R.id.username);
        numberOfReviews = findViewById(R.id.numberOfReviews);
        ratingBar = findViewById(R.id.ratingBar);


        mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mFavoriteButton.setOnClickListener(new LikeButtonOnClickListenerRandom(this));
        mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
        mExportImageButton.setOnClickListener(new ExportImageOnClickListenerRandom(this));

        getRandomQuote();
    }

    public void repeat(View view) {
        Utils.buttonAnimation(view);
        getRandomQuote();
    }

    public void getRandomQuote() {
        UserPrefs userPrefs = new UserPrefs(this);
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<Quote> call = api.getRandomQuote("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful()) {
                    currentQuote = response.body();

                    mAddToCollectionButton.setOnClickListener(new AddToCollectionOnClickListener(RandomQuoteActivity.this, currentQuote.getId()));
                    numberOfReviews.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.buttonAnimation(view);

                            Intent detailIntent = new Intent(RandomQuoteActivity.this, ReviewsActivity.class);
                            detailIntent.putExtra(EXTRA_ID, currentQuote.getId());
                            startActivityForResult(detailIntent, Config.REVIEW_REQUEST);
                        }
                    });

                    mWordsTextView.setText(currentQuote.getText());
                    mAuthorTextView.setText(currentQuote.getAuthor());

                    mFavoriteButton.setChecked(currentQuote.getLiked());

                    username.setText(currentQuote.getOwnerName());
                    numberOfReviews.setText(currentQuote.getReviewsNo().toString() + " Reviews");
                    ratingBar.setRating(currentQuote.getReviewsAvg());
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

    public Quote getCurrentQuote() {
        return currentQuote;
    }

    public TextView getWordsTextView() {
        return mWordsTextView;
    }

    public TextView getAuthorTextView() {
        return mAuthorTextView;
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

    public Button getAddToCollectionButton() {
        return mAddToCollectionButton;
    }

    public ImageButton getGetAnotherQuoteButton() {
        return mGetAnotherQuoteButton;
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