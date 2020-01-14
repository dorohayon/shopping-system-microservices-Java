package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService {
    private int speed;
    private int duration;
    private Timer timer;
    private int currentTime;
    private TimerTask task;

    public TimeService(String name, int speed, int duration) {
        super(name);
        this.currentTime = 0;
        this.speed = speed;
        this.duration = duration;
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (currentTime == duration) {
                    this.cancel();
                    timer.cancel();
                    timer.purge();
                    sendBroadcast(new terminateBroadcast());
                    sendBroadcast(new ResolveAllBroadcast());
                } else {
                    currentTime++;
                    sendBroadcast(new TickBroadcast(currentTime, duration));
                }

            }

        };
    }

    @Override
    protected void initialize() {
        timer.schedule(task, 0, speed);
        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });
        this.sendBroadcast(new durationBroadcast(this.speed, this.duration));

    }

}
