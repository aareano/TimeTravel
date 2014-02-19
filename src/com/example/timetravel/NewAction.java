package com.example.timetravel;

// eventually, I could guess, based on the current time, what action is most likely and fill that in 

/* right now, things are working off names. If two activities have the same name, they both get deleted. Get a class to keep
 * track of the acts, work with Ids instead!  -- easy to switch once actCatalog is established.
 */

// NEXT up: build a ActionRegistry -- also, rename everything to action from 

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetravel.EndDatePickerFragment.onEndDateSetListener;
import com.example.timetravel.EndTimePickerFragment.onEndTimeSetListener;
import com.example.timetravel.StartDatePickerFragment.onStartDateSetListener;
import com.example.timetravel.StartTimePickerFragment.onStartTimeSetListener;

public class NewAction extends FragmentActivity implements onStartTimeSetListener, onStartDateSetListener, 
		onEndTimeSetListener, onEndDateSetListener {
	
	private String TAG = "TimeTravel";
	public final String ACTIONid = "com.example.timetravel.ACTIONid";
	private ActionDataSource datasource;
	
	// Components of an Action
	public Calendar startTime;
	public Calendar endTime;
	private Calendar timeCreated;
	private String name;
	private String category;
	private boolean isEdit;
	private Action action;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "NewAct.onCreate()");
		
		
		setContentView(R.layout.activity_new_action);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		datasource = new ActionDataSource(this);
		datasource.open();
		
		importAndSetData();
		setSpinners();
		setButtonStrings();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Clicked a ActionBar button");
		boolean saved = false;
		switch (item.getItemId()) {
            case R.id.action_save:
             	saved = saveAction();
         		break;
            case android.R.id.home:
            	// Navigate up
            	// DOESN'T WORK ON DEVICE
                // NavUtils.navigateUpFromSameTask(this); -- because of app name should be "mainmenu"
                
            	Intent intent = new Intent(this, MainMenu.class);
        		startActivity(intent);
                Log.d(TAG, "Navigating up from NewAction");
                break;
		}    
		// If the action was not saved, we stay at NewAction
		// Respond to the action bar's Up/Home button
		if (saved && !isEdit) {
			Intent intent = new Intent(this, MainMenu.class);
    		startActivity(intent);
			// NavUtils.navigateUpFromSameTask(this);
		}
		if (saved && isEdit) {
			Intent intent = new Intent(this, DisplayActions.class);
			intent.putExtra("actionId", action.getId());
			intent.putExtra("returnFromEdit", true);
			startActivity(intent);
		}

        return super.onOptionsItemSelected(item);
	}

	private void setSpinners() {
		Log.i(TAG, "NewAction.setSpinners()");

		// get categories from database
		String tableName = MySQLiteHelper.TABLE_ACTIONS;
		String[] columns = new String[] {
				MySQLiteHelper.COLUMN_ID, 
				MySQLiteHelper.COLUMN_CATEGORY };
		
        // Return a cursor that points to all categories -- order from database, by last used
        Cursor cursor = datasource.proxyQuery(tableName, columns, null, null, null, null, null);
        Log.d(TAG, "Categories: " + cursor.getCount());
        
		final Spinner categorySpinner = (Spinner) findViewById(R.id.spinner_categories);
		
		// Create an ArrayAdapter
		final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
		
		// Get sorted list of categories
		List<String> sortedList = new ArrayList<String>();
		sortedList = sortCatList(cursor);
		
		// Add "Choose category..." option first
		String chooseCategory = getResources().getString(R.string.choose_category);
		adapter.add(chooseCategory);
		
		// Add categories one by one
		if (sortedList != null) {
			Log.d(TAG, "sortedList: " + sortedList.toString());
			for (int i = 0; i < sortedList.size(); i++) {
				adapter.add(sortedList.get(i));
			}
		}
		// Add "New Category..." option last
		final String newCategory = getResources().getString(R.string.new_category);
		adapter.add(newCategory);
		
		/*
		// Pre-set category
		if (isEdit) {
			for (int position = 0; position < adapter.getCount(); position++)
		    {
		        if (categorySpinner.getItemAtPosition(position).toString().equals(category))
		        {
		            categorySpinner.setSelection(position, true);
		        }
		    }
		}
		*/
		
		// make sure the category EditText is gone 
		final EditText editCategory = (EditText) findViewById(R.id.edit_text_new_category);
        editCategory.setVisibility(View.GONE);
		
								// ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		categorySpinner.setAdapter(adapter);
		// Set OnItemSelectedListener
		
		categorySpinner.setOnItemSelectedListener (new OnItemSelectedListener() {
			
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Retrieve selected item
				category = categorySpinner.getItemAtPosition(pos).toString();
	            
				// Edit category block
				if(category.matches(newCategory))
	            {
	            	// Exchange spinner for EditText
	            	categorySpinner.setVisibility(View.GONE);
	            	editCategory.setText("");	// start new
	                editCategory.setVisibility(View.VISIBLE);
	                editCategory.requestFocus();
	                editCategory.addTextChangedListener(new TextWatcher() {
	                    @Override
	                    public void afterTextChanged(Editable s) {
	                    	// save text every time it is changed
	                    	category = editCategory.getText().toString();
	                    }

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							
						}

						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
						
						}
	                });
	                
	                editCategory.setOnFocusChangeListener(new OnFocusChangeListener() {          

                       public void onFocusChange(View v, boolean hasFocus) {
                           if(!hasFocus) {
                        	   Log.d(TAG, "!hasFocus");
                        	   // Exchange EditText for spinner
                        	   editCategory.setVisibility(View.GONE);
                        	   categorySpinner.setVisibility(View.VISIBLE);
                        	   
                        	   if (!category.equals(newCategory)) {
	                        	   Log.d(TAG, "Adding category: " + category.toString());
	                        	   
	                        	   // Add and remove to get proper order
	                        	   adapter.remove(newCategory);
	                        	   adapter.add(category); 	// least frequent, so put at the bottom
	                        	   adapter.add(newCategory);
                        	   }
	                       }
                       }
                   });
                }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				category = "Unspecified category";
			}
		});
	}
	
	/**
	 * Change cursor to usable list ordered by frequency of use, then most recently used
	 * @param cursor
	 * @return
	 */
	public ArrayList<String> sortCatList(Cursor cursor) {
		cursor.moveToFirst();

		// Check for null cursor
		if (cursor.getCount() == 0)
			return null;
		
        // Create arrayList of categories
        List<String> catList = new ArrayList<String>();
        while (!cursor.isAfterLast()) {
        	catList.add(cursor.getString(1));
        	cursor.moveToNext();
        }
        cursor.close();
        
        Log.i(TAG, "catList: " + catList.toString());
        
        // Create arrayList that contains only 1 instance of category (no repetitions)
        List<String> sortingList = new ArrayList<String>();
        for (int i = 0; i < catList.size(); i++) {
        	String cat = catList.get(i);
        	if (!sortingList.contains(cat)) {
    			sortingList.add(cat);
        	}
        }
        
        Log.i(TAG, "sortingList: " + sortingList.toString());
        
        // Create array that has an int that represents the number of occurrences 
        // of the category at the corresponding index of sortingList
        // Is an array because number of elements is important and must not change 
        //   	>> arrays have an immutable number of elements
        int[] freq = new int[sortingList.size()];
        
        for (int i = 0; i < sortingList.size(); i++) {
        	String cat = sortingList.get(i);
        	
        	// if catList contains the 'cat', start at the beginning of catList and go through looking for 'cat'
        	if (catList.contains(cat)) {
	        	Log.i(TAG, "catList.contains(cat) == true");
        		for (int j = 0; j < catList.size(); j++) {
		        	if (catList.get(j).equals(cat)) {
		        		// increase frequency int by 1
		        		freq[i] = freq[i] + 1;
		        		Log.i(TAG, "frequency ++ :: freq[" + i + "] = " + freq[i]);
		        	}
			        // test to see if this is the last
			        if (j == catList.lastIndexOf(cat)) {
			        	Log.i(TAG, "this is the lastIndexOf: " + cat.toString());
			        	break;
			        }
		        }
        	}
        }
        
        // Create an sortedList and sort populate in order of frequency (if equal frequency, ordered by most recently used)
        List<String> sortedList = new ArrayList<String>();
        int max = 0;
        int atIndex = 0;
        
        while (sortedList.size() < sortingList.size()) {
	        Log.d(TAG, "before for");
        	for (int i = 0; i < freq.length; i++) {
	        	// find max frequency and its index 
	        	if (freq[i] > max) {
	        		max = freq[i];
	        		atIndex = i;
	        	}
	        }
	        // reset frequency so it won't claim max again, reset max, add category to sortedList
	        freq[atIndex] = 0;
	        max = 0;
	        sortedList.add(sortingList.get(atIndex));
	        Log.i(TAG, "Adding '" + sortingList.get(atIndex) + "' to sortedList from sortingList -- index " + atIndex);
        }
        
        return (ArrayList<String>) sortedList;
	}
	
	/**
	 * Prepare class variables name, category, startTime, and endTime
	 * Takes appropriate action if this is an edit
	 */
	public void importAndSetData() {
		Log.i(TAG, "NewAction.importAndSetData()");
		
		// These variables must be initiated if they are to be changed later
		long millisInHour = 3600000;
		timeCreated = Calendar.getInstance();
		startTime = Calendar.getInstance();				// startTime = current time
		endTime = Calendar.getInstance();				// endTime = current time + 1 hour
		endTime.setTimeInMillis(startTime.getTimeInMillis() + millisInHour);
		
		Intent intent = getIntent();
		String booleanExtraTag = "com.example.timetravel.IsEDIT";
		isEdit = intent.getBooleanExtra(booleanExtraTag, false);
		
		Log.i(TAG, "IsEdit: " + isEdit);

		
		/** The action could be started by main menu = not an edit
		  * or by the History listing = edit
		  */
		if (isEdit) {
			
			// Get action  (find by its name)
			int id = intent.getIntExtra(ACTIONid, 0);
			action = null; 		// initialize
			
			try {
				action = Action.findActionById(id);
			} catch (NoActionFoundException e) {
				e.printStackTrace();
			}
			
			// Set category, startTime, endTime
			name = action.getName();
			category = action.getCategory();
			Log.d(TAG, "category = " + category);
			startTime = action.getStartTime();
			endTime = action.getEndTime();
			
			// Set name
			EditText editName = (EditText) findViewById(R.id.editText_action_name);
			editName.setText(name, TextView.BufferType.EDITABLE);
		}
	}
	
	/**
	 * Set strings for buttons
	 */
	public void setButtonStrings() {
		Log.i(TAG, "setButtonStrings()");
		
		Button sTimeBut = (Button) findViewById(R.id.button_startTime);
		Button sDateBut = (Button) findViewById(R.id.button_startDate);
		Button eTimeBut = (Button) findViewById(R.id.button_endTime);
		Button eDateBut = (Button) findViewById(R.id.button_endDate);
		
		Date startDate = startTime.getTime();
		Date endDate = endTime.getTime();
		
		SimpleDateFormat time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH);
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
		
		sTimeBut.setText(time.format(startDate));
		sDateBut.setText(date.format(startDate));
		eTimeBut.setText(time.format(endDate));
		eDateBut.setText(date.format(endDate));
	}
	
	
	// startTime or endTime is passed in as a parameter
	// Start time
	public void showStartTimePickerDialog(View v) {
		Log.d(TAG, "before dialog startTime: " + startTime.getTime().toString());
		DialogFragment newFragment = StartTimePickerFragment.newInstance(startTime.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "startTimePicker");
	}
	
	// Start date
	public void showStartDatePickerDialog(View v) {
		Log.d(TAG, "before dialog startTime: " + startTime.getTime().toString());
		DialogFragment newFragment = StartDatePickerFragment.newInstance(startTime.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "startDatePicker");
	}

	// End time
	public void showEndTimePickerDialog(View v) {
		Log.d(TAG, "before dialog endTime: " + endTime.getTime().toString());
		DialogFragment newFragment = EndTimePickerFragment.newInstance(endTime.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "endTimePicker");
	}
	
	// End date
	public void showEndDatePickerDialog(View v) {
		Log.d(TAG, "before dialog endTime: " + endTime.getTime().toString());
		DialogFragment newFragment = EndDatePickerFragment.newInstance(endTime.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "endDatePicker");
	}
	
	// Start time
	@Override
	public void onStartTimeSet(int hourOfDay, int minute) {
		startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		startTime.set(Calendar.MINUTE, minute);
		Log.d(TAG, "startTime: " + startTime.getTime().toString());
		setButtonStrings();
	}

	// Start date
	@Override
	public void onStartDateSet(int year, int month, int monthDay) {
		startTime.set(Calendar.YEAR, year);
		startTime.set(Calendar.MONTH, month);
		startTime.set(Calendar.DAY_OF_MONTH, monthDay);
		Log.d(TAG, "startTime: " + startTime.getTime().toString());
		setButtonStrings();
	}
	
	// End time
	@Override
	public void onEndTimeSet(int hourOfDay, int minute) {
		endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
		endTime.set(Calendar.MINUTE, minute);
		Log.d(TAG, "endTime: " + endTime.getTime().toString());
		setButtonStrings();
	}
	
	// End date
	@Override
	public void onEndDateSet(int year, int month, int monthDay) {
		endTime.set(Calendar.YEAR, year);
		endTime.set(Calendar.MONTH, month);
		endTime.set(Calendar.DAY_OF_MONTH, monthDay);
		Log.d(TAG, "endTime: " + endTime.getTime().toString());
		setButtonStrings();
	}
		
	private boolean saveAction() {
		boolean saved = false;
		
		// Name
		EditText toName = (EditText) findViewById(R.id.editText_action_name);
		name = toName.getText().toString();
		if (name ==  null) {
			Toast.makeText(this, "Enter a name", Toast.LENGTH_LONG).show();
			return saved;
		}
		
		// Round startTime and endTime off to minutes
		startTime.set(Calendar.SECOND, 0);
		startTime.set(Calendar.MILLISECOND, 0);
		endTime.set(Calendar.SECOND, 0);
		endTime.set(Calendar.MILLISECOND, 0);
		
		// set startTime and endTime String objects (in milliseconds)
		String startTimeString = String.valueOf(startTime.getTimeInMillis());
        String endTimeString = String.valueOf(endTime.getTimeInMillis());
		
        // set timeCreated String objects (in milliseconds)
        String timeCreatedString = String.valueOf(timeCreated.getTimeInMillis());
        
		Log.d(TAG, "timeCreated: " + timeCreated.getTime().toString());
		Log.d(TAG, "startTime: " + startTime.getTime().toString());
		Log.d(TAG, "endTime: " + endTime.getTime().toString());
		
		if (endTime.getTimeInMillis() <= startTime.getTimeInMillis()) {
			Toast.makeText(this, "The action must begin before it ends", Toast.LENGTH_LONG).show();
			return saved;
		}
		if (endTime.getTimeInMillis() - startTime.getTimeInMillis() > 2147483640) {
			// tests against the (rounded) max value of an integer
			Toast.makeText(this, "Action is too long", Toast.LENGTH_LONG).show();
			return saved;
		}
        if (isEdit) {
        	// Delete original action if this is an edit
        	Log.i(TAG, "Deleting original action");
        	datasource.deleteAction(action.getId());
        }
        if (category.equals(getResources().getString(R.string.choose_category))) {
			Toast.makeText(this, "Choose category", Toast.LENGTH_LONG).show();
			return saved;
		}
        
		// Save action
		datasource.createAction(name, category, startTimeString, endTimeString, timeCreatedString);
		Toast.makeText(this, "Saved action", Toast.LENGTH_SHORT).show();
		saved = true;
		return saved;
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "NewAction.onResume()");
		if (datasource != null)
			datasource.open();
		super.onResume();
	}
	  
	@Override
	protected void onPause() {
		Log.i(TAG, "NewAction.onPause()");
		datasource.close();
		super.onPause();
	}
}
