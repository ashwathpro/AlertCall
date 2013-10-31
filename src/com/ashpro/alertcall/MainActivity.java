package com.ashpro.alertcall;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class MainActivity extends Activity {
	
	static Context contextMainActivity;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		contextMainActivity = MainActivity.this.getApplicationContext();
		
		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide(100);
	}
	/**
	 * Create a toast notification to the user! simple feedback mechanism 
	 * @param toastText the string which is displayed to the user
	 */
	public static void makeToast(String toastText)
	{
		int toastDuration = Toast.LENGTH_SHORT;
		Toast reminderToast = Toast.makeText(contextMainActivity, 
									toastText, toastDuration);
		reminderToast.show();
		Log.i("ToastNotification", "Toast for " + toastText);	
	}
	
	public void startNextActivity(Context currentActivityContext, String nextActivity)
	{
		Class<?> nextActivityClass = null;
		if(nextActivity != null) {
		    try {
		    	nextActivityClass = Class.forName(nextActivity );
		    } catch (ClassNotFoundException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}
		Intent intent = new Intent(currentActivityContext, nextActivityClass);
		startActivity(intent);
	}

}
