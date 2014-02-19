package com.example.timetravel;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class ActionFragment extends ListFragment {
	
	final String TAG = "TimeTravel";
	SelectAction mCallback;
	
	public interface SelectAction {
		void onSelect(Action action);
		void showDialog(String actionName);
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (SelectAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement SelectAction interface");
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_action, container, false);
    }

	@Override
	public void onActivityCreated(Bundle saved) {
		super.onActivityCreated(saved);
    }
	
	/**
	 * Includes database call and the setting of the CustomCursorAdapter
	 * 
	 * For Sort option "tree" see SortFragment class comments
	 * @param orderBy
	 */
	public boolean setList(String[] sortParams) {
		Log.d(TAG, "ActionFragment.setList()");
		
		boolean success = false;
		
		String[] topSortArray = getResources().getStringArray(R.array.top_sort_array);
		String[] allSortArray = getResources().getStringArray(R.array.sort_all_array);
		String[] recentSortArray = getResources().getStringArray(R.array.recent_res_array);
	
		// Declare/initialize variables for the cursor call
		String whereClause = null;
		String orderBy = null;
		String[] columns = null;
		String[] selectionArgs = null;
		
		// Default values for tableName and columns (ID column must always be called)
		String tableName = MySQLiteHelper.TABLE_ACTIONS;
		columns = new String[] {
				MySQLiteHelper.COLUMN_ID, 
				MySQLiteHelper.COLUMN_NAME, 
				MySQLiteHelper.COLUMN_CATEGORY, 
				MySQLiteHelper.COLUMN_END_TIME
				};
		
		/* SORT MAP
		if (sortParams[0].equals(topSortArray[0]))
			show current sort chronologically
		if (sortParams[0].equals(topSortArray[1]))
			if (sortParams[1].equals(allSortArray[0]))
				show all sort chronologically
			if (sortParams[1].equals(allSortArray[1]))
				show all sort alpha-numerically
		if (sortParams[0].equals(topSortArray[2]))
			String category = sortParams[1];
		if (sortParams[0].equals(topSortArray[3]))
			if (sortParams[1].equals(recentSortArray[0]))
				recentRes = "hours";
				hours = sortParams[2];
			if (sortParams[1].equals(recentSortArray[1]))
				recentRes = "days";
				days = sortParams[2];
			if (sortParams[1].equals(recentSortArray[2]))
				recentRes = "weeks";
				weeks = sortParams[2];
			if (sortParams[1].equals(recentSortArray[3]))
				recentRes = "months";
				months = sortParams[2];
		*/
		
		// need to define: whereClause, orderBy, selectionArgs
		
		if (sortParams[0].equals(topSortArray[0])) {
			// show current sort chronologically
			
			Time t = new Time();
			t.setToNow();
			long nowMillis = t.toMillis(true);
			
			// "where endTimeMillis >= nowMillis" -> Action is ongoing
			whereClause = MySQLiteHelper.COLUMN_END_TIME + " >= ?";
	        selectionArgs = new String[] { String.valueOf(nowMillis) };
			
	        // sort chronologically
	        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
	        
		} else if (sortParams[0].equals(topSortArray[1])) {
			if (sortParams[1].equals(allSortArray[0])) {
				// show all sort chronologically
				
				// return all data entries, no restrictions
				whereClause = null;
		        selectionArgs = null;
				
		        // sort chronologically
		        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
		        
			} else if (sortParams[1].equals(allSortArray[1])) {
				//show all sort alpha-numerically
				
				// return all data entries, no restrictions
				whereClause = null;
		        selectionArgs = null;
				
		        // sort by name a-z, 0-9
		        orderBy = MySQLiteHelper.COLUMN_NAME + " ASC";
			}
		} else if (sortParams[0].equals(topSortArray[2])) {
			// show 1 category sort chronologically
			
			// return entries with correct category
			whereClause = MySQLiteHelper.COLUMN_CATEGORY + " = ?";
	        
			String category = sortParams[1];
			selectionArgs = new String[] { category };
			
	        // sort chronologically
	        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
			
		} else if (sortParams[0].equals(topSortArray[3])) {
			
			long millisPerDay = 86400000;
			
			if (sortParams[1].equals(recentSortArray[0])) {
				// show any in the last x DAYS of time sort chronologically
				 
				double days = Double.parseDouble(sortParams[2]);

				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				
				long minMillis = (long) (nowMillis - (days * millisPerDay));
				whereClause = MySQLiteHelper.COLUMN_END_TIME + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
				
			} else if (sortParams[1].equals(recentSortArray[1])) {
				// show any in the last x WEEKS of time sort chronologically
				
				double weeks = Double.parseDouble(sortParams[2]);
				
				Log.d(TAG, "weeks = " + weeks);
				
				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				long millisPerWeek = millisPerDay * 7;
				
				long minMillis = (long) (nowMillis - (weeks * millisPerWeek));
				whereClause = MySQLiteHelper.COLUMN_END_TIME + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
		        
			} else if (sortParams[1].equals(recentSortArray[2])) {
				// show any in the last x MONTHS of time sort chronologically
				
				double months = Double.parseDouble(sortParams[2]);
				
				Log.d(TAG, "months = " + months);
				
				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				long millisPerMonth = millisPerDay * 31;		// not very robust
				
				long minMillis = (long) (nowMillis - (months * millisPerMonth));
				whereClause = MySQLiteHelper.COLUMN_END_TIME + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = MySQLiteHelper.COLUMN_END_TIME + " DESC";
			}
		}

        // Return a cursor
        Cursor cursor = (DisplayActions.datasource).proxyQuery(tableName, columns, whereClause, selectionArgs, null, null, orderBy);
    	
        Log.i(TAG, "ActionFragment cursor count: " + cursor.getCount());
    	if (cursor.getCount() > 0)
    		success = true;
    	
        String [] bindFrom = new String[] {
        		MySQLiteHelper.COLUMN_NAME, 
        		MySQLiteHelper.COLUMN_CATEGORY };
        
        int[] bindTo = new int[] {
        		R.id.action_name, 
        		R.id.action_category };
        
        CustomCursorAdapter custAdapter = new CustomCursorAdapter ((Context) getActivity(), R.layout.action_list_item,
        		cursor, bindFrom, bindTo, SimpleCursorAdapter.NO_SELECTION);
        setListAdapter(custAdapter);
        custAdapter.notifyDataSetChanged();
        // Log.i(TAG, "Adapter set >> closing ActionFragment's cursor"); 		this makes it angry....
        // cursor.close();
        
        // longClickListener
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
 			@Override
 			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
 				Log.i(TAG, "onItemLongClick()");
 				// TODO
 				return true;
 			}
 		});
	
        return success;
	}
    
	@Override
    public void onListItemClick(ListView lv, View view, int position, long id) {
    	// use this to highlight item: selected.isHovered()
    	// use for custom animation: selected.animate()
		Log.i(TAG, "onListItemClicked");
		String actionName = "" + ((TextView) view.findViewById(R.id.action_name)).getText();
		Log.d(TAG, "actionName = " + actionName);
		mCallback.showDialog(actionName);
    }

}
