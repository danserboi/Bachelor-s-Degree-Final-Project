package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.CollectionsActivity;
import ro.danserboi.quotesformindandsoul.adapters.CollectionAdapter;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.requests.NameWrapper;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class UpdateCollectionOnClickListener implements View.OnClickListener{
    CollectionAdapter collectionAdapter;
    TextView title;
    CollectionsActivity context;
    List<Collection> collections;
    Collection collection;

    public UpdateCollectionOnClickListener(CollectionAdapter collectionAdapter, TextView title) {
        this.collectionAdapter = collectionAdapter;
        this.title = title;
        this.context = (CollectionsActivity)collectionAdapter.getContext();
        this.collections = collectionAdapter.getCollectionsData();
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        // update collection
        collection = Utils.getCollectionByName(collections, title.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ViewGroup viewGroup = ((CollectionsActivity) context).findViewById(android.R.id.content);
        final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.rename_collection_dialog, viewGroup, false);
        EditText editText = dialogView.findViewById(R.id.collection_input_text);
        editText.setText(title.getText().toString());
        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, id) -> {
            EditText inputTextView = dialogView.findViewById(R.id.collection_input_text);
            String inputText = inputTextView.getText().toString();
            if (TextUtils.isEmpty(inputText)) {
                Utils.displayToast(context, context.getString(R.string.text_not_entered));
            } else {
                if(inputText.equals(collection.getName())) {
                    Utils.displayToast(context, "You didn't modify the collection name.");
                } else {
                    UserPrefs userPrefs = new UserPrefs(context);
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    Call<Void> call = api.updateCollection("Bearer " + userPrefs.getJWT(), collection.getId(), new NameWrapper(inputText));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(response.isSuccessful()) {
                                collection.setName(inputText);
                                title.setText(inputText);
                                collectionAdapter.notifyDataSetChanged();
                                Utils.displayToast(context, collection.getName() + " collection has been renamed.");
                            } else {
                                assert response.errorBody() != null;
                                Utils.processErrorResponse(context, response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {

                        }
                    });
                }

            }


        });
        builder.setNeutralButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
        });
        builder.setNegativeButton(R.string.delete, (dialog, id) -> {
            UserPrefs userPrefs = new UserPrefs(context);
            RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
            Call<Void> call = api.deleteCollection("Bearer " + userPrefs.getJWT(), collection.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if(response.isSuccessful()) {
                        collections.remove(collection);
                        collectionAdapter.notifyDataSetChanged();
                        Utils.displayToast(context, collection.getName() + " collection has been deleted.");
                    } else {
                        assert response.errorBody() != null;
                        Utils.processErrorResponse(context, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Utils.displayToast(context, "Network connection error.");
                }
            });
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



}
