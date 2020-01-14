package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class durationBroadcast implements Broadcast {
    private int speed;
    private int duration;

    public durationBroadcast(int speed, int duration) {
        this.speed = speed;
        this.duration = duration;
    }

    public int getSpeed() {
        return speed;
    }

    public int getDuration() {
        return duration;
    }
}
