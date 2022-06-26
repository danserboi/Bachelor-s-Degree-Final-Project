package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.Config.EXTRA_AUTHOR;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_ID;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_LIKED;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_LIKES_COUNT;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_OWNER_NAME;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_POSITION;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_REVIEWS_AVG;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_REVIEWS_NO;
import static ro.danserboi.quotesformindandsoul.Config.EXTRA_TEXT;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.onclicklisteners.AddToCollectionOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ChangeBackgroundOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ChangeFontButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.CopyButtonOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ExportImageOnClickListener;
import ro.danserboi.quotesformindandsoul.onclicklisteners.LikeButtonOnClickListenerSingle;
import ro.danserboi.quotesformindandsoul.onclicklisteners.ShareButtonOnClickListener;

public class SingleQuoteActivity extends AppCompatActivity {
    Integer quotePosition = -1;
    Integer id;
    String author;
    String text;
    Boolean liked;
    Integer likesCount;
    String ownerName;
    String genre;
    Integer reviewsNo;
    Float reviewsAvg;

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

    public Button getExportImageButton() {
        return mExportImageButton;
    }

    public Button getChangeBackgroundButton() {
        return mChangeBackgroundButton;
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

    public Integer getId() {
        return id;
    }

    public Boolean getLiked() {
        return liked;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

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

        Intent detailIntent = getIntent();

        if (detailIntent != null) {
            quotePosition = detailIntent.getIntExtra(EXTRA_POSITION, -1);
            id = detailIntent.getIntExtra(EXTRA_ID, 0);
            author = detailIntent.getStringExtra(EXTRA_AUTHOR);
            text = detailIntent.getStringExtra(EXTRA_TEXT);
            liked = detailIntent.getBooleanExtra(EXTRA_LIKED, false);
            likesCount = detailIntent.getIntExtra(EXTRA_LIKES_COUNT, 0);
            ownerName = detailIntent.getStringExtra(EXTRA_OWNER_NAME);
            reviewsNo = detailIntent.getIntExtra(EXTRA_REVIEWS_NO, 0);
            reviewsAvg = detailIntent.getFloatExtra(EXTRA_REVIEWS_AVG, 0);

            mWordsTextView.setText(text);
            mAuthorTextView.setText(author);

            mFavoriteButton.setChecked(liked);

            username.setText(ownerName);
            numberOfReviews.setText(reviewsNo.toString() + " Reviews");
            ratingBar.setRating(reviewsAvg);

        }

        // We set OnClickListeners to buttons
        mCopyButton.setOnClickListener(new CopyButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mFavoriteButton.setOnClickListener(new LikeButtonOnClickListenerSingle(this));
        mShareButton.setOnClickListener(new ShareButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeFontButton.setOnClickListener(new ChangeFontButtonOnClickListener(this, mWordsTextView, mAuthorTextView));
        mChangeBackgroundButton.setOnClickListener(new ChangeBackgroundOnClickListener(this));
        mExportImageButton.setOnClickListener(new ExportImageOnClickListener(this));
        addToCollectionButtonSingle.setOnClickListener(new AddToCollectionOnClickListener(this, id));
        numberOfReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.buttonAnimation(view);

                Intent detailIntent = new Intent(SingleQuoteActivity.this, ReviewsActivity.class);
                detailIntent.putExtra(EXTRA_ID, id);
                startActivityForResult(detailIntent, Config.REVIEW_REQUEST);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent stateIntent = new Intent();

        stateIntent.putExtra(EXTRA_POSITION, quotePosition);
        stateIntent.putExtra(EXTRA_LIKED, liked);
        stateIntent.putExtra(EXTRA_LIKES_COUNT, likesCount);

        setResult(RESULT_OK, stateIntent);
        finish();
        super.onBackPressed();
    }
}