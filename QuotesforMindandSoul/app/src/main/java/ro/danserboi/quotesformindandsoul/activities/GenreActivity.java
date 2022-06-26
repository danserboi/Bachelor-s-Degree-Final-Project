package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.Utils.extractPage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;

import androidx.annotation.Nullable;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.transition.TransitionInflater;

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

public class GenreActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private QuoteAdapter mAdapter;
    private ArrayList<Quote> mQuotesData;
    private String currentQuotesCategory;
    private Dialog dialog;
    LinearLayoutManager manager;
    Boolean isScrolling = false;
    int currentItems, scrollOutItems, totalItems;
    int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.quotes_recyclerview);
        // Set the LinearLayoutManager.
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
                        getData();
                }
            }
        });


        currentQuotesCategory = getIntent().getStringExtra("title");

        final Toolbar toolbar = findViewById(R.id.anim_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(null);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(currentQuotesCategory);
        ImageView categoryImage = findViewById(R.id.categImageDetail);

        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.shared_element));

        int idx = getIntent().getIntExtra("image_resource", 0);
        TypedArray categoriesImageResources =
                getResources().obtainTypedArray(R.array.categories_images_1440p);
        int imageResource = categoriesImageResources.getResourceId(idx, 0);
        categoriesImageResources.recycle();
        Glide.with(this).load(imageResource)
                .into(categoryImage);


        getData();

    }

    private void getData() {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<GetQuotesResponse> call = api.getAllQuotes("Bearer " + userPrefs.getJWT(), null, currentQuotesCategory.toLowerCase(Locale.ROOT), currentPage);
        dialog.show();
        call.enqueue(new Callback<GetQuotesResponse>() {
            @Override
            public void onResponse(Call<GetQuotesResponse> call, Response<GetQuotesResponse> response) {
                if(response.isSuccessful()) {
                    dialog.dismiss();
                    currentPage = extractPage(response.body().getLinks().getNext());
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