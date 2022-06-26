package ro.danserboi.quotesformindandsoul.requests;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("email")
    final String email;
    @SerializedName("password")
    final String password;
    @SerializedName("first_name")
    final String firstName;
    @SerializedName("last_name")
    final String lastName;

    public RegisterRequest(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
