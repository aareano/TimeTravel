
// Create a picture of what you want to make.


// working on: 	screw the loaders. use async task 

package com.example.timetravel;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.timetravel.ActionDialog.DisplayActionDialog;
import com.example.timetravel.ActionFragment.SelectAction;
import com.example.timetravel.SortFragment.Sort;

/**
 * This activity displays Actions:
 * 		- all action sorted by name or category or time
 * 		- of a specific category
 * 		- of a specific time or since a specific time
 * 
 * the only difference in all of these is the cursor: what to get, what to display, how to display it.
 * 
 * It will also be able to search for specific names.
 * @author Aaron
 *
 */
public class DisplayActions extends FragmentActivity implements DisplayActionDialog, Sort, SelectAction {
	
	public static DatabaseHelper datasource;
	public static SortFragment sortFragment = new SortFragment();
	public static ActionFragment actionFragment = new ActionFragment();
	public String sortFragTag = "SortFragment";
	public String actionFragTag = "ActionFragment";
	
	private String TAG = "TimeTravel";
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "DisplayActions.onCreate()");
		
		Log.i(TAG, "DisplayActions.onCreate() >> opened datasource");
		datasource = new DatabaseHelper(this);

		setContentView(R.layout.activity_display_actions);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Add fragments dynamically -- added in onresume()
		/*
		FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.fragment_container_sort, sortFragment, sortFragTag);
		ft.add(R.id.fragment_container_list, actionFragment, actionFragTag);
		ft.commit();
		*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display_actions, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Clicked a ActionBar button");
		boolean saved = false;
		switch (item.getItemId()) {
            case android.R.id.home:
            	// Navigate up
            	// DOESN'T WORK ON DEVICE
                //NavUtils.navigateUpFromSameTask(this);
                Log.d(TAG, "Navigating up from NewAction");
                break;
		}
		Intent intent = new Intent(this, MainMenu.class);
		startActivity(intent);
        return super.onOptionsItemSelected(item);
	}
	
	// Callback methods
	
	/**
	 * 
	 */
	@Override
	public void onSelect(Action action) {
		// TODO
	}
	
	
	/**
	 * Passes sort parameters from SortFragment to ActionFragment.
	 * This is the middle man.
	 */
	@Override
	public boolean sortBy (String[] sortParams) {
		boolean success = actionFragment.setList(sortParams);
		return success;
	}

	/**
	 * Callback method to show dialog fragment
	 * @Override
	 */
	public void showDialog(Action action) {
		Log.d(TAG, "showDialog()");
		
		// DialogFragment.show() will take care of adding the fragment
		// in a transaction.  We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("dialog");
		
		if (prev != null) {
			Log.d(TAG, "dialog not null, moving to backstack");
			ft.remove(prev);
		}
		
		ft.addToBackStack(null);
		
		// Create and show the dialog.
		DialogFragment newFragment = ActionDialog.newInstance(action);
		newFragment.show(ft, "dialog");
	}
	
	/**
	 * Callback method to change to NewAction activity to edit the selected action
	 * @Override
	 */
	public void editAction(Action action) {
		// Add fragment to backStack
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment dialog = fm.findFragmentByTag("dialog");
	    if (dialog != null) {
	    	Log.d(TAG, "dialog not null, moving to backstack");
	    	ft.remove(dialog);
	    }
	    ft.addToBackStack(action.getName() + " dialog").commit();
		
	    // move to NewAction activity
		int id = action.getId();
		Intent intent = new Intent(this, NewAction.class);
		intent.putExtra("isEdit", true);
		intent.putExtra("action_id", id);
		
		List<String> categories = new ArrayList<String>();
		
		intent.putExtra("action_id", id);
		intent.putExtra("action_id", id);
		
		startActivity(intent);
	}
	
	/**
	 * Callback method to delete an action from the database
	 * @Override
	 */
	public void deleteAction(Action action) {
		// Add fragment to backStack
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment dialog = fm.findFragmentByTag("dialog");
	    if (dialog != null) {
	        Log.d(TAG, "dialog not null, moving to backstack");
	    	ft.remove(dialog);
	    }
	    ft.addToBackStack(action.getName() + " dialog").commit();
		
	    // delete Action
		int rowsDeleted = datasource.deleteAction(action);
		if (rowsDeleted > 0) {
			if (rowsDeleted == 1)
				Toast.makeText(this, "Action deleted", Toast.LENGTH_SHORT).show();
			else if (rowsDeleted > 1) 
				Toast.makeText(this, rowsDeleted + " actions deleted", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(this, "Action was not deleted", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onResume() {
		Log.i(TAG, "DisplayActions.onResume() >> opened datasource");
		datasource = new DatabaseHelper(this);
		
		// Add fragments
		FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.fragment_container_sort, sortFragment, sortFragTag);
		ft.add(R.id.fragment_container_list, actionFragment, actionFragTag);
		ft.commit();
		
		super.onResume();
	}

	
	@Override
	public void onPause() {
		Log.i(TAG, "DisplayActions.onPause() >> closed datasource");
		datasource.close();
		
		// Remove fragments
		FragmentManager fm = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
		ft.remove(fm.findFragmentByTag(sortFragTag));
		ft.remove(fm.findFragmentByTag(actionFragTag));
		ft.commit();
		
		super.onPause();
	}

}