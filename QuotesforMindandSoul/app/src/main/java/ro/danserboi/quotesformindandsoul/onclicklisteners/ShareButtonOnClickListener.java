package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.content.Context;
import androidx.core.app.ShareCompat;
import android.view.View;
import android.widget.TextView;

import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.Utils;

public class ShareButtonOnClickListener implements View.OnClickListener{
    private final Context context;
    private final TextView mWordsTextView;
    private final TextView mAuthorTextView;

    public ShareButtonOnClickListener(Context context, TextView mWordsTextView, TextView mAuthorTextView) {
        this.context = context;
        this.mWordsTextView = mWordsTextView;
        this.mAuthorTextView = mAuthorTextView;
    }

    @Override
    public void onClick(View view) {
        Utils.buttonAnimation(view);
        String txt = "\"" + mWordsTextView.getText() + "\" - " + mAuthorTextView.getText();
        String mimeType = "text/plain";
        new ShareCompat.IntentBuilder(context)
                .setType(mimeType)
                .setChooserTitle(R.string.share_chooser_title)
                .setText(txt)
                .setSubject(context.getString(R.string.share_subject))
                .startChooser();
    }
}
