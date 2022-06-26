package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.MyQuotesActivity;
import ro.danserboi.quotesformindandsoul.adapters.QuoteAdapter;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class DeleteQuoteOnClickListener implements View.OnClickListener{
    QuoteAdapter quoteAdapter;
    TextView quoteText;
    MyQuotesActivity context;
    List<Quote> quotes;
    Quote quote;

    public DeleteQuoteOnClickListener(QuoteAdapter quoteAdapter, TextView quoteText) {
        this.quoteAdapter = quoteAdapter;
        this.quoteText = quoteText;
        this.context = (MyQuotesActivity) quoteAdapter.getContext();
        this.quotes = quoteAdapter.getQuotesData();
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        // delete quote
        quote = Utils.getQuoteByName(quotes, quoteText.getText().toString());

        UserPrefs userPrefs = new UserPrefs(context);
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<Void> call = api.deleteQuote("Bearer " + userPrefs.getJWT(), quote.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    quotes.remove(quote);
                    quoteAdapter.notifyDataSetChanged();
                    Utils.displayToast(context,  "Quote has been removed.");
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
