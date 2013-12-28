package com.android.music.simple;

import android.app.Application;
import android.content.Intent;

import com.android.music.simple.model.PlayerService;
import com.android.music.simple.presentation.NotificationPresenter;
import com.android.music.simple.presentation.WidgetPresenter;
import com.android.music.simple.remote.RemoteControlService;
import com.github.androidutils.logger.LogcatLogWriterWithLines;
import com.github.androidutils.logger.Logger;

public class PlayerApplication extends Application {
    @Override
    public void onCreate() {
        Logger.getDefaultLogger().addLogWriter(LogcatLogWriterWithLines.getInstance());
        startService(new Intent(getApplicationContext(), PlayerService.class));
        startService(new Intent(getApplicationContext(), RemoteControlService.class));
        startService(new Intent(getApplicationContext(), NotificationPresenter.class));
        startService(new Intent(getApplicationContext(), WidgetPresenter.class));
    }
}
