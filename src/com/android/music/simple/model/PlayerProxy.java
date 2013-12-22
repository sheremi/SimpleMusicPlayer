package com.android.music.simple.model;

public class PlayerProxy implements IPlayer {
    private volatile IPlayer realPlayer;

    @Override
    public void toggleShuffle() {
        realPlayer.toggleShuffle();
    }

    @Override
    public void cycleRepeat() {
        realPlayer.cycleRepeat();
    }

    @Override
    public void togglePauseResume() {
        realPlayer.togglePauseResume();
    }

    @Override
    public void useAsARingtone() {
        realPlayer.useAsARingtone();
    }

    @Override
    public void togglePartyShuffle() {
        realPlayer.togglePartyShuffle();
    }

    @Override
    public void scanForward(int repcnt, long delta) {
        realPlayer.scanForward(repcnt, delta);
    }

    @Override
    public void scanBackward(int repcnt, long delta) {
        realPlayer.scanBackward(repcnt, delta);
    }

    public void setRealPlayer(IPlayer realPlayer) {
        synchronized (this) {
            this.realPlayer = realPlayer;
        }
    }

    @Override
    public void resume() {
        realPlayer.resume();
    }

    @Override
    public void pause() {
        realPlayer.pause();
    }

    @Override
    public void stop() {
        realPlayer.stop();
    }

    @Override
    public void skipForwards() {
        realPlayer.skipForwards();
    }

    @Override
    public void skipBackwards() {
        realPlayer.skipBackwards();
    }

    public PlayerProxy() {
        realPlayer = new NullPlayer();
    }
}
