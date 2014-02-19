package com.example.timetravel;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class HistoryCurrentFrag extends ListFragment {
	
	private final static String TAG = "TimeTravel";
	
	/*private static onDeleteRequested mCallback;
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (onDeleteRequested) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onItemSelectedListener interface");
		}
	}*/
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_history_current, container, false);
    }

	@Override
	public void onActivityCreated(Bundle saved) {
		super.onActivityCreated(saved);
    }
	
	/**
	 * Includes database call and the setting of the custom list adapter
	 * @param orderBy
	 */
	public void setList(String orderBy) {
		
		if (orderBy != null) {
			if (orderBy.equals("Order chronologically")) {
				orderBy = MySQLiteHelper.COLUMN_END_TIME+ " DESC";		// Order by end time
			} else if (orderBy.equals("Order by category"))
        		orderBy = MySQLiteHelper.COLUMN_CATEGORY + " ASC";		// Order alpha by category
		}
		
		String tableName = MySQLiteHelper.TABLE_ACTIONS;
		String[] columns = new String[] {
				MySQLiteHelper.COLUMN_ID, 
				MySQLiteHelper.COLUMN_NAME, 
				MySQLiteHelper.COLUMN_CATEGORY, 
        		MySQLiteHelper.COLUMN_START_TIME, 
        		MySQLiteHelper.COLUMN_END_TIME };
        
		// I don't know what this is for
		/*long millis = Long.parseLong(MySQLiteHelper.COLUMN_END_TIME);
		Time endTime = new Time();
		endTime.set(millis);*/
		
		Time nowTime = new Time();
		nowTime.setToNow();
		long currentMillis = nowTime.toMillis(true);
		
		// Acts with undefined endTimes will always have endTime equal to currentMillis 
		String whereClause = MySQLiteHelper.COLUMN_END_TIME + " >= ?";
        String[] selectionArgs = new String[] { String.valueOf(currentMillis) };
        
        // Return a cursor with one Act
        Cursor cursor = (History.datasource).proxyQuery(tableName, columns, whereClause, selectionArgs, null, null, orderBy);
    	Log.i(TAG, "HistoryCurrentFrag cursor count: " + cursor.getCount());
    	
        String [] bindFrom = new String[] {
        		MySQLiteHelper.COLUMN_NAME, 
        		MySQLiteHelper.COLUMN_CATEGORY,
        		MySQLiteHelper.COLUMN_START_TIME, 
        		MySQLiteHelper.COLUMN_END_TIME };
        
        int[] bindTo = new int[] {
        		R.id.text_name, 
        		R.id.text_category, 
        		R.id.text_startTime,
        		R.id.text_endTime }; // need to get a custom adapter to convert times to readable strings >> attach
        
        CustomCursorAdapter custAdapter = new CustomCursorAdapter ((Context) getActivity(), R.layout.history_list_item,
        		cursor, bindFrom, bindTo, SimpleCursorAdapter.NO_SELECTION);
        
        setListAdapter(custAdapter);
        //Log.i(TAG, "Adapter set >> closing HistoryCurrentFrag cursor");
        //cursor.close();
        
        // longClickListener
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
 			@Override
 			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
 				Log.i(TAG, "onItemLongClick()");
 				// send to editAct or startAct mCallback.onItemSelectedListener(some item);
 				return true;
 			}
 		});
	}
    
	@Override
    public void onListItemClick(ListView lv, View view, int position, long id) {
    	// use this to highlight item: selected.isHovered()
    	// use for custom animation: selected.animate()
		Log.i(TAG, "onListItemClicked");
		String actionName = "" + ((TextView) view.findViewById(R.id.text_name)).getText();
		Log.d(TAG, "actionName = " + actionName);
		showDialog(actionName);
    }
	
	void showDialog(String actionName) {
		
	    // DialogFragment.show() will take care of adding the fragment
	    // in a transaction.  We also want to remove any currently showing
	    // dialog, so make our own transaction and take care of that here.
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    
	    // Create and show the dialog.
	    DialogFragment newFragment = ActionDialog.newInstance(actionName);
	    newFragment.show(ft, "dialog");
	}
}