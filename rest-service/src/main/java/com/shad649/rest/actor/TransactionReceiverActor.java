package com.shad649.rest.actor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.shad649.actor.messages.GetCollectedData;
import com.shad649.actor.messages.ProcessTransaction;

import akka.actor.ActorSelection;
import akka.actor.Address;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.CurrentClusterState;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.ReachabilityEvent;
import akka.cluster.ClusterEvent.ReachableMember;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.cluster.Member;
import akka.cluster.MemberStatus;

/**
 * Actor that is responsible to connect the REST entpoints to the cluster nodes tagged as compute.
 * 
 * Handles {@link ProcessTransaction} used to send the transaction submitted to the endpoint
 * to the workers actor.
 * Handles {@link GetCollectedData} used to retrieve the processed data
 * @author Emanuele Lombardi
 *
 */
public class TransactionReceiverActor extends UntypedActor {

    final Set<Address> nodes = new HashSet<Address>();
    Cluster cluster = Cluster.get(getContext().system());
    String servicePath;
    String receiverPath;

    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), MemberEvent.class, ReachabilityEvent.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    public TransactionReceiverActor(String servicePath, String receiverPath) {
        this.servicePath = servicePath;
        this.receiverPath = receiverPath;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof ProcessTransaction) {
            if (nodes.size() > 0) {
                List<Address> nodesList = new ArrayList<Address>(nodes);
                Address address = nodesList.get(ThreadLocalRandom.current().nextInt(nodesList.size()));
                ActorSelection service = getContext().actorSelection(address + servicePath);
                service.tell(message, getSelf());
                getSender().tell("added", getSelf());
            } else {
                getSender().tell("notReady", getSelf());
            }
        } else if (message instanceof GetCollectedData) {
            if (nodes.size() > 0) {
                List<Address> nodesList = new ArrayList<Address>(nodes);
                Address address = nodesList.get(ThreadLocalRandom.current().nextInt(nodesList.size()));
                ActorSelection service = getContext().actorSelection(address + receiverPath);
                service.tell(message, getSender());
            } else {
                getSender().tell("notReady", getSelf());
            }
        } else if (message instanceof CurrentClusterState) {
            CurrentClusterState state = (CurrentClusterState) message;
            nodes.clear();
            for (Member member : state.getMembers()) {
                if (member.hasRole("compute") && member.status().equals(MemberStatus.up())) {
                    nodes.add(member.address());
                }
            }

        } else if (message instanceof MemberUp) {
            MemberUp mUp = (MemberUp) message;
            if (mUp.member().hasRole("compute"))
                nodes.add(mUp.member().address());

        } else if (message instanceof MemberEvent) {
            MemberEvent other = (MemberEvent) message;
            nodes.remove(other.member().address());

        } else if (message instanceof UnreachableMember) {
            UnreachableMember unreachable = (UnreachableMember) message;
            nodes.remove(unreachable.member().address());

        } else if (message instanceof ReachableMember) {
            ReachableMember reachable = (ReachableMember) message;
            if (reachable.member().hasRole("compute"))
                nodes.add(reachable.member().address());

        } else {
            unhandled(message);
        }
    }

}
