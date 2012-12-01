package com.anyway;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Welcome extends Activity {
	 private final int Welcome_DISPLAY_LENGHT = 2000;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        new Handler().postDelayed(new Runnable(){  
        	  
            @Override  
            public void run() {  
                Intent mainIntent = new Intent(Welcome.this,NewMain.class);  
                Welcome.this.startActivity(mainIntent);  
                Welcome.this.finish();  
            }  
               
           }, Welcome_DISPLAY_LENGHT );
    }
}