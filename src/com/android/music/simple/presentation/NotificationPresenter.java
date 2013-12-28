package com.android.music.simple.presentation;

import android.content.Context;

/**
 * TODO
 * 
 * @author Yuriy
 * 
 */
public class NotificationPresenter {
    private Context context;

    // private void onTrackChanged() {
    // RemoteViews views = new RemoteViews(getPackageName(),
    // R.layout.statusbar);
    // views.setImageViewResource(R.id.icon,
    // R.drawable.stat_notify_musicplayer);
    // if (getAudioId() < 0) {
    // // streaming
    // views.setTextViewText(R.id.trackname, getPath());
    // views.setTextViewText(R.id.artistalbum, null);
    // } else {
    // String artist = getArtistName();
    // views.setTextViewText(R.id.trackname, getTrackName());
    // if (artist == null || artist.equals(MediaStore.UNKNOWN_STRING)) {
    // artist = getString(R.string.unknown_artist_name);
    // }
    // String album = getAlbumName();
    // if (album == null || album.equals(MediaStore.UNKNOWN_STRING)) {
    // album = context.getString(R.string.unknown_album_name);
    // }
    //
    // views.setTextViewText(R.id.artistalbum,
    // context.getString(R.string.notification_artist_album, artist, album));
    // }
    //
    // Notification status = new Notification();
    // status.contentView = views;
    // status.flags |= Notification.FLAG_ONGOING_EVENT;
    // status.icon = R.drawable.stat_notify_musicplayer;
    // status.contentIntent = PendingIntent.getActivity(context, 0,
    // new
    // Intent("com.android.music.PLAYBACK_VIEWER").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    // 0);
    // startForeground(PLAYBACKSERVICE_STATUS, status);
    // }
}
