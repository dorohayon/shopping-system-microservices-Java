package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

import java.util.concurrent.CountDownLatch;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService {
    private MoneyRegister moneyRegister;
    private int currentTime;
    private CountDownLatch countDownLatch;

    public SellingService(String name, CountDownLatch countDownLatch) {
        super(name);
        this.countDownLatch = countDownLatch;
        moneyRegister = MoneyRegister.getInstance();
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, message -> {
            this.currentTime = message.getCurrentTick();
            if (message.getCurrentTick() >= message.getDuration())
                this.terminate();
        });
        this.subscribeEvent(BookOrderEvent.class, message -> {
            int processtick = this.currentTime;
            synchronized (message.getClient()) {
                Future<Integer> future;
                InventoryEvent inventoryEvent = new InventoryEvent(message.getBookTitle(), message.getClient());
                future = this.sendEvent(inventoryEvent);
                if (future != null && future.get() != null) {
                    //charge the customer
                    message.getClient().charge(future.get());
                    //make recipt
                    OrderReceipt receipt = new OrderReceipt(message.getOrderid(), this.getName(), message.getClient().getId(), future.get(), message.getBookTitle(), processtick, message.getMakeTime(), this.currentTime);
                    message.getClient().getCustomerReceiptList().add(receipt);
                    moneyRegister.file(receipt);
                    this.complete(message, receipt);
                    DeliveryEvent deliveryEvent = new DeliveryEvent(message.getClient());
                    Future<String> deliveryfuture;
                    deliveryfuture = this.sendEvent(deliveryEvent);
                } else {
                    this.complete(message, null);
                }
            }


        });

        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });
        countDownLatch.countDown();
    }

}
