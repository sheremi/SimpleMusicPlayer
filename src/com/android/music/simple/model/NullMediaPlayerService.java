package com.android.music.simple.model;

import android.os.IBinder;
import android.os.RemoteException;

import com.android.music.IMediaPlaybackService;

final class NullMediaPlayerService implements IMediaPlaybackService {

    @Override
    public IBinder asBinder() {
        return null;
    }

    @Override
    public void stop() throws RemoteException {
    }

    @Override
    public void setShuffleMode(int shufflemode) throws RemoteException {
    }

    @Override
    public void setRepeatMode(int repeatmode) throws RemoteException {
    }

    @Override
    public void setQueuePosition(int index) throws RemoteException {
    }

    @Override
    public long seek(long pos) throws RemoteException {
        return 0;
    }

    @Override
    public int removeTracks(int first, int last) throws RemoteException {
        return 0;
    }

    @Override
    public int removeTrack(long id) throws RemoteException {
        return 0;
    }

    @Override
    public void prev() throws RemoteException {
    }

    @Override
    public long position() throws RemoteException {
        return 0;
    }

    @Override
    public void play() throws RemoteException {
    }

    @Override
    public void pause() throws RemoteException {
    }

    @Override
    public void openFile(String path) throws RemoteException {
    }

    @Override
    public void open(long[] list, int position) throws RemoteException {
    }

    @Override
    public void next() throws RemoteException {
    }

    @Override
    public void moveQueueItem(int from, int to) throws RemoteException {
    }

    @Override
    public boolean isPlaying() throws RemoteException {
        return false;
    }

    @Override
    public String getTrackName() throws RemoteException {
        return null;
    }

    @Override
    public int getShuffleMode() throws RemoteException {
        return 0;
    }

    @Override
    public int getRepeatMode() throws RemoteException {
        return 0;
    }

    @Override
    public int getQueuePosition() throws RemoteException {
        return 0;
    }

    @Override
    public long[] getQueue() throws RemoteException {
        return null;
    }

    @Override
    public String getPath() throws RemoteException {
        return null;
    }

    @Override
    public int getMediaMountedCount() throws RemoteException {
        return 0;
    }

    @Override
    public int getAudioSessionId() throws RemoteException {
        return 0;
    }

    @Override
    public long getAudioId() throws RemoteException {
        return 0;
    }

    @Override
    public String getArtistName() throws RemoteException {
        return null;
    }

    @Override
    public long getArtistId() throws RemoteException {
        return 0;
    }

    @Override
    public String getAlbumName() throws RemoteException {
        return null;
    }

    @Override
    public long getAlbumId() throws RemoteException {
        return 0;
    }

    @Override
    public void enqueue(long[] list, int action) throws RemoteException {
    }

    @Override
    public long duration() throws RemoteException {
        return 0;
    }
}