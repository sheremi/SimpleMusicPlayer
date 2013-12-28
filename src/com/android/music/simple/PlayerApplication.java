package com.android.music.simple;

import android.app.Application;
import android.content.Intent;

import com.android.music.simple.model.PlayerService;
import com.github.androidutils.logger.LogcatLogWriterWithLines;
import com.github.androidutils.logger.Logger;

public class PlayerApplication extends Application {
    @Override
    public void onCreate() {
        Logger.getDefaultLogger().addLogWriter(LogcatLogWriterWithLines.getInstance());
        startService(new Intent(getApplicationContext(), PlayerService.class));
        // TODO start other services OR register their receivers in manifest
    }
}
