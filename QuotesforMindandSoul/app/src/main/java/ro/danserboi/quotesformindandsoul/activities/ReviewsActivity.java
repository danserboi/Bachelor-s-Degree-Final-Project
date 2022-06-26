package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.Config.EXTRA_ID;
import static ro.danserboi.quotesformindandsoul.Utils.extractPage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.adapters.ReviewAdapter;
import ro.danserboi.quotesformindandsoul.models.Review;
import ro.danserboi.quotesformindandsoul.requests.AddReviewRequest;
import ro.danserboi.quotesformindandsoul.responses.GetReviewsResponse;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class ReviewsActivity extends AppCompatActivity {
    Integer quoteId;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mAdapter;
    private ArrayList<Review> mReviewsData;
    private FloatingActionButton fab;
    LinearLayoutManager manager;
    Boolean isScrolling = false;
    int currentItems, scrollOutItems, totalItems;
    int currentPage = 1;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setView(R.layout.progress);
        dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        // Initialize the RecyclerView.
        mRecyclerView = findViewById(R.id.reviews_recyclerview);
        // Set the GridLayoutManager.
        manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);

        // Initialize the ArrayList that will contain the data.
        mReviewsData = new ArrayList<>();

        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ReviewAdapter(ReviewsActivity.this, mReviewsData);
        mRecyclerView.setAdapter(mAdapter);

        Intent detailIntent = getIntent();

        if (detailIntent != null) {
            quoteId = detailIntent.getIntExtra(EXTRA_ID, 0);
        }

        fab = findViewById(R.id.fab_reviews);
        setFabOnClickListener(fab);

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

        getData();
    }


    private void getData() {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<GetReviewsResponse> call = api.getAllReviews("Bearer " + userPrefs.getJWT(), currentPage, quoteId);
        call.enqueue(new Callback<GetReviewsResponse>() {
            @Override
            public void onResponse(Call<GetReviewsResponse> call, Response<GetReviewsResponse> response) {
                if(response.isSuccessful()) {
                    dialog.dismiss();
                    currentPage = extractPage(response.body().getLinks().getNext());
                    List<Review> data = response.body().getData().getReviews();
                    mReviewsData.addAll(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GetReviewsResponse> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });


    }

    private void setFabOnClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.add_review_dialog, viewGroup, false);
            builder.setView(dialogView);
            builder.setPositiveButton("Add", (dialog, id) -> {
                EditText inputTextView = dialogView.findViewById(R.id.reviewEditText);
                String inputText = inputTextView.getText().toString();
                RatingBar ratingBar = dialogView.findViewById(R.id.ratingBar);
                int rating = (int)ratingBar.getRating();
                if (TextUtils.isEmpty(inputText)) {
                    Utils.displayToast(this, getString(R.string.text_not_entered));
                } else {
                    UserPrefs userPrefs = new UserPrefs(getApplicationContext());
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    Call<Review> call = api.addReview("Bearer " + userPrefs.getJWT(), new AddReviewRequest(quoteId, inputText, rating));
                    call.enqueue(new Callback<Review>() {
                        @Override
                        public void onResponse(Call<Review> call, Response<Review> response) {
                            if(response.isSuccessful()) {
                                mReviewsData.add(response.body());
                                mAdapter.notifyDataSetChanged();
                                Utils.displayToast(getApplicationContext(), "Review has been added.");
                            } else {
                                assert response.errorBody() != null;
                                Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Review> call, Throwable t) {
                            Utils.displayToast(getApplicationContext(), "Network connection error.");
                        }
                    });
                }


            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

}