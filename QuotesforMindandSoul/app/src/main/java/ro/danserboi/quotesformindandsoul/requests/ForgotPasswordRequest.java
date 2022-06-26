package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class ForgotPasswordRequest {
    @SerializedName("email")
    final String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }
}
