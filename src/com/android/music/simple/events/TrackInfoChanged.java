package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class TrackInfoChanged extends ReflectionParcelableEvent {

    public String trackName;
    public String artistName;
    public String albumName;
    public String durationAsText;
    public boolean isArtistNameAvailable;
    public boolean isAlbumNameAvailable;
    public boolean isAlbumArtAvailable;

}
