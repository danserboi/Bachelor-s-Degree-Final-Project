package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.app.Activity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.responses.CollectionList;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class AddToCollectionOnClickListener implements View.OnClickListener{
    Activity activity;
    Integer quoteId;

    public AddToCollectionOnClickListener(Activity activity, Integer quoteId) {
        this.activity = activity;
        this.quoteId = quoteId;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);

        UserPrefs userPrefs = new UserPrefs(activity);
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<CollectionList> call = api.getCollections("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<CollectionList>() {
            @Override
            public void onResponse(Call<CollectionList> call, Response<CollectionList> response) {
                if(response.isSuccessful()) {
                    if(response.body().getCollections().isEmpty()) {
                        Utils.displayToast(activity, "You don't have any collection to add this quote to.");
                    } else {
                        PopupMenu popupMenu = new PopupMenu(activity, view);
                        for(Collection c : response.body().getCollections()) {
                            popupMenu.getMenu().add(c.getName());
                        }
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                String collectionName = menuItem.getTitle().toString();
                                Call<Void> call = api.addQuoteToCollection("Bearer " + userPrefs.getJWT(), quoteId, collectionName);
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(response.isSuccessful()) {
                                            Utils.displayToast(activity, "Quote has been added to collection.");
                                        } else {
                                            assert response.errorBody() != null;
                                            Utils.processErrorResponse(activity, response.errorBody());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        Utils.displayToast(activity, "Network connection error.");
                                    }
                                });


                                return true;
                            }
                        });
                        popupMenu.show();
                    }
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(activity, response.errorBody());
                }
            }
            @Override
            public void onFailure(Call<CollectionList> call, Throwable t) {
                Utils.displayToast(activity, "Network connection error.");
            }
        });
    }

    void addQuoteToCollection(Collection collection) {

    }

}
