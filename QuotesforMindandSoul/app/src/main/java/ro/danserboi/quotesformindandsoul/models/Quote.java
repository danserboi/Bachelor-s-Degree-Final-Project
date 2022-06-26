package ro.danserboi.quotesformindandsoul.models;

import com.google.gson.annotations.SerializedName;

public class Quote {
    Integer id;
    String author;
    String text;
    Boolean liked;
    @SerializedName("likes_count")
    Integer likesCount;
    @SerializedName("owner_name")
    String ownerName;
    String genre;
    @SerializedName("reviews_no")
    Integer reviewsNo;
    @SerializedName("reviews_avg")
    Float reviewsAvg;

    public Quote(Integer id, String author, String text, Boolean liked, Integer likesCount, String ownerName, String genre, Integer reviewsNo, Float reviewsAvg) {
        this.id = id;
        this.author = author;
        this.text = text;
        this.liked = liked;
        this.likesCount = likesCount;
        this.ownerName = ownerName;
        this.genre = genre;
        this.reviewsNo = reviewsNo;
        this.reviewsAvg = reviewsAvg;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getReviewsNo() {
        return reviewsNo;
    }

    public void setReviewsNo(Integer reviewsNo) {
        this.reviewsNo = reviewsNo;
    }

    public Float getReviewsAvg() {
        return reviewsAvg;
    }

    public void setReviewsAvg(Float reviewsAvg) {
        this.reviewsAvg = reviewsAvg;
    }

}
