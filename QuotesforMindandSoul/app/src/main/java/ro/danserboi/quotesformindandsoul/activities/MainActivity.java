package ro.danserboi.quotesformindandsoul.activities;

import static ro.danserboi.quotesformindandsoul.Config.APP_BAR_LAYOUT_DEFAULT_HEIGHT;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ShareCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.danserboi.quotesformindandsoul.Config;
import ro.danserboi.quotesformindandsoul.QuoteOfTheDayWorker;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.UserPrefs;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.fragments.HomeFragment;
import ro.danserboi.quotesformindandsoul.fragments.LoginFragment;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitAPI;
import ro.danserboi.quotesformindandsoul.retrofit.RetrofitClient;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    AppBarLayout appBarLayout;
    public FloatingActionButton fab;
    private List<Collection> collectionList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserPrefs userPrefs = new UserPrefs(this);
        if(userPrefs.getFirstLogin()) {
            final WorkManager mWorkManager = WorkManager.getInstance();
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            final PeriodicWorkRequest workRequest =
                    new PeriodicWorkRequest.Builder(QuoteOfTheDayWorker.class, 1, TimeUnit.DAYS)
                            .setConstraints(constraints)
                            .build();
            mWorkManager.enqueue(workRequest);
            userPrefs.setFirstLogin();
        }
        AppCompatDelegate.setDefaultNightMode(userPrefs.getDarkModeState());

        FrameLayout container_layout = findViewById(R.id.fragment_container);
        appBarLayout = findViewById(R.id.app_bar_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        if (drawer != null)
            drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);

        fab = findViewById(R.id.fab);
        setFabOnClickListener(fab);


        // retain app bar layout height when running first time
        int appBarLayoutHeight = userPrefs.getAppBarLayoutHeight();
        if(appBarLayoutHeight == APP_BAR_LAYOUT_DEFAULT_HEIGHT) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
            userPrefs.setAppBarLayoutHeight(lp.height);
        }

        String jwt = userPrefs.getJWT();
        if(jwt != null) {
            DecodedJWT decodedJWT = JWT.decode(jwt);
            if(decodedJWT.getExpiresAt().before(new Date())) {
                userPrefs.setJWT(null);
                jwt = null;
            }
        }

        if(jwt == null){
            fab.hide();
            hideAppBar();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment())
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

    }

    public void hideAppBar() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        lp.height = 0;
        appBarLayout.setLayoutParams(lp);
    }

    public void showAppBar() {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        UserPrefs userPrefs = new UserPrefs(this);
        lp.height = userPrefs.getAppBarLayoutHeight();
        appBarLayout.setLayoutParams(lp);
    }

    private void setFabOnClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.search_quote_dialog, viewGroup, false);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                // User clicked OK button
                RadioButton authorRadioButton = dialogView.findViewById(R.id.author_radio_button);
                RadioButton categoryRadioButton = dialogView.findViewById(R.id.category_radio_button);
                EditText inputTextView = dialogView.findViewById(R.id.input_text);
                String inputText = inputTextView.getText().toString();
                if (TextUtils.isEmpty(inputText)) {
                    Utils.displayToast(MainActivity.this, getString(R.string.text_not_entered));
                } else {
                    Intent searchIntent = new Intent(MainActivity.this, SearchQuotesActivity.class);
                    if (authorRadioButton.isChecked()) {
                        searchIntent.putExtra("method", Config.QUERY_AUTHOR_PARAM);
                    } else if (categoryRadioButton.isChecked()) {
                        searchIntent.putExtra("method", Config.QUERY_GENRE_PARAM);
                    }
                    searchIntent.putExtra("input", inputText);
                    startActivity(searchIntent);
                }
            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        switch (menuItem.getItemId()) {
            case R.id.nav_collections:
                drawer.closeDrawers();
                startActivity(new Intent(this, CollectionsActivity.class));
                return true;
            case R.id.nav_favorites:
                drawer.closeDrawers();
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
            case R.id.nav_quote_of_the_day:
                drawer.closeDrawers();
                startActivity(new Intent(this, QuoteOfTheDayActivity.class));
                return true;
            case R.id.nav_random_quote:
                drawer.closeDrawers();
                startActivity(new Intent(this, RandomQuoteActivity.class));
                return true;
            case R.id.nav_add_quote:
                drawer.closeDrawers();
                startActivity(new Intent(this, AddQuoteActivity.class));
                return true;
            case R.id.nav_my_quotes:
                drawer.closeDrawers();
                startActivity(new Intent(this, MyQuotesActivity.class));
                return true;
            case R.id.nav_settings:
                drawer.closeDrawers();
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.nav_share:
                drawer.closeDrawers();
                share();
                return true;
            case R.id.nav_rate:
                drawer.closeDrawers();
                rate();
                return true;
            case R.id.nav_feedback:
                drawer.closeDrawers();
                feedback();
                return true;
            case R.id.nav_about:
                drawer.closeDrawers();
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.nav_change_password:
                drawer.closeDrawers();
                startActivity(new Intent(this, ChangePasswordActivity.class));
                return true;
            case R.id.nav_resend_validation_email:
                drawer.closeDrawers();
                resend_validation_email();
                return true;
            case R.id.nav_logout:
                drawer.closeDrawers();

                UserPrefs userPrefs = new UserPrefs(getApplicationContext());
                RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
                Call<Void> call = api.logoutUser("Bearer " + userPrefs.getJWT());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            UserPrefs userPrefs = new UserPrefs(getApplicationContext());
                            userPrefs.setJWT(null);
                            hideAppBar();
                            fab.hide();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                                    .replace(R.id.fragment_container, new LoginFragment())
                                    .commit();
                        } else {
                            assert response.errorBody() != null;
                            Utils.processErrorResponse(getApplicationContext(), response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Utils.displayToast(getApplicationContext(), "Network connection error.");
                    }
                });

                return true;
            default:
                return false;
        }
    }

    private void resend_validation_email() {
        UserPrefs userPrefs = new UserPrefs(this);
        RetrofitAPI api = RetrofitClient.getRetrofitAPIInstance();
        Call<Void> call = api.emailValidation("Bearer " + userPrefs.getJWT());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Utils.displayToast(MainActivity.this, "Successfully resend email.");
                } else {
                    assert response.errorBody() != null;
                    Utils.processErrorResponse(MainActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Utils.displayToast(MainActivity.this, "Network connection error.");
            }
        });
    }

    public void share() {
        String txt = getString(R.string.share_app_text) +
                "https://play.google.com/store/apps/details?id=" + getPackageName();
        String mimeType = "text/plain";
        new ShareCompat.IntentBuilder(this)
                .setType(mimeType)
                .setChooserTitle("Share this app with: ")
                .setText(txt)
                .setSubject(getString(R.string.share_app_subject))
                .startChooser();
    }

    public void rate() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (ActivityNotFoundException e1) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e2) {
                Utils.displayToast(this, getString(R.string.no_playstore));
            }
        }
    }

    public void feedback() {
        // Create and fire off our Intent in one fell swoop
        new ShareCompat.IntentBuilder(this)
                // chooser title
                .setChooserTitle(R.string.chooser_title_feedback)
                // most general text sharing MIME type
                .setType("text/plain")
                // feedback email address
                .setEmailTo(new String[]{getString(R.string.feedback_mail_address)})
                // mail subject
                .setSubject(getString(R.string.feedback_subject))
                /*
                 * The title of the chooser that the system will show
                 * to allow the user to select an app
                 */
                .startChooser();
    }
}