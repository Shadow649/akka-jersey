package com.shad649.rest.actor;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.shad649.rest.TransactionEndpoint;
import com.shad649.transaction.Transaction;

public class FinanciaServicesTest extends JerseyTest {
    
    protected Application configure() {
        return new TransactionEndpoint();
    }
    
    protected void configureClient(ClientConfig clientConfig) {
        clientConfig.register(new JacksonJsonProvider());
    }
    
    @Test
    public void testPostTransaction() {
        Entity<Transaction> entity = Entity.<Transaction> entity(
                new Transaction(), MediaType.APPLICATION_JSON_TYPE);
        Response resp = target("transactions").path("process").request()
                .post(entity);
        
        assertEquals(201, resp.getStatus());
    }
}
