package com.ashpro.alertcall;

import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class CreateRuleActivity extends MainActivity {

	private static final int CONTACT_PICKER_RESULT = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_rule);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_rule, menu);
		return true;
	}
	/*
	 * method to check if the textbox is empty or not
	 */
	private boolean isEmpty(EditText etText) {
	    if (etText.getText().toString().trim().equals("")) {
	        return true;
	    } else {
	        return false;
	    }
	}

	/*
	 * Get the text values of name and message and submit the results to its parent (RuleScreenActivity) 
	 */
	public void submitRule(View view)
	{
		// get the rule's name and message from the edit text boxes
		Intent returnIntent = new Intent();
		
		// check if text not empty
		EditText ruleNameText = ((EditText)findViewById(R.id.ruleContactNameValue));
		EditText ruleMessageText = ((EditText)findViewById(R.id.ruleMessageValue));
		EditText ruleNumberText = ((EditText)findViewById(R.id.ruleContactNumberValue));
		CheckBox checkRepeat = (CheckBox)findViewById(R.id.repeatCheckBox);
		CheckBox notificationboxnotification_checkbox = (CheckBox)findViewById(R.id.notification_checkbox);
		CheckBox text_checkbox = (CheckBox)findViewById(R.id.text_checkbox);
		String ruleMessage = "",ruleContactName = "", ruleNumber = "";
		boolean ruleRepeat = false;
		boolean rulePop = false, rulePush = true;
		String ruleNum = "";
		
		if(!isEmpty(ruleMessageText) && !isEmpty(ruleNumberText))
		{	
			ruleContactName = ruleNameText.getText().toString();
			ruleMessage = ruleMessageText.getText().toString();
			ruleNumber = ruleNumberText.getText().toString();
			if(ruleNumber.charAt(0) == '+')
            {
            	Log.i("CreateRule", "Trimming number" + ruleNumber);
            	// add try catch if phone number is just + 
            	ruleNum = ruleNumber.substring(1);
            	Log.i("CreateRule", "Trimmed number" + ruleNum);
            }
            else {
            	ruleNum = ruleNumber;
            }
            // makeToast(phoneNum);
            ruleRepeat = checkRepeat.isChecked();
            // Log.i("CreateRule", "Rule checkbox: " + (ruleRepeat?"true":"false"));
		}
		else
		{
			makeToast("Contact number or message cannot be empty");
			setResult(RESULT_CANCELED,returnIntent); 
			return;
		}
		
		rulePop = text_checkbox.isChecked();
		rulePush = notificationboxnotification_checkbox.isChecked();
		
		if(rulePop || rulePush)
		{	
			ruleRepeat = checkRepeat.isChecked();
            Log.i("CreateRule", "Rule rulePop: " + (rulePop?"true":"false" + "Rule rulePush: " + (rulePush?"true":"false")));
		}
		else
		{
			makeToast("Please select atleast one method of notification");
			setResult(RESULT_CANCELED,returnIntent); 
			return;
		}
		
		// prepare the intent for returning the result
		returnIntent.putExtra("ruleContactName",ruleContactName);
		returnIntent.putExtra("ruleMessage",ruleMessage);
		returnIntent.putExtra("ruleNumber",ruleNum);
		returnIntent.putExtra("ruleRepeat",ruleRepeat);
		returnIntent.putExtra("rulePush",rulePush);
		returnIntent.putExtra("rulePop",rulePop);
		setResult(RESULT_OK,returnIntent);     
		// RuleListDbOpenHelper rdb = new RuleListDbOpenHelper(contextMainActivity);
		// rdb.addRule(1, ruleContactName, ruleNum, ruleMessage, false);
		Log.i("CreateRule", "DB created rule!!");
		finish();		
	}
	
	/**
	 * Picks contact using contact picker and populates the name & number edit text
	 * @param view
	 */
	public void pickContact(View view)
	{
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intent, CONTACT_PICKER_RESULT);
	}
	
	@Override  
    public void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            switch (requestCode) {  
            case CONTACT_PICKER_RESULT:  
            	
            	String TAG = "CallPicker";
                // handle contact results  
            	Uri result = data.getData();
            	Log.v(TAG, "Got a result: " + result.toString());

            	// get the contact id from the Uri
            	String id = result.getLastPathSegment();

            	// query for phone numbers for the selected contact id
            	Cursor c = null;  
            	c = getContentResolver().query(
            	    Phone.CONTENT_URI, null,
            	    Phone._ID + "=?",
            	    new String[]{id}, null);

            	int phoneIdx = c.getColumnIndex(Phone.NUMBER);
            	int phoneNameIdx = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            	EditText tvName = (EditText)findViewById(R.id.ruleContactNameValue);
        		
        		final EditText tvNum = (EditText)findViewById(R.id.ruleContactNumberValue);
        		
            	if(c.getCount() == 1) {
            	    // contact has a single phone number, so there's no need to display a second dialog
            		if(c.moveToFirst()) {
                        String phoneNumber = PhoneNumberUtils.stripSeparators(c.getString(phoneIdx));
                        String phoneNum = "";
                        if(phoneNumber.charAt(0) == '+')
                        {
                        	Log.i("CreateRule", "Trimming number" + phoneNumber);
                        	// add try catch if phone number is just + 
                        	phoneNum = phoneNumber.substring(1);
                        }
                        else {
                        	phoneNum = phoneNumber;
                        }
                        String contact = c.getString(phoneNameIdx);
                        // makeToast(phoneNum);
                        //set text of view to name and number
                        tvNum.setText(phoneNum);
                        tvName.setText(contact);
            		}
            	}
                break;  
            }  
        } else {  
            // gracefully handle failure  
            Log.w("CallPicker", "Warning: activity result not ok");  
        }  
    }  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
