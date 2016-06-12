package edu.gvsu.masl.echoprint;

/**
 * Created by ravitheja on 9/6/16.
 */
public interface FingerprintListener
{
    void didFinishFingerprinting(String code);
    void didFinishRecording();
}