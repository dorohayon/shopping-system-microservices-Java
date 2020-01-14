package bgu.spl.mics.application.passiveObjects;

import java.awt.print.Book;
import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import bgu.spl.mics.application.FilePrinter;

import java.awt.print.Book;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory implements Serializable {
    private ConcurrentHashMap<String, BookInventoryInfo> books;

    /**
     * Retrieves the single instance of this class.
     */
    private static class SingletonHolder {
        private static Inventory instance = new Inventory();
    }

    public static Inventory getInstance() {
        return SingletonHolder.instance;
    }

    private Inventory() {
        books = new ConcurrentHashMap<String, BookInventoryInfo>();
    }

    /**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(BookInventoryInfo[] inventory) {
        for (int i = 0; i < inventory.length; i++) {
            books.put(inventory[i].getBookTitle(), inventory[i]);
        }
    }

    /**
     * Attempts to take one book from the store.
     * <p>
     *
     * @param book Name of the book to take from the store
     * @return an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * The first should not change the state of the inventory while the
     * second should reduce by one the number of books of the desired type.
     */
    public OrderResult take(String book) {
        if (BookByTitle(book) == null)
            return OrderResult.NOT_IN_STOCK;
        else {
            if (BookByTitle(book).getAmountInInventory() > 0) {
                BookByTitle(book).DecreaseAmmountInInventory();
                return OrderResult.SUCCESSFULLY_TAKEN;
            } else {
                books.remove(book);
                return OrderResult.NOT_IN_STOCK;
            }

        }
    }

    /**
     * Checks if a certain book is available in the inventory.
     * <p>
     *
     * @param book Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
    public int checkAvailabiltyAndGetPrice(String book) {
        if (BookByTitle(book) == null)
            return -1;
        else {
            if (BookByTitle(book).getAmountInInventory() > 0)
                return BookByTitle(book).getPrice();
            return -1;
        }
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public void printInventoryToFile(String filename) {
        FilePrinter obj = FilePrinter.getInstance();
        HashMap<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, BookInventoryInfo> entry : books.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAmountInInventory());
        }
        obj.SerializeObject(map, filename);
    }


    private BookInventoryInfo BookByTitle(String book) {
        for (Map.Entry<String, BookInventoryInfo> entry : books.entrySet()) {
            if (entry.getValue().getBookTitle().equals(book))
                return entry.getValue();
        }
        return null;
    }
}


