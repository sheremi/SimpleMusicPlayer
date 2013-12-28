package com.android.music.simple.model;

final class NullPlayer implements IPlayer {
    @Override
    public void stop() {
    }

    @Override
    public void skipForwards() {
    }

    @Override
    public void skipBackwards() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void toggleShuffle() {
    }

    @Override
    public void cycleRepeat() {
    }

    @Override
    public void togglePauseResume() {
    }

    @Override
    public void useAsARingtone() {
    }

    @Override
    public void togglePartyShuffle() {
    }

    @Override
    public void scanForward(int repcnt, long delta) {
    }

    @Override
    public void scanBackward(int repcnt, long delta) {
    }

    @Override
    public void startPlayback() {
    }
}