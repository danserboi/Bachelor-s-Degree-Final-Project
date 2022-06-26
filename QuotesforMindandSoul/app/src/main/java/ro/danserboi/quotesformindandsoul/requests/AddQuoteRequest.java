package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class AddQuoteRequest {
    String author;
    String text;
    String genre;
    @SerializedName("is_public")
    Boolean isPublic;
    @SerializedName("collection_id")
    Integer collectionId;

    public AddQuoteRequest(String author, String text, String genre, Boolean isPublic, Integer collectionId) {
        this.author = author;
        this.text = text;
        this.genre = genre;
        this.isPublic = isPublic;
        this.collectionId = collectionId;
    }
}
