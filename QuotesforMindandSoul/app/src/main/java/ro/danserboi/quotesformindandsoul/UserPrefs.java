package ro.danserboi.quotesformindandsoul;

import static android.content.Context.MODE_PRIVATE;

import static ro.danserboi.quotesformindandsoul.Config.APP_BAR_LAYOUT_DEFAULT_HEIGHT;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class UserPrefs {
    private SharedPreferences userSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    public UserPrefs(Context context) {
        this.userSharedPrefs = context.getSharedPreferences(Config.SHARED_PREF_FILE, MODE_PRIVATE);
        this.prefsEditor = userSharedPrefs.edit();
    }

    public String getJWT() {
        return userSharedPrefs.getString(Config.JWT_TOKEN_KEY, null);
    }

    public void setJWT(String jwt) {
        prefsEditor.putString(Config.JWT_TOKEN_KEY, jwt).apply();
    }

    public int getAppBarLayoutHeight() {
        return userSharedPrefs.getInt(Config.APP_BAR_LAYOUT_HEIGHT, APP_BAR_LAYOUT_DEFAULT_HEIGHT);
    }

    public void setAppBarLayoutHeight(int appBarLayoutHeight) {
        prefsEditor.putInt(Config.APP_BAR_LAYOUT_HEIGHT, appBarLayoutHeight).apply();
    }

    public int getDarkModeState() {
        return userSharedPrefs.getInt(Config.DARK_MODE_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public void setDarkModeState(int darkModeState) {
        prefsEditor.putInt(Config.DARK_MODE_KEY, darkModeState).apply();
    }

    public Boolean getFirstLogin() {
        return userSharedPrefs.getBoolean(Config.FIRST_LOGIN, true);
    }

    public void setFirstLogin() {
        prefsEditor.putBoolean(Config.FIRST_LOGIN, false).apply();
    }

    public Boolean getNotificationsOn() {
        return userSharedPrefs.getBoolean(Config.NOTIFICATIONS_ON, false);
    }

    public void setNotificationsOn(Boolean notificationsOn) {
        prefsEditor.putBoolean(Config.NOTIFICATIONS_ON, notificationsOn).apply();
    }

    public int getDailyQuoteId() {
        return userSharedPrefs.getInt(Config.DAILY_QUOTE_ID, -1);
    }

    public void setDailyQuoteId(int dailyQuoteId) {
        prefsEditor.putInt(Config.DAILY_QUOTE_ID, dailyQuoteId).apply();
    }
}
