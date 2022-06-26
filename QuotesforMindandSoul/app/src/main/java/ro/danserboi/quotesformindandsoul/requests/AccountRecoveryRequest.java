package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class AccountRecoveryRequest {
    @SerializedName("reset_pin")
    final String code;
    @SerializedName("email")
    final String email;
    @SerializedName("new_password")
    final String newPassword;

    public AccountRecoveryRequest(String code, String email, String newPassword) {
        this.code = code;
        this.email = email;
        this.newPassword = newPassword;
    }
}
