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
	        name.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
	        Log.d(TAG, "NAME:: " + cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_NAME)));
	
	        // set category
	        TextView category = (TextView) view.findViewById(R.id.action_category);
	        category.setText(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY)));
	        Log.d(TAG, "CATEGORY:: " + cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY)));
	        
	        /*
	        // set start time
	        String startMillisS = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_START_TIME));
	        Long startMillis = Long.parseLong(startMillisS);
	        Date startTime = new Date(startMillis);
	        
	        SimpleDateFormat fmt = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
	        String startTimeToShow = fmt.format(startTime);

	        TextView dateView = (TextView) view.findViewById(R.id.text_startTime);
	        dateView.setText(startTimeToShow);
	        
	        // set end time
	        String endMillisS = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_END_TIME));
	        Long endMillis = Long.parseLong(endMillisS);
	        Date endTime = new Date(endMillis);
	        
	        SimpleDateFormat frmt = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);
	        String endTimeToShow = frmt.format(endTime);

	        TextView dateView1 = (TextView) view.findViewById(R.id.text_endTime);
	        dateView1.setText(endTimeToShow);
	        
	        // collapse list item
	        view.findViewById(R.id.expRow_date_time).setVisibility(View.GONE);
	        view.findViewById(R.id.expRow_edit_delete).setVisibility(View.GONE);
	        Log.i(TAG, "GONE!");
	        */
    }
}