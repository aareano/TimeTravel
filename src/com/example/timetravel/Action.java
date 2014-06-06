package com.example.timetravel;

import java.util.ArrayList;
import java.util.Calendar;

// I think category/sub should be in their own classes, not this one.


public class Action {

	public final static String TAG = "TimeTravel";
	private int id;
	private String name;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private Calendar start = Calendar.getInstance();
	private Calendar end = Calendar.getInstance();
	private Calendar timeCreated = Calendar.getInstance();
	
	
	// Constructors
	public Action() {
		
	}

	public Action(String name, String category, int catLevel, Calendar start, Calendar end) {
		this.name = name;
		categories.add(new Category(category, catLevel));
		this.start = start;
		this.end = end;
	}
	
	public Action(int id, String name, String category, int catLevel, Calendar start, Calendar end, Calendar timeCreated) {
		this.id = id;
		this.name = name;
		categories.add(new Category(category, catLevel));
		this.start = start;
		this.end = end;
		this.timeCreated = timeCreated;
	}
	
	
	/*
	// Ensures ActionRegistry mirrors what's in the database
	public static void updateActionRegistry(DatabaseHelper datasource) {
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
	
    
    
    
    // Get Actions
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
    */
    
    
    
    // Setters
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCategory(String category, int level) {
		categories.add(new Category(category, level));
	}
	public void setCategory(Category category) {
		categories.add(category);
	}
	
	public void setStart (long startMillis) {
		start.setTimeInMillis(startMillis);
	}
	
	public void setEnd (long endMillis) {
		end.setTimeInMillis(endMillis);
	}
	
	public void setTimeCreated (long timeCreatedMillis) {
		timeCreated.setTimeInMillis(timeCreatedMillis);
	}
	
	// Getters
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Category getCategory(int level) {
		
		for (int i = 0; i < categories.size(); i++) {
			if (categories.get(i).getLevel() == level)
				return categories.get(i);
		}
		
		// default
		return new Category(null, -1);
	}
	
	public Calendar getStart () {
		return start;
	}
	
	public Calendar getEnd () {
		return end;
	}
	
	public Calendar getTimeCreated() {
		return timeCreated;
	}
	
	@Override
	public String toString() {
		return name;
	}
}