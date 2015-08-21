package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Warrick on 8/18/2015.
 */
public class ParcelableTrack implements Parcelable {
    private String artist_name;
    private String album_name;
    private String album_artwork;
    private String track_name;
    private Integer track_duration;
    private String preview_url;

    public ParcelableTrack(Track track) {
        this.artist_name = track.artists.get(0).name;
        this.album_name = track.album.name;
        this.album_artwork = track.album.images.get(0).url;
        this.track_name = track.name;
        this.track_duration = 30000;
        this.preview_url = track.preview_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getArtist_name());
        dest.writeString(getAlbum_name());
        dest.writeString(getAlbum_artwork());
        dest.writeString(getTrack_name());
        dest.writeString(getPreview_url());
        dest.writeInt(getTrack_duration());

    }

    public String getPreview_url() {
        return preview_url;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getAlbum_name() {
        return album_name;
    }

    public String getAlbum_artwork() {
        return album_artwork;
    }

    public String getTrack_name() {
        return track_name;
    }

    public Integer getTrack_duration() {
        return track_duration;
    }
}
