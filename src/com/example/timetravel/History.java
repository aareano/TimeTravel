package com.example.timetravel;

import com.example.timetravel.ActionDialog.DisplayActionDialog;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class History extends FragmentActivity implements DisplayActionDialog {
	
	public final String IsEDIT = "com.example.timetravel.IsEDIT";
	public final String ACTIONid = "com.example.timetravel.ACTIONid";
	public static ActionDataSource datasource;
	public static HistoryCurrentFrag currentListFrag;
	public static HistoryListFrag historyListFrag;
	private String TAG;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "History.onCreate()");
		
		setContentView(R.layout.activity_history);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		setSpinner();
		
		// Initialize field variables
		datasource = new ActionDataSource(this);
		currentListFrag = new HistoryCurrentFrag();
		historyListFrag = new HistoryListFrag();
		TAG = "TimeTravel";
		
		Log.i(TAG, "History.onCreate() >> opened datasource");
		datasource.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.history, menu);
		return true;
	}
	
	private void setSpinner() {
		// History spinner
		Spinner spinner = (Spinner) (findViewById(R.id.spinner_history));
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.history_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		// Set OnItemSelectedListener
		spinner.setOnItemSelectedListener (new OnItemSelectedListener() {
		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Retrieve selected item
				Object item = parent.getItemAtPosition(pos);
				String orderBy = item.toString();
				
				HistoryCurrentFrag currentFragment =  ((HistoryCurrentFrag) getSupportFragmentManager().
						findFragmentById(R.id.history_current_frag));
				if(currentFragment != null) currentFragment.setList(orderBy);
				
				Log.i(TAG, "Spinner set!");
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			
		});
	}
	
	@Override
	public void editAction(Action action) {
		// Add fragment to backStack
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment dialog = getFragmentManager().findFragmentByTag("dialog");
	    if (dialog != null) {
	        ft.remove(dialog);
	    }
	    ft.addToBackStack(action.getName() + " dialog").commit();
		
	    // move to NewAction
		int id = action.getId();
		Intent intent = new Intent(this, NewAction.class);
		intent.putExtra(IsEDIT, true);
		intent.putExtra(ACTIONid, id);
		startActivity(intent);
	}
	
	@Override
	public void deleteAction(Action action) {
		// Add fragment to backStack
		FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment dialog = getFragmentManager().findFragmentByTag("dialog");
	    if (dialog != null) {
	        ft.remove(dialog);
	    }
	    ft.addToBackStack(action.getName() + " dialog").commit();
		
	    // delete Action
		int rowsDeleted = datasource.deleteAction(action.getId());
		if (rowsDeleted > 0) {
			if (rowsDeleted == 1)
				Toast.makeText(this, "Activity deleted", Toast.LENGTH_SHORT).show();
			else if (rowsDeleted > 1) 
				Toast.makeText(this, rowsDeleted + " activities deleted", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(this, "Activity was not deleted", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onResume() {
		Log.i(TAG, "History.onResume() >> opened datasource");
		datasource.open();
		Action.updateActionRegistry(datasource);
		
		super.onResume();
	}

	
	@Override
	public void onPause() {
		Log.i(TAG, "History.onPause() >> closed datasource");
		datasource.close();
		super.onPause();
	}
}


/*
// Add History activity's Header, List, and Current Actions fragments
if (savedInstanceState == null) {
	
	android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
	Log.i(TAG, "History gotFragmentManager()");
																					// need to set weight of fragments
    
    // HistoryListFragment
    android.app.Fragment  newList = new HistoryListFrag();
    ft.add(R.id.history_list_frag, newList);
    Log.i(TAG, "Added HistoryHeaderFrag");
    
    // CurrentActionsFragment
    android.app.Fragment newCurrent= new HistoryCurrentFrag();
    ft.add(R.id.history_current_frag, newCurrent);
    Log.i(TAG, "Added HistoryHeaderFrag");
	
    ft.commit();
    Log.i(TAG, "Committed fragments");
    
    
}*/