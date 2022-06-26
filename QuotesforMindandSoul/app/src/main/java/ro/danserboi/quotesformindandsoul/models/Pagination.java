package ro.danserboi.quotesformindandsoul.models;

import com.google.gson.annotations.SerializedName;

public class Pagination {
    Integer page;
    @SerializedName("per_page")
    Integer perPage;
    Integer total;

    public Pagination(Integer page, Integer perPage, Integer total) {
        this.page = page;
        this.perPage = perPage;
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPerPage() {
        return perPage;
    }

    public void setPerPage(Integer perPage) {
        this.perPage = perPage;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
