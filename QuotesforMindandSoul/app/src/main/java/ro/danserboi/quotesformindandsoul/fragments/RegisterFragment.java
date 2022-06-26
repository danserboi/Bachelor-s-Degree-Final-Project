package ro.danserboi.quotesformindandsoul.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.MainActivity;
import ro.danserboi.quotesformindandsoul.requests.RegisterRequest;
import ro.danserboi.quotesformindandsoul.responses.AuthTokenResponse;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class RegisterFragment extends Fragment {
    private TextInputLayout firstNameInput, lastNameInput, emailInput, passwordInput;
    private MaterialButton registerButton;
    private ImageView goBackToLoginImageView;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.hideAppBar();
        mainActivity.fab.hide();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstNameInput = view.findViewById(R.id.edit_firstname);
        lastNameInput = view.findViewById(R.id.edit_lastname);
        emailInput = view.findViewById(R.id.edit_email);
        passwordInput = view.findViewById(R.id.edit_password);

        registerButton = view.findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameInput.getEditText().getText().toString();
                String lastName = lastNameInput.getEditText().getText().toString();
                String email = emailInput.getEditText().getText().toString();
                String password = passwordInput.getEditText().getText().toString();

                boolean ok = true;
                if (firstName.length() == 0){
                    firstNameInput.setError("This field is required.");
                    ok = false;
                }
                if (lastName.length() == 0) {
                    lastNameInput.setError("This field is required.");
                    ok = false;
                }
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
                if(!Utils.isPasswordValid(password)) {
                    passwordInput.setError("Password needs to be at least 8 characters long and contain one lowercase letter, one uppercase letter, and a number.");
                    ok = false;
                }
                if(ok) {
                    RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                    Call<AuthTokenResponse> call = api.registerUser(new RegisterRequest(email, password, firstName, lastName));
                    call.enqueue(new Callback<AuthTokenResponse>() {
                        @Override
                        public void onResponse(Call<AuthTokenResponse> call, Response<AuthTokenResponse> response) {
                            if (response.isSuccessful()) {
                                AuthTokenResponse authTokenResponse = response.body();
                                UserPrefs userPrefs = new UserPrefs(getActivity());
                                userPrefs.setJWT(authTokenResponse.getAuthToken());

                                Utils.displayToastLong(getActivity(), "You have been registered. Please verify your email.");

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

        goBackToLoginImageView = view.findViewById(R.id.go_back_to_login_arrow);
        goBackToLoginImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.buttonAnimation(view);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });
    }

}