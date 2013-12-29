package com.android.music.simple.remote;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.IBinder;

import com.android.music.simple.events.AlbumArtDecoded;
import com.android.music.simple.events.PlaybackStatusChanged;
import com.android.music.simple.events.TrackInfoChanged;
import com.github.androidutils.eventbus.EventBusBroadcastReceiver;
import com.google.common.eventbus.Subscribe;

public class RemoteControlService extends Service {
    private EventBusBroadcastReceiver eventBusBroadcastReceiver;
    private AudioManager mAudioManager;
    private RemoteControlClient mRemoteControlClient;

    @Override
    public void onCreate() {
        super.onCreate();
        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, this);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ComponentName myEventReceiver = new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(myEventReceiver);

        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(myEventReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
        mAudioManager.registerRemoteControlClient(mRemoteControlClient);

        int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
        mRemoteControlClient.setTransportControlFlags(flags);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Subscribe
    public void handle(PlaybackStatusChanged event) {

        mRemoteControlClient.setPlaybackState(event.isPlaying ? RemoteControlClient.PLAYSTATE_PLAYING
                : RemoteControlClient.PLAYSTATE_PAUSED);
    }

    @Subscribe
    public void handle(TrackInfoChanged event) {
        RemoteControlClient.MetadataEditor ed = mRemoteControlClient.editMetadata(true);
        ed.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, event.trackName);
        ed.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, event.albumName);
        ed.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST, event.artistName);
        // TODO ed.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION,
        // event.durationAsText);
        ed.apply();
    }

    @Subscribe
    public void handle(AlbumArtDecoded event) {
        RemoteControlClient.MetadataEditor ed = mRemoteControlClient.editMetadata(false);
        ed.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, event.bitmap);
        ed.apply();
    }

    @Override
    public void onDestroy() {
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
