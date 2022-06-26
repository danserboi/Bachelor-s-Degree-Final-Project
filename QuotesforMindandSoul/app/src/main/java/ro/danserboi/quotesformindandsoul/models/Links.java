package ro.danserboi.quotesformindandsoul.models;

public class Links {
    String next;
    String prev;

    public Links(String next, String prev) {
        this.next = next;
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public String getPrev() {
        return prev;
    }
}
