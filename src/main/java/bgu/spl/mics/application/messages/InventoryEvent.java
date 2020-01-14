package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;

public class InventoryEvent implements Event {

    private String bookTitle;
    private Customer client;

    public InventoryEvent(String bookTitle, Customer client) {
        this.bookTitle = bookTitle;
        this.client = client;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Customer getClient() {
        return client;
    }
}




