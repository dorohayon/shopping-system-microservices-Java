package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.InventoryEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService {
    private Inventory inventory;
    private CountDownLatch countDownLatch;

    public InventoryService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
        inventory = Inventory.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeEvent(InventoryEvent.class, message -> {
            synchronized (inventory) {
                //check if available of not enough money to the customer
                int price = inventory.checkAvailabiltyAndGetPrice(message.getBookTitle());
                if (price == -1 || price > message.getClient().getAvailableCreditAmount()) {
                    this.complete(message, null);
                } else {
                    OrderResult result = inventory.take(message.getBookTitle());
                    if (result == OrderResult.SUCCESSFULLY_TAKEN) {
                        this.complete(message, price);
                    }
                }
            }
        });

        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });
        countDownLatch.countDown();

    }

}
