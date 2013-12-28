package com.android.music.simple.remote;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.os.IBinder;

import com.android.music.simple.presentation.NowPlayingActivity;

public class RemoteControlService extends Service {
    private AudioManager mAudioManager;
    private RemoteControlClient mRemoteControlClient;

    @Override
    public void onCreate() {
        super.onCreate();
        // TODO register on a bus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        ComponentName myEventReceiver = new ComponentName(getPackageName(), MediaButtonIntentReceiver.class.getName());
        mAudioManager.registerMediaButtonEventReceiver(myEventReceiver);

        Intent intent = new Intent(this, NowPlayingActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        AudioManager myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myAudioManager.registerMediaButtonEventReceiver(myEventReceiver);
        // build the PendingIntent for the remote control client
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setComponent(myEventReceiver);
        PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, mediaButtonIntent, 0);
        // create and register the remote control client
        mRemoteControlClient = new RemoteControlClient(mediaPendingIntent);
        myAudioManager.registerRemoteControlClient(mRemoteControlClient);

        int flags = RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS | RemoteControlClient.FLAG_KEY_MEDIA_NEXT
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY | RemoteControlClient.FLAG_KEY_MEDIA_PAUSE
                | RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE | RemoteControlClient.FLAG_KEY_MEDIA_STOP;
        mRemoteControlClient.setTransportControlFlags(flags);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void handle() {
        // TODO
        // if (what.equals(PLAYSTATE_CHANGED)) {
        // mRemoteControlClient.setPlaybackState(isPlaying() ?
        // RemoteControlClient.PLAYSTATE_PLAYING
        // : RemoteControlClient.PLAYSTATE_PAUSED);
        // } else if (what.equals(META_CHANGED)) {
        // RemoteControlClient.MetadataEditor ed =
        // mRemoteControlClient.editMetadata(true);
        // ed.putString(MediaMetadataRetriever.METADATA_KEY_TITLE,
        // getTrackName());
        // ed.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM,
        // getAlbumName());
        // ed.putString(MediaMetadataRetriever.METADATA_KEY_ARTIST,
        // getArtistName());
        // ed.putLong(MediaMetadataRetriever.METADATA_KEY_DURATION, duration());
        // Bitmap b = MusicUtils.getArtwork(this, getAudioId(), getAlbumId(),
        // false);
        // if (b != null) {
        // ed.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, b);
        // }
        // ed.apply();
        // }
    }

    @Override
    public void onDestroy() {
        mAudioManager.unregisterRemoteControlClient(mRemoteControlClient);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
