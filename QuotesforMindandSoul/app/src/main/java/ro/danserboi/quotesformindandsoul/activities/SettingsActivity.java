package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.QuoteOfTheDayWorker.showQuoteNotification;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.models.Quote;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

public class SettingsActivity extends AppCompatActivity {
    private static int darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        UserPrefs userPrefs = new UserPrefs(getApplicationContext());
        userPrefs.setDarkModeState(darkModeState);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final Preference darkModePreference = findPreference("dark_mode_pref");
            if (darkModePreference != null) {
                darkModePreference.setOnPreferenceChangeListener(this);
                ((ListPreference)darkModePreference).setNegativeButtonText(getString(R.string.dialog_cancel));
            }

            final SwitchPreferenceCompat notificationSwitchPreference = findPreference("notification_switch_pref");
            if (notificationSwitchPreference != null) {
                notificationSwitchPreference.setOnPreferenceClickListener(preference -> {
                    UserPrefs userPrefs = new UserPrefs(getActivity());
                    if (notificationSwitchPreference.isChecked()) {
                        userPrefs.setNotificationsOn(true);
                        // get quote
                        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                        Call<Quote> call = api.getQuote("Bearer " + userPrefs.getJWT(), userPrefs.getDailyQuoteId());
                        call.enqueue(new Callback<Quote>() {
                            @Override
                            public void onResponse(Call<Quote> call, Response<Quote> response) {
                                if (response.isSuccessful()) {
                                    Quote dailyQuote = response.body();
                                    showQuoteNotification(getActivity(), dailyQuote.getAuthor(), dailyQuote.getText());
                                } else {
                                    assert response.errorBody() != null;
                                    Utils.processErrorResponse(getActivity(), response.errorBody());
                                }
                            }
                            @Override
                            public void onFailure(Call<Quote> call, Throwable t) {
                                Utils.displayToast(getActivity(), "Network connection error.");
                            }
                        });
                    } else {
                        userPrefs.setNotificationsOn(false);
                    }
                    return true;
                });
            }


        }

        @Override
        public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
            switch ((String) newValue) {
                case "Off":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_NO);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_NO;
                    break;
                case "On":
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_YES);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_YES;
                    break;
                case "Follow system":
                default:
                    AppCompatDelegate.setDefaultNightMode
                            (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    darkModeState = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                    break;
            }
            return true;
        }
    }
}