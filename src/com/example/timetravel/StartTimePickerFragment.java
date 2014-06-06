package com.example.timetravel;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

public class StartTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	private static String TAG = "TimeTravel";
	private static onStartTimeSetListener mCallback;
	
	int callCount = 0;   //To track number of calls to onTimeSet()
	
    	public interface onStartTimeSetListener {
    		void onStartTimeSet(int hourOfDay, int minute);
    	}
    	
	@Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (onStartTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onStartTimeSetListener");
        }
    }
    	
	/**
     * Create a new instance of StartTimePIckerFragment, providing 'Calendar startTime'
     * converted to milliseconds as an argument.
     */
    static StartTimePickerFragment newInstance(long startMillis) {
    	StartTimePickerFragment f = new StartTimePickerFragment();
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
        
        int hour 		 = c.get(Calendar.HOUR_OF_DAY);
        int minute 		 = c.get(Calendar.MINUTE);

        Log.i(TAG, "Creating StartTimePickerFragment...");
		
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
	
	@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	
		if(callCount == 1)    // On second call
        {
			mCallback.onStartTimeSet(hourOfDay, minute);
        }

        callCount ++;    // Incrementing call count.

    }
}