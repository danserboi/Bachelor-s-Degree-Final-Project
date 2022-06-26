package ro.danserboi.quotesformindandsoul.onclicklisteners;

import static ro.danserboi.quotesformindandsoul.Utils.buttonAnimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.RandomQuoteActivity;

public class ExportImageOnClickListenerRandom implements View.OnClickListener{
    Context context;

    public ExportImageOnClickListenerRandom(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        buttonAnimation(view);

        RandomQuoteActivity randomQuoteActivity = (RandomQuoteActivity) context;

        float alpha = randomQuoteActivity.getExportImageButton().getAlpha();

        randomQuoteActivity.getExportImageButton().setAlpha(0);
        setButtonsVisibility(randomQuoteActivity, View.INVISIBLE);

        Bitmap bitmap = Utils.takeScreenShot((Activity) context);
        Utils.saveImageAndShowNotification(bitmap, context);

        randomQuoteActivity.getExportImageButton().setAlpha(alpha);
        setButtonsVisibility(randomQuoteActivity, View.VISIBLE);
    }

    private void setButtonsVisibility(RandomQuoteActivity randomQuoteActivity, int invisible) {
        randomQuoteActivity.getGetAnotherQuoteButton().setVisibility(invisible);
        randomQuoteActivity.getAddToCollectionButton().setVisibility(invisible);
        randomQuoteActivity.getChangeBackgroundButton().setVisibility(invisible);
        randomQuoteActivity.getChangeFontButton().setVisibility(invisible);
        randomQuoteActivity.getCopyButton().setVisibility(invisible);
        randomQuoteActivity.getFavoriteButton().setVisibility(invisible);
        randomQuoteActivity.getShareButton().setVisibility(invisible);


        randomQuoteActivity.getUsername().setVisibility(invisible);
        randomQuoteActivity.getNumberOfReviews().setVisibility(invisible);
        randomQuoteActivity.getRatingBar().setVisibility(invisible);
    }
}
