package ro.danserboi.quotesformindandsoul.fragments;

import static ro.danserboi.quotesformindandsoul.Config.CODE_LENTGH;
import static ro.danserboi.quotesformindandsoul.Config.EMAIL_FRAGMENT_KEY;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.requests.AccountRecoveryRequest;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class PasswordResetFragment extends Fragment {
    private TextInputLayout codeTextInput, newPasswordTextInput;
    private MaterialButton resetPassword, resendCode;
    private String email;
    private TextView otpCountDown;
    private static final String FORMAT = "%02d:%02d";

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_account_recovery, container, false);
        email = getArguments().getString(EMAIL_FRAGMENT_KEY);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        codeTextInput = view.findViewById(R.id.code);
        newPasswordTextInput = view.findViewById(R.id.new_password);
        resetPassword = view.findViewById(R.id.button_reset_password);
        resendCode = view.findViewById(R.id.button_resend_code);
        otpCountDown = view.findViewById(R.id.otpCountDown);
        countDown();
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeTextInput.getEditText().getText().toString();
                String newPassword = newPasswordTextInput.getEditText().getText().toString();

                boolean ok = true;
                if (code.length() != CODE_LENTGH) {
                    codeTextInput.setError("Wrong input.");
                    ok = false;
                }
                if (newPassword.length() == 0) {
                    newPasswordTextInput.setError("This field is required.");
                    ok = false;
                }
                if (!Utils.isPasswordValid(newPassword)) {
                    newPasswordTextInput.setError("Password needs to be at least 8 characters long and contain one lowercase letter, one uppercase letter, and a number.");
                    ok = false;
                }
                if(ok) {
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    Call<Void> call = api.accountRecovery(new AccountRecoveryRequest(code, email, newPassword));
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Utils.displayToastLong(getActivity(), "Password has been changed.");
                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .replace(R.id.fragment_container, new LoginFragment())
                                        .commit();
                            } else {
                                assert response.errorBody() != null;
                                Utils.processErrorResponse(getActivity(), response.errorBody());
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Utils.displayToast(getActivity(), "Network connection error.");
                        }
                    });
                }
            }
        });
    }

    private void countDown() {
        new CountDownTimer(300000, 1000) { // adjust the milli seconds here

            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            public void onTick(long millisUntilFinished) {
                otpCountDown.setVisibility(View.VISIBLE);
                otpCountDown.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)) ));
            }

            public void onFinish() {
                otpCountDown.setVisibility(View.GONE);
                resendCode.setEnabled(true);
            }
        }.start();
    }
}

