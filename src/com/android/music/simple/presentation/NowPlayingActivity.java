package com.android.music.simple.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;

import com.android.music.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = Logger.getDefaultLogger();
        setContentView(R.layout.simple_now_playing_activity);
        player = PlayerService.getPlayer();
        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, this);
    }

    @Subscribe
    public void handle(PlaypositionChanged event) {
        logger.d(event.toString());
    }

    @Subscribe
    public void handle(TrackInfoChanged event) {
        String artistName = event.artistName;
        if (MediaStore.UNKNOWN_STRING.equals(artistName)) {
            artistName = getString(R.string.unknown_artist_name);
        }
        mArtistName.setText(artistName);
    }
}
