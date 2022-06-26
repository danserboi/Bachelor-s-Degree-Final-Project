package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import static ro.danserboi.quotesformindandsoul.Utils.buttonAnimation;

import ro.danserboi.quotesformindandsoul.R;

public class ChangeBackgroundOnClickListener implements View.OnClickListener {
    private Context context;
    private int backgroundIdx;

    private static final int GRADIENTS_NO = 13;

    public ChangeBackgroundOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        buttonAnimation(view);
        TypedArray gradients;
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES ||
                ((AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED ||
                        AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        && (context.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)) {
            // Night mode is set explicitly or app follows the system that is in night mode
            gradients = context.getResources().obtainTypedArray(R.array.background_gradients_dark);
        } else {
            // Day mode is set explicitly or app follows the system that is in day mode
            gradients = context.getResources().obtainTypedArray(R.array.background_gradients);
        }
        Window window = ((AppCompatActivity) context).getWindow();
        Drawable background = AppCompatResources.getDrawable(context, gradients.getResourceId(backgroundIdx, -1));
        gradients.recycle();
        window.setBackgroundDrawable(background);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(context.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(context.getResources().getColor(android.R.color.transparent));
        backgroundIdx++;
        if (backgroundIdx == GRADIENTS_NO) {
            backgroundIdx = 0;
        }
    }
}
