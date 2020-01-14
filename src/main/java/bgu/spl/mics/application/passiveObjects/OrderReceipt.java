package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {
    private int orderId;
    private String seller;
    private int customerId;
    private int price;
    private String bookTitle;
    private int proccesedTick;
    private int orderTick;
    private int issuedTick;

    public OrderReceipt(int orderId, String seller, int customerId, int price, String bookTitle, int proccesedTick, int orderTick, int issuedTick) {
        this.orderId = orderId;
        this.seller = seller;
        this.customerId = customerId;
        this.price = price;
        this.bookTitle = bookTitle;
        this.proccesedTick = proccesedTick;
        this.orderTick = orderTick;
        this.issuedTick = issuedTick;
    }

    /**
     * Retrieves the orderId of this receipt.
     */
    public int getOrderId() {
        return this.orderId;
    }

    /**
     * Retrieves the name of the selling service which handled the order.
     */
    public String getSeller() {
        return this.seller;
    }

    /**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     *
     * @return the ID of the customer
     */
    public int getCustomerId() {
        return this.customerId;
    }

    /**
     * Retrieves the name of the book which was bought.
     */
    public String getBookTitle() {
        return this.bookTitle;
    }

    /**
     * Retrieves the price the customer paid for the book.
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Retrieves the tick in which this receipt was issued.
     */
    public int getIssuedTick() {
        return this.issuedTick;
    }

    /**
     * Retrieves the tick in which the customer sent the purchase request.
     */
    public int getOrderTick() {
        return this.orderTick;
    }

    /**
     * Retrieves the tick in which the treating selling service started
     * processing the order.
     */
    public int getProcessTick() {
        return this.proccesedTick;
    }
}
