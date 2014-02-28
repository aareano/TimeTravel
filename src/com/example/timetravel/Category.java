package com.example.timetravel;

import java.util.Calendar;


public class Category {

	//private String TAG = getResources().getString(R.string.TAG);
	private int id;
	private int level;		// cat = 0, subcat = 1, subsubcat = 2, etc.
	private String catName;
	private Calendar timeCreated;
	
	// constructors
	public Category() {
	}
	
	public Category(int id, String catName, String timeCreated) {
		this.id = id;
		this.catName = catName;
		this.timeCreated = Calendar.getInstance();
		this.timeCreated.setTimeInMillis(Long.parseLong(timeCreated));
	}
	
	// setters
	
	public void setId(int id) {
		this.id = id;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setCatName(String catName) {
		this.catName = catName;
	}
	
	public void setTimeCreated(String timeCreated) {
		this.timeCreated.setTimeInMillis(Long.parseLong(timeCreated));
	}
	
	// getters
	public int getId () {
		return this.id;
	}
	
	public int getLevel () {
		return this.level;
	}
	
	public String getCatName () {
		return this.catName;
	}
	
	public long getTimeCreated () {
		return timeCreated.getTimeInMillis();
	}

	@Override
	public String toString() {
		return catName;
	}
}