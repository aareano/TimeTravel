package com.example.timetravel;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

/*
 * what I'm imagining is that there will be a checklist of categories and swiping to the left with bring 
 * up subcategories, subsubcat., etc. 
 * 
 * in settings, similar thing, swiping further will create new tables for further resolution.
 */


// things to do here: 
// look up how to have a dialog and list fragment
// check the boxes for applicable categories
// create swipe navigation
// replace spinner with link to this dialog




// NEED TO PASS IN ISEDIT TO DETERMINE WHETHER ITS SAFE TO ACCESS ACTION ID


public class CategoryDialog extends DialogFragment {

	private static String TAG = "CategoryDialog";
	private DatabaseHelper datasource;
	
	private static onCategorySelected mCallback;
	private String mCategorySelected;
	private int mSelectedPos;
	private ArrayList<Category> categories;
	
	private String newCat = "New category";
	
	public interface onCategorySelected {
		public void onDialogPositiveClick(DialogFragment dialog, Category category);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void showCreateCategoryDialog();
        public String getCategory();
	}

	
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "onAttach()");
		try {
			mCallback = (onCategorySelected) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement onCategorySelecteds");
		}
	}
    
	
	
	/**
     * Create a new instance of ActionDialog fragment, providing and action id and action name
     * as an argument.
     */
    static CategoryDialog newInstance(boolean isEdit) {
    	Log.d(TAG, "newInstance()");
    	CategoryDialog f = new CategoryDialog();
        Bundle args = new Bundle();

        args.putBoolean("isEdit", isEdit);
        f.setArguments(args);
        return f;
    }
    
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		datasource = new DatabaseHelper(getActivity());
		super.onCreate(savedInstanceState);
	}

	// NEED TO ALLOW FOR NULL CURSOR
    @Override 
    public Dialog onCreateDialog(Bundle savedInstanceStates) {
    
    	Log.d(TAG, "onCreateDialog()");
    	
    	final int NONE_SELECTED = -1;
       	
       	// TODO swiping levels will increment here
    	int catLevel = 0;
    	
    	
    	categories = datasource.getCatsFromLevel(catLevel);
    	
    	
    	// find item to check (-1 if none)
    	mSelectedPos = NONE_SELECTED;
    	if (getArguments().getBoolean("isEdit")) {
            
    		
    		String category = mCallback.getCategory();
    		Log.i(TAG, "current category: " + category);
    		
    		
    		if (category != null) {
    			for (int i = 0; i < categories.size(); i++) {
	    		
    				// if the category and action categories equals
	    			if (categories.get(i).getCatName().equals(category))
	    				mSelectedPos = i;	// TODO seems like a non-robust way of finding the position
    			}
    		}
    	}
    	
    	
    	// transfer categories into an array for dialog builder
    	final String[] items = new String[categories.size()];		// plus one for new category
		for (int i = 0; i < categories.size(); i++) {
				items[i] = categories.get(i).getCatName();
		}
      
		// Get the layout inflater
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	
    	
		
      	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	
	    	// Add title
	    	builder.setTitle(R.string.pick_category_title)
	    	
	    		// Inflate and set the layout for the dialog
	    		// Pass null as the parent view because its going in the dialog layout
	    		.setView(inflater.inflate(R.layout.dialogfragment_category_list, null))
	    		
	    		// Add list
	    		.setSingleChoiceItems(items, mSelectedPos,
	    				new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int id) {
					mSelectedPos = id;
					
					
					// get the category string from cursor
					if (id < items.length) {
						mCategorySelected = items[id];
					} else if (id == items.length)
						mCategorySelected = newCat;
					
					// If "create new category" was selected, dismiss dialog and open new category dialog
					if (mCategorySelected.equals(newCat)) {
 	        		   dismiss();
 	        		   mCallback.showCreateCategoryDialog();
					}
					Log.d(TAG, "item id = " + id + ", and category = " + mCategorySelected);
				}
	           })
	           
	           // Add the buttons
	           .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	        	   public void onClick(DialogInterface dialog, int id) {
	        		   // User clicked OK button
	        		   Log.d(TAG, "User clicked OK button");
	        		   
    	        	   if (mSelectedPos == NONE_SELECTED) {
    	        		   Log.d(TAG, "No selection made");
    	        		   Toast.makeText(getActivity(), "No selection made", Toast.LENGTH_SHORT).show();
    	        	   } else if (mCategorySelected != null) {
    	        		   
    	        		   	Category category = new Category();
    	        		   	for (int i = 0; i < categories.size(); i++) {
    	        		   		if (categories.get(i).getCatName().equals(mCategorySelected))
    	        		   			category = categories.get(i); 
    	        		   	}
    	        		   
    	        		    Log.d(TAG, "selection made: " + mCategorySelected);
    	        	   		mCallback.onDialogPositiveClick(CategoryDialog.this, category);
    	        	   		dismiss();
    	        	   }
    	           }
    	       })
    		   .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	               // User cancelled the dialog
    	        	   Log.d(TAG, "User clicked cancel button");
    	        	   mCallback.onDialogNegativeClick(CategoryDialog.this);
    	        	   dismiss();
    	           }
    		   });
    	// Create the AlertDialog
    	return builder.create();
    }

	@Override
	public void onDismiss(DialogInterface dialog) {
		super.onDismiss(dialog);
	}
}