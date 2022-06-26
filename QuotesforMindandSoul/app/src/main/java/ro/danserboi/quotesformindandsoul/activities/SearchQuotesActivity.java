package ro.danserboi.quotesformindandsoul.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.adapters.QuoteAdapter;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.responses.GetQuotesResponse;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class SearchQuotesActivity extends AppCompatActivity{
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;
    private Dialog dialog;
    LinearLayoutManager manager;
    Boolean isScrolling = false;
    int currentItems, scrollOutItems, totalItems;
    int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_quotes);

        String searchMethod = getIntent().getStringExtra("method");
        String input = getIntent().getStringExtra("input");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.search_quotes_recyclerview);
        // Set the GridLayoutManager.
        manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        // Initialize the ArrayList that will contain the data.
        mQuotesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new QuoteAdapter(this, mQuotesData);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    if(currentPage != -1)
                        searchQuotes(searchMethod, input);
                }
            }
        });

        if(input != null) {
            SearchQuotesActivity.this.setTitle("Results for " + input);
        }

        searchQuotes(searchMethod, input);
    }

    private void searchQuotes(String searchMethod, String input) {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<GetQuotesResponse> call = null;
        if(searchMethod.equals(Config.QUERY_AUTHOR_PARAM)) {
            call = api.getAllQuotes("Bearer " + userPrefs.getJWT(), input, null, currentPage);
        } else if (searchMethod.equals(Config.QUERY_GENRE_PARAM)) {
            call = api.getAllQuotes("Bearer " + userPrefs.getJWT(), null, input.toLowerCase(Locale.ROOT), currentPage);
        }

        dialog.show();
        assert call != null;
        call.enqueue(new Callback<GetQuotesResponse>() {
            @Override
            public void onResponse(Call<GetQuotesResponse> call, Response<GetQuotesResponse> response) {
                if(response.isSuccessful()) {
                    dialog.dismiss();
                    currentPage = Utils.extractPage(response.body().getLinks().getNext());
                    List<Quote> data = response.body().getData().getQuotes();
                    mQuotesData.addAll(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GetQuotesResponse> call, Throwable t) {
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