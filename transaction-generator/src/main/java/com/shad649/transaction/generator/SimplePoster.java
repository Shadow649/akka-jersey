package com.shad649.transaction.generator;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimplePoster {
    private HttpURLConnection conn;
    private OutputStream os;
    public void doPost(String message) {
        try {
            URL url = new URL(
                    "http://127.0.0.1:8080/labcamp/transactions/process");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            os = conn.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new PostException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
        } catch (Throwable e) {
            throw new PostException(e);
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                //TODO
            }
            conn.disconnect();
        }
    }
}
