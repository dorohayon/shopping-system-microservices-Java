package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

    private HashMap<Integer, List<OrderSchedule>> orders;
    private int currentTime;
    private Customer customer;
    private CountDownLatch countDownLatch;

    public APIService(String name, List<OrderSchedule> ord, Customer customer, CountDownLatch countDownLatch) {
        super(name);
        this.currentTime = 0;
        this.countDownLatch = countDownLatch;
        this.customer = customer;
        orders = new HashMap<>();
        Iterator<OrderSchedule> it = ord.iterator();
        //insert the orders to the right tick in the map
        while (it.hasNext()) {
            OrderSchedule orderSchedule = it.next();
            int tick = orderSchedule.getTick();
            if (!orders.containsKey(tick)) {
                List<OrderSchedule> list = new LinkedList<>();
                list.add(orderSchedule);
                orders.put(tick, list);
            } else
                orders.get(tick).add(orderSchedule);
        }
    }

    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, message -> {
            this.currentTime = message.getCurrentTick();
            if (message.getCurrentTick() >= message.getDuration())
                this.terminate();
            //send the orders on the correct tick
            if (orders.containsKey(currentTime)) {
                List<OrderSchedule> list = orders.get(currentTime);
                while (!list.isEmpty()) {
                    OrderSchedule orderSchedule = list.remove(0);
                    BookOrderEvent bookOrderEvent = new BookOrderEvent(orderSchedule.getBookTitle(), this.customer, orderSchedule.getTick());
                    Future<OrderReceipt> future = sendEvent(bookOrderEvent);
                }
            }
        });

        this.subscribeBroadcast(terminateBroadcast.class, message -> {
            this.terminate();
        });


        countDownLatch.countDown();

    }

}
