package ro.danserboi.quotesformindandsoul.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Genre implements Parcelable {
    private final int imageResource;
    private final String title;

    public Genre(String title, int imageResource) {
        this.title = title;
        this.imageResource = imageResource;
    }

    private Genre(Parcel in) {
        title = in.readString();
        imageResource = in.readInt();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public int getImageResource() {
        return imageResource;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeInt(imageResource);
    }
}
