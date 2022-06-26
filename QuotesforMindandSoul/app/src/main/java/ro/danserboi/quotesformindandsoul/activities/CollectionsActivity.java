package ro.danserboi.quotesformindandsoul.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.adapters.CollectionAdapter;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.responses.CollectionList;
import ro.danserboi.quotesformindandsoul.requests.NameWrapper;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class CollectionsActivity extends AppCompatActivity {
    private ArrayList<Collection> mCollectionsData;
    private CollectionAdapter mAdapter;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);


        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = findViewById(R.id.collections_recyclerview);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new
                GridLayoutManager(this, gridColumnCount));
        mRecyclerView.setHasFixedSize(true);
        // Initialize the ArrayList that will contain the data.
        mCollectionsData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new CollectionAdapter(this, mCollectionsData);
        mRecyclerView.setAdapter(mAdapter);

        fab = findViewById(R.id.fab_collections);
        setFabOnClickListener(fab);

        getData();

    }

    private void getData() {
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<CollectionList> call = api.getCollections("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<CollectionList>() {
            @Override
            public void onResponse(Call<CollectionList> call, Response<CollectionList> response) {
                if(response.isSuccessful()) {
                    mCollectionsData.addAll(response.body().getCollections());
                    mAdapter.notifyDataSetChanged();
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<CollectionList> call, Throwable t) {
                Utils.displayToast(getApplicationContext(), "Network connection error.");
            }
        });
    }

    private void setFabOnClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.create_collection_dialog, viewGroup, false);
            builder.setView(dialogView);
            builder.setPositiveButton("Create", (dialog, id) -> {
                // User clicked Create button

                EditText inputTextView = dialogView.findViewById(R.id.create_collection_input_text);
                String inputText = inputTextView.getText().toString();
                if (TextUtils.isEmpty(inputText)) {
                    Utils.displayToast(this, getString(R.string.text_not_entered));
                } else {
                        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
                        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                        Call<Collection> call = api.createCollection("Bearer " + userPrefs.getJWT(), new NameWrapper(inputText));
                        call.enqueue(new Callback<Collection>() {
                            @Override
                            public void onResponse(Call<Collection> call, Response<Collection> response) {
                                if(response.isSuccessful()) {
                                    mCollectionsData.add(response.body());
                                    mAdapter.notifyDataSetChanged();
                                    Utils.displayToast(getApplicationContext(), inputText + " collection has been created.");
                                } else {
                                    assert response.errorBody() != null;
                                    Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                                }
                            }

                            @Override
                            public void onFailure(Call<Collection> call, Throwable t) {
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