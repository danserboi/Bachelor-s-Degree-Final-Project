package ro.danserboi.quotesformindandsoul.models;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("review_id")
    Integer reviewId;
    String text;
    Integer rating;
    @SerializedName("quote_id")
    Integer quoteId;
    String username;
    Integer timestamp;

    public Review(Integer reviewId, String text, Integer rating, Integer quoteId, String username, Integer timestamp) {
        this.reviewId = reviewId;
        this.text = text;
        this.rating = rating;
        this.quoteId = quoteId;
        this.username = username;
        this.timestamp = timestamp;
    }

    public Integer getReviewId() {
        return reviewId;
    }

    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Integer quoteId) {
        this.quoteId = quoteId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Integer timestamp) {
        this.timestamp = timestamp;
    }
}
