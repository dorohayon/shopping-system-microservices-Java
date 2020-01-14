package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class DeliveryEvent implements Event {
    private Customer client;

    public DeliveryEvent(Customer client) {
        this.client = client;
    }

    public Customer getClient() {
        return client;
    }
}
