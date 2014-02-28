package com.example.timetravel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * This is a really convoluted class...needs some cleanup. Maybe some out-sourcing?
 * 
 * @author Aaron
 *
 */
public class SortFragment extends Fragment {

	private final static String TAG = "TimeTravel";

	// call back variables
	private static Sort mCallback;
	
	public interface Sort {
		public boolean sortBy(String[] sortParams);
		// for any permutation, there are only every 2 options >> 2 options will be passed back
	}
	
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (Sort) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement Sort interface");
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_sort, container, false);
    }

	@Override
	public void onActivityCreated(Bundle saved) {
		super.onActivityCreated(saved);
		setSpinners();
    }

	/**
	 * Sets the spinners for the top choice, the manner for ordering "all", the 'recent' resolution (day, month, etc) 
	 * for "Recent", and the categories spinner for viewing a single category.
	 */
	private void setSpinners() {
		
		// Top spinner
		Spinner spinnerTOP = (Spinner) getActivity().findViewById(R.id.spinner_top);
		ArrayAdapter<CharSequence> adapterTOP = ArrayAdapter.createFromResource(getActivity(), R.array.top_sort_array,
				android.R.layout.simple_spinner_item);
		adapterTOP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerTOP.setAdapter(adapterTOP);
		spinnerTOP.setOnItemSelectedListener (new OnItemSelectedListener() {
		
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Retrieve selected item
				Object item = parent.getItemAtPosition(pos);
				String sortTOP = item.toString();
		
				// how: everything comes from the top. When a choice is picked, 
				// THEN set everything and make the right things visible.
				
				final String[] topSortArray = getResources().getStringArray(R.array.top_sort_array);
				final String[] allSortArray = getResources().getStringArray(R.array.sort_all_array);
				
				// ensure sub-choice views are GONE, they will be made VISIBLE as needed.
				getActivity().findViewById(R.id.tblrow_view_all).setVisibility(View.GONE);			// all: text, spinner
				getActivity().findViewById(R.id.tblrow_view_by_category).setVisibility(View.GONE);	// category: text, spinner
				getActivity().findViewById(R.id.tblrow_view_recent).setVisibility(View.GONE);		// recent: text, edittext, spinner
				
				
				// View "Current" -- default option				
				if (sortTOP.equals(topSortArray[0])) {
					// pass choice >> Activity >> ActionFragment
					Log.i(TAG, "sortTOP = " + topSortArray[0]);
					String[] sortParams = {topSortArray[0], null, null};
					sendParams(sortParams);
				}
				
				// View "All"
				if (sortTOP.equals(topSortArray[1])) {
					Log.i(TAG, "sortTOP = " + topSortArray[1]);
					
					// Set spinner
					Spinner spinnerALL = (Spinner) (getActivity().findViewById(R.id.spinner_org_all));
					ArrayAdapter<CharSequence> adapterALL = ArrayAdapter.createFromResource(getActivity(), R.array.sort_all_array,
							android.R.layout.simple_spinner_item);
					adapterALL.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerALL.setAdapter(adapterALL);
					spinnerALL.setOnItemSelectedListener (new OnItemSelectedListener() {
						
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
							// Retrieve selected item
							Object item = parent.getItemAtPosition(pos);
							String sortALL = item.toString();
							
							// Set appropriate views for each choice
							
							// Sort "chronologically"
							if (sortALL.equals(allSortArray[0])) {
								// pass choice >> Activity >> ActionFragment
								Log.i(TAG, "sortALL = " + allSortArray[0]);
								String[] sortParams = {topSortArray[1], allSortArray[0], null};
								sendParams(sortParams);
							}
							
							// Sort "alpha-numerically"
							if (sortALL.equals(allSortArray[1])) {
								// pass choice >> Activity >> ActionFragment
								Log.i(TAG, "sortALL = " + allSortArray[1]);
								String[] sortParams = {topSortArray[1], allSortArray[1], null};
								sendParams(sortParams);
							}	
						}
						
						// spinnerALL's method
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
						// Do nothing
					});					

					// Make text and spinner visible
					getActivity().findViewById(R.id.tblrow_view_all).setVisibility(View.VISIBLE);	// table row
				}
				
				// View "by category"
				if (sortTOP.equals(topSortArray[2])) {
					Log.i(TAG, "sortTOP = " + topSortArray[2]);
					// Category spinner (retrieves all possible categories and lists them by frequency of use)
					
					// get categories from database
					String tableName = DatabaseHelper.TABLE_ACTIONS;
					String[] columns = new String[] {
							DatabaseHelper.COLUMN_ID, 
							DatabaseHelper.COLUMN_CATEGORY };
					
			        // Return a cursor that points to all categories -- order from database, by last used
					DatabaseHelper datasource = new DatabaseHelper(getActivity());
			        Cursor cursor = datasource.proxyQuery(tableName, columns, null, null, null, null, null);
			        datasource.close();
			        Log.d(TAG, "Categories: " + cursor.getCount());
			        
			        Spinner spinnerCAT = (Spinner) (getActivity().findViewById(R.id.spinner_categories));
					final ArrayAdapter<CharSequence> adapterCAT = new ArrayAdapter<CharSequence>(getActivity(), android.R.layout.simple_spinner_item);
					
					// Get sorted list of categories
					List<String> sortedList = new ArrayList<String>();
					sortedList = sortCatList(cursor);
						
					// Add "Choose category..." option first
					final String chooseCategory = getResources().getString(R.string.choose_Category);
					adapterCAT.add(chooseCategory);

					// Add categories one by one
					if (sortedList != null) {
						Log.d(TAG, "fragment sortedList: " + sortedList.toString());
						for (int i = 0; i < sortedList.size(); i++) {
							adapterCAT.add(sortedList.get(i));
						}
					}
						
					adapterCAT.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerCAT.setAdapter(adapterCAT);
					spinnerCAT.setOnItemSelectedListener (new OnItemSelectedListener() {
					
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
							// Retrieve selected item
							Object item = parent.getItemAtPosition(pos);
							String singleCat = item.toString();
							
							String[] sortParams = {topSortArray[2], singleCat, null};
							if (!singleCat.equals(chooseCategory)) {
								Log.i(TAG, "singleCat = " + singleCat);
								sendParams(sortParams);
							}
						}
						
						// spinnerCAT's method
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
							// Do nothing
						});

					// make Categories' text and spinner visible
					getActivity().findViewById(R.id.tblrow_view_by_category).setVisibility(View.VISIBLE);	// table row
				}
				
				// View "a fixed time"
				if (sortTOP.equals(topSortArray[3])) {
					Log.i(TAG, "sortTOP = " + topSortArray[3]);
					// "Recent" (RESolution) spinner
					Spinner spinnerRES = (Spinner) (getActivity().findViewById(R.id.spinner_recent_res));
					ArrayAdapter<CharSequence> adapterRES = ArrayAdapter.createFromResource(getActivity(), R.array.recent_res_array,
							android.R.layout.simple_spinner_item);
					adapterRES.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinnerRES.setAdapter(adapterRES);
					spinnerRES.setOnItemSelectedListener (new OnItemSelectedListener() {
					
						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
							// Retrieve selected item
							Object item = parent.getItemAtPosition(pos);
							String recentRes = item.toString();
							Log.i(TAG, "recentRes = " + recentRes);
							
							String[] sortParams = {topSortArray[3], recentRes, null};
							
							setRecency(sortParams);
						}
						
						@Override
						public void onNothingSelected(AdapterView<?> arg0) {}
						// Do nothing
					});
					
					// Make Text, EditText, and Spinner visible
					getActivity().findViewById(R.id.tblrow_view_recent).setVisibility(View.VISIBLE);	// table row
				}
			}
			
			// spinnerTOP's method
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
			// Do nothing
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
	
	public void setRecency(final String[] sortParams) {
		
		final EditText howRecent = (EditText) getActivity().findViewById(R.id.how_recent);
		String recentNum = howRecent.getText().toString();
		
		// value already set
		if (recentNum != null && !recentNum.equals("")) {
			sortParams[2] = recentNum;
    		sendParams(sortParams);
		}
			
		// otherwise...
    	final String hint = "4";	// default value
		howRecent.setHint(hint);

		howRecent.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            	// save text every time it is changed
            	String recentNum = howRecent.getText().toString();
            	if (recentNum.equals(""))
            			recentNum = hint;	// default value
            	sortParams[2] = recentNum;
            	sendParams(sortParams);
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
	 * Passes parameterizations for sorting and displaying the actions (e.g. alphabetically, only since last week, etc.)
	 */
	public void sendParams(String[] sortParams) {
		boolean success = mCallback.sortBy(sortParams);
		if (success) {
			Toast toast = Toast.makeText(getActivity(), "Actions sorted", Toast.LENGTH_SHORT);  
			toast.setGravity(Gravity.TOP|Gravity.RIGHT, 0, 100);
			toast.show();
		}
	}
}
