package com.example.timetravel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * This guy does the actual SQL interacting with the database bit
 */
public class MySQLiteHelper_deprecated extends SQLiteOpenHelper {
	public static final String TAG = "TimeTravel";
	
	public static final String TABLE_ACTIONS = "Actions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "Name";
	public static final String COLUMN_CATEGORY = "Category";
	public static final String COLUMN_START_TIME = "StartTime";
	public static final String COLUMN_END_TIME = "EndTime";
	public static final String COLUMN_TIME_CREATED = "TimeCreated";
	
	private static final String DATABASE_NAME = "acts.db";
	private static final int DATABASE_VERSION = 11;

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " + TABLE_ACTIONS 
	+ "("
	+ COLUMN_ID + " integer primary key autoincrement, "
	+ COLUMN_NAME + " text not null, "
	+ COLUMN_CATEGORY + " text not null, "
	+ COLUMN_START_TIME + " text not null, "
	+ COLUMN_END_TIME + " text not null, "
	+ COLUMN_TIME_CREATED + " text not null);";
	  
	// types of StartTime, EndTime, and TimeCreated should be long

	public MySQLiteHelper_deprecated(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.i(TAG, "MySQLiteHelper constructor.");
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.i(TAG, "MySQLiteHelper onOpen()");
		super.onOpen(db);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.d(TAG, "Created new " + DATABASE_NAME + " database");
		database.execSQL(DATABASE_CREATE);
	}

	  
	// Called when Database version is different than current version.
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade()");
		// Use string literals instead of variables >> variables will change as database updates
//	    for (int i = oldVersion; i < newVersion; i++) {
//	    	switch(i){
//            	case 6:
            		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
//            		break;
            		database.execSQL("DROP TABLE IF EXISTS Actions");
            	    onCreate(database);
//          }
//      }
	}
}

