package ro.danserboi.quotesformindandsoul.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.requests.PasswordChangeRequest;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;


public class ChangePasswordActivity extends AppCompatActivity {
    TextInputLayout oldPasswordView;
    TextInputLayout newPasswordView;
    Button changePasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordView = findViewById(R.id.old_password);
        newPasswordView = findViewById(R.id.new_password);
        changePasswordButton = findViewById(R.id.change_password_button);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = oldPasswordView.getEditText().getText().toString();
                String newPassword = newPasswordView.getEditText().getText().toString();
                if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
                    Utils.displayToast(ChangePasswordActivity.this, "Please complete both fields.");
                } else {
                    if(!Utils.isPasswordValid(newPassword)) {
                        Utils.displayToast(ChangePasswordActivity.this, "Password needs to be at least 8 characters long and contain one lowercase letter, one uppercase letter, and a number.");
                    } else {
                        UserPrefs userPrefs = new UserPrefs(ChangePasswordActivity.this);
                        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                        Call<Void> call = api.passwordChange("Bearer " + userPrefs.getJWT(), new PasswordChangeRequest(oldPassword, newPassword));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if(response.isSuccessful()) {
                                    Utils.displayToast(ChangePasswordActivity.this, "Password has been changed.");
                                    oldPasswordView.getEditText().setText(null);
                                    newPasswordView.getEditText().setText(null);
                                } else {
                                    assert response.errorBody() != null;
                                    Utils.processErrorResponse(ChangePasswordActivity.this, response.errorBody());
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Utils.displayToast(ChangePasswordActivity.this, "Network connection error.");
                            }
                        });
                    }
                }
            }
        });


    }
}