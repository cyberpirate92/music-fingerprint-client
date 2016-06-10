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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import edu.gvsu.masl.echoprint.FingerprintListener;
import edu.gvsu.masl.echoprint.Fingerprinter;

public class EchoprintTestActivity extends Activity implements FingerprintListener
{
	ProgressBar progressBar;
	boolean recording;
	Fingerprinter fingerprinter;
	TextView textView;
	TextView status;
	Button btn;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		textView = (TextView) findViewById(R.id.textView);
		btn = (Button) findViewById(R.id.recordButton);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setVisibility(View.INVISIBLE);

		status = (TextView) findViewById(R.id.status);
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
					btn.setText("Stop");
				}
			}
		});
	}

	@Override
	public void didFinishListening(String code) {
		progressBar.setVisibility(View.INVISIBLE);
		btn.setText("Start");
		recording = false;
        status.setText("Press the button to start a new recording :)");
		textView.setText(code);
	}

	@Override
	public void didFinishRecording() {
		progressBar.setVisibility(View.VISIBLE);
		status.setText("Generating fingerprint, please wait...");
	}
}