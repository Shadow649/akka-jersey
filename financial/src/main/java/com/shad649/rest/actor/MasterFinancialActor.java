package com.shad649.rest.actor;

import com.shad649.actor.messages.ProcessTransaction;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.FromConfig;
import akka.routing.ConsistentHashingRouter.ConsistentHashableEnvelope;
/**
 * Handles {@link ProcessTransaction} that are sent through a cluster aware router to a worker
 * in the cluster. See financial.conf for details  
 * 
 * @author Emanuele Lombardi
 *
 */
public class MasterFinancialActor extends UntypedActor {
    
    private ActorRef router;
    
    public MasterFinancialActor() {
        router = getContext().actorOf(
                FromConfig.getInstance().props(Props.create(TransactionAggregatorWorker.class)),
                "workerRouter");    }

    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ProcessTransaction) {
            router.tell(new ConsistentHashableEnvelope(message, 
                    ((ProcessTransaction) message).getTransaction()), getSelf());
        } else {
            unhandled(message);
        }
    }
    
}
