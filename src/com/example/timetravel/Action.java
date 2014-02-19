package com.example.timetravel;

import java.util.ArrayList;
import java.util.Calendar;

import android.util.Log;

public class Action {

	public final static String TAG = "TimeTravel";
	private static ArrayList<Action> ActionRegistry = new ArrayList<Action>();
	private int id;
	private String name;
	private String category;
	private Calendar startTime;
	private Calendar endTime;
	private Calendar timeCreated;
	
	public Action(int setId, String setName, String setCategory, Calendar setStart, Calendar setEnd, Calendar setWhenCreated) {
		id = setId;		// Id is the row Id in the SQLite database (not the position in the ArrayList
		name = setName;
		category = setCategory;
		startTime = setStart;
		endTime = setEnd;
		timeCreated = setWhenCreated;
	}
	
	// The ActionRegistry is for use inside this class so that repetitive calls to the database don't have to be made.
	
	// Makes sure ActionRegistry mirrors what's in the database
	// Data about actions will be taken from the ActionRegistry instead of the database >> faster? :D
	public static void updateActionRegistry(ActionDataSource datasource) {
		ActionRegistry = datasource.getAllActions();
		Log.i(TAG, "ActionRegistry updated: " + ActionRegistry.size() + " Actions");
	}
	
	public static void addAction(Action action) {
        ActionRegistry.add(action);
    }
	
	// called by ActionDataSource after actual database deletion
    public static Action removeAction(int id) {
    	Action action = null;
    	for(int i = 0; i < ActionRegistry.size(); i++) {
    		action = ActionRegistry.get(i);
    		if (action.getId() == id);
    		ActionRegistry.remove(i);
    	}
    	Log.i(TAG, "removed from ActionRegistry");
    	return action;
    }
	
    public static Action findActionById(int id) throws NoActionFoundException {
    	
    	for (int index = 0; index < ActionRegistry.size(); index++) {
    		Action action = ActionRegistry.get(index);
    		if (action.getId() == id)
    			return action;
    	}
    		throw new NoActionFoundException ("No Action with ID " + id + " was found.");
    }
    
    public static Action findActionByName(String name) throws NoActionFoundException {
    	
    	for (int index = 0; index < ActionRegistry.size(); index++) {
    		Action action = ActionRegistry.get(index);
    		if (action.getName().equals(name))
    			return action;
    	}
    		throw new NoActionFoundException ("No Action with name " + name + " was found.");
    }
    
    public static ArrayList<Action> getAllActions() {
    	return ActionRegistry;
    }
    
    // Non-static methods
    
	// Id
	public int getId() {
		return id;
	}
	
	public void setId (int id) {
		this.id = id;
	}
	
	// name
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	// category
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	// start Time
	public Calendar getStartTime () {
		return startTime;
	}
	
	public void setStartTime (long startMillis) {
		startTime.setTimeInMillis(startMillis);
	}
	
	// end Time
	public Calendar getEndTime() {
		return endTime;
	}
	
	public void setEndTime (long endMillis) {
		endTime.setTimeInMillis(endMillis);
	}
	
	// Time created
	public Calendar TimeCreated() {
		return endTime;
	}
	
	public void setTimeCreated (long timeCreatedMillis) {
		timeCreated.setTimeInMillis(timeCreatedMillis);
		}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return name;
	}
}