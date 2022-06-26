package ro.danserboi.quotesformindandsoul.responses;

import java.util.List;

import ro.danserboi.quotesformindandsoul.models.Review;

public class ReviewsList {
    List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
