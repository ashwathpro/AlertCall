package com.ashpro.alertcall;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Main launch screen of the Alert Call application. Displays a list of all the alerts.
 * Gives you options to create or delete alerts  
 * @see SystemUiHider
 */
public class RuleScreenActivity extends MainActivity {
	
	/**
	 * The main data structure that contains the list of all the rules
	 */
	private ArrayList<RuleRow> RuleList;
	/**
	 * An array adapter that binds RuleList data structure with the ruleList list view
	 */
	private ArrayAdapter<RuleRow> RuleListAdapter;
	public NotificationManager notificationManager;
	public Context context;
	private RulesDataSource datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rule_screen);
		Log.i("RuleScreenActivity", "RuleScreenActivity onCreate() called..");

		datasource = new RulesDataSource(this);
	    datasource.dbopen();

		RuleList = datasource.getAllRules();
		// create a notification manager instance
		context = RuleScreenActivity.this.getApplicationContext();
		notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		// Code for the Listview and arrayAdapter
		ListView myListView = (ListView)findViewById(R.id.ruleList);
	    // Create the array adapter to bind the array to the listview	    
	    RuleListAdapter = new ArrayAdapter<RuleRow>(this, android.R.layout.simple_list_item_1, RuleList);
	    // Bind the array adapter to the listview.
	    myListView.setAdapter(RuleListAdapter);
   	    
	    //*
	    myListView.setOnItemClickListener(new OnItemClickListener() {
	    	  @Override
	    	  public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
	    	    // makeToast("Delete this item Number " + position);
	    	    final int pos = position;
	    	    AlertDialog.Builder builder = new AlertDialog.Builder(RuleScreenActivity.this);
	    	    builder.setTitle("Delete Rule?");
	    	    builder.setMessage(RuleList.get(position).rule.toString());
	    	    builder.setNegativeButton("Cancel", null);
	    	    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    // Delete the rule if user confirms it
	                	delRule(RuleList.get(pos));
	                }
	            });
	    	    AlertDialog dialog = builder.create();
	    	    dialog.show();

	    	    // Must call show() prior to fetching text view
	    	    //TextView messageView = (TextView)dialog.findViewById(android.R.id.message);
	    	    //messageView.setGravity(Gravity.CENTER);
	    	  }
	    	});
	    /*
		butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				todoItems.add(new Rule("you added this!","your message"));
				RuleListAdapter.notifyDataSetChanged();
			} 
		});
	    //*/

	    myListView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View view) {
				// TODO Auto-generated method stub
				makeToast("long click works! Delete this item");
				return false;
			}
		});
	    
	}

	public void createRule(View view) {
		Intent CreateRuleIntent = new Intent(this, CreateRuleActivity.class);
		startActivityForResult(CreateRuleIntent, 1);
	}

	/*
	 * Function to handle the results that the CreateRule activity returns. 
	 * It basically Extracts the name and message from the create rule activity
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("RuleScreenActivity", "RuleScreenActivity onActivityResult() called..");

		if (requestCode == 1) {
// TODO: 			
			if(resultCode == RESULT_OK){      
				String ruleContactName=data.getStringExtra("ruleContactName");          
				String ruleMessage=data.getStringExtra("ruleMessage");
				Long ruleNumber=Long.parseLong(data.getStringExtra("ruleNumber"));
				boolean ruleCheckRepeat = data.getBooleanExtra("ruleRepeat", false);
				
				int pushNotificationCheck = (data.getBooleanExtra("rulePush", true)?1:0);
				int textCheck = (data.getBooleanExtra("rulePop", false)?1:0);
				
				int rep = (ruleCheckRepeat?1:0) ;
				
				// Add the rule to list
				addRule(ruleContactName, ruleNumber.toString() , ruleMessage, rep, pushNotificationCheck, textCheck);
				/*
				// Create the rule and add it to arrayadapter here
				RuleRow rulerow = datasource.createRuleRow(ruleContactName, ruleNumber.toString(), ruleMessage, false);
				RuleList.add(rulerow);

				// refresh your view here			     
				RuleListAdapter.notifyDataSetChanged();
				*/
				// makeToast("Added a rule for " + ruleContactName + " " + ruleMessage + " " + ruleNumber + " " + rep);
				makeToast("Added a rule for " + ruleContactName );

			}
			if (resultCode == RESULT_CANCELED) {    
				// Write your code if there's no result
				Log.i("RuleScreenActivity","ERROR: Activity did not return the rule properly!");
			}
		}
	}

	public void addRule(String ruleContactName, String ruleNumber, String ruleMessage, int rep, int push, int popup)
	{
		Log.i("RuleScreenActivity", "RuleScreenActivity addRule() called..");
		// Create the rule and add it to arrayadapter here
		RuleRow rulerow = datasource.createRuleRow(ruleContactName, ruleNumber, ruleMessage, rep, push, popup);
		RuleList.add(rulerow);

		// refresh your view here			     
		RuleListAdapter.notifyDataSetChanged();
	}

	public void delRule(RuleRow rulerow)
	{
		Log.i("RuleScreenActivity", "RuleScreenActivity delRule() called..");
		// Create the rule and add it to arrayadapter here
		datasource.deleteRule(rulerow);
		RuleList.remove(rulerow);

		// refresh your view here			     
		RuleListAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		Log.i("RuleScreenActivity", "RuleScreenActivity onResume called..");
		datasource.dbopen();
		super.onResume();
		
	}

	@Override
	protected void onPause() {
		Log.i("RuleScreenActivity", "RuleScreenActivity onPause called..");
		datasource.dbclose();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		Log.i("RuleScreenActivity", "RuleScreenActivity onDestroy called..");
		// following line is not necessary as the onPause is always called before onDestroy 
		// datasource.dbclose();
		super.onDestroy();
	}

}

