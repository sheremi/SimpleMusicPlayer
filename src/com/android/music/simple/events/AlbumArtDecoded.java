package com.android.music.simple.events;

import android.graphics.Bitmap;

import com.github.androidutils.eventbus.ReflectionParcelableEvent;

public class AlbumArtDecoded extends ReflectionParcelableEvent {

    public Bitmap bitmap;
    public boolean isDrawableDithered;

}
