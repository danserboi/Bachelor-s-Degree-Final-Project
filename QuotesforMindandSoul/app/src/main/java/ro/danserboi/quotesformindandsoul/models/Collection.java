package ro.danserboi.quotesformindandsoul.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Collection {
    Integer id;
    String name;
    @SerializedName("created_at")
    Integer createdAt;
    List<Quote> quotes;

    public Collection(Integer id, String name, Integer createdAt, List<Quote> quotes) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.quotes = quotes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Integer createdAt) {
        this.createdAt = createdAt;
    }

    public List<Quote> getQuotes() {
        return quotes;
    }

    public void setQuotes(List<Quote> quotes) {
        this.quotes = quotes;
    }
}
