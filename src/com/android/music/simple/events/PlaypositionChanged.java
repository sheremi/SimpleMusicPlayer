package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class PlaypositionChanged extends ReflectionParcelableEvent {

    public String timeAsText;
    public boolean isPlaying;
    public int progress;

}
