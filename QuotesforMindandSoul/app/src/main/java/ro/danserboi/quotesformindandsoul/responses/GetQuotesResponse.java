package ro.danserboi.quotesformindandsoul.responses;

import ro.danserboi.quotesformindandsoul.models.Links;
import ro.danserboi.quotesformindandsoul.models.Pagination;

public class GetQuotesResponse {
    Pagination pagination;
    QuotesList data;
    Links links;

    public GetQuotesResponse(Pagination pagination, QuotesList data, Links links) {
        this.pagination = pagination;
        this.data = data;
        this.links = links;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public QuotesList getData() {
        return data;
    }

    public Links getLinks() {
        return links;
    }
}
