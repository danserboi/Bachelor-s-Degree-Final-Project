package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class AddReviewRequest {
    @SerializedName("quote_id")
    Integer quoteId;
    String text;
    Integer rating;

    public AddReviewRequest(Integer quoteId, String text, Integer rating) {
        this.quoteId = quoteId;
        this.text = text;
        this.rating = rating;
    }
}
