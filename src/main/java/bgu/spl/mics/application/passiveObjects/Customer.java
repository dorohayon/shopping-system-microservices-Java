package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
    private int id;
    private String name;
    private String address;
    private int distance;
    private creditCard creditCard;
    private List<OrderReceipt> orderReceipts;
    private ArrayList<OrderSchedule> orderSchedule;

    public Customer() {
        this.orderReceipts = new LinkedList<>();
    }

    public Customer(String name, int id, String address, int distance, creditCard creditCard, ArrayList<OrderSchedule> orderSchedule) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.creditCard = creditCard;
        this.orderSchedule = orderSchedule;
        this.orderReceipts = new LinkedList<>();

    }

    /**
     * Retrieves the name of the customer.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retrieves the ID of the customer  .
     */
    public int getId() {
        return this.id;
    }

    /**
     * Retrieves the address of the customer.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Retrieves the distance of the customer from the store.
     */
    public int getDistance() {
        return this.distance;
    }


    /**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     *
     * @return A list of receipts.
     */
    public List<OrderReceipt> getCustomerReceiptList() {
        return this.orderReceipts;
    }

    /**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     *
     * @return Amount of money left.
     */
    public int getAvailableCreditAmount() {
        return this.creditCard.getAmount();
    }

    /**
     * Retrieves this customers credit card serial number.
     */
    public int getCreditNumber() {
        return this.creditCard.getNumber();
    }

    public void charge(int amount) {
        this.creditCard.setAmount(this.creditCard.getAmount() - amount);
    }

    public ArrayList<OrderSchedule> getOrderSchedule() {
        return orderSchedule;
    }
}

