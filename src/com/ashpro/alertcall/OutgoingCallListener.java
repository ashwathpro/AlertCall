package com.ashpro.alertcall;
/**
 * 
 */


import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Listens to the outgoing phone call
 * @author ash
 */
public class OutgoingCallListener extends BroadcastReceiver {

	private static final String LOG_TAG = "OutgoingCallListener";
	NotificationManager notificationManager;
	Context cont;
	private RulesDataSource datasource;
	private ArrayList<RuleRow> RuleList;
	/**
	 * An array adapter that binds RuleList data structure with the ruleList list view
	 */
	private ArrayAdapter<RuleRow> RuleListAdapter;
	
	public OutgoingCallListener()
	{

	}

	public OutgoingCallListener(Context c, NotificationManager n) {
		this.cont = c;
		// this.cont = new RuleScreenActivity();
		this.notificationManager = n;
	}

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {                                         // 2
		// Extract the outgoing number 
		final String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          // 3
		Log.i(LOG_TAG, "OUTGOING, number: " + outgoingNumber);


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
			if(PhoneNumberUtils.compare(String.valueOf(RuleList.get(i).rule.contactName.number),outgoingNumber))
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
					Log.i(LOG_TAG, "Toast sent for " + outgoingNumber);
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
					Log.i(LOG_TAG, "Notification reminder sent for " + outgoingNumber);
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
}
