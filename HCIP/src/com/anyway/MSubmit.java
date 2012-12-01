package com.anyway;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MSubmit extends Activity {

	private Button mButton01;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.msubmit);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mButton01 = (Button) findViewById(R.id.button1);
		mButton01.setOnClickListener(new Button01Listener());
		
		
	}

	class Button01Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			MSubmit.this.finish();
		}
	}
	
}