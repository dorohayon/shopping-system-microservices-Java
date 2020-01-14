package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireEvent;
import bgu.spl.mics.application.messages.ReleaseVehivleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

import java.util.concurrent.CountDownLatch;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
    private ResourcesHolder resourcesHolder;
    private CountDownLatch countDownLatch;

    public ResourceService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
        resourcesHolder = ResourcesHolder.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(AcquireEvent.class, message -> {
            Future<DeliveryVehicle> vehicle = resourcesHolder.acquireVehicle();
            this.complete(message, vehicle);
        });

        this.subscribeEvent(ReleaseVehivleEvent.class, message -> {
            resourcesHolder.releaseVehicle(message.getVehicle());
            this.complete(message, "released");
        });

        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });
        countDownLatch.countDown();
    }

}
