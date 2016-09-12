package com.shad649.rest.actor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.shad649.actor.messages.ProcessedTransactions;
import com.shad649.actor.messages.Transform;

import java.util.Set;

import akka.actor.UntypedActor;
/**
 * Responsible to translate the messages in a format understandable from the UI library.
 * [{\"label\": \"key\",\"data\":[[1,6]]}]
 * 
 * Handles {@link Transform} message 
 * @author Emanuele Lombardi
 *
 */
public class FlotTransformerActor extends UntypedActor {
    
    
    @Override
    public void onReceive(Object m) throws Exception {
        if (m instanceof Transform) {
            Transform message = (Transform) m;
            Map<String, List<ProcessedTransactions>> map = message.getData();
            StringBuilder json = new StringBuilder();
            json.append("[");
            Iterator<Entry<String, List<ProcessedTransactions>>> it = map.entrySet()
                    .iterator();
            while (it.hasNext()) {
                Entry<String, List<ProcessedTransactions>> entry = it.next();
                json.append("{");
                String label = entry.getKey();
                json.append("\"label\": \"" + label + "\",");
                String data = getData(entry.getValue());
                json.append("\"data\":" + data);
                json.append("}");
                if (it.hasNext()) {
                    json.append(",");
                }
            }
            json.append("]");
            getSender().tell(json, getSelf());
        } else {
            unhandled(m);
        }
        
    }
    
    private String getData(List<ProcessedTransactions> value) {
        Set<Long> times = new HashSet<>();
        StringBuilder result = new StringBuilder();
        result.append("[");
        Iterator<ProcessedTransactions> it = value.iterator();
        String separator = "";
        while (it.hasNext()) {
            ProcessedTransactions data = it.next();
            if(!times.contains(data.getDate().getTime())) {
                times.add(data.getDate().getTime());
                result.append(separator);
                separator = ",";
                result.append("[" + data.getDate().getTime() + "," + data.getNumber() + "]");
            }
            
        }
        result.append("]");
        return result.toString();
    }
    
}
