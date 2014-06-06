package com.example.timetravel;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;


/*
 * notes: best practice: expand the category, subcat, subsub, etc. all the way through on the top/middle? subcategories
 * so user can see how many levels and can easily navigate through them
 * 
 */
public class Statistics extends FragmentActivity {

	private String TAG = "Statistics";
	private Category mCurrentCat;
	private final int SENTINEL = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		mCurrentCat = new Category();
		mCurrentCat.setId(SENTINEL);

		setSpinner();
	
		setCategoryScroll();
		
		setInformationTable();
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Clicked a ActionBar button");
        return super.onOptionsItemSelected(item);
	}
	
	public void setSpinner() {
		Log.i(TAG, "Statistics.setSpinner()");
		
		Spinner spinner = (Spinner) findViewById(R.id.time_span);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timeSpan_array, 
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	public void setCategoryScroll() {
		Log.d(TAG, "setCategoryScroll()");
		DatabaseHelper datasource = new DatabaseHelper(this);
		
		int parentLevel = SENTINEL;		// represents the level of the selected category. it is the 'parent' of the subsequent list
		String parentName = null;	// represents the name of the selected category. it is the 'parent' of the subsequent list
		
		int totalLevels = datasource.getTotalLevels();
		Log.d(TAG, "totalLevels = " + totalLevels);
		
		ListView[] lvs = new ListView[totalLevels];
		
		if (parentLevel < totalLevels)
			setScrollList(parentName, parentLevel, totalLevels, lvs);
	}
	
	/**
	 * Is a recursive method that constructs the ListViews for each layer of categories.
	 * @param parentName is the name of the parent category
	 * @param parentLevel is the level of the parent category
	 * @param totalLevels is the total number of category levels
	 * @param lvs is an array of ListViews that have been created, size is totalLevels
	 */
	public void setScrollList (String parentName, final int parentLevel, final int totalLevels, final ListView[] lvs) {
		Log.d(TAG, "setList(), parentLevel = " + parentLevel);
		final DatabaseHelper datasource = new DatabaseHelper(this);
		
		final int currentLevel = parentLevel + 1; 
		final LinearLayout ll = (LinearLayout) findViewById(R.id.list_layout);
		ListView lv = new ListView(this);
		ArrayList<Category> categories = new ArrayList<Category>();
		
		// get subcategories
		if (parentName == null) {
			categories = datasource.getCatsFromLevel(currentLevel); 
		}
		else {
			categories = datasource.getSubCategories(parentName, parentLevel);
		}
		
		// check for no categories on the first level
		if (categories.size() == 0 && currentLevel == 0) {
			
			// "no categories"
			TextView noC = (TextView) findViewById(R.id.no_categories);
			noC.setVisibility(View.VISIBLE);
			
			// remove horizontal scrolls
			HorizontalScrollView catScroll = (HorizontalScrollView) findViewById(R.id.hScroll_categories);
			catScroll.setVisibility(View.GONE);
			
			Log.d(TAG, "no categories in database");
			return;
			
		} else if (categories.size() == 0) {
			Log.d(TAG, "no subcategories in database");
			return;
		}
		
		// default initial category value
		if (currentLevel == 0)
			mCurrentCat = categories.get(0);
		
		Log.d(TAG, "categories = " + categories.toString());
		
		// set category listview layoutparams and add to linearlayout
		LayoutParams params = new LayoutParams(
		        LayoutParams.WRAP_CONTENT,      
		        LayoutParams.WRAP_CONTENT
		);
		params.setMargins(10, 0, 25, 0);
		lv.setLayoutParams(params);
		lv.setDividerHeight(3);
		
		ll.addView(lv);
		lvs[currentLevel] = lv;
		
		ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.item_hscroll_cateogry, categories);
		
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView tv = (TextView) view;
				
				// set color
				tv.setBackgroundColor(getResources().getColor(R.color.grey));
				// get name
				String newName = tv.getText().toString();
				
				Log.d(TAG, "category = " + newName + ", newParentLevel = " + currentLevel);
				
				if (totalLevels > 1 + currentLevel) {
					// remove any listviews beyond this level
					for (int i = currentLevel + 1; i < lvs.length; i++) {
						ll.removeView(lvs[i]);
						lvs[i] = null;
						//ll.removeViewAt(i); // TODO if this works, we don't need the array
					}
					
					// recursion
					setScrollList(newName, currentLevel, totalLevels, lvs);
				}
				
				// update mCurrentCategory
				Category category = datasource.getCategory(newName, currentLevel);
				mCurrentCat = category;
				
				// show stats in the lower portion of screen
				setInformationTable();
			}
		});
	}
	
	public void setInformationTable() {
		DatabaseHelper datasource = new DatabaseHelper(this);
		ArrayList<Action> actions = datasource.getActionsOfCategory(mCurrentCat);
		Log.d(TAG, "actions: " + actions.toString());
		Log.d(TAG, "mCurrentCat: " + mCurrentCat.toString());
		
		// check for no actions
		if (actions.size() == 0) {
			Log.d(TAG, "no actions in database");

			// "no actions"
			TextView noA = (TextView) findViewById(R.id.no_actions);
			noA.setVisibility(View.VISIBLE);
			
			// remove info header
			TableRow tbrow = (TableRow) findViewById(R.id.info_header);
			tbrow.setVisibility(View.GONE);
		}
		else {
			// remove "no actions"
			TextView noA = (TextView) findViewById(R.id.no_actions);
			noA.setVisibility(View.GONE);

			// info header
			TableRow tbrow = (TableRow) findViewById(R.id.info_header);
			tbrow.setVisibility(View.VISIBLE);
		}
		
		setOverallStats(actions);
		setActionList(actions);
		
		// if a category is actually selected...
		if (mCurrentCat.getId() != SENTINEL) {
			TextView catName = (TextView) findViewById(R.id.cat_name);
			catName.setText((CharSequence) mCurrentCat.getCatName());
		}
	}
	
	public void setOverallStats(ArrayList<Action> actions) {
		final long milsPerHour = 3600000;
		final long milsPerDay = 86400000;
		
		long totalMils = calcTotalTime(actions);
		int totalHours = (int) (totalMils/milsPerHour);
		int totalDays = (int) (totalMils/milsPerDay);
		
		Log.d(TAG, "totalMils = " + totalMils + ", totalHours = " + totalHours + ", totalDays = " + totalDays);
		
		TextView tv = (TextView) findViewById(R.id.hours);
		tv.setText((CharSequence) (totalHours + " hours"));
		
		TextView tv1 = (TextView) findViewById(R.id.percentage);
		tv1.setText((CharSequence) (totalDays + " days"));
	}
	
	public long calcTotalTime(ArrayList<Action> actions) { 
		long totalTime = 0;
		
		for (int i = 0; i < actions.size(); i++) {
			Action action = actions.get(i);
			long spanMil = action.getEnd().getTimeInMillis() - action.getStart().getTimeInMillis();
			totalTime += spanMil;
		}
		
		return totalTime;
	}
	
	public void setActionList(final ArrayList<Action> actions) {
		
		StatActionAdapter adapter =  new StatActionAdapter(this, R.layout.item_action_stats, actions);
		ListView lv = (ListView) findViewById(R.id.action_list);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Action a = new Action();
				TableRow tbrow = (TableRow) view;
				TextView tv = (TextView) tbrow.findViewById(R.id.name);
				
				a.setName(tv.getText().toString());
				a.setId(SENTINEL);
				
				Log.i(TAG, "clicked: " + a.getName());
				
				// loop through arraylist of actions to find matching name
				for (int i = 0; i < actions.size(); i++) {
					if (a.getName().equals(actions.get(i))) {
						a = actions.get(i);
					}
				}
				
				// show dialog
				if (a.getId() != SENTINEL)
					showDialog(a);
			}
		});
		
	lv.setOnLongClickListener(new OnLongClickListener() {

		@Override
		public boolean onLongClick(View arg0) {
			// TODO select individual actions to see stats on them
			return false;
		}
		
	});
	}
	
	public void showDialog(Action action) {
		Log.i(TAG, "Statistics.showDialog()");
		
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
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}