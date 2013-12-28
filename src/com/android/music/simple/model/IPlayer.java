package com.android.music.simple.model;

/** A facade for all player interaction */
public interface IPlayer {
    public void resume();

    public void pause();

    public void stop();

    public void skipForwards();

    public void skipBackwards();

    void toggleShuffle();

    void cycleRepeat();

    void togglePauseResume();

    void useAsARingtone();

    void togglePartyShuffle();

    void scanForward(int repcnt, long delta);

    void scanBackward(int repcnt, long delta);

    void seek(int progress);
}
