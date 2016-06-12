package edu.gvsu.masl;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by zen on 11/6/16.
 */
public class MusicDetector extends Thread {

    private String fingerprint, serverResponse;
    private Context context;
    private ServerListener listener;

    public MusicDetector(String fingerprint, Context context, ServerListener listener) {
        this.fingerprint = fingerprint;
        this.context = context;
        this.listener = listener;
        this.start();
    }
    public void run() {
        try {
            // notifying that the thread has started execution
            didStart();

            URL url = new URL("http://192.168.1.36/music_fingerprinter/identify.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("code",this.fingerprint);
            String query = builder.build().getEncodedQuery();
            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            Log.d("Fingerprinter v2",query);
            writer.flush();
            writer.close();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while((line = reader.readLine()) != null) {
                if(this.serverResponse == null) {
                    this.serverResponse = line;
                }
                else {
                    this.serverResponse = this.serverResponse + line;
                }
            }
            didReceiveResponse();
        }
        catch(MalformedURLException mue) {
            Log.d("Fingerprinter v2","Malformed Exception : "+mue);
            didReceiveException(mue);

        }
        catch(IOException ioe) {
            Log.d("Fingerprinter v2","IO Exception : "+ioe );
            didReceiveException(ioe);
        }

    }

    public void didStart()
    {
        if(listener == null ) {
            return ;
        }
        if(listener instanceof Activity) {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.didStart();
                }
            });
        }
        else {
            listener.didStart();
        }
    }

    public void didReceiveResponse() {
        if(listener == null) {
            return;
        }
        if(listener instanceof Activity) {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.didReceiveResponse(MusicDetector.this.serverResponse);
                }
            });
        }
        else
            listener.didReceiveResponse(this.serverResponse);
    }

    public void didReceiveException(Exception e) {

        final Exception ex = e;

        if(listener == null) {
            return;
        }
        if(listener instanceof Activity) {
            Activity activity = (Activity) listener;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    listener.didReceiveException(ex);
                }
            });
        }
        else {
            listener.didReceiveException(ex);
        }
    }

    public void displayToast(String msg) {
        Toast.makeText(this.context,msg,Toast.LENGTH_LONG).show();
    }
}
