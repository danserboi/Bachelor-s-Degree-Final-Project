package ro.danserboi.quotesformindandsoul.onclicklisteners;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static ro.danserboi.quotesformindandsoul.Utils.buttonAnimation;

import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.Utils;

public class CopyButtonOnClickListener implements View.OnClickListener{
    private final Context context;
    private final TextView mWordsTextView;
    private final TextView mAuthorTextView;

    public CopyButtonOnClickListener(Context context, TextView mWordsTextView, TextView mAuthorTextView) {
        this.context = context;
        this.mWordsTextView = mWordsTextView;
        this.mAuthorTextView = mAuthorTextView;
    }

    @Override
    public void onClick(View view) {
        buttonAnimation(view);
        String txt = "\"" + mWordsTextView.getText() + "\" - " + mAuthorTextView.getText();
        ClipboardManager clipboard = (ClipboardManager) ((AppCompatActivity)context).getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Quote", txt);
        clipboard.setPrimaryClip(clip);
        Utils.displayToast(context, context.getString(R.string.copied_to_clipboard));
    }
}
