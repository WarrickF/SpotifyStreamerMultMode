package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Warrick on 8/18/2015.
 */
public class ParcelableTrackArrayList implements Parcelable {
    List<ParcelableTrack> tracks = new ArrayList<ParcelableTrack>();
    //ParcelableTrack[] tracks;



    public ParcelableTrackArrayList(ArrayList<ParcelableTrack> trackList) {
        //tracks = trackList.toArray(tracks);
        tracks = trackList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(tracks);
    }
}

//public class arlist implements ParcelableA
