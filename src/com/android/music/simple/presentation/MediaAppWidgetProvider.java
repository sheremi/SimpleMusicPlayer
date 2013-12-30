/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.music.simple.presentation;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.RemoteViews;

import com.android.music.MediaPlaybackActivity;
import com.android.music.MediaPlaybackService;
import com.android.music.MusicBrowserActivity;
import com.android.music.R;
import com.android.music.simple.events.PlaybackStatusChanged;
import com.android.music.simple.events.TrackInfoChanged;
import com.github.androidutils.eventbus.EventBusBroadcastReceiver;
import com.google.common.eventbus.Subscribe;

/**
 * Simple widget to show currently playing album art along with play/pause and
 * next track buttons.
 */
public class MediaAppWidgetProvider extends AppWidgetProvider {
    private Context context;
    private EventBusBroadcastReceiver eventBusBroadcastReceiver;

    @SuppressWarnings("unused")
    private static MediaAppWidgetProvider sInstance;

    @Override
    public void onUpdate(Context restrictedContext, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        sInstance = this;

        this.context = restrictedContext.getApplicationContext();
        defaultAppWidget(context, appWidgetIds);

        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, context);
    }

    /**
     * Initialize given widgets to default state, where we launch Music on
     * default click and hide actions if service not running.
     */
    private void defaultAppWidget(Context context, int[] appWidgetIds) {
        final Resources res = context.getResources();
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.album_appwidget);

        views.setViewVisibility(R.id.title, View.GONE);
        views.setTextViewText(R.id.artist, res.getText(R.string.widget_initial_text));

        linkButtons(context, views, false /* not playing */);
        pushUpdate(context, appWidgetIds, views);
    }

    private void pushUpdate(Context context, int[] appWidgetIds, RemoteViews views) {
        // Update specific list of appWidgetIds if given, otherwise default to
        // all
        final AppWidgetManager gm = AppWidgetManager.getInstance(context);
        if (appWidgetIds != null) {
            gm.updateAppWidget(appWidgetIds, views);
        } else {
            gm.updateAppWidget(new ComponentName(context, this.getClass()), views);
        }
    }

    @Subscribe
    public void handle(TrackInfoChanged event) {
        // final Resources res = context.getResources();
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.album_appwidget);

        // No error, so show normal titles
        views.setViewVisibility(R.id.title, View.VISIBLE);
        views.setTextViewText(R.id.title, event.trackName);
        views.setTextViewText(R.id.artist, event.artistName);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        pushUpdate(context, appWidgetIds, views);
    }

    @Subscribe
    public void handle(PlaybackStatusChanged event) {
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.album_appwidget);
        if (event.isPlaying) {
            views.setImageViewResource(R.id.control_play, R.drawable.ic_appwidget_music_pause);
        } else {
            views.setImageViewResource(R.id.control_play, R.drawable.ic_appwidget_music_play);
        }

        // Link actions buttons to intents
        linkButtons(context, views, event.isPlaying);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        pushUpdate(context, appWidgetIds, views);
    }

    /**
     * Link up various button actions using {@link PendingIntents}.
     * 
     * @param playerActive
     *            True if player is active in background, which means widget
     *            click will launch {@link MediaPlaybackActivity}, otherwise we
     *            launch {@link MusicBrowserActivity}.
     */
    private void linkButtons(Context context, RemoteViews views, boolean playerActive) {
        // Connect up various buttons and touch events
        Intent intent;
        PendingIntent pendingIntent;

        final ComponentName serviceName = new ComponentName(context, MediaPlaybackService.class);

        intent = new Intent("com.android.music.PLAYBACK_VIEWER");
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.album_appwidget, pendingIntent);

        intent = new Intent(MediaPlaybackService.TOGGLEPAUSE_ACTION);
        intent.setComponent(serviceName);
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.control_play, pendingIntent);

        intent = new Intent(MediaPlaybackService.NEXT_ACTION);
        intent.setComponent(serviceName);
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.control_next, pendingIntent);
    }
}
