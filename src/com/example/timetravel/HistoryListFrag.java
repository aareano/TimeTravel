package com.example.timetravel;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class HistoryListFrag extends ListFragment {

	private static final String TAG = "TimeTravel";
	/*private static onItemSelectedListener mCallback;
	
	public interface onItemSelectedListener {
		void onItemSelected();
	}*/
	
	/*@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i(TAG, "onAttached HistoryListFrag");
		
		 		No callbacks yet
		try {
			mCallback = (onItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onItemSelectedListener interface");
		}
		
	}*/
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_history_list, container, false);
    }
	
	@Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        //setList(null);
        Log.d(TAG, "HistoryListFrag's list is set!");
    }
	
	
	public void setList(String orderBy) {
		Log.i(TAG, "HistoryListFrag.setList()");
        
		if (orderBy != null) {
			if (orderBy.equals("Order chronologically")) {
				orderBy = MySQLiteHelper.COLUMN_END_TIME+ " DESC";		// Order by end time
			} else if (orderBy.equals("Order by category"))
        		orderBy = MySQLiteHelper.COLUMN_CATEGORY + " DESC";		// Order alpha by category
		}
		
		String tableName = MySQLiteHelper.TABLE_ACTIONS;
		String[] columns = new String[] {
				MySQLiteHelper.COLUMN_ID, 
				MySQLiteHelper.COLUMN_NAME, 
				MySQLiteHelper.COLUMN_CATEGORY, 
				MySQLiteHelper.COLUMN_START_TIME, 
        		MySQLiteHelper.COLUMN_END_TIME };
        
		// Return a cursor with everything
        Cursor cursor = (History.datasource).proxyQuery(tableName, columns, null, null, null, null, orderBy);
        
        String [] bindFrom = new String[] {
        		MySQLiteHelper.COLUMN_NAME, 
        		MySQLiteHelper.COLUMN_CATEGORY,
        		MySQLiteHelper.COLUMN_START_TIME, 
        		MySQLiteHelper.COLUMN_END_TIME };
        
        int[] bindTo = new int[] {
        		R.id.text_name, 
        		R.id.text_category, 
        		R.id.text_startTime,
        		R.id.text_endTime };
        
        //CustomCursorAdapter custAdapter = new CustomCursorAdapter ((Context) getActivity(), R.layout.history_list_item,
        //		cursor, columnsForViews, viewsForColumns, SimpleCursorAdapter.NO_SELECTION);
        
        SimpleCursorAdapter simpleAdapter = new SimpleCursorAdapter ((Context) getActivity(), R.layout.history_list_item, cursor, 
        		bindFrom, bindTo, SimpleCursorAdapter.NO_SELECTION);
                
        setListAdapter(simpleAdapter);
        //Log.i(TAG, "Adapter set >> closing HistoryListFrag cursor");
        //cursor.close();												//<< this makes it very unhappy.
        
        
        // Note on closing these guys: close either one and it crashes: attempt to reopen already-closed Object.
        // 
        
        
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
		if (view.findViewById(R.id.expRow_date_time).getVisibility() == (View.GONE)) {
			view.findViewById(R.id.expRow_date_time).setVisibility(View.VISIBLE);
			Log.i(TAG, "clicked >> VISIBLE!");
		} else {
			view.findViewById(R.id.expRow_date_time).setVisibility(View.GONE);
			Log.i(TAG, "clicked >> GONE!");
		}
	}
}