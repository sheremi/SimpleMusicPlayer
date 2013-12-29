package com.android.music.simple.presentation;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.android.music.R;
import com.android.music.simple.events.TrackInfoChanged;
import com.github.androidutils.eventbus.EventBusBroadcastReceiver;
import com.google.common.eventbus.Subscribe;

/**
 * TODO
 * 
 * @author Kate
 * 
 */
public class NotificationPresenter extends Service {
    private Context context;
    private EventBusBroadcastReceiver eventBusBroadcastReceiver;
    public static final int PLAYBACKSERVICE_STATUS = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, this);
        context = this;

    }

    @Subscribe
    public void handle(TrackInfoChanged event) {
        RemoteViews views = new RemoteViews(getPackageName(), R.layout.statusbar);
        views.setImageViewResource(R.id.icon, R.drawable.stat_notify_musicplayer);
        views.setTextViewText(R.id.trackname, event.trackName);
        if (!event.isArtistNameAvailable || !event.isAlbumNameAvailable) {
            // streaming
            views.setTextViewText(R.id.artistalbum, null);
        } else {
            views.setTextViewText(R.id.artistalbum,
                    context.getString(R.string.notification_artist_album, event.artistName, event.albumName));
        }

        Notification status = new Notification();
        status.contentView = views;
        status.flags |= Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.stat_notify_musicplayer;
        status.contentIntent = PendingIntent.getActivity(context, 0,
                new Intent("com.android.music.PLAYBACK_VIEWER").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
        startForeground(PLAYBACKSERVICE_STATUS, status);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
