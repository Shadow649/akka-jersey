package com.shad649.rest.actor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.shad649.actor.messages.GetCollectedData;
import com.shad649.actor.messages.ProcessedTransactions;
import com.shad649.actor.messages.Store;
import com.shad649.actor.messages.Transform;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
/**
 * Mock implementation that uses an in-memory map as storage.
 * 
 * Handles {@link Store} to store the data processed.
 * Handles {@link GetCollectedData} to retrieve all the stored data. 
 * @author Emanuele Lombardi
 *
 */
public class MockStorageActor extends UntypedActor{
    private final Map<String,List<ProcessedTransactions>> map = new HashMap<String, List<ProcessedTransactions>>();
    private final ActorRef transformerActor;
    public MockStorageActor() {
        transformerActor = getContext().actorOf(Props.create(FlotTransformerActor.class));
    }
    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof Store) {
            Store toStore = (Store) message;
            String key = toStore.getTransactionCurrencies();
            List<ProcessedTransactions> update = map.get(key);
            if(update == null) {
                update = new LinkedList<ProcessedTransactions>();
            }
            update.add(toStore.getNumberOfTransaction());
            map.put(key, update);
        } else if (message instanceof GetCollectedData){
            transformerActor.forward(new Transform(Collections.unmodifiableMap(map)), getContext());
        } else {
            unhandled(message);
        }
    }
}
