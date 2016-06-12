package edu.gvsu.masl.echoprint;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by ravitheja on 9/6/16.
 */

public class Fingerprinter implements Runnable
{
    private volatile boolean isRunning = false;
    private int bufferSize,secondsToRecord;
    private short[] audioData;
    private AudioRecord mRecordInstance;
    private String code;
    private FingerprintListener listener;

    private final int FREQUENCY = 11025;
    private final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public Fingerprinter(FingerprintListener listener)
    {
        this.listener = listener;
    }

    public void fingerprint(int seconds)
    {
        this.secondsToRecord = seconds;
        Thread t = new Thread(this);
        t.start();
    }

    public void run()
    {
        this.isRunning = true;
        try
        {
            int minBufferSize = AudioRecord.getMinBufferSize(FREQUENCY,CHANNEL,ENCODING);
            bufferSize = Math.max(minBufferSize,FREQUENCY*this.secondsToRecord);
            audioData = new short[bufferSize];
            mRecordInstance = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    FREQUENCY, CHANNEL,
                    ENCODING, minBufferSize);
            mRecordInstance.startRecording();
            try
            {
                long time = System.currentTimeMillis();
                int samplesIn = 0;
                do
                {
                    samplesIn += mRecordInstance.read(audioData, samplesIn, bufferSize - samplesIn);

                    if(mRecordInstance.getRecordingState() == AudioRecord.RECORDSTATE_STOPPED)
                        break;
                }
                while (samplesIn < bufferSize);
                time = System.currentTimeMillis();
                didFinishRecording();
                Codegen codegen = new Codegen();
                code = codegen.generate(audioData, samplesIn);
                Log.d("Fingerprinter v2","Code generated : "+code);
            }
            catch(Exception e)
            {
                Log.d("Fingerprinter v2","Exception Occured : "+e);
            }
        }
        catch(Exception e)
        {
            Log.d("Fingerprinter v2","Exception Occured : "+e);
        }
        if(mRecordInstance != null)
        {
            mRecordInstance.stop();
            mRecordInstance.release();
            mRecordInstance = null;
        }
        this.isRunning = false;
        didFinishListening();
    }
    public void didFinishListening()
    {
        if(listener == null)
            return;

        if(listener instanceof Activity)
        {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    listener.didFinishFingerprinting(code);
                }
            });
        }
        else
            listener.didFinishFingerprinting(this.code);
    }
    public void didFinishRecording()
    {
        if(listener == null) {
            return;
        }
        if(listener instanceof Activity) {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.didFinishRecording();
                }
            });
        }
        else
            listener.didFinishRecording();
    }
    public void stop()
    {
        if(mRecordInstance != null)
            mRecordInstance.stop();
    }
}