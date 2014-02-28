package com.example.timetravel;

import java.util.Calendar;

// I think category/sub should be in their own classes, not this one.


public class Action {

	public final static String TAG = "TimeTravel";
	// private static ArrayList<Action> ActionRegistry = new ArrayList<Action>();
	private int id;
	private String name;
	private String category;
	private String sub1Category;
	private String sub2Category;
	private Calendar start = Calendar.getInstance();
	private Calendar end = Calendar.getInstance();
	private Calendar timeCreated = Calendar.getInstance();
	
	
	// Constructors
	public Action() {
		
	}
	
	public Action(String setName, String setCategory, Calendar setStart, Calendar setEnd) {
		name = setName;
		category = setCategory;
		start = setStart;
		end = setEnd;
	}

	public Action(int setId, String setName, String setCategory, String setSub1Category, String setSub2Category, 
			Calendar setStart, Calendar setEnd, Calendar setWhenCreated) {
		id = setId;
		name = setName;
		category = setCategory;
		sub1Category = setSub1Category;
		sub2Category = setSub2Category;
		start = setStart;
		end = setEnd;
		timeCreated = setWhenCreated;
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
	
	public void setCategory(String category) {
		this.category = category;
	}
	public void setCategory(Category category) {
		this.category = category.toString();
	}
	
	public void setSub1Category(String sub1Category) {
		this.sub1Category = sub1Category;
	}

	public void setSub2Category(String sub2Category) {
		this.sub2Category = sub2Category;
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
	
	public String getCategory() {
		return category;
	}
	
	public String getSub1Category() {
		return sub1Category;
	}
	
	public String getSub2Category() {
		return sub2Category;
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