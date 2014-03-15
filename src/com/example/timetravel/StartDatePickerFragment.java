package com.example.timetravel;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

public class StartDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private static String TAG = "TimeTravel";
	private static onStartDateSetListener mCallback;
	
	public interface onStartDateSetListener {
		void onStartDateSet(int year, int month, int monthDay);
	}
	
	@Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onStartDateSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onStartDateSetListener");
        }
    }
	
	/**
     * Create a new instance of StartDatePIckerFragment, providing 'Calendar startTime'
     * converted to milliseconds as an argument.
     */
    static StartDatePickerFragment newInstance(long startMillis) {
    	StartDatePickerFragment f = new StartDatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("startMillis", startMillis);
        f.setArguments(args);
        return f;
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the passed in time as the default values for the picker
		long startMillis = getArguments().getLong("startMillis");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startMillis);
        
		int year 	= c.get(Calendar.YEAR);
		int month 	= c.get(Calendar.MONTH);
		int day 	= c.get(Calendar.DAY_OF_MONTH);
        
		Log.i(TAG, "Creating StartDatePickerFragment...");
		
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int monthDay) {
		mCallback.onStartDateSet(year, month, monthDay);
    };
	
}
