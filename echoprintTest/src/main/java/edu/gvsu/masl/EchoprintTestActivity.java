/**
 * EchoprintTestActivity.java
 * EchoprintTest
 *
 * Created by Alex Restrepo on 1/22/12.
 * Copyright (C) 2012 Grand Valley State University (http://masl.cis.gvsu.edu/)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.gvsu.masl;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import edu.gvsu.masl.echoprint.FingerprintListener;
import edu.gvsu.masl.echoprint.Fingerprinter;

public class EchoprintTestActivity extends Activity implements FingerprintListener, ServerListener
{
	private ProgressBar progressBar;
	private boolean recording;
	private Fingerprinter fingerprinter;
	private TextView status;
	private ImageButton btn;
	private String fingerprint;
	private Typeface defaultTypeFace;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		btn = (ImageButton) findViewById(R.id.imageButton);
		progressBar = (ProgressBar) findViewById(R.id.progressBar2);
		progressBar.setVisibility(View.INVISIBLE);
		status = (TextView) findViewById(R.id.statusTextView);
		setFonts();
		btn.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				// Perform action on click
				if(recording)
				{
					fingerprinter.stop();
				}
				else
				{
					if(fingerprinter == null)
						fingerprinter = new Fingerprinter(EchoprintTestActivity.this);

					fingerprinter.fingerprint(15);
					status.setText("Recording Audio...");

					btn.setImageResource(R.drawable.ic_stop_black_36dp);
				}
			}
		});
	}

	public void setFonts() {
		AssetManager manager = this.getApplicationContext().getAssets();
		Typeface typeface = Typeface.createFromAsset(manager,String.format(Locale.US,"fonts/monaco.ttf"));
		status.setTypeface(typeface);
		status.setTextSize(20);
		this.defaultTypeFace = typeface;
	}

	@Override
	public void didFinishFingerprinting(String code) {
		progressBar.setVisibility(View.INVISIBLE);
		btn.setImageResource(R.drawable.ic_mic_none_black_36dp);
		recording = false;
		status.setText("Press the button to start a new recording :)");
		displayToast("Fingerprint generated successfully");
		this.fingerprint = code;
		sendFingerPrintToServer();
	}

    @Override
	public void didFinishRecording() {
		progressBar.setVisibility(View.VISIBLE);
		status.setText("Generating fingerprint, please wait...");
	}



	public void sendFingerPrintToServer() {
		// starts off a new thread ...
		MusicDetector detector = new MusicDetector(this.fingerprint,this.getApplicationContext(),this);
	}

	public void displayToast(String msg) {
		Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
	}

	@Override
	public void didStart() {
		displayResponse("Waiting for server response.");
	}

	@Override
	public void didReceiveResponse(String response) {
		this.displayResponse(response);
	}

	@Override
	public void didReceiveException(Exception e) {
		this.displayResponse(e.toString());
	}

	public void displayResponse(String text) {
		TextView responseTextView = (TextView) findViewById(R.id.responseTextView);
		responseTextView.setTypeface(defaultTypeFace);
		responseTextView.setText(text);
	}
}