package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehivleEvent implements Event {
    private DeliveryVehicle vehicle;

    public ReleaseVehivleEvent(DeliveryVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public DeliveryVehicle getVehicle() {
        return vehicle;
    }
}

