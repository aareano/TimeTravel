package com.example.timetravel;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

public class EndDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private static String TAG = "TimeTravel";
	private static onEndDateSetListener mCallback;

	int callCount = 0;   //To track number of calls to onDateSet()
	
	// Container Activity must implement this interface, Callback interface
	public interface onEndDateSetListener {
		void onEndDateSet(int year, int month, int monthDay);
	}
    
    @Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onEndDateSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onEndDateSetListener");
        }
    }
	
    /**
     * Create a new instance of EndDatePIckerFragment, providing 'Calendar endTime'
     * converted to milliseconds as an argument.
     */
    static EndDatePickerFragment newInstance(long endMillis) {
    	EndDatePickerFragment f = new EndDatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("endMillis", endMillis);
        f.setArguments(args);
        return f;
    }
    
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the passed in time as the default values for the picker
		long endMillis = getArguments().getLong("endMillis");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(endMillis);
        
        int year 	= c.get(Calendar.YEAR);
        int month 	= c.get(Calendar.MONTH);
        int day 	= c.get(Calendar.DAY_OF_MONTH);

        Log.i(TAG, "Creating EndDatePickerFragment...");
		
        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
	
	@Override
    public void onDateSet(DatePicker view, int year, int month, int monthDay) {
		
		if(callCount == 1)    // On second call
        {
			mCallback.onEndDateSet(year, month, monthDay);
        }

        callCount ++;    // Incrementing call count.
    }
}