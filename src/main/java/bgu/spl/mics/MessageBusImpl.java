package bgu.spl.mics;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

import bgu.spl.mics.application.messages.ResolveAllBroadcast;
import sun.misc.Queue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
    private ConcurrentMap<MicroService, LinkedBlockingQueue<Message>> queueHashMap;
    private ConcurrentMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventHashMap;
    private ConcurrentMap<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> broadcastHashMap;
    private ConcurrentMap<Event, Future> futureHashMap;

    private static class SingletonHolder {
        private static MessageBusImpl instance = new MessageBusImpl();
    }

    private MessageBusImpl() {
        queueHashMap = new ConcurrentHashMap<>();
        eventHashMap = new ConcurrentHashMap<>();
        broadcastHashMap = new ConcurrentHashMap<>();
        futureHashMap = new ConcurrentHashMap<>();
    }

    public static MessageBusImpl getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        synchronized (eventHashMap) {
            if (eventHashMap.containsKey(type)) {
                eventHashMap.get(type).add(m);
            } else {
                eventHashMap.put(type, new LinkedBlockingQueue<>());
                eventHashMap.get(type).add(m);
            }
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        synchronized (broadcastHashMap) {
            LinkedBlockingQueue<MicroService> linkedBlockingQueue = new LinkedBlockingQueue<>();
            if (broadcastHashMap.containsKey(type)) {
                broadcastHashMap.get(type).add(m);
            } else {
                linkedBlockingQueue.add(m);
                broadcastHashMap.put(type, linkedBlockingQueue);
            }
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        futureHashMap.get(e).resolve(result);
        this.futureHashMap.remove(e);
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        if (b.getClass().equals(ResolveAllBroadcast.class)) {
            for (Map.Entry<Event, Future> entry : futureHashMap.entrySet()) {
                entry.getValue().resolve(null);
            }
        }


        LinkedBlockingQueue<MicroService> services = broadcastHashMap.get(b.getClass());
        if (services != null) {
            Iterator<MicroService> iter = services.iterator();
            while (iter.hasNext()) {
                try {
                    MicroService microService = iter.next();
                    if (queueHashMap.get(microService) != null)
                        queueHashMap.get(microService).put(b);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        synchronized (e.getClass()) {
            if (!eventHashMap.containsKey(e.getClass()))
                return null;
            else if (eventHashMap.get(e.getClass()).isEmpty())
                return null;
            Future f = new Future();
            futureHashMap.put(e, f);
            LinkedBlockingQueue<MicroService> services = eventHashMap.get(e.getClass());
            try {
                MicroService microService = services.take();
                if (queueHashMap.get(microService) != null)
                    queueHashMap.get(microService).add(e);
                services.add(microService);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            //f.get();
            return f;
        }
    }


    @Override
    public void register(MicroService m) {
        synchronized (queueHashMap) {
            LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
            queueHashMap.put(m, messageQueue);
        }
    }

    @Override
    public void unregister(MicroService m) {
        if (queueHashMap.containsKey(m))
            queueHashMap.remove(m);
        synchronized (eventHashMap) {
            for (Map.Entry<Class<? extends Event>, LinkedBlockingQueue<MicroService>> entry : eventHashMap.entrySet()) {
                if (entry.getValue().contains(m)) {
                    entry.getValue().remove(m);
                }
            }
        }
        synchronized (broadcastHashMap) {
            for (Map.Entry<Class<? extends Broadcast>, LinkedBlockingQueue<MicroService>> entry : broadcastHashMap.entrySet()) {
                if (entry.getValue().contains(m))
                    entry.getValue().remove(m);
            }
        }

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        if (!queueHashMap.containsKey(m))
            throw new IllegalStateException("non registered service");
        LinkedBlockingQueue<Message> messageQueue = queueHashMap.get(m);
        Message message = messageQueue.take();
        return message;
    }
}


