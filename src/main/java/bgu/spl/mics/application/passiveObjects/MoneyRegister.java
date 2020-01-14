package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.FilePrinter;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
    private int totalEarnings;
    private List<OrderReceipt> orderRecipts;

    private static class SingletonHolder {
        private static MoneyRegister instance = new MoneyRegister();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MoneyRegister getInstance() {
        return MoneyRegister.SingletonHolder.instance;
    }

    private MoneyRegister() {
        this.orderRecipts = new LinkedList<OrderReceipt>();
        this.totalEarnings = 0;
    }


    /**
     * Saves an order receipt in the money register.
     * <p>
     *
     * @param r The receipt to save in the money register.
     */
    public void file(OrderReceipt r) {
        this.orderRecipts.add(r);
        this.totalEarnings = totalEarnings + r.getPrice();

    }

    /**
     * Retrieves the current total earnings of the store.
     */
    public int getTotalEarnings() {
        return this.totalEarnings;
    }

    /**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     *
     * @param amount amount to charge
     */
    public void chargeCreditCard(Customer c, int amount) {
        c.charge(amount);
    }

    /**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output..
     */
    private List<OrderReceipt> getOrderRecipts() {
        return this.orderRecipts;
    }

    /**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output..
     */
    public void printOrderReceipts(String filename) {
        FilePrinter obj = FilePrinter.getInstance();
        List<OrderReceipt> receipts = this.getOrderRecipts();
        obj.SerializeObject(receipts, filename);
    }
}
