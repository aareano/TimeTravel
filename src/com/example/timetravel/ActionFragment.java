package com.example.timetravel;

// this class is aaaallllll messed up because of the database restructuring TODO

import java.util.ArrayList;
import java.util.List;

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
	List<Action> mActions = new ArrayList<Action>();
	
	public interface SelectAction {
		void onSelect(Action action);
		void showDialog(Action action);
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
		String tableName = DatabaseHelper.TABLE_ACTIONS + ", " + DatabaseHelper.TABLE_CATEGORIES + ", " 
					+ DatabaseHelper.TABLE_ACTION_CATEGORIES;
		
		columns = new String[] {
				DatabaseHelper.TABLE_ACTIONS + "." + DatabaseHelper.COLUMN_ID,
				DatabaseHelper.COLUMN_NAME, 
				DatabaseHelper.COLUMN_CATEGORY,
				DatabaseHelper.COLUMN_END
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
			whereClause = DatabaseHelper.COLUMN_END + " >= ?";
	        selectionArgs = new String[] { String.valueOf(nowMillis) };
			
	        // sort chronologically
	        orderBy = DatabaseHelper.COLUMN_END + " DESC";
	        
		} else if (sortParams[0].equals(topSortArray[1])) {
			if (sortParams[1].equals(allSortArray[0])) {
				// show all sort chronologically
				
				// return all data entries, no restrictions
				whereClause = null;
		        selectionArgs = null;
				
		        // sort chronologically
		        orderBy = DatabaseHelper.COLUMN_END + " DESC";
		        
			} else if (sortParams[1].equals(allSortArray[1])) {
				//show all sort alpha-numerically
				
				// return all data entries, no restrictions
				whereClause = null;
		        selectionArgs = null;
				
		        // sort by name a-z, 0-9
		        orderBy = DatabaseHelper.COLUMN_NAME + " ASC";
			}
		} else if (sortParams[0].equals(topSortArray[2])) {
			// show 1 category sort chronologically
			
			// return entries with correct category
			whereClause = DatabaseHelper.COLUMN_CATEGORY + " = ?";
	        
			String category = sortParams[1];
			selectionArgs = new String[] { category };
			
	        // sort chronologically
	        orderBy = DatabaseHelper.COLUMN_END + " DESC";
			
		} else if (sortParams[0].equals(topSortArray[3])) {
			
			final long millisPerDay = 86400000;
			
			if (sortParams[1].equals(recentSortArray[0])) {
				// show any in the last x DAYS of time sort chronologically
				 
				double days = Double.parseDouble(sortParams[2]);

				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				
				long minMillis = (long) (nowMillis - (days * millisPerDay));
				whereClause = DatabaseHelper.COLUMN_END + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = DatabaseHelper.COLUMN_END + " DESC";
				
			} else if (sortParams[1].equals(recentSortArray[1])) {
				// show any in the last x WEEKS of time sort chronologically
				
				double weeks = Double.parseDouble(sortParams[2]);
				
				Log.d(TAG, "weeks = " + weeks);
				
				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				long millisPerWeek = millisPerDay * 7;
				
				long minMillis = (long) (nowMillis - (weeks * millisPerWeek));
				whereClause = DatabaseHelper.COLUMN_END + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = DatabaseHelper.COLUMN_END + " DESC";
		        
			} else if (sortParams[1].equals(recentSortArray[2])) {
				// show any in the last x MONTHS of time sort chronologically
				
				double months = Double.parseDouble(sortParams[2]);
				
				Log.d(TAG, "months = " + months);
				
				Time t = new Time();
				t.setToNow();
				long nowMillis = t.toMillis(true);
				long millisPerMonth = millisPerDay * 31;		// not very robust
				
				long minMillis = (long) (nowMillis - (months * millisPerMonth));
				whereClause = DatabaseHelper.COLUMN_END + " >= ?";
				
				selectionArgs = new String[] { String.valueOf(minMillis) };
				
				// sort chronologically
		        orderBy = DatabaseHelper.COLUMN_END + " DESC";
			}
		}

        // Return a cursor
		DatabaseHelper datasource = new DatabaseHelper(getActivity());
        Cursor cursor = datasource.proxyQuery(tableName, columns, whereClause, selectionArgs, null, null, orderBy);
        
        Log.i(TAG, "ActionFragment cursor count: " + cursor.getCount());
    	if (cursor.getCount() > 0)
    		success = true;
    	
        String [] bindFrom = new String[] {
        		DatabaseHelper.COLUMN_NAME, 
        		DatabaseHelper.COLUMN_CATEGORY };
        
        int[] bindTo = new int[] {
        		R.id.action_name, 
        		R.id.action_category };
        
        if (cursor.moveToFirst()) {
    		do {
    			Action a = new Action();
    			a.setId(cursor.getInt(0));
    	    	a.setName(cursor.getString(1));
    	    	a.setCategory(cursor.getString(2), 0);	// default at setting top category
    	    	a.setEnd(cursor.getLong(3));
    	    	
    	    	// adding to action list
    	    	mActions.add(a);
    		} while (cursor.moveToNext());
    	}
        
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
		
		
		String name = "" + ((TextView) view.findViewById(R.id.action_name)).getText();
		
		Log.d(TAG, "Clicked: actionName = " + name);
		int pos = 0;
		while (mActions.get(pos).getName() != name && pos < mActions.size())
			pos++;
		mCallback.showDialog(mActions.get(pos));
    }

}
