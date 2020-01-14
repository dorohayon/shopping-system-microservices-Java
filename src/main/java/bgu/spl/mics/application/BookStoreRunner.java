package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        HashMap<Integer, Customer> customerHashMap = new HashMap<>();
        Gson gson = new Gson();
        try {
            Path path = Paths.get(args[0]);
            FileReader reader = new FileReader(path.toAbsolutePath().toString());
            //reading from Json
            BookStore bookStore = gson.fromJson(reader, BookStore.class);
            Inventory.getInstance().load(bookStore.getInitialInventory());
            Resources[] resources = bookStore.getInitialResources();
            for (int i = 0; i < resources.length; i++) {
                ResourcesHolder.getInstance().load(resources[i].getVehicles());
            }
            Services services = bookStore.getServices();
            //count number of all services except timeService
            int sum = services.getInventoryService() + services.getLogistics() + services.getSelling() + services.getCustomers().length + services.getResourcesService();
            CountDownLatch countDownLatch = new CountDownLatch(sum);
            LinkedList<Thread> threads = new LinkedList<>();

            //start all the services
            for (int i = 0; i < services.getInventoryService(); i++) {
                Thread t = new Thread(new InventoryService("Inventory Service " + i, countDownLatch));
                threads.add(t);
            }
            for (int i = 0; i < services.getLogistics(); i++) {
                Thread t = new Thread(new LogisticsService("Logistics Service " + i, countDownLatch));
                threads.add(t);
            }
            for (int i = 0; i < services.getResourcesService(); i++) {
                Thread t = new Thread(new ResourceService("Resource Service " + i, countDownLatch));
                threads.add(t);
            }
            for (int i = 0; i < services.getSelling(); i++) {
                Thread t = new Thread(new SellingService("Selling Service " + i, countDownLatch));
                threads.add(t);
            }

            for (int i = 0; i < services.getCustomers().length; i++) {
                ArrayList<OrderSchedule> orderSchedule = services.getCustomers()[i].getOrderSchedule();
                Thread t = new Thread(new APIService("API Service " + services.getCustomers()[i].getName(), orderSchedule, services.getCustomers()[i], countDownLatch));
                threads.add(t);
            }

            for (Thread t : threads) {
                t.start();
            }
            //wait for all the services to start running and register before timeservice.
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread thread = new Thread(new TimeService("Time Service", services.getTime().getSpeed(), services.getTime().getDuration()));
            threads.add(thread);
            thread.start();

            //wait for all the threads to finish.
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //make the outputs file
            FilePrinter printer = FilePrinter.getInstance();
            for (int i = 0; i < services.getCustomers().length; i++) {
                customerHashMap.put(services.getCustomers()[i].getId(), services.getCustomers()[i]);
            }

            printer.SerializeObject(customerHashMap, args[1]);
            Inventory inventory = Inventory.getInstance();
            inventory.printInventoryToFile(args[2]);
            MoneyRegister moneyRegister = MoneyRegister.getInstance();
            moneyRegister.printOrderReceipts(args[3]);
            printer.SerializeObject(moneyRegister, args[4]);

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

}
