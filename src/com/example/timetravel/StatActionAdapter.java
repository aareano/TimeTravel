package com.example.timetravel;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StatActionAdapter extends ArrayAdapter<Action> {

	String TAG = "StatActionAdapter";
	ArrayList<Action> objects;
	Context context; 
    int layoutResourceId;    
	
	public StatActionAdapter(Context context, int layoutResourceId, ArrayList<Action> objects) {
		super(context, layoutResourceId, objects);
		this.layoutResourceId = layoutResourceId;
        this.context = context;
		this.objects = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// convert to local variable - why??
		View tbrow = convertView;
		
		// no idea what this is about...
		if (tbrow == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			tbrow = inflater.inflate(R.layout.item_action_stats, parent, false);
		}
		
		// get action
		Action a = objects.get(position);
		
		if (a != null) {
			Log.d(TAG, "Action.getName() = " + a.getName());
			
			// find TextViews
			TextView name = (TextView) tbrow.findViewById(R.id.name);
			TextView tT = (TextView) tbrow.findViewById(R.id.total_time);
			
			// name
			if (name != null)
				name.setText((CharSequence) a.getName());
			
			// total time
			if (tT != null) {
				long milsPerHour = 3600000;
				long totalMil = a.getEnd().getTimeInMillis() - a.getStart().getTimeInMillis();
				int totalHours = (int) (totalMil/milsPerHour);
				
				tT.setText((CharSequence) (totalHours + " hours"));
			}
		}
		return tbrow;
	}
}