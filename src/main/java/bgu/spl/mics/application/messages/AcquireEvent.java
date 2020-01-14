package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class AcquireEvent implements Event {
    private Customer client;

    public AcquireEvent(Customer client) {
        this.client = client;
    }

    public Customer getClient() {
        return client;
    }
}
