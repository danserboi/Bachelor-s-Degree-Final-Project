package ro.danserboi.quotesformindandsoul.retrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ro.danserboi.quotesformindandsoul.responses.QuotesList;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.responses.CollectionList;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.models.Review;
import ro.danserboi.quotesformindandsoul.requests.AddQuoteRequest;
import ro.danserboi.quotesformindandsoul.requests.AddReviewRequest;
import ro.danserboi.quotesformindandsoul.requests.LoginRequest;
import ro.danserboi.quotesformindandsoul.requests.NameWrapper;
import ro.danserboi.quotesformindandsoul.requests.PasswordChangeRequest;
import ro.danserboi.quotesformindandsoul.requests.ForgotPasswordRequest;
import ro.danserboi.quotesformindandsoul.requests.RegisterRequest;
import ro.danserboi.quotesformindandsoul.requests.AccountRecoveryRequest;
import ro.danserboi.quotesformindandsoul.responses.AuthTokenResponse;
import ro.danserboi.quotesformindandsoul.responses.DailyQuoteResponse;
import ro.danserboi.quotesformindandsoul.responses.GetQuotesResponse;
import ro.danserboi.quotesformindandsoul.responses.GetReviewsResponse;

public interface RetrofitAPI {
    @Headers({"Accept: application/json"})
    @POST("/register")
    Call<AuthTokenResponse> registerUser(@Body RegisterRequest body);

    @Headers({"Accept: application/json"})
    @POST("/login")
    Call<AuthTokenResponse> loginUser(@Body LoginRequest body);

    @Headers({"Accept: application/json"})
    @POST("/logout")
    Call<Void> logoutUser(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @GET("/email_validation")
    Call<Void> emailValidation(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @PUT("/password_change")
    Call<Void> passwordChange(@Header("Authorization") String authHeader, @Body PasswordChangeRequest body);

    @Headers({"Accept: application/json"})
    @POST("/forgot_password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest body);

    @Headers({"Accept: application/json"})
    @PUT("/account_recovery")
    Call<Void> accountRecovery(@Body AccountRecoveryRequest body);

    @Headers({"Accept: application/json"})
    @POST("/quotes")
    Call<Void> addQuote(@Header("Authorization") String authHeader, @Body AddQuoteRequest addQuoteRequest);

    @Headers({"Accept: application/json"})
    @GET("/quotes/{quote_id}")
    Call<Quote> getQuote(@Header("Authorization") String authHeader, @Path("quote_id") Integer id);

    @Headers({"Accept: application/json"})
    @GET("/quotes")
    Call<GetQuotesResponse> getAllQuotes(@Header("Authorization") String authHeader, @Query("author") String author, @Query("genre") String genre, @Query("page") Integer page);

    @Headers({"Accept: application/json"})
    @DELETE("/quotes/{quote_id}")
    Call<Void> deleteQuote(@Header("Authorization") String authHeader, @Path("quote_id") Integer id);

    @Headers({"Accept: application/json"})
    @GET("/quotes/random")
    Call<Quote> getRandomQuote(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @GET("/quotes/daily")
    Call<DailyQuoteResponse> getDailyQuote();

    @Headers({"Accept: application/json"})
    @GET("/quotes/liked")
    Call<QuotesList> getFavorites(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @GET("/quotes/owned")
    Call<QuotesList> getMyQuotes(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST("/quotes/{quote_id}/like")
    Call<Void> likeQuote(@Header("Authorization") String authHeader, @Path("quote_id") Integer id);

    @Headers({"Accept: application/json"})
    @DELETE("/quotes/{quote_id}/like")
    Call<Void> dislikeQuote(@Header("Authorization") String authHeader, @Path("quote_id") Integer id);

    @Headers({"Accept: application/json"})
    @GET("/collections")
    Call<CollectionList> getCollections(@Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST("/collections")
    Call<Collection> createCollection(@Header("Authorization") String authHeader, @Body NameWrapper nameWrapper);

    @Headers({"Accept: application/json"})
    @DELETE("/collections/{collection_id}")
    Call<Void> deleteCollection(@Header("Authorization") String authHeader, @Path("collection_id") Integer id);

    @Headers({"Accept: application/json"})
    @PUT("/collections/{collection_id}")
    Call<Void> updateCollection(@Header("Authorization") String authHeader, @Path("collection_id") Integer id, @Body NameWrapper nameWrapper);

    @Headers({"Accept: application/json"})
    @POST("/quotes/{quote_id}/collections/{collection_name}")
    Call<Void> addQuoteToCollection(@Header("Authorization") String authHeader, @Path("quote_id") Integer id, @Path("collection_name") String name);

    @Headers({"Accept: application/json"})
    @GET("/collections/{collection_id}")
    Call<QuotesList> getCollectionQuotes(@Header("Authorization") String authHeader, @Path("collection_id") Integer id);

    @Headers({"Accept: application/json"})
    @POST("/reviews")
    Call<Review> addReview(@Header("Authorization") String authHeader, @Body AddReviewRequest addReviewRequest);

    @Headers({"Accept: application/json"})
    @GET("/reviews")
    Call<GetReviewsResponse> getAllReviews(@Header("Authorization") String authHeader, @Query("page") Integer page, @Query("quote_id") Integer quote_id);
}
