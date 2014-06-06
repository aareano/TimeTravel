package com.example.timetravel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/*
 * This guy does takes messages from activities/what-not and tosses them over to the MySQLiteHelper class
 * The Data access object (DAO)
 */


// REMEBER TO PUT DB QUERIES INTO ASYNCTASK!!!


public class ActionDataSource_depreciated {
	/*
	private static final String TAG = "TimeTravel";
	
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_CATEGORY,
			MySQLiteHelper.COLUMN_START_TIME, MySQLiteHelper.COLUMN_END_TIME, MySQLiteHelper.COLUMN_TIME_CREATED};
	
	public ActionDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	  }
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public ArrayList<Action> getAllActions() {
		Log.i(TAG, "ActionDataSource.getAllActions()");						// THIS IS A LOT OF UNNCESSARY WORK
																			// I WILL PROBABLY NEVER NEED /ALL/ OF THEM
	    List<Action> allActions = new ArrayList<Action>();
	
		// Grabs all Acts in the database
		Cursor cursor;
		cursor = database.query(MySQLiteHelper.TABLE_ACTIONS, allColumns, null, null, null, null, null);
		
		// Points cursor focus to first entry within the cursor
		cursor.moveToFirst();
		
		int i = 0;
		while (!cursor.isAfterLast()) {
			// Move through list of cursors and add each one to the 'acts'
			Action action = cursorToAction(cursor);
			allActions.add(action);
			cursor.moveToNext();
			i++;
		}
		
		// make sure to close the cursor
		cursor.close();
		Log.i(TAG, i + " Actions exist in the database");
		
		return (ArrayList<Action>) allActions;
	}
	
	public List<Action> getFirst() {
		Log.i(TAG, "ActionDataSource.getFirst()");
		
	    List<Action> actionList = new ArrayList<Action>();
	
	    String selection = "_ID ='1'";
	    
		// Grabs all Acts in the database
		Cursor cursor;
		cursor = database.query(MySQLiteHelper.TABLE_ACTIONS, allColumns, selection, null, null, null, null);

		Action action = cursorToAction(cursor);
		actionList.add(action);
		Log.i(TAG, "new Act: " + actionList.toString());
		// make sure to close the cursor
		cursor.close();
		return actionList;
	}

	public Cursor proxyQuery(String table, String[] columns, String whereClause, String[] selectionArgs, 
			String groupBy, String having, String orderBy) {
		
		Cursor cursor;
		cursor = database.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	
	private Action cursorToAction(Cursor cursor) {
	
		// Transcribe information from database to Act object
		int setId = cursor.getInt(0);
		String setName = cursor.getString(1);
	    String setCategory = cursor.getString(2);
	    
	    // Get milliseconds
	    long startMillis = Long.parseLong(cursor.getString(3));
	    long endMillis = Long.parseLong(cursor.getString(4));
	    long createdMillis = Long.parseLong(cursor.getString(5));
	    
	    // Initiate
	    Calendar setStartTime = Calendar.getInstance();
	    Calendar setEndTime = Calendar.getInstance();
	    Calendar setTimeCreated = Calendar.getInstance();
	    
	    // Set
	    setStartTime.setTimeInMillis(startMillis);
	    setEndTime.setTimeInMillis(endMillis);
	    setTimeCreated.setTimeInMillis(createdMillis);
	    
	    // Creates a new action and adds it to ActionRegistry
	    Action action = new Action (setId, setName, setCategory, setStartTime, setEndTime, setTimeCreated);
	    Action.addAction(action);
	    
	    return action;
	}

	public int deleteAction(int id) {
		// Deletes action and removes it from ActionRegistry
		int rowsDeleted = database.delete(MySQLiteHelper.TABLE_ACTIONS, MySQLiteHelper.COLUMN_ID + " = ?", 
				new String[]{Integer.toString(id)});
		if (rowsDeleted > 0)
			Action.removeAction(id);
		
		return rowsDeleted;
	}
	
	public void createAction(String name, String category, String startTime, String endTime, String timeCreated) {	
		Log.i(TAG, "ActionDataSource.createAction()");
		new CreateAction().execute(name, category, startTime, endTime, timeCreated);
	}
	
	public class CreateAction extends AsyncTask <String, Void, Boolean> {
		
		protected void onPreExecute() {
//				Toast.makeText(NewAction.this, "Saving activity...", Toast.LENGTH_SHORT).show();
		}
		
		protected Boolean doInBackground(String... params) {
			ContentValues values = new ContentValues();
			
			// Add row with name and category
			values.put(MySQLiteHelper.COLUMN_NAME, params[0]);
			values.put(MySQLiteHelper.COLUMN_CATEGORY, params[1]);
			values.put(MySQLiteHelper.COLUMN_START_TIME, params[2]);
			values.put(MySQLiteHelper.COLUMN_END_TIME, params[3]);
			values.put(MySQLiteHelper.COLUMN_TIME_CREATED, params[4]);

			long insertId = database.insert(MySQLiteHelper.TABLE_ACTIONS, null, values);
			if (insertId == -1) {
				Log.d(TAG, "Something went wrong");
				return false;
			} else
				Log.d(TAG, "Successfully saved!");
				return true;
		}
		
		protected void onProgressUpdate(Integer... progress) {
			//setProgressPercent(progress[0]);
	    }
		
	    protected void onPostExecute(Long result) {
	    	//showDialog("Downloaded " + result + " bytes");
	    	//Toast.makeText(this, "Action saved", Toast.LENGTH_SHORT).show();
	    }
	}
	*/
}