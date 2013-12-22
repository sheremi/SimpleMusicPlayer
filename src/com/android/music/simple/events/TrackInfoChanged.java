package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class TrackInfoChanged extends ReflectionParcelableEvent {

    public String trackName;
    public String artistName;
    public String durationAsText;

}
