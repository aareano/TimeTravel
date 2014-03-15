package com.example.timetravel;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

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

import com.example.timetravel.ActionDialog.DisplayActionDialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

public class CategoryDialog extends DialogFragment {
	
	private static CategoryDialog mCallback; 
	
	private View mLayout;
	private static Action mAction;
	private static int mId;
	private ArrayList<Category> mAllCats;
	
	public interface onCategoriesSelected {
		void onSelect (ArrayList<String> categories);
	}

	
	@Override
	public void onAttach (Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (CategoryDialog) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement CategoryDialog");
		}
	}
    
	/**
     * Create a new instance of ActionDialog fragment, providing and action id and action name
     * as an argument.
     */
    static ActionDialog newInstance(Action action) {
    	ActionDialog f = new ActionDialog();
        Bundle args = new Bundle();

        args.putInt("id", action.getId());
        args.putString("name", action.getName());
        f.setArguments(args);
        return f;
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseHelper datasource = new DatabaseHelper(getActivity());
        
        mId = getArguments().getInt("id");
        
        // Get action object
        mAction = new Action();
		mAction = datasource.getAction(mId);

		// get the categories of the action
		// category table = 0
		int categoryLevel = 0;
		Category category = datasource.getCategoryOfAction(mAction, categoryLevel);
		
		mAction.setCategory(category);
		
		// get all categories
		mAllCats = datasource.getAllCats(categoryLevel);
		
		setCategoryList(mAllCats);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	// Inflate the layout
    	View v = inflater.inflate(R.layout.dialogfragment_category_dialog, container, false);
    	mLayout = v;
    	return v;
    }
	
    public void setCategoryList(ArrayList<Category> categoryList) {
    	List<String> categories = new ArrayList<String>();
    	for (int i = 0; i < categoryList.size(); i++) {
    		categories.add(categoryList.get(i).toString());
    	}
    	
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), 
    			R.layout.dialogfragment_category_dialog, R.id.category, categories);
        setAdapter(adapter);
    	
    }
}