package com.example.timetravel;

import java.util.Calendar;

import com.example.timetravel.EndDatePickerFragment.onEndDateSetListener;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.TimePicker;

@SuppressWarnings("unused")
public class EndTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
	private static String TAG = "TimeTravel";
	private static onEndTimeSetListener mCallback;
	
	int callCount = 0;   //To track number of calls to onTimeSet()
	
    	public interface onEndTimeSetListener {
    		void onEndTimeSet(int hourOfDay, int minute);
    	}
    	
	@Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        Log.d("TimeTravel", "EndTimePicker onAttach");
        try {
            mCallback = (onEndTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onEndTimeSetListener");
        }
    }
	
	/**
     * Create a new instance of EndTimePIckerFragment, providing 'Calendar endTime'
     * converted to milliseconds as an argument.
     */
    static EndTimePickerFragment newInstance(long endMillis) {
    	EndTimePickerFragment f = new EndTimePickerFragment();
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
        
        Log.d("TimeTravel", "In EndTimePicker: " + c.getTime().toString());
        
		int hour 		 = c.get(Calendar.HOUR_OF_DAY);
        int minute	 	 = c.get(Calendar.MINUTE);
        
        Log.d("TimeTravel", "After decl. in EndTimePicker: " + c.getTime().toString());

        Log.i(TAG, "Creating EndTimePickerFragment...");
		
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }
	
	@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		if(callCount == 1)    // On second call
        {
			Log.i(TAG, "EndTimePicker setting time");
			mCallback.onEndTimeSet(hourOfDay, minute);
        }

        callCount ++;    // Incrementing call count.
    }
}
