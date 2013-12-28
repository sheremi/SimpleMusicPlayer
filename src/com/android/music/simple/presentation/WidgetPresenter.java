package com.android.music.simple.presentation;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.android.music.MediaAppWidgetProvider;

/**
 * TODO connect to the bus
 * 
 * @author Yuriy
 * 
 */
public class WidgetPresenter extends Service {
    private final MediaAppWidgetProvider mAppWidgetProvider = MediaAppWidgetProvider.getInstance();

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            if (MediaAppWidgetProvider.CMDAPPWIDGETUPDATE.equals(cmd)) {
                // Someone asked us to refresh a set of specific widgets,
                // probably
                // because they were just added.
                int[] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
                // TODO do update if we have data
                // mAppWidgetProvider.performUpdate(WidgetPresenter.this,
                // appWidgetIds);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
