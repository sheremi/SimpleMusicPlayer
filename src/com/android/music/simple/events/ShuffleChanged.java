package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class ShuffleChanged extends ReflectionParcelableEvent {
    public static final int SHUFFLE_NONE = 0;
    public static final int SHUFFLE_NORMAL = 1;
    public static final int SHUFFLE_AUTO = 2;
    public int shuffle;

}
