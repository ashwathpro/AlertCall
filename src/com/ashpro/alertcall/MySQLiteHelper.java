package com.ashpro.alertcall;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "AlertRules.db";
	private static final int DATABASE_VERSION = 2;
	public static final String TAG = "AllMyDatabase";

	public static final String RULE_TABLE_NAME = "RuleListTable";
	public static final String RULE_ID = "_id";
	public static final String CONTACT_NAME = "CONTACT_NAME";
	public static final String CONTACT_NUMBER = "CONTACT_NUMBER";
	public static final String RULE_MESSAGE= "RULE_MESSAGE";
	public static final String RULE_REPEATS = "RULE_REPEATS";

	// Database creation sql statement
	private static final String RULE_TABLE_CREATE ="create table " + RULE_TABLE_NAME + "(" + 
            RULE_ID + " integer primary key autoincrement, " +
            CONTACT_NAME + " text, " +
            CONTACT_NUMBER + " integer not null, " +
            RULE_REPEATS + " integer, " + 
            RULE_MESSAGE + " text" + 
            		");";


	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(RULE_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG,
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + RULE_TABLE_NAME);
		onCreate(db);
	}

}
