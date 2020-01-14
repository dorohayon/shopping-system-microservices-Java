package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.*;

public class BookOrderEvent implements Event {
    private String bookTitle;
    private Customer client;
    private int orderid;
    private int makeTime;

    public BookOrderEvent(String bookTitle, Customer client, int makeTime) {
        this.bookTitle = bookTitle;
        this.client = client;
        this.makeTime = makeTime;
        this.orderid = 0;
    }

    public String getBookTitle() {
        return this.bookTitle;
    }

    public Customer getClient() {
        return client;
    }

    public int getOrderid() {
        return orderid;
    }

    public int getMakeTime() {
        return makeTime;
    }
}


