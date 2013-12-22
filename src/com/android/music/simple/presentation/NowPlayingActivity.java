package com.android.music.simple.presentation;

import android.app.Activity;
import android.os.Bundle;

import com.android.music.R;
import com.android.music.simple.events.PlaypositionChanged;
import com.android.music.simple.model.IPlayer;
import com.android.music.simple.model.PlayerService;
import com.github.androidutils.eventbus.EventBusBroadcastReceiver;
import com.github.androidutils.logger.Logger;
import com.google.common.eventbus.Subscribe;

public class NowPlayingActivity extends Activity {
    EventBusBroadcastReceiver eventBusBroadcastReceiver;
    private Logger logger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logger = Logger.getDefaultLogger();
        setContentView(R.layout.audio_player);
        IPlayer player = PlayerService.getPlayer();
        eventBusBroadcastReceiver = new EventBusBroadcastReceiver();
        eventBusBroadcastReceiver.register(this, this);
    }

    @Subscribe
    public void handle(PlaypositionChanged event) {
        logger.d(event.toString());
    }
}
