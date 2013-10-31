/**
 * 
 */
package com.ashpro.alertcall;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Listens to the incoming phone call
 * @author ash
 */
public class IncomingCallListener extends BroadcastReceiver {

	private static final String LOG_TAG = "IncomingCallListener";
	NotificationManager notificationManager;
	Context cont;
	private RulesDataSource datasource;
	private ArrayList<RuleRow> RuleList;
	/**
	 * An array adapter that binds RuleList data structure with the ruleList list view
	 */
	private ArrayAdapter<RuleRow> RuleListAdapter;
	
	public IncomingCallListener()
	{

	}
	
	public IncomingCallListener(Context c, NotificationManager n) {
		this.cont = c;
		// this.cont = new RuleScreenActivity();
		this.notificationManager = n;
	}

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {                                         // 2
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);                         // 3

		//datasource = new RulesDataSource(cont);	
		
		// phone ringing
		if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {                                   // 4
			String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);  // 5
			Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			Intent notificationIntent = new Intent(context,RuleScreenActivity.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notificationIntent, 0);

			notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			String toastText="";
			
			//*
			try{
				datasource = new RulesDataSource(context);
				datasource.dbopen();
				Log.i(MySQLiteHelper.TAG, "Database opened!");
			}
			catch(Exception e)
			{
				Log.i(MySQLiteHelper.TAG, e.getMessage());
			}
			// */
			
			RuleList = datasource.getAllRules();
			LayoutInflater mInflater = LayoutInflater.from(context);
			View myView = mInflater.inflate(R.layout.activity_rule_screen, null);
			
			ListView myListView = (ListView)myView.findViewById(R.id.ruleList);
		    // Create the array adapter to bind the array to the listview	    
		    RuleListAdapter = new ArrayAdapter<RuleRow>(context, android.R.layout.simple_list_item_1, RuleList);
		    // Bind the array adapter to the listview.
		    myListView.setAdapter(RuleListAdapter);
		    
		    ArrayList<Integer> indexToDelete = new ArrayList<Integer>();
	   	    
			Log.i(LOG_TAG, "Size of RuleList " + String.valueOf(RuleList.size()));
			for (int i = 0; i < RuleList.size(); i++) {
				// Log.i(LOG_TAG, "Number in rule: " + String.valueOf(RuleList.get(i).rule.contactName.number));
				// Log.i("CreateRule", "DB created rule! "+RuleList.get(i).getRule().recurrence);
				if(PhoneNumberUtils.compare(String.valueOf(RuleList.get(i).rule.contactName.number),incomingNumber))
				{
					//show both the reminders...  make it user configurable!!!!
					toastText = RuleList.get(i).rule.reminderMessage;
					
					// uncomment the following line to configure toast reminder as well
					if(RuleList.get(i).rule.popupText)
					{
						// MainActivity is not present!!! 
						// It will be killed if the app is removed from recent apps list
						// MainActivity.makeToast(toastText);
						Toast reminderToast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
						reminderToast.show();
						Log.i(LOG_TAG, "Toast sent for " + incomingNumber);
					}

					if(RuleList.get(i).rule.pushNotification)
					{

						Notification reminder = new Notification.Builder(context)
						.setContentTitle(RuleList.get(i).rule.reminderMessage)
						.setTicker(RuleList.get(i).rule.reminderMessage)
						.setContentText("Alert Call Alert!")
						.setSmallIcon(android.R.drawable. ic_dialog_info )
						.setAutoCancel(true)
						.setContentIntent(contentIntent)
						.setWhen(System.currentTimeMillis())
						.build();

						notificationManager.notify(i,reminder);
						Log.i(LOG_TAG, "Notification reminder sent for " + incomingNumber);
					}
					// if rule has the recurrence flag as false, then delete it 
					if(RuleList.get(i).rule.recurrence == false)
					{
						indexToDelete.add(i);
					}
				}
			}
			for (int i = 0; i < indexToDelete.size(); i++) {
				// delete this rule in database
				RuleRow r = RuleList.remove((int)indexToDelete.get(i));
				datasource.deleteRule(r);
				Log.i(MySQLiteHelper.TAG,String.format("Deleted: ruleID: ? ,name: ? ,number: ?", 
						new Object[] {r.id, r.rule.contactName.name, r.rule.contactName.number}));
			}
			RuleListAdapter.notifyDataSetChanged();
			
			//*
			try{
				datasource.dbclose();
				Log.i(MySQLiteHelper.TAG, "Database closed");
			}
			catch(Exception e)
			{
				Log.i(MySQLiteHelper.TAG, e.getMessage());
			}
			// */
			
		}

		if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state)) {
			// active
			Log.i(LOG_TAG, "OFFHOOK");
			Log.i(LOG_TAG, "OFFHOOK" + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)); 
		}
		if (TelephonyManager.EXTRA_STATE_IDLE.equals(state)) {
			Log.i(LOG_TAG, "IDLE number");
			Log.i(LOG_TAG, "OFFHOOK" + intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
		}
	}

}
