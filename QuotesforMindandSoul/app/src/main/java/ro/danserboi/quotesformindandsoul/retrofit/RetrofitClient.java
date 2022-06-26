package ro.danserboi.quotesformindandsoul.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ro.danserboi.quotesformindandsoul.Config;

public class RetrofitClient {
    private static RetrofitAPI retrofitAPI;

    public static RetrofitAPI getRetrofitAPIInstance() {
        if (retrofitAPI == null) {
            retrofitAPI = new Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RetrofitAPI.class);
        }

        return retrofitAPI;
    }

}

