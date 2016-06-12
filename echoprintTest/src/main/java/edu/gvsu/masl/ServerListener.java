package edu.gvsu.masl;

/**
 * Created by zen on 11/6/16.
 */
public interface ServerListener {
    void didStart();
    void didReceiveResponse(String response);
    void didReceiveException(Exception e);
}
