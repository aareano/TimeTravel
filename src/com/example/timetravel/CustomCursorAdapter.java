package com.example.timetravel;

//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class CustomCursorAdapter extends SimpleCursorAdapter{
	
	private static final String TAG = "TimeTravel";
	
    public CustomCursorAdapter (Context context, int layout, Cursor c, String[] from, int[] to, int flags) 	 {
    	super(context, layout, c, from, to, flags);
    }

	@Override
    public void bindView(View view, Context context, Cursor cursor) {
    	super.bindView(view, context, cursor);
    	Log.i(TAG, "new iteration of bindView()");
	    	// set name
	    	TextView name = (TextView) view.findViewById(R.id.action_name);
	        name.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
	        Log.d(TAG, "NAME:: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)));
	
	        // set category
	        TextView category = (TextView) view.findViewById(R.id.action_category);
	        category.setText(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)));
	        Log.d(TAG, "CATEGORY:: " + cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)));
    }
}