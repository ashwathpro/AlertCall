package com.ashpro.alertcall;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RulesDataSource {
	// Database fields
		  private SQLiteDatabase ruleTable;
		  private MySQLiteHelper dbHelper;
		  private String[] allColumns = { MySQLiteHelper.RULE_ID,
				  MySQLiteHelper.CONTACT_NAME,
				  MySQLiteHelper.RULE_MESSAGE,
				  MySQLiteHelper.CONTACT_NUMBER,
				  MySQLiteHelper.RULE_REPEATS };

		  private static final String TAG = "AllMyDatabase";

		  public RulesDataSource(Context context) {
		    dbHelper = new MySQLiteHelper(context);
		  }

		  public void dbopen() throws SQLException {
		    ruleTable = dbHelper.getWritableDatabase();
		  }

		  public void dbclose() {
		    dbHelper.close();
		  }

		  public RuleRow createRuleRow(String contactName, String contactNum, String ruleMessage, int rep)
		  {
			  ContentValues initialValues = new ContentValues();
			  initialValues.put(MySQLiteHelper.CONTACT_NAME, contactName);
			  initialValues.put(MySQLiteHelper.CONTACT_NUMBER, contactNum);
			  initialValues.put(MySQLiteHelper.RULE_MESSAGE, ruleMessage);
			  initialValues.put(MySQLiteHelper.RULE_REPEATS, Integer.valueOf(rep));
			  ruleTable = dbHelper.getWritableDatabase();			  
			  long insertId = ruleTable.insert(MySQLiteHelper.RULE_TABLE_NAME, null,
					  initialValues );
			  Cursor cursor = ruleTable.query(MySQLiteHelper.RULE_TABLE_NAME,
		        allColumns, MySQLiteHelper.RULE_ID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    RuleRow newRule = cursorToRow(cursor);
		    cursor.close();
		    return newRule;
		  }

		  public void deleteRule(RuleRow ruleRow) {
		    long id = ruleRow.getId();
		    Log.i(MySQLiteHelper.TAG, "RuleRow deleted with id: " + id);
		    ruleTable.delete(MySQLiteHelper.RULE_TABLE_NAME, MySQLiteHelper.RULE_ID
		        + " = " + id, null);
		  }

		  public ArrayList<RuleRow> getAllRules() {
			  ArrayList<RuleRow> rules = new ArrayList<RuleRow>();

		    Cursor cursor = ruleTable.query(MySQLiteHelper.RULE_TABLE_NAME,
		        allColumns, null, null, null, null, null);

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      RuleRow ruleRow = cursorToRow(cursor);
		      rules.add(ruleRow);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return rules;
		  }

		  private RuleRow cursorToRow(Cursor cursor) {
		    RuleRow ruleRow = new RuleRow();
		    try
		    {
		    	ruleRow.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_id")));
		    	ruleRow.setRule(new Rule(cursor.getString(cursor.getColumnIndexOrThrow("CONTACT_NAME")),
		    			cursor.getLong(cursor.getColumnIndexOrThrow("CONTACT_NUMBER")),
		    			cursor.getString(cursor.getColumnIndexOrThrow("RULE_MESSAGE")),
		    			cursor.getInt(cursor.getColumnIndexOrThrow("RULE_REPEATS"))
		    			));
		    }
		    catch(Exception e)
		    {
		    	Log.i(TAG, e.getMessage());
		    }
		    return ruleRow;
		  }
}
