package ro.danserboi.quotesformindandsoul.onclicklisteners;

import static ro.danserboi.quotesformindandsoul.Utils.buttonAnimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.SingleQuoteActivity;

public class ExportImageOnClickListener implements View.OnClickListener{
    Context context;

    public ExportImageOnClickListener(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(View view) {
        buttonAnimation(view);

        SingleQuoteActivity singleQuoteActivity = (SingleQuoteActivity) context;

        float alpha = singleQuoteActivity.getExportImageButton().getAlpha();

        singleQuoteActivity.getExportImageButton().setAlpha(0);
        setButtonsVisibility(singleQuoteActivity, View.INVISIBLE);

        Bitmap bitmap = Utils.takeScreenShot((Activity) context);
        Utils.saveImageAndShowNotification(bitmap, context);

        singleQuoteActivity.getExportImageButton().setAlpha(alpha);
        setButtonsVisibility(singleQuoteActivity, View.VISIBLE);
    }

    private void setButtonsVisibility(SingleQuoteActivity singleQuoteActivity, int invisible) {
        singleQuoteActivity.getAddToCollectionButtonSingle().setVisibility(invisible);
        singleQuoteActivity.getChangeBackgroundButton().setVisibility(invisible);
        singleQuoteActivity.getChangeFontButton().setVisibility(invisible);
        singleQuoteActivity.getCopyButton().setVisibility(invisible);
        singleQuoteActivity.getFavoriteButton().setVisibility(invisible);
        singleQuoteActivity.getShareButton().setVisibility(invisible);
        singleQuoteActivity.getUsername().setVisibility(invisible);
        singleQuoteActivity.getNumberOfReviews().setVisibility(invisible);
        singleQuoteActivity.getRatingBar().setVisibility(invisible);
    }
}
