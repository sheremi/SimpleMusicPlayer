package com.android.music.simple.events;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class RepeatChanged extends ReflectionParcelableEvent {
    public static final int REPEAT_NONE = 0;
    public static final int REPEAT_CURRENT = 1;
    public static final int REPEAT_ALL = 2;
    public int repeat;
}
