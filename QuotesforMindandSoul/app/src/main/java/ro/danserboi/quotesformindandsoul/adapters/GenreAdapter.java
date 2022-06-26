package ro.danserboi.quotesformindandsoul.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ro.danserboi.quotesformindandsoul.models.Genre;
import ro.danserboi.quotesformindandsoul.R;
import ro.danserboi.quotesformindandsoul.activities.GenreActivity;


public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    // Member variables.
    private final ArrayList<Genre> mCategoriesData;
    private final Context mContext;

    public GenreAdapter(Context context, ArrayList<Genre> categoriesData) {
        this.mCategoriesData = categoriesData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public GenreAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.category_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(GenreAdapter.ViewHolder holder,
                                 int position) {
        // Get current category.
        Genre currentGenre = mCategoriesData.get(position);

        // Populate text views with data.
        holder.bindTo(currentGenre);
    }

    @Override
    public int getItemCount() {
        return mCategoriesData.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Member Variables
        private final TextView mTitleText;
        private final ImageView mCategoryImage;

        ViewHolder(View itemView) {
            super(itemView);

            // Initialize the views.
            mTitleText = itemView.findViewById(R.id.categoryTitle);
            mCategoryImage = itemView.findViewById(R.id.categoryImage);

            // Set the OnClickListener to the entire view.
            itemView.setOnClickListener(this);
        }

        void bindTo(Genre currentGenre) {
            // Populate text views with data.
            mTitleText.setText(currentGenre.getTitle());
            Glide.with(mContext).load(currentGenre.getImageResource()).into(mCategoryImage);
        }

        @Override
        public void onClick(View v) {
            Genre currentGenre = mCategoriesData.get(getBindingAdapterPosition());
            Intent detailIntent = new Intent(mContext, GenreActivity.class);
            detailIntent.putExtra("title", currentGenre.getTitle());
            detailIntent.putExtra("image_resource",
                    getBindingAdapterPosition());
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation((Activity) mContext, mCategoryImage, "categoryImageSharedTransition");
            mContext.startActivity(detailIntent, options.toBundle());
        }
    }
}
