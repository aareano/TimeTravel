package com.example.timetravel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

public class CreateCategoryDialog extends DialogFragment {

	public CreateCategoryListener mCallback;
	private String TAG = "CreateCategoryDialog";
	private EditText newCategory;
	
	interface CreateCategoryListener {
		public void onCreateCategory(String category);
	}
	
	@Override
	public void onAttach(Activity activity) {
		
		try {
			mCallback = (CreateCategoryListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException (activity.toString() + " must implement CreateCategoryDialog");
		}
		super.onAttach(activity);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// Get the layout inflater
    	LayoutInflater inflater = getActivity().getLayoutInflater();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		// set title
		builder.setTitle(R.string.create_category_title)
				
			// set view
			.setView(inflater.inflate(R.layout.dialogfragment_create_category, null))
			
			// Add the buttons
			.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button
					Log.d(TAG, "User clicked OK button");
				   
					String created = newCategory.getText().toString();
					Log.d(TAG, "category: " + created);
					
					if (created == null) {
					   Log.d(TAG, "EditText is empty");
					   Toast.makeText(getActivity(), "Create a category", Toast.LENGTH_SHORT).show();
					   return;
					} else {
						dismiss();
						mCallback.onCreateCategory(created);
						return;
					}
					}
				})
			.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog	
					Log.d(TAG, "User clicked cancel button");
					dismiss();
				}
			});
	    	
		// Create the AlertDialog
		Log.d(TAG, "building dialog...");
		return builder.create();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Get category edittext
		View v = inflater.inflate(R.layout.dialogfragment_create_category, container, false);
		newCategory = (EditText) v.findViewById(R.id.edit_new_category);
		newCategory.setText("text!");
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public static CreateCategoryDialog newInstance() {
		CreateCategoryDialog d = new CreateCategoryDialog();
		return d;
	}	
}