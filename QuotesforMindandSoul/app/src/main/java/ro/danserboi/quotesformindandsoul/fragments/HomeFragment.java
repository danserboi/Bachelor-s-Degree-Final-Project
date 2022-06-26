package ro.danserboi.quotesformindandsoul.fragments;

import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ro.danserboi.quotesformindandsoul.models.Genre;
import ro.danserboi.quotesformindandsoul.activities.MainActivity;
import ro.danserboi.quotesformindandsoul.adapters.GenreAdapter;
import ro.danserboi.quotesformindandsoul.R;


public class HomeFragment extends Fragment {
    private ArrayList<Genre> mCategoriesData;
    private GenreAdapter mAdapter;
    private AnimationDrawable animationDrawable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setEnterTransition(inflater.inflateTransition(R.transition.slide));
        setExitTransition(inflater.inflateTransition(R.transition.fade));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.content_main, container, false);

        RelativeLayout relativeLayout = view.findViewById(R.id.relative_layout);
        Drawable background = AppCompatResources.getDrawable(getActivity(), R.drawable.gradient_animation);
        relativeLayout.setBackground(background);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(2000);

        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);
        // Initialize the RecyclerView.
        RecyclerView mRecyclerView = view.findViewById(R.id.categoryRecyclerView);
        // Set the GridLayoutManager.
        mRecyclerView.setLayoutManager(new
                GridLayoutManager(getActivity(), gridColumnCount));

        // Initialize the ArrayList that will contain the data.
        mCategoriesData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new GenreAdapter(getActivity(), mCategoriesData);
        mRecyclerView.setAdapter(mAdapter);
        initializeData();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showAppBar();
        mainActivity.fab.show();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
    }

    private void initializeData() {
        // Get the resources from the XML file.
        String[] categoriesList = getResources()
                .getStringArray(R.array.categories_titles);
        TypedArray categoriesImageResources =
                getResources().obtainTypedArray(R.array.categories_images);
        // Clear the existing data (to avoid duplication).
        mCategoriesData.clear();

        // Create the ArrayList of categories objects with titles for each category.
        for (int i = 0; i < categoriesList.length; i++) {
            mCategoriesData.add(new Genre(categoriesList[i], categoriesImageResources.getResourceId(i, 0)));
        }
        categoriesImageResources.recycle();
        // Notify the adapter of the change.
        mAdapter.notifyDataSetChanged();
    }

}