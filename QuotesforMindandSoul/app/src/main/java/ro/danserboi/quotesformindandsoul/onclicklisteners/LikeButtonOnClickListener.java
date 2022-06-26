package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.content.Context;
import android.view.View;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class LikeButtonOnClickListener implements View.OnClickListener {
    Context context;
    Quote currentQuote;

    public LikeButtonOnClickListener(Context context, Quote currentQuote) {
        this.context = context;
        this.currentQuote = currentQuote;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        if (((ToggleButton) view).isChecked()) {
            ((ToggleButton) view).setText(currentQuote.getLikesCount() + 1 + " Likes");
            UserPrefs userPrefs = new UserPrefs(context);
            RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
            Call<Void> call = api.likeQuote("Bearer " + userPrefs.getJWT(), currentQuote.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        currentQuote.setLiked(true);
                        currentQuote.setLikesCount(currentQuote.getLikesCount() + 1);
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
        } else {
            ((ToggleButton) view).setText(currentQuote.getLikesCount() - 1 + " Likes");
            UserPrefs userPrefs = new UserPrefs(context);
            RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
            Call<Void> call = api.dislikeQuote("Bearer " + userPrefs.getJWT(), currentQuote.getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        currentQuote.setLiked(false);
                        currentQuote.setLikesCount(currentQuote.getLikesCount() - 1);
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
        }
    }

}
