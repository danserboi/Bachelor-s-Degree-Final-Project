package ro.danserboi.quotesformindandsoul.fragments;


import static ro.danserboi.quotesformindandsoul.Config.EMAIL_FRAGMENT_KEY;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.MainActivity;
import ro.danserboi.quotesformindandsoul.requests.LoginRequest;
import ro.danserboi.quotesformindandsoul.requests.ForgotPasswordRequest;
import ro.danserboi.quotesformindandsoul.responses.AuthTokenResponse;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;


public class LoginFragment extends Fragment {
    private TextInputLayout emailInput, passwordInput;
    private Button loginButton;
    private ImageView goRegisterImageView;
    private MaterialButton forgotPasswordButton;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideAppBar();
        mainActivity.fab.hide();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailInput = view.findViewById(R.id.edit_email);
        passwordInput = view.findViewById(R.id.edit_password);
        loginButton = view.findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailInput.getEditText().getText().toString();
                String password = passwordInput.getEditText().getText().toString();

                boolean ok = true;
                if (email.length() == 0){
                    emailInput.setError("This field is required.");
                    ok = false;
                }
                if (password.length() == 0) {
                    passwordInput.setError("This field is required.");
                    ok = false;
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Please enter a valid email address.");
                    ok = false;
                }
                if (ok) {
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    Call<AuthTokenResponse> call = api.loginUser(new LoginRequest(email, password));
                    call.enqueue(new Callback<AuthTokenResponse>() {
                        @Override
                        public void onResponse(Call<AuthTokenResponse> call, Response<AuthTokenResponse> response) {
                            if (response.isSuccessful()) {
                                AuthTokenResponse authTokenResponse = response.body();
                                UserPrefs userPrefs = new UserPrefs(getActivity());
                                userPrefs.setJWT(authTokenResponse.getAuthToken());

                                getActivity().getSupportFragmentManager()
                                        .beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .replace(R.id.fragment_container, new HomeFragment())
                                        .commit();
                            } else {
                                assert response.errorBody() != null;
                                Utils.processErrorResponse(getActivity(), response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthTokenResponse> call, Throwable t) {
                            Utils.displayToast(getActivity(), "Network connection error.");
                        }
                    });
                }
            }
        });
        goRegisterImageView = view.findViewById(R.id.go_register_arrow);
        goRegisterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.buttonAnimation(view);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
            }
        });

        forgotPasswordButton = view.findViewById(R.id.button_forgot_password);
        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPasswordDialog();
            }
        });

    }

    private void forgotPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.forgot_password_dialog, null);
        AlertDialog emailDialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setTitle("Forgot Password")
                .setCancelable(false)
                .setPositiveButton("Send Code", (dialog, which) -> {})
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();

        TextInputLayout emailInput = dialogView.findViewById(R.id.edit_email);

        Objects.requireNonNull(emailInput.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(emailInput.getEditText().getText().length() > 0){
                    emailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                } else {
                    emailDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailDialog.setOnShowListener(dialog -> {
            final Button b = emailDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setEnabled(false);

            b.setOnClickListener(view -> {
                String email = emailInput.getEditText().getText().toString();

                if (!email.isEmpty()) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                        Call<Void> call = api.forgotPassword(new ForgotPasswordRequest(email));
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    PasswordResetFragment pwFragment = new PasswordResetFragment();
                                    Bundle args = new Bundle();
                                    args.putString(EMAIL_FRAGMENT_KEY, email);
                                    pwFragment.setArguments(args);

                                    getActivity().getSupportFragmentManager()
                                            .beginTransaction()
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .replace(R.id.fragment_container, pwFragment)
                                            .addToBackStack(null)
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
                        dialog.dismiss();
                    } else {
                        Utils.displayToast(getActivity(), "Please enter a valid email address.");
                    }
                } else {
                    Utils.displayToast(getActivity(), "Please enter email address.");
                }

            });
        });

        emailDialog.show();
    }

}
