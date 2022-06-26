package ro.danserboi.quotesformindandsoul.adapters;

import static ro.danserboi.quotesformindandsoul.Config.COLLECTION_ID;
import static ro.danserboi.quotesformindandsoul.Config.COLLECTION_NAME;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.Utils;
import ro.danserboi.quotesformindandsoul.activities.CollectionQuotesActivity;
import ro.danserboi.quotesformindandsoul.models.Collection;
import ro.danserboi.quotesformindandsoul.onclicklisteners.UpdateCollectionOnClickListener;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.ViewHolder> {

    // Member variables.
    private final ArrayList<Collection> mCollectionsData;
    private final Context mContext;

    public ArrayList<Collection> getCollectionsData() {
        return mCollectionsData;
    }

    public Context getContext() {
        return mContext;
    }

    public CollectionAdapter(Context context, ArrayList<Collection> collectionsData) {
        this.mCollectionsData = collectionsData;
        this.mContext = context;
    }


    @NonNull
    @Override
    public CollectionAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.collection_title_item, parent, false));
    }

    @Override
    public void onBindViewHolder(CollectionAdapter.ViewHolder holder,
                                 int position) {
        // Get current collection.
        Collection currentCollection = mCollectionsData.get(position);

        // Populate text views with data.
        holder.bindTo(currentCollection);
    }

    @Override
    public int getItemCount() {
        return mCollectionsData.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView title;
        private final ImageView updateCollectionTitleButton;

        ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            updateCollectionTitleButton = itemView.findViewById(R.id.updateCollectionTitleButton);
            updateCollectionTitleButton.setOnClickListener(new UpdateCollectionOnClickListener(CollectionAdapter.this, title));

            itemView.setOnClickListener(this);
        }

        void bindTo(Collection currentCollection) {
            title.setText(currentCollection.getName());
        }

        @Override
        public void onClick(View v) {
            Collection currentCollection = mCollectionsData.get(getBindingAdapterPosition());
            if(currentCollection.getQuotes().isEmpty()) {
                Utils.displayToast(mContext, "This collection doesn't have any quotes.");
            } else {
                Intent detailIntent = new Intent(mContext, CollectionQuotesActivity.class);
                detailIntent.putExtra(COLLECTION_ID, currentCollection.getId());
                detailIntent.putExtra(COLLECTION_NAME, currentCollection.getName());
                mContext.startActivity(detailIntent);
            }
        }
    }
}
