package com.android.music.simple.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.music.R;
import com.android.music.RepeatingImageButton;
import com.android.music.simple.events.PlaypositionChanged;
import com.android.music.simple.events.TrackInfoChanged;
import com.android.music.simple.model.IPlayer;
import com.android.music.simple.model.PlayerService;
import com.github.androidutils.eventbus.EventBusBroadcastReceiver;
import com.github.androidutils.logger.Logger;
import com.google.common.eventbus.Subscribe;

public class NowPlayingActivity extends Activity {
    EventBusBroadcastReceiver eventBusBroadcastReceiver;
    private Logger logger;
    private TextView mArtistName;
    private IPlayer player;
    private ImageView mAlbum;
    private TextView mAlbumName;
    private TextView mTrackName;
    private TextView mCurrentTime;
    private TextView mTotalTime;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = Logger.getDefaultLogger();
        setContentView(R.layout.simple_now_playing_activity);

        mArtistName = (TextView) findViewById(R.id.artistname);
        mAlbum = (ImageView) findViewById(R.id.album);
        mAlbumName = (TextView) findViewById(R.id.albumname);
        mTrackName = (TextView) findViewById(R.id.trackname);
        mCurrentTime = (TextView) findViewById(R.id.currenttime);
        mTotalTime = (TextView) findViewById(R.id.totaltime);

        mProgress = (ProgressBar) findViewById(android.R.id.progress);

        RepeatingImageButton prevButton = (RepeatingImageButton) findViewById(R.id.prev);
        PrevButtonListener prevButtonListener = new PrevButtonListener();
        prevButton.setOnClickListener(prevButtonListener);
        prevButton.setRepeatListener(prevButtonListener, 260);

        ImageButton pauseButton = (ImageButton) findViewById(R.id.pause);
        pauseButton.requestFocus();
        pauseButton.setOnClickListener(new PlayPauseButtonListener());

        RepeatingImageButton nextButton = (RepeatingImageButton) findViewById(R.id.next);
        NextButtonListener nextButtonListener = new NextButtonListener();
        nextButton.setOnClickListener(nextButtonListener);
        nextButton.setRepeatListener(nextButtonListener, 260);

        player = PlayerService.getPlayer();
        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, this);
    }

    @Subscribe
    public void handle(PlaypositionChanged event) {
        mCurrentTime.setText(event.timeAsText);
    }

    @Subscribe
    public void handle(TrackInfoChanged event) {
        String artistName = event.artistName;
        String albumName = event.albumName;
        if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
            artistName = getString(R.string.unknown_artist_name);
        }
        if (MediaStore.UNKNOWN_STRING.equals(albumName)) {
            albumName = getString(R.string.unknown_album_name);
        }
        mArtistName.setText(artistName);
        mAlbumName.setText(albumName);
        mTrackName.setText(event.trackName);
        mTotalTime.setText(event.durationAsText);
    }

    private final class NextButtonListener implements View.OnClickListener, RepeatingImageButton.RepeatListener {
        @Override
        public void onClick(View v) {
            player.skipForwards();
        }

        @Override
        public void onRepeat(View v, long howlong, int repcnt) {
            player.scanForward(repcnt, howlong);
        }
    }

    private final class PrevButtonListener implements View.OnClickListener, RepeatingImageButton.RepeatListener {
        @Override
        public void onClick(View v) {
            player.skipBackwards();
        }

        @Override
        public void onRepeat(View v, long howlong, int repcnt) {
            player.scanBackward(repcnt, howlong);
        }
    }

    private final class PlayPauseButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            player.togglePauseResume();
        }
    }

    private final View.OnClickListener mRepeatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            player.cycleRepeat();
        }
    };

}