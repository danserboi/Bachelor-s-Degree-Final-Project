package ro.danserboi.quotesformindandsoul.responses;

import com.google.gson.annotations.SerializedName;

public class AuthTokenResponse {
    @SerializedName("auth_token")
    String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
