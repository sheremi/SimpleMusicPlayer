package com.android.music.simple.model;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import com.android.music.IMediaPlaybackService;
import com.android.music.MediaPlaybackService;
import com.android.music.MusicUtils;
import com.android.music.MusicUtils.Defs;
import com.android.music.MusicUtils.ServiceToken;
import com.android.music.R;
import com.android.music.simple.events.AlbumArtDecoded;
import com.android.music.simple.events.PlaypositionChanged;
import com.android.music.simple.events.TrackInfoChanged;
import com.github.androidutils.eventbus.IEventBus;
import com.github.androidutils.eventbus.IntentEventBus;
import com.github.androidutils.logger.Logger;

public class PlayerService extends Service {

    private static final PlayerProxy playerProxy = new PlayerProxy();

    public static IPlayer getPlayer() {
        return playerProxy;
    }

    private IMediaPlaybackService mService;

    private Logger logger;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ACTIVITY IMPORT

    private static final int USE_AS_RINGTONE = Defs.CHILD_MENU_BASE;

    private boolean mSeeking = false;
    private long mStartSeekPos = 0;
    private long mLastSeekEventTime;
    private Worker mAlbumArtWorker;
    private AlbumArtHandler mAlbumArtHandler;
    private ServiceToken mToken;
    private IEventBus eventBus;

    /** Called when the activity is first created. */
    @Override
    public void onCreate() {
        super.onCreate();
        logger = Logger.getDefaultLogger();

        eventBus = new IntentEventBus(getApplicationContext());
        playerProxy.setRealPlayer(new IPlayer() {
            @Override
            public void resume() {
                try {
                    if (!mService.isPlaying()) {
                        mService.play();
                    }
                    refreshNow();
                    setPauseButtonImage();
                } catch (RemoteException ex) {
                }
            }

            @Override
            public void pause() {
                try {
                    if (mService.isPlaying()) {
                        mService.pause();
                        refreshNow();
                        setPauseButtonImage();
                    }
                } catch (RemoteException ex) {
                }
            }

            @Override
            public void stop() {
                try {
                    mService.stop();
                    refreshNow();
                    setPauseButtonImage();
                } catch (RemoteException ex) {
                }
            }

            @Override
            public void skipForwards() {
                if (mService == null) return;
                try {
                    mService.next();
                } catch (RemoteException ex) {
                }
            }

            @Override
            public void skipBackwards() {
                try {
                    if (mService.position() < 2000) {
                        mService.prev();
                    } else {
                        mService.seek(0);
                        mService.play();
                    }
                } catch (RemoteException ex) {
                }
            }

            @Override
            public void toggleShuffle() {
                PlayerService.this.toggleShuffle();
            }

            @Override
            public void cycleRepeat() {
                PlayerService.this.cycleRepeat();
            }

            @Override
            public void togglePauseResume() {
                PlayerService.this.doPauseResume();
            }

            @Override
            public void scanBackward(int repcnt, long delta) {
                PlayerService.this.scanBackward(repcnt, delta);
            }

            @Override
            public void scanForward(int repcnt, long delta) {
                PlayerService.this.scanForward(repcnt, delta);
            }

            @Override
            public void useAsARingtone() {
                try {
                    MusicUtils.setRingtone(PlayerService.this, mService.getAudioId());
                } catch (RemoteException e) {
                }
            }

            @Override
            public void togglePartyShuffle() {
                MusicUtils.togglePartyShuffle();
            }
        });

        // TODO setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mAlbumArtWorker = new Worker("album art worker");
        mAlbumArtHandler = new AlbumArtHandler(mAlbumArtWorker.getLooper());

        seekmethod = 1;

        paused = false;

        mToken = MusicUtils.bindToService(this, osc);
        if (mToken == null) {
            logger.e("something went wrong");
            // something went wrong
            mHandler.sendEmptyMessage(QUIT);
        }

        IntentFilter f = new IntentFilter();
        f.addAction(MediaPlaybackService.PLAYSTATE_CHANGED);
        f.addAction(MediaPlaybackService.META_CHANGED);
        registerReceiver(mStatusListener, new IntentFilter(f));
        // TODO updateTrackInfo();
        // TODO long next = refreshNow();
        // TODO queueNextRefresh(next);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    int mInitialX = -1;
    int mLastX = -1;
    int mTextWidth = 0;
    int mViewWidth = 0;

    @Override
    public void onDestroy() {
        paused = true;
        mHandler.removeMessages(REFRESH);
        unregisterReceiver(mStatusListener);
        MusicUtils.unbindFromService(mToken);
        mService = new NullMediaPlayerService();
        mAlbumArtWorker.quit();
        super.onDestroy();
    }

    private int lastX;
    private int lastY;

    private void scanBackward(int repcnt, long delta) {
        if (mService == null) return;
        try {
            if (repcnt == 0) {
                mStartSeekPos = mService.position();
                mLastSeekEventTime = 0;
                mSeeking = false;
            } else {
                mSeeking = true;
                if (delta < 5000) {
                    // seek at 10x speed for the first 5 seconds
                    delta = delta * 10;
                } else {
                    // seek at 40x after that
                    delta = 50000 + (delta - 5000) * 40;
                }
                long newpos = mStartSeekPos - delta;
                if (newpos < 0) {
                    // move to previous track
                    mService.prev();
                    long duration = mService.duration();
                    mStartSeekPos += duration;
                    newpos += duration;
                }
                if (delta - mLastSeekEventTime > 250 || repcnt < 0) {
                    mService.seek(newpos);
                    mLastSeekEventTime = delta;
                }
                if (repcnt >= 0) {
                    mPosOverride = newpos;
                } else {
                    mPosOverride = -1;
                }
                refreshNow();
            }
        } catch (RemoteException ex) {
        }
    }

    private void scanForward(int repcnt, long delta) {
        if (mService == null) return;
        try {
            if (repcnt == 0) {
                mStartSeekPos = mService.position();
                mLastSeekEventTime = 0;
                mSeeking = false;
            } else {
                mSeeking = true;
                if (delta < 5000) {
                    // seek at 10x speed for the first 5 seconds
                    delta = delta * 10;
                } else {
                    // seek at 40x after that
                    delta = 50000 + (delta - 5000) * 40;
                }
                long newpos = mStartSeekPos + delta;
                long duration = mService.duration();
                if (newpos >= duration) {
                    // move to next track
                    mService.next();
                    mStartSeekPos -= duration; // is OK to go negative
                    newpos -= duration;
                }
                if (delta - mLastSeekEventTime > 250 || repcnt < 0) {
                    mService.seek(newpos);
                    mLastSeekEventTime = delta;
                }
                if (repcnt >= 0) {
                    mPosOverride = newpos;
                } else {
                    mPosOverride = -1;
                }
                refreshNow();
            }
        } catch (RemoteException ex) {
        }
    }

    private void doPauseResume() {
        try {
            if (mService != null) {
                if (mService.isPlaying()) {
                    mService.pause();
                } else {
                    mService.play();
                }
                refreshNow();
                setPauseButtonImage();
            }
        } catch (RemoteException ex) {
        }
    }

    private void toggleShuffle() {
        if (mService == null) return;
        try {
            int shuffle = mService.getShuffleMode();
            if (shuffle == MediaPlaybackService.SHUFFLE_NONE) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NORMAL);
                if (mService.getRepeatMode() == MediaPlaybackService.REPEAT_CURRENT) {
                    mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
                    setRepeatButtonImage();
                }
            } else if (shuffle == MediaPlaybackService.SHUFFLE_NORMAL || shuffle == MediaPlaybackService.SHUFFLE_AUTO) {
                mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
            } else {
                Log.e("MediaPlaybackActivity", "Invalid shuffle mode: " + shuffle);
            }
            setShuffleButtonImage();
        } catch (RemoteException ex) {
        }
    }

    private void cycleRepeat() {
        if (mService == null) return;
        try {
            int mode = mService.getRepeatMode();
            if (mode == MediaPlaybackService.REPEAT_NONE) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_ALL);
            } else if (mode == MediaPlaybackService.REPEAT_ALL) {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_CURRENT);
                if (mService.getShuffleMode() != MediaPlaybackService.SHUFFLE_NONE) {
                    mService.setShuffleMode(MediaPlaybackService.SHUFFLE_NONE);
                    setShuffleButtonImage();
                }
            } else {
                mService.setRepeatMode(MediaPlaybackService.REPEAT_NONE);
            }
            setRepeatButtonImage();
        } catch (RemoteException ex) {
        }
    }

    private void startPlayback() {
        if (mService == null) return;
        try {
            mService.play();
        } catch (RemoteException e) {
        }
        updateTrackInfo();
        long next = refreshNow();
        queueNextRefresh(next);
    }

    private final ServiceConnection osc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName classname, IBinder obj) {
            mService = IMediaPlaybackService.Stub.asInterface(obj);
            startPlayback();
            try {
                // Assume something is playing when the service says it is,
                // but also if the audio ID is valid but the service is paused.
                // something is playing now, we're done
                // TODO send some updates
                if (mService.getAudioId() >= 0 || mService.isPlaying() || mService.getPath() != null) return;
            } catch (RemoteException ex) {
            }
            // Service is dead or not playing anything. If we got here as part
            // of a "play this file" Intent, exit. Otherwise go to the Music
            // app start screen.
        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };

    private void setRepeatButtonImage() {
        if (mService == null) return;
        try {
            switch (mService.getRepeatMode()) {
            case MediaPlaybackService.REPEAT_ALL:
                // TODO
                break;
            case MediaPlaybackService.REPEAT_CURRENT:
                // TODO
                break;
            default:
                // TODO
                break;
            }
        } catch (RemoteException ex) {
        }
    }

    private void setShuffleButtonImage() {
        if (mService == null) return;
        try {
            switch (mService.getShuffleMode()) {
            case MediaPlaybackService.SHUFFLE_NONE:
                // TODO
                break;
            case MediaPlaybackService.SHUFFLE_AUTO:
                // TODO
                break;
            default:
                // TODO
                break;
            }
        } catch (RemoteException ex) {
        }
    }

    private void setPauseButtonImage() {
        try {
            if (mService != null && mService.isPlaying()) {
                // TODO
            } else {
                // TODO
            }
        } catch (RemoteException ex) {
        }
    }

    private long mPosOverride = -1;
    private final boolean mFromTouch = false;
    private long mDuration;
    private int seekmethod;
    private boolean paused;

    private static final int REFRESH = 1;
    private static final int QUIT = 2;
    private static final int GET_ALBUM_ART = 3;
    private static final int ALBUM_ART_DECODED = 4;

    private void queueNextRefresh(long delay) {
        if (!paused) {
            Message msg = mHandler.obtainMessage(REFRESH);
            mHandler.removeMessages(REFRESH);
            mHandler.sendMessageDelayed(msg, delay);
        }
    }

    private long refreshNow() {
        if (mService == null) return 500;
        PlaypositionChanged playpositionChanged = new PlaypositionChanged();
        try {

            long pos = mPosOverride < 0 ? mService.position() : mPosOverride;
            long remaining = 1000 - pos % 1000;
            if (pos >= 0 && mDuration > 0) {
                playpositionChanged.timeAsText = MusicUtils.makeTimeString(this, pos / 1000);

                playpositionChanged.isPlaying = mService.isPlaying();

                playpositionChanged.progress = ((int) (1000 * pos / mDuration));
            } else {
                playpositionChanged.timeAsText = ("--:--");
                playpositionChanged.progress = (1000);
            }
            eventBus.post(playpositionChanged);
            // return the number of milliseconds until the next full second, so
            // the counter can be updated at just the right time
            return remaining;
        } catch (RemoteException ex) {
        }
        return 500;
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ALBUM_ART_DECODED:
                AlbumArtDecoded albumArtDecoded = new AlbumArtDecoded();
                albumArtDecoded.bitmap = ((Bitmap) msg.obj);
                albumArtDecoded.isDrawableDithered = true;
                eventBus.post(albumArtDecoded);
                break;

            case REFRESH:
                long next = refreshNow();
                queueNextRefresh(next);
                break;

            default:
                break;
            }
        }
    };

    private final BroadcastReceiver mStatusListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MediaPlaybackService.META_CHANGED)) {
                // redraw the artist/title info and
                // set new max for progress bar
                updateTrackInfo();
                setPauseButtonImage();
                queueNextRefresh(1);
            } else if (action.equals(MediaPlaybackService.PLAYSTATE_CHANGED)) {
                setPauseButtonImage();
            }
        }
    };

    private static class AlbumSongIdWrapper {
        public long albumid;
        public long songid;

        AlbumSongIdWrapper(long aid, long sid) {
            albumid = aid;
            songid = sid;
        }
    }

    private void updateTrackInfo() {
        if (mService == null) return;
        TrackInfoChanged trackInfoChanged = new TrackInfoChanged();
        try {
            String path = mService.getPath();
            if (path == null) // TODO
                return;

            long songid = mService.getAudioId();
            if (songid < 0 && path.toLowerCase().startsWith("http://")) {
                // Once we can get album art and meta data from MediaPlayer, we
                // can show that info again when streaming.
                // TODO ((View)
                // mArtistName.getParent()).setVisibility(View.INVISIBLE);
                // TODO ((View)
                // mAlbumName.getParent()).setVisibility(View.INVISIBLE);
                // TODO mAlbum.setVisibility(View.GONE);
                trackInfoChanged.trackName = path;
                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART, new AlbumSongIdWrapper(-1, -1)).sendToTarget();
            } else {
                // TOD((View)
                // mArtistName.getParent()).setVisibility(View.VISIBLE);
                // TODO ((View)
                // mAlbumName.getParent()).setVisibility(View.VISIBLE);
                String artistName = mService.getArtistName();
                if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
                    artistName = getString(R.string.unknown_artist_name);
                }
                trackInfoChanged.artistName = artistName;
                String albumName = mService.getAlbumName();
                long albumid = mService.getAlbumId();
                if (MediaStore.UNKNOWN_STRING.equals(albumName)) {
                    albumName = getString(R.string.unknown_album_name);
                    albumid = -1;
                }
                trackInfoChanged.artistName = artistName;
                trackInfoChanged.trackName = mService.getTrackName();
                mAlbumArtHandler.removeMessages(GET_ALBUM_ART);
                mAlbumArtHandler.obtainMessage(GET_ALBUM_ART, new AlbumSongIdWrapper(albumid, songid)).sendToTarget();
                // TODO mAlbum.setVisibility(View.VISIBLE);
            }
            mDuration = mService.duration();
            trackInfoChanged.durationAsText = MusicUtils.makeTimeString(this, mDuration / 1000);
        } catch (RemoteException ex) {
        }
        eventBus.post(trackInfoChanged);
    }

    public class AlbumArtHandler extends Handler {
        private long mAlbumId = -1;

        public AlbumArtHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            long albumid = ((AlbumSongIdWrapper) msg.obj).albumid;
            long songid = ((AlbumSongIdWrapper) msg.obj).songid;
            if (msg.what == GET_ALBUM_ART && (mAlbumId != albumid || albumid < 0)) {
                // while decoding the new image, show the default album art
                Message numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, null);
                mHandler.removeMessages(ALBUM_ART_DECODED);
                mHandler.sendMessageDelayed(numsg, 300);
                // Don't allow default artwork here, because we want to fall
                // back to song-specific
                // album art if we can't find anything for the album.
                Bitmap bm = MusicUtils.getArtwork(PlayerService.this, songid, albumid, false);
                if (bm == null) {
                    bm = MusicUtils.getArtwork(PlayerService.this, songid, -1);
                    albumid = -1;
                }
                if (bm != null) {
                    numsg = mHandler.obtainMessage(ALBUM_ART_DECODED, bm);
                    mHandler.removeMessages(ALBUM_ART_DECODED);
                    mHandler.sendMessage(numsg);
                }
                mAlbumId = albumid;
            }
        }
    }

    private static class Worker implements Runnable {
        private final Object mLock = new Object();
        private Looper mLooper;

        /**
         * Creates a worker thread with the given name. The thread then runs a
         * {@link android.os.Looper}.
         * 
         * @param name
         *            A name for the new thread
         */
        Worker(String name) {
            Thread t = new Thread(null, this, name);
            t.setPriority(Thread.MIN_PRIORITY);
            t.start();
            synchronized (mLock) {
                while (mLooper == null) {
                    try {
                        mLock.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        public Looper getLooper() {
            return mLooper;
        }

        @Override
        public void run() {
            synchronized (mLock) {
                Looper.prepare();
                mLooper = Looper.myLooper();
                mLock.notifyAll();
            }
            Looper.loop();
        }

        public void quit() {
            mLooper.quit();
        }
    }

}
