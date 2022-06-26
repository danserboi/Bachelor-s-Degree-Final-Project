package ro.danserboi.quotesformindandsoul.responses;

import ro.danserboi.quotesformindandsoul.models.Links;
import ro.danserboi.quotesformindandsoul.models.Pagination;

public class GetReviewsResponse {
    Pagination pagination;
    ReviewsList data;
    Links links;

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public ReviewsList getData() {
        return data;
    }

    public void setData(ReviewsList data) {
        this.data = data;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }
}
