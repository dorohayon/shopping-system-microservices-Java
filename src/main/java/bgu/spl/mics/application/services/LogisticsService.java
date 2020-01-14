package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
    private int currentTime;
    private CountDownLatch countDownLatch;
    private int duration;
    private int speed;

    public LogisticsService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, message -> {
            this.currentTime = message.getCurrentTick();
            this.duration = message.getDuration();
            if (message.getCurrentTick() >= message.getDuration())
                this.terminate();
        });
        this.subscribeEvent(DeliveryEvent.class, message -> {
            Future<Future<DeliveryVehicle>> futureVehicle;
            AcquireEvent acquireEvent = new AcquireEvent(message.getClient());
            futureVehicle = this.sendEvent(acquireEvent);
            DeliveryVehicle vehicle = null;
            if (futureVehicle != null) {
                //wait for the vehicle to become available.
                Future<DeliveryVehicle> future = futureVehicle.get();
                if (future != null)
                    vehicle = future.get(duration * speed, TimeUnit.MILLISECONDS);
            }
            if (vehicle != null) {
                //check if the delievery time is more than the program time
                int ticksdelievery = (message.getClient().getDistance() * vehicle.getSpeed()) / this.speed;
                if (currentTime + ticksdelievery < duration)
                    vehicle.deliver(message.getClient().getAddress(), message.getClient().getDistance());
                Future<String> releaseFuture;
                ReleaseVehivleEvent releaseVehivleEvent = new ReleaseVehivleEvent(vehicle);
                releaseFuture = this.sendEvent(releaseVehivleEvent);
                if (releaseFuture != null) {
                    this.complete(message, "delivered succcessfully");
                } else {
                    this.complete(message, "not completed");
                }

            } else {
                this.complete(message, "not completed");
            }


        });
        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });

        this.subscribeBroadcast(durationBroadcast.class, message -> {
            this.duration = message.getDuration();
            this.speed = message.getSpeed();
        });

        countDownLatch.countDown();
    }

}
