package com.anyway;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Information extends Activity {
	private Button mButton01, mButton02;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.information);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		

		// Search Button
		mButton01 = (Button) findViewById(R.id.button1);
		mButton01.setOnClickListener(new Button01Listener());

		// Back Button
		mButton02 = (Button) findViewById(R.id.button2);
		mButton02.setOnClickListener(new Button02Listener());
	}

	class Button01Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=The+Starry+Night+Vincent+Willem+van+Gogh"));
	        it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
	        Information.this.startActivity(it);
		}
	}

	class Button02Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Information.this.finish();
		}
	}
}