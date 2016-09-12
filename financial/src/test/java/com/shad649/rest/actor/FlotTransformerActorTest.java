package com.shad649.rest.actor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.shad649.actor.messages.ProcessedTransactions;
import com.shad649.actor.messages.Transform;
import com.shad649.rest.actor.FlotTransformerActor;

import akka.testkit.TestActorRef;
import akka.testkit.TestProbe;

import static org.junit.Assert.*;

public class FlotTransformerActorTest extends AbstractActorSystemTest {
    
    @Test
    public void testTransofrmMessage() {
        final TestActorRef<FlotTransformerActor> ref = actorOf(FlotTransformerActor.class);
        TestProbe probe = testProbe();
        Map<String, List<ProcessedTransactions>> data = createData();
        ref.tell(new Transform(data), probe.ref());
        probe.expectMsgClass(StringBuilder.class);
        StringBuilder builder = (StringBuilder) probe.lastMessage().msg();
        assertNotNull(builder);
        assertEquals("[{\"label\": \"key\",\"data\":[[1,6]]}]",
                builder.toString());
    }
    
    private Map<String, List<ProcessedTransactions>> createData() {
        Map<String, List<ProcessedTransactions>> data = new HashMap<String, List<ProcessedTransactions>>();
        List<ProcessedTransactions> l = new ArrayList<ProcessedTransactions>(1);
        l.add(new ProcessedTransactions(new Date(1), 6));
        data.put("key", l);
        return data;
    }
    
}
