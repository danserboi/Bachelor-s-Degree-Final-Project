package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.content.Context;
import android.view.View;
import android.widget.ToggleButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.QuoteOfTheDayActivity;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class LikeButtonOnClickListenerDailyQuote implements View.OnClickListener {
    Context context;

    public LikeButtonOnClickListenerDailyQuote(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        if (((ToggleButton) view).isChecked()) {
            UserPrefs userPrefs = new UserPrefs(context);
            RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
            Call<Void> call = api.likeQuote("Bearer " + userPrefs.getJWT(), ((QuoteOfTheDayActivity)context).getDailyQuote().getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        ((QuoteOfTheDayActivity)context).getDailyQuote().setLiked(true);
                        ((QuoteOfTheDayActivity)context).getDailyQuote().setLikesCount(((QuoteOfTheDayActivity)context).getDailyQuote().getLikesCount() + 1);
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
            UserPrefs userPrefs = new UserPrefs(context);
            RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
            Call<Void> call = api.dislikeQuote("Bearer " + userPrefs.getJWT(), ((QuoteOfTheDayActivity)context).getDailyQuote().getId());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        ((QuoteOfTheDayActivity)context).getDailyQuote().setLiked(false);
                        ((QuoteOfTheDayActivity)context).getDailyQuote().setLikesCount(((QuoteOfTheDayActivity)context).getDailyQuote().getLikesCount() - 1);
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
