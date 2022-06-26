package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("email")
    final String email;
    @SerializedName("password")
    final String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
