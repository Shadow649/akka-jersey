package com.shad649.rest.service;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ManagedAsync;

import com.shad649.actor.messages.GetCollectedData;
import com.shad649.actor.messages.ProcessTransaction;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.dispatch.OnComplete;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.util.Timeout;

/**
 * 
 * @author Emanuele Lombardi
 *
 */
@Path("/transactions")
public class FinancialServices {
    
    @Context
    protected ActorSystem actorSystem;
    
    @Context ActorRef actor;
    LoggingAdapter log;
    
    @POST
    @Path("process")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void postTransaction(String value, @Suspended final AsyncResponse res) {
        ActorSelection receiverActor = actorSystem
                .actorSelection("/user/transactionReceiver");
        Timeout timeout = new Timeout(Duration.create(2, "seconds"));
        
        ProcessTransaction message = new ProcessTransaction(value);
        Future<Object> future = Patterns.ask(receiverActor, message, timeout);
        
        future.onComplete(new OnComplete<Object>() {
            
            @Override
            public void onComplete(Throwable ex, Object result)
                    throws Throwable {
                if (ex == null) {
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("results", result);
                    res.resume(Response.status(201).entity(response).build());
                } else {
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("results", ex.getMessage());
                    res.resume(Response.status(500).entity(response).build());
                }
                
            }
            
        }, actorSystem.dispatcher());
    }
    
    @GET
    @Path("trends")
    @Produces(MediaType.APPLICATION_JSON)
    @ManagedAsync
    public void getTrends(@Suspended final AsyncResponse res) {
        Timeout timeout = new Timeout(Duration.create(2, "seconds"));
        
        GetCollectedData message = new GetCollectedData();
        Future<Object> future = Patterns.ask(actor, message, timeout);
        
        future.onComplete(new OnComplete<Object>() {
            
            @Override
            public void onComplete(Throwable ex, Object result)
                    throws Throwable {
                if (ex == null) {
                    res.resume(Response.status(200).entity(result).build());
                } else {
                    HashMap<String, Object> response = new HashMap<String, Object>();
                    response.put("results", ex.getMessage());
                    res.resume(Response.status(500).entity(response).build());
                }
                
            }
        }, actorSystem.dispatcher());
    }
}
