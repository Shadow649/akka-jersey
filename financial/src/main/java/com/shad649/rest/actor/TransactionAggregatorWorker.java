package com.shad649.rest.actor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.routing.Router;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shad649.actor.messages.ProcessTransaction;
import com.shad649.actor.messages.ProcessedTransactions;
import com.shad649.actor.messages.Store;
import com.shad649.transaction.Transaction;
/**
 * Worker Actor created by the {@link Router} to handle transactions.
 * This worker simply keep track of the transaction sent to the system and every second is
 * responsible to aggregate and store the number of exchanges between currencies.
 * 
 * Handles {@link ProcessTransaction} transaction sent to this actor from the REST endpoint
 * Handles {@link Aggregate} message sent by the actor itself every second
 * @author Emanuele Lombardi
 *
 */
public class TransactionAggregatorWorker extends UntypedActor {
    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, Integer> map = new HashMap<String, Integer>();
    private final ActorSelection storageActor = getContext().actorSelection(
            "/user/financialStorageProxy");
    
    public TransactionAggregatorWorker() {
        getContext()
                .system()
                .scheduler()
                .schedule(Duration.Zero(),
                        Duration.create(1000, TimeUnit.MILLISECONDS),
                        new Runnable() {
                            public void run() {
                                getSelf().tell(new Aggregate(), getSelf());
                            }
                        }, getContext().system().dispatcher());
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ProcessTransaction) {
            ProcessTransaction toProcess = (ProcessTransaction) message;
            Transaction t = mapper.readValue(toProcess.getTransaction(),
                    Transaction.class);
            String curr = t.getCurrencyFrom() + "/" + t.getCurrencyTo();
            int i = 1;
            if (map.containsKey(curr)) {
                i = map.get(curr);
                i++;
            }
            map.put(curr, i);
        } else if (message instanceof Aggregate) {
            for (String key : map.keySet()) {
                ProcessedTransactions transactions = new ProcessedTransactions(new Date(), map.get(key));
                Store toSend = new Store(key,
                        transactions);
                storageActor.tell(toSend, getSelf());
            }
            map.clear();
        } else {
            unhandled(message);
        }
    }
    
    /**
     * Event sent to trigger the aggregation process 
     * @author Emanuele Lombardi
     *
     */
    private final class Aggregate implements Serializable {
        
        private static final long serialVersionUID = 1L;
        
    }
}
