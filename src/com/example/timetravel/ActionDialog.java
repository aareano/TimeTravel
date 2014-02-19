package com.example.timetravel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ActionDialog extends DialogFragment {
	
	public static String TAG = "TimeTravel";
	public DisplayActionDialog mCallback;
	public String mName;
	public Action mAction;
	public View mLayout;
	public Calendar startTime;
	public Calendar endTime;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateDialog(savedInstanceState);
	}

	public interface DisplayActionDialog {
		void editAction(Action mAction);
		void deleteAction(Action mAction);
	}
    	
	@Override
    public void onAttach (Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (DisplayActionDialog) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DisplayActionDialog");
        }
    }
	
	/**
     * Create a new instance of MyDialogFragment, providing string "name"
     * as an argument.
     */
    static ActionDialog newInstance(String name) {
    	ActionDialog f = new ActionDialog();
        Bundle args = new Bundle();
        // Use name instead of the id because the id would require 2 
        // searches in ActionRegistry instead of just 1.
        args.putString("name", name);
        f.setArguments(args);
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mName = getArguments().getString("name");
        
        // Get action object
        mAction = null;
		try {
			mAction = Action.findActionByName(mName);
			Log.d(TAG, "dialog mName = " + mAction.getName());
		} catch (NoActionFoundException e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
		}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	// Inflate the layout
    	View v = inflater.inflate(R.layout.dialogfragment_action_dialog, container, false);
    	mLayout = v;
    	setContent();
    	setButtons();
    	return v;
    }
    
    /**
     * Calls methods for managing the name, category, start and end times, 
     * and the status message and progress bar
     */
    public void setContent() {
    	setNameAndCategory();
    	setTimes();
    	setStatusAndProgress();
    }
    	
    /**
     * Prints the name and category
     */
    public void setNameAndCategory() {
    	// name
    	//((TextView) mLayout.findViewById(R.id.name)).setText(mAction.getName());
    	getDialog().setTitle(mAction.getName());
    	mLayout.findViewById(R.id.name).setVisibility(View.GONE);
    	
    	// category
    	((TextView) mLayout.findViewById(R.id.category)).setText(mAction.getCategory());
    }
    
    /**
     * Prints the start and end times
     */
    public void setTimes() {
    	// start time
    	startTime = mAction.getStartTime();
    	Date startDate = startTime.getTime(); 
    	SimpleDateFormat fmt = new SimpleDateFormat("MM/dd HH:mm", Locale.ENGLISH);		// used multiple times
    	String start = fmt.format(startDate);
    	((TextView) mLayout.findViewById(R.id.start_time)).setText(start);
    	
    	// end time
    	endTime = mAction.getEndTime();
    	Date endDate = endTime.getTime();
    	String end = fmt.format(endDate);
    	((TextView) mLayout.findViewById(R.id.end_time)).setText(end);
    }
    
    /**
     * Manages status message and the progress bar
     */
    public void setStatusAndProgress() {
    	Log.d(TAG, "setStatusAndProgess()");
    	// status
    	Calendar now = Calendar.getInstance();
    	
    	boolean started = true;
    	if (startTime.getTimeInMillis() > now.getTimeInMillis())
    		started = false;
    	
    	boolean complete = false;
    	// 0 if equal, -1 if now is before endTime, 1 if now is after endTime
    	switch (now.compareTo(endTime)) {
	    	case  0:  	complete = true;
	    				break;
	    	case -1:	complete = false;
	    				break;
	    	case  1:	complete = true;
	    				break;
    	}

    	// action can be started AND complete/in progress OR not started
    	if (started) {
	    	if (complete) {
	    		Log.d(TAG, "action complete");
	    		mLayout.findViewById(R.id.textView_complete).setVisibility(View.VISIBLE);
	    		mLayout.findViewById(R.id.textView_current).setVisibility(View.GONE);
	    		mLayout.findViewById(R.id.textView_not_yet_begun).setVisibility(View.GONE);
	    	} else {
	    		Log.d(TAG, "action in progress...");
	    		mLayout.findViewById(R.id.textView_complete).setVisibility(View.GONE);
	    		mLayout.findViewById(R.id.textView_current).setVisibility(View.VISIBLE);
	    		mLayout.findViewById(R.id.textView_not_yet_begun).setVisibility(View.GONE);
	    		
	    		// Progress bar
	    		final ProgressBar bar = (ProgressBar) mLayout.findViewById(R.id.progressBar);
	    		
	    		long startMillis = startTime.getTimeInMillis();
	    		long endMillis = endTime.getTimeInMillis();
	    		long nowMillis = now.getTimeInMillis();
	    		
	    		final long millisTotal = endMillis - startMillis;
	    		long millisSoFar = nowMillis - startMillis;		// problem here		make allowances for negatives (haven't started)
	    		long millisToGo = millisTotal - millisSoFar;
	    		
	    		Log.d(TAG, "endTime.getTimeInMillis() = " + endTime.getTimeInMillis());
	    		Log.d(TAG, "startTime.getTimeInMillis() = " + startTime.getTimeInMillis());
	    		Log.d(TAG, "millisSoFar: " + millisSoFar + ", millisTotal: " + millisTotal);
	    		Log.d(TAG, millisSoFar + " % " + millisTotal + " = " + millisSoFar%millisTotal);
	    		
	    		bar.setMax((int) millisTotal);	// set max "progress" as total number of milliseconds in action
	    		double progress = (millisSoFar);	// progress as a percent of 100
	    		bar.setProgress((int) progress);
	    		Log.d(TAG, "bar.setProgress(" + progress + ")");
	
	    	    /** 
	    	     * CountDownTimer starts with a percentage of progress and runs onTick every 1/2 second
	    	     */
	    	    new CountDownTimer(millisToGo, 500) { 
	
	    	        public void onTick(long millisUntilFinished) {
	    	    		double progress = millisTotal - millisUntilFinished;
	    	    		bar.setProgress((int) progress);
	    	    		//Log.d(TAG, "bar.setProgress(" + progress + ")");
	    	        }
	
	    	        public void onFinish() {
	    	        	Log.d(TAG, "OnFinish() >> complete");
	    	    		mLayout.findViewById(R.id.textView_complete).setVisibility(View.VISIBLE);
	    	    		mLayout.findViewById(R.id.textView_current).setVisibility(View.GONE);
	    	    		mLayout.findViewById(R.id.textView_not_yet_begun).setVisibility(View.GONE);
	    	        }
	    	    }.start();
	    	}
    	} else {
    		Log.d(TAG, "action not yet started");
    		mLayout.findViewById(R.id.textView_complete).setVisibility(View.GONE);
    		mLayout.findViewById(R.id.textView_current).setVisibility(View.GONE);
    		mLayout.findViewById(R.id.textView_not_yet_begun).setVisibility(View.VISIBLE);
    		
    		/**
    		 * Repeatedly checks to see if action has begun yet
    		 */
    		new Thread (new Runnable() {
    		    public void run() {
    		    	Log.d(TAG, "new thread");
    		    	int compared = -1;
    		    	while (compared == -1) {
    		    		Log.d(TAG, "check");
    		    		Calendar now = Calendar.getInstance();
    		    		// 0 if equal, -1 if now is before startTime, 1 if now is after startTime
    		    		compared = now.compareTo(startTime);
    		    		// pause for 5 seconds
    		    		try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
    		    	}
    		    	setStatusAndProgress();						// problem, can't access UI from worker thread
		    	}
		    }).start();
    	}
    }
    
    public void setButtons() {
    	Button edit = (Button) mLayout.findViewById(R.id.button_edit);
    	edit.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			mCallback.editAction(mAction);
    		}
    	});
    	
    	Button delete = (Button) mLayout.findViewById(R.id.button_delete);
    	delete.setOnClickListener(new OnClickListener() {
    		public void onClick(View v) {
    			mCallback.deleteAction(mAction);
    		}
    	});
    }
    
    @Override
    public void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    }
}
