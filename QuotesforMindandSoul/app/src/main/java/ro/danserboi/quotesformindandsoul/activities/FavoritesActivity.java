package ro.danserboi.quotesformindandsoul.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ToggleButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.adapters.QuoteAdapter;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.responses.QuotesList;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.favorites_quotes_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this));

        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new QuoteAdapter(FavoritesActivity.this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);

        getData();
    }

    private void getData() {
        UserPrefs userPrefs = new UserPrefs(this);
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<QuotesList> call = api.getFavorites("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<QuotesList>() {
            @Override
            public void onResponse(Call<QuotesList> call, Response<QuotesList> response) {
                if (response.isSuccessful()) {
                    mQuotesData.addAll(response.body().getQuotes());
                    mAdapter.notifyDataSetChanged();
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<QuotesList> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.FAVORITE_STATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                int quotePosition = data.getIntExtra(Config.EXTRA_POSITION, -1);
                Boolean liked = data.getBooleanExtra(Config.EXTRA_LIKED, false);
                int likesCount = data.getIntExtra(Config.EXTRA_LIKES_COUNT, -1);
                try {
                    ToggleButton favoriteButton = mRecyclerView.getLayoutManager()
                            .findViewByPosition(quotePosition)
                            .findViewById(R.id.favoriteButton);

                    favoriteButton.setChecked(liked);
                    favoriteButton.setText(likesCount + " Likes");

                    mQuotesData.get(quotePosition).setLiked(liked);
                    mQuotesData.get(quotePosition).setLikesCount(likesCount);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}