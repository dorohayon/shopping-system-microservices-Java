package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
    private BlockingQueue<DeliveryVehicle> vehicles;
    private BlockingQueue<Future<DeliveryVehicle>> futureQueue;

    /**
     * Retrieves the single instance of this class.
     */
    private static class SingletonHolder {
        private static ResourcesHolder instance = new ResourcesHolder();
    }

    public static ResourcesHolder getInstance() {
        return ResourcesHolder.SingletonHolder.instance;
    }

    private ResourcesHolder() {
        this.vehicles = new LinkedBlockingQueue();
        this.futureQueue = new LinkedBlockingQueue<>();
    }

    /**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     *
     * @return {@link Future<DeliveryVehicle>} object which will resolve to a
     * {@link DeliveryVehicle} when completed.
     */
    public synchronized Future<DeliveryVehicle> acquireVehicle() {
        Future<DeliveryVehicle> deliveryVehicleFuture = new Future<>();
        if (!vehicles.isEmpty()) {
            deliveryVehicleFuture.resolve(vehicles.remove());
            return deliveryVehicleFuture;
        } else {
            futureQueue.add(deliveryVehicleFuture);
            return deliveryVehicleFuture;
        }

    }

    /**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     *
     * @param vehicle {@link DeliveryVehicle} to be released.
     */
    public synchronized void releaseVehicle(DeliveryVehicle vehicle) {
        vehicles.add(vehicle);
        if (!futureQueue.isEmpty()) {
            Future<DeliveryVehicle> deliveryVehicleFuture = futureQueue.remove();
            deliveryVehicleFuture.resolve(vehicle);
        }
    }

    /**
     * Receives a collection of vehicles and stores them.
     * <p>
     *
     * @param vehicles Array of {@link DeliveryVehicle} instances to store.
     */
    public void load(DeliveryVehicle[] vehicles) {
        for (int i = 0; i < vehicles.length; i++) {
            this.vehicles.add(vehicles[i]);
        }
    }

}
