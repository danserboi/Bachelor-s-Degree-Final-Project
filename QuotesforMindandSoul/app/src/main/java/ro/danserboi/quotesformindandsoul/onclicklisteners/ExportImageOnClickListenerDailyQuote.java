package ro.danserboi.quotesformindandsoul.onclicklisteners;

import static ro.danserboi.quotesformindandsoul.Utils.buttonAnimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.QuoteOfTheDayActivity;

public class ExportImageOnClickListenerDailyQuote implements View.OnClickListener{
    Context context;

    public ExportImageOnClickListenerDailyQuote(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        buttonAnimation(view);

        QuoteOfTheDayActivity activity = (QuoteOfTheDayActivity) context;

        float alpha = activity.getExportImageButton().getAlpha();

        activity.getExportImageButton().setAlpha(0);
        setButtonsVisibility(activity, View.INVISIBLE);

        Bitmap bitmap = Utils.takeScreenShot((Activity) context);
        Utils.saveImageAndShowNotification(bitmap, context);

        activity.getExportImageButton().setAlpha(alpha);
        setButtonsVisibility(activity, View.VISIBLE);
    }

    private void setButtonsVisibility(QuoteOfTheDayActivity activity, int invisible) {
        activity.getAddToCollectionButtonSingle().setVisibility(invisible);
        activity.getChangeBackgroundButton().setVisibility(invisible);
        activity.getChangeFontButton().setVisibility(invisible);
        activity.getCopyButton().setVisibility(invisible);
        activity.getFavoriteButton().setVisibility(invisible);
        activity.getShareButton().setVisibility(invisible);
        activity.getUsername().setVisibility(invisible);
        activity.getNumberOfReviews().setVisibility(invisible);
        activity.getRatingBar().setVisibility(invisible);
    }
}
