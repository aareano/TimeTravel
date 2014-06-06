package com.example.timetravel;

// eventually, I could guess, based on the current time, what action is most likely and fill that in 

/* right now, things are working off names. If two activities have the same name, they both get deleted. Get a class to keep
 * track of the acts, work with Ids instead!  -- easy to switch once actCatalog is established.
 */

// NEXT up: build a ActionRegistry -- also, rename everything to action from 

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.timetravel.CategoryDialog.onCategorySelected;
import com.example.timetravel.CreateCategoryDialog.CreateCategoryListener;
import com.example.timetravel.EndDatePickerFragment.onEndDateSetListener;
import com.example.timetravel.EndTimePickerFragment.onEndTimeSetListener;
import com.example.timetravel.StartDatePickerFragment.onStartDateSetListener;
import com.example.timetravel.StartTimePickerFragment.onStartTimeSetListener;

public class NewAction extends FragmentActivity implements onStartTimeSetListener, onStartDateSetListener, 
		onEndTimeSetListener, onEndDateSetListener, onCategorySelected, CreateCategoryListener {
	
	private String TAG = "TimeTravel";
	private DatabaseHelper datasource;
	
	// Components of an Action
	public Calendar start;
	public Calendar end;
	private String name;
	private Category category = new Category();
	private static boolean isEdit;
	private Action action;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "NewAct.onCreate()");
		
		
		setContentView(R.layout.activity_new_action);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		datasource = new DatabaseHelper(this);
		
		// receive data in.
		importAndSetData();
		
		// create spinner with all categories in it. set category (if isEdit)
		// setSpinners();
		
		// set start and end time
		setButtonStrings();
		
		listenForName();
		
		// opened again in onResume().
		datasource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Clicked an ActionBar button");
		boolean saved = false;
		switch (item.getItemId()) {
            case R.id.action_save:
             	saved = saveAction();
         		break;
            case android.R.id.home:
            	// Navigate up
            	Log.d(TAG, "Navigating up from NewAction");
                NavUtils.navigateUpFromSameTask(this);
                break;
		}    
		// If the action was not saved, we stay at NewAction
		if (saved && !isEdit) {
			NavUtils.navigateUpFromSameTask(this);
		}
		if (saved && isEdit) {
			Intent intent = new Intent(this, DisplayActions.class);
			intent.putExtra("actionId", action.getId());
			intent.putExtra("returnFromEdit", true);
			startActivity(intent);
		}

        return super.onOptionsItemSelected(item);
	}

	/**
	 * creates instance of CategoryDialog and shows it.
	 * @param v the view of the category button
	 */
	public void showCategoryDialog(View v) {
		Log.d(TAG, "showCategoryDialog()");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("dialog");
		
		if (prev != null) {
			Log.d(TAG, "dialog not null, moving to backstack");
			ft.remove(prev);
		}
		
		ft.addToBackStack(null);
		
		// Create and show the dialog.
		DialogFragment newFragment = CategoryDialog.newInstance(isEdit);
		newFragment.show(ft, "category dialog");
	}
	
	/**
	 * initializes member variables name, category, start, and end
	 * receives in data if this is an edit.
	 */
	public void importAndSetData() {
		
		// These variables must be initiated if they are to be changed later
		long millisInHour = 3600000;
		start = Calendar.getInstance();				// start = current time
		end = Calendar.getInstance();				// end = current time + 1 hour
		end.setTimeInMillis(start.getTimeInMillis() + millisInHour);
		
		Intent intent = getIntent();
		String booleanExtraTag = "com.example.timetravel.IsEDIT";
		isEdit = intent.getBooleanExtra(booleanExtraTag, false);
		
		Log.i(TAG, "IsEdit: " + isEdit);

		
		/** The action could be started by main menu = not an edit
		  * or by the History listing = edit
		  */
		if (isEdit) {
			
			// Get action  (find by its name)
			int id = intent.getIntExtra("action_id", 0);
			action = new Action(); 		// initialize
			
			action = datasource.getAction(id);
			
			// Set name, category, start, end
			name = action.getName();
			category = action.getCategory(0);	// TODO this is gross
			start = action.getStart();
			end = action.getEnd();
			
			// Set name
			EditText editName = (EditText) findViewById(R.id.editText_action_name);
			editName.setText(name, TextView.BufferType.EDITABLE);
		}
	}
	
	/*
	private void setSpinners() {

		// get categories from database
		String tableName = DatabaseHelper.TABLE_CATEGORIES;
		String[] columns = new String[] {
				DatabaseHelper.COLUMN_ID, 
				DatabaseHelper.COLUMN_CATEGORY };
		
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
		
		
		
		// TODO
		
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
		
		// make sure the category EditText is gone 
		final EditText editCategory = (EditText) findViewById(R.id.edit_text_new_category);
        editCategory.setVisibility(View.GONE);
		
        
        // set these to gone
 		findViewById(R.id.spinner_categories).setVisibility(View.GONE);
 		findViewById(R.id.edit_text_new_category).setVisibility(View.GONE);
        
		
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
	
	/*
	 /**
	 *
	 * Change cursor to usable list ordered by frequency of use, then most recently used
	 * @param cursor
	 * @return
	 *
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
	*/
	
	
	public void listenForName() {
		final EditText nameEdit = (EditText) findViewById(R.id.editText_action_name);
		nameEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				name = nameEdit.getText().toString();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
	}
	

	/**
	 * creates instance of CategoryDialog and shows it.
	 * @param v the view of the category button
	 */
	public void showCreateCategoryDialog() {
		Log.d(TAG, "showNewCategoryDialog()");
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment prev = fm.findFragmentByTag("category dialog");
		
		if (prev != null) {
			Log.d(TAG, "dialog not null, moving to backstack");
			ft.remove(prev);
		}
		
		ft.addToBackStack(null);
		
		// Create and show the dialog.
		DialogFragment newFragment = CreateCategoryDialog.newInstance();
		newFragment.show(ft, "new category dialog");
	}
	
	// for choose category dialog
	@Override
	public void onDialogPositiveClick(DialogFragment dialog, Category category) {
		this.category = category;
		Button button = (Button) findViewById(R.id.button_choose_category);
		button.setText(category.getCatName());
	}
	
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// do nothing
	}
	
	@Override
	public void onCreateCategory(String newCategory) {
		category.setCatName(newCategory);
		Button button = (Button) findViewById(R.id.button_choose_category);
		button.setText((CharSequence) newCategory);
	}
	
	/**
	 * Set strings for buttons
	 */
	public void setButtonStrings() {
		
		Button sTimeBut = (Button) findViewById(R.id.button_startTime);
		Button sDateBut = (Button) findViewById(R.id.button_startDate);
		Button eTimeBut = (Button) findViewById(R.id.button_endTime);
		Button eDateBut = (Button) findViewById(R.id.button_endDate);
		
		Date startDate = start.getTime();
		Date endDate = end.getTime();
		
		SimpleDateFormat time = new SimpleDateFormat("KK:mm a", Locale.ENGLISH);
		SimpleDateFormat date = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
		
		sTimeBut.setText(time.format(startDate));
		sDateBut.setText(date.format(startDate));
		eTimeBut.setText(time.format(endDate));
		eDateBut.setText(date.format(endDate));
	}
	
	
	// start or end is passed in as a parameter
	// Start time
	public void showStartTimePickerDialog(View v) {
		DialogFragment newFragment = StartTimePickerFragment.newInstance(start.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "startTimePicker");
	}
	
	// Start date
	public void showStartDatePickerDialog(View v) {
		DialogFragment newFragment = StartDatePickerFragment.newInstance(start.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "startDatePicker");
	}

	// End time
	public void showEndTimePickerDialog(View v) {
		DialogFragment newFragment = EndTimePickerFragment.newInstance(end.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "endTimePicker");
	}
	
	// End date
	public void showEndDatePickerDialog(View v) {
		DialogFragment newFragment = EndDatePickerFragment.newInstance(end.getTimeInMillis());
	    newFragment.show(getSupportFragmentManager(), "endDatePicker");
	}
	
	// Start time
	@Override
	public void onStartTimeSet(int hourOfDay, int minute) {
		start.set(Calendar.HOUR_OF_DAY, hourOfDay);
		start.set(Calendar.MINUTE, minute);
		Log.d(TAG, "start: " + start.getTime().toString());
		setButtonStrings();
	}

	// Start date
	@Override
	public void onStartDateSet(int year, int month, int monthDay) {
		start.set(Calendar.YEAR, year);
		start.set(Calendar.MONTH, month);
		start.set(Calendar.DAY_OF_MONTH, monthDay);
		Log.d(TAG, "start: " + start.getTime().toString());
		setButtonStrings();
	}
	
	// End time
	@Override
	public void onEndTimeSet(int hourOfDay, int minute) {
		end.set(Calendar.HOUR_OF_DAY, hourOfDay);
		end.set(Calendar.MINUTE, minute);
		Log.d(TAG, "end: " + end.getTime().toString());
		setButtonStrings();
	}
	
	// End date
	@Override
	public void onEndDateSet(int year, int month, int monthDay) {
		end.set(Calendar.YEAR, year);
		end.set(Calendar.MONTH, month);
		end.set(Calendar.DAY_OF_MONTH, monthDay);
		Log.d(TAG, "end: " + end.getTime().toString());
		setButtonStrings();
	}
		
	private boolean saveAction() {

		// valid to save
		boolean valid = false;

		// Round 'start' and 'end' off to whole minutes
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MILLISECOND, 0);
		
		
		// TESTS
		if (!passesTests())
			return valid;
        
		
		// Concoct action
		
		// Save action (only saves top category right now) TODO
        Action a = new Action(name, category.getCatName(), 0, start, end);
        long action_id = datasource.createAction(a);
        
        
        
        // Save category
        long category_id = -1;
        // TODO "CATEGORY_TABLE_LEVEL" needs to become variable
        if (datasource.categoryExists(category.getCatName(), DatabaseHelper.CATEGORY_TABLE_LEVEL)) {
        	category_id = category.getId();		// TODO need id
        }
        if (!datasource.categoryExists(category.getCatName(), DatabaseHelper.CATEGORY_TABLE_LEVEL)) {
        	category_id = datasource.createCategory(category.getCatName(), DatabaseHelper.CATEGORY_TABLE_LEVEL, -1);
        }
        
        
        
        // Create action-category relationship
        Log.d(TAG, "action_id = " + action_id + "category_id = " + category_id);
        long relationship_id = datasource.createActionCategory(action_id, category_id, DatabaseHelper.CATEGORY_TABLE_LEVEL);
        
        
        // Alert user
        if (action_id != -1 && category_id != -1 && relationship_id != -1) {
        	valid = true;
			Toast.makeText(this, "Saved action", Toast.LENGTH_SHORT).show();
			
			Log.d(TAG, "Action: (name: '" + name + "', category: " + category + ", start: " + start.getTime().toString() 
					+ ", end: " + end.getTime().toString());
			
        } else
        	Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        
        
		return valid;
	}
	
	public boolean passesTests() {
		boolean passes = false;
		
		if (name ==  null) {
			Toast.makeText(this, "Enter a name", Toast.LENGTH_LONG).show();
			return passes;
		}
		
		if (end.getTimeInMillis() <= start.getTimeInMillis()) {
			Toast.makeText(this, "The action must begin before it ends", Toast.LENGTH_LONG).show();
			return passes;
			
		} else if (end.getTimeInMillis() - start.getTimeInMillis() > 2147483640) {
			// tests against the (rounded) max value of an integer
			Toast.makeText(this, "Action is too long", Toast.LENGTH_LONG).show();
			return passes;
			
		} else if (category.equals(getResources().getString(R.string.choose_category))) {
			Toast.makeText(this, "Choose category", Toast.LENGTH_LONG).show();
			return passes;
			
		} else if (isEdit) {
        	// Delete original action if this is an edit
        	Log.i(TAG, "Deleting original action");
        	datasource.deleteAction(action.getId());
        }
		
		passes = true;	// passes all tests
		return passes;
	}
	
	public String getCategory() {
		Category cat;
		if (isEdit) {
			cat = datasource.getCategoryOfAction(action, DatabaseHelper.CATEGORY_TABLE_LEVEL);
			return cat.toString();
		} else
			return null;
	}
	
	@Override
	protected void onResume() {
		Log.i(TAG, "NewAction.onResume()");
		datasource = new DatabaseHelper(this);
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.i(TAG, "NewAction.onPause()");
		datasource.close();
		super.onPause();
	}

}