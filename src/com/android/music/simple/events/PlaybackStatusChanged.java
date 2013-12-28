package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class PlaybackStatusChanged extends ReflectionParcelableEvent {
    public boolean isPlaying;
}
