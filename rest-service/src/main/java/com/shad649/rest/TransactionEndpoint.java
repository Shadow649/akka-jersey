package com.shad649.rest;

import javax.annotation.PreDestroy;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.shad649.rest.actor.TransactionReceiverActor;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
/**
 * Jersey main endpoint.
 * @author Emanuele Lombardi
 *
 */
@ApplicationPath("labcamp")
public class TransactionEndpoint extends ResourceConfig {
  private ActorSystem system;
  private ActorRef tr;

  public TransactionEndpoint() {

      system = ActorSystem.create("ClusterSystem", ConfigFactory.load("financial"));
      tr = system.actorOf(Props.create(TransactionReceiverActor.class, "/user/financialServiceProxy","/user/financialStorageProxy"), "transactionReceiver");


      register(new AbstractBinder() {
          protected void configure() {
              bind(system).to(ActorSystem.class);
              bind(tr).to(ActorRef.class);
          }
      });

      register(new JacksonJsonProvider().
          configure(SerializationFeature.INDENT_OUTPUT, true));

      packages("com.shad649.rest.service,com.shad649.transaction");

  }

  @PreDestroy
  private void shutdown() {
      system.terminate();
  }
}
