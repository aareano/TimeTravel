package com.example.timetravel;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * This guy does the actual SQL interacting with the database bit
 * Five tables: actions, categories, subcategories, subsubcategories, and action_categories
 * 		actions 	- contains actions
 * 		categories 	- contains categories
 * 		subcategories 	- contains subcategories
 * 		subsubcategories 	- contains subsubcategories
 *		action_categories	- contains action ids, category ids, and which category level the category id is from (category, subcategory, subsubcategory, etc.)
 *
 *
 * When things have the same name, that's when ids matter.
 *
 * - need to clean up other tables after removing the action...
 * 
 * source: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	
	// Logcat tag
    private static final String TAG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "actionManager";
    
    // Database variable
    private static SQLiteDatabase db;
    
    // Table Names
    static final String TABLE_ACTIONS = "actions";
    static final String TABLE_CATEGORIES = "categories";
    static final String TABLE_SUB_1_CATEGORIES = "sub_1_categories";
    static final String TABLE_SUB_2_CATEGORIES = "sub_2_categories";
    static final String TABLE_ACTION_CATEGORIES = "action_categories";
 
    //  Category table levels
    static final int CATEGORY_TABLE_LEVEL = 0;
    static final int SUB_1_CATEGORY_TABLE_LEVEL = 1;
    static final int SUB_2_CATEGORY_TABLE_LEVEL = 2;
    
    // Common column names
    static final String COLUMN_ID = "_id";
    static final String COLUMN_CREATED_AT = "created_at";
    static final String COLUMN_CATEGORY = "category";
    static final String COLUMN_ACTION_ID = "action_id";
 
    // ACTION Table - column names
    static final String COLUMN_NAME = "name";
    static final String COLUMN_START = "start";
    static final String COLUMN_END = "end";
    
    // ACTION_CATEGORIES Table - column names
    static final String COLUMN_CATEGORY_ID = "category_id";
    static final String COLUMN_CATEGORY_TABLE = "category_table";	// specifies which category table to talk to (cat, subcat, subsubcat)
    
	
    // Table Create Statements
    // ACTION table create statement
    private static final String CREATE_TABLE_ACTION = "CREATE TABLE "
            + TABLE_ACTIONS + "(" 
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_START + " DATETIME, "
            + COLUMN_END + " DATETIME, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";
    
    // CATEGORY table create statement
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "
            + TABLE_CATEGORIES + "(" 
            + COLUMN_ID + " INTEGER PRIMARY KEY, " 
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";
 
    // SUB_1_CATEGORY table create statement
    private static final String CREATE_TABLE_SUB_1_CATEGORY = "CREATE TABLE "
            + TABLE_SUB_1_CATEGORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, " 
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";
 
    // SUB_2_CATEGORY table create statement
    private static final String CREATE_TABLE_SUB_2_CATEGORY = "CREATE TABLE "
    		+ TABLE_SUB_2_CATEGORIES + "(" 
    		+ COLUMN_ID + " INTEGER PRIMARY KEY, " 
            + COLUMN_CATEGORY + " TEXT, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";

    // ACTION_CATEGORIES table create statement
    private static final String CREATE_TABLE_ACTION_CATEGORIES = "CREATE TABLE "
    		+ TABLE_ACTION_CATEGORIES + "("
    		+ COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_ACTION_ID + " INTEGER, "
    		+ COLUMN_CATEGORY_ID + " INTEGER, "
    		+ COLUMN_CATEGORY_TABLE + " TEXT,"+ ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		SQLiteDatabase db = this.getWritableDatabase();
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		Log.i(TAG, "DatabaseHelper onOpen()");
		super.onOpen(db);
	}
	
	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "onCreate()");
        // creating required tables
		db.execSQL("CREATE_TABLE_ACTION");
		db.execSQL("CREATE_TABLE_CATEGORY");
		db.execSQL("CREATE_TABLE_SUB_1_CATEGORY");
		db.execSQL("CREATE_TABLE_SUB_2_CATEGORY");
		db.execSQL("CREATE_TABLE_ACTION_CATEGORIES");
    }
 	  
	// Called when Database version is different than current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade()");
		
		// Use string literals instead of variables >> variables will change as database updates
	    for (int i = oldVersion; i < newVersion; i++) {
	    	switch(i){
            	case 1:
            		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            		db.execSQL("DROP TABLE IF EXISTS actions");
            		db.execSQL("DROP TABLE IF EXISTS categories");
            		db.execSQL("DROP TABLE IF EXISTS sub_1_categories");
            		db.execSQL("DROP TABLE IF EXISTS sub_2_categories");
            		db.execSQL("DROP TABLE IF EXISTS actions_categories");
            		break;
	    	}
	    }
	    onCreate(db);
	}
	
	// should this be around? defeats the purpose, doesn't it?
	public Cursor proxyQuery(String table, String[] columns, String whereClause, String[] selectionArgs, 
			String groupBy, String having, String orderBy) {
		SQLiteDatabase database = this.getWritableDatabase();
		
		Cursor cursor;
		cursor = database.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	
	// Action table
	
	// CRUD - C
	
	/**
	 * Creates an action.
	 * 
	 * @param action 	- the action to be created
	 * @return the database id of the row the action is saved in (-1 if an error occured)
	 */
	public long createAction(Action action) {
		
	    
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_NAME, action.getName());
	    values.put(COLUMN_START, action.getStart().getTimeInMillis());
	    values.put(COLUMN_END, action.getEnd().getTimeInMillis());
	    values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
	    
	    // insert row
	    long action_id = db.insert(TABLE_ACTIONS, null, values);
	    
	    return action_id;
	}
	
	
    
    // CRUD - R
	/**
     * This will fetch a single action
     */
    public Action getAction(Action action) {
    	
    	
    	int action_id = action.getId();
    	
    	// SELECT * FROM actions WHERE id = 1
    	String selectQuery = "SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COLUMN_ID + " = " + action_id; 
    	Log.e(TAG, selectQuery);

    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	if (c != null) {
			c.moveToFirst();
	    	
			Action a = new Action();
	    	a.setName(c.getString(1));
	    	a.setStart(c.getLong(2));
	    	a.setEnd(c.getLong(3));
	    	a.setTimeCreated(c.getLong(4));
    	
	    	// returns without category, subcategories
	    	return a;
    	}
    	return null;
    	
    }
	/**
     * This will fetch a single action
     */
    public Action getAction(int action_id) {
    	
    	
    	// SELECT * FROM actions WHERE id = 1
    	String selectQuery = "SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COLUMN_ID + " = " + action_id; 
    	Log.e(TAG, selectQuery);

    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	if (c != null) {
			c.moveToFirst();
	    	
			Action a = new Action();
	    	a.setName(c.getString(1));
	    	a.setStart(c.getLong(2));
	    	a.setEnd(c.getLong(3));
	    	a.setTimeCreated(c.getLong(4));
    	
	    	// returns without category, subcategories
	    	return a;
    	}
    	return null;
    	
    }
    
    /**
     * This will fetch all Actions in a single category, subcategory, **OR** subsubcategory.
     */
    public List<Action> getAction(Category category) {
    	
    	List<Action> actions = new ArrayList<Action>();
    	
    	String catTable = getCategoryTable(category.getLevel());
    	
    	// Select from category table
    	// SELECT * FROM actions acts, categories cats, action_categories ac, 
    	//		WHERE cats.category = ‘cathere’ AND cats.id = ac.category_id 
    	//		AND act.id = ac.action_id AND ac.category_table = "categories"
    	String selectQuery = "SELECT * FROM " + TABLE_ACTIONS + " acts, " 
    		+ TABLE_CATEGORIES + " cats, " + TABLE_ACTION_CATEGORIES 
    		+ " ac WHERE cats." + COLUMN_CATEGORY + " = '" + category.getCatName() 
    		+ "' AND cats." + COLUMN_ID + " = ac." + COLUMN_CATEGORY_ID
    		+ " AND acts." + COLUMN_ID + " = ac." + COLUMN_ACTION_ID 
    		+ " AND ac." + COLUMN_CATEGORY_TABLE + " = " + catTable;
    	
    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	// looping through all rows and adding to list
    	if (c.moveToFirst()) {
    		do {
    			Action a = new Action();
    			a.setName(c.getString(1));
    	    	a.setStart(c.getLong(2));
    	    	a.setEnd(c.getLong(3));
    	    	a.setTimeCreated(c.getLong(4));
    	    	
    	    	// adding to action list
    	    	actions.add(a);
    		} while (c.moveToNext());
    	}
    	
    	// returns without category, subcategories
    	return actions;
    }
    
    /**
     * This will fetch all actions
     */
    public List<Action> getAllActions() {
    	
    	List<Action> actions = new ArrayList<Action>();
    	
    	String selectQuery = "Select * FROM " + TABLE_ACTIONS; 
    	Log.e(TAG, selectQuery);

    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	// looping through all rows and adding to list
    	if (c.moveToFirst()) {
    		do {
    			Action a = new Action();
    	    	a.setName(c.getString(1));
    	    	a.setStart(c.getLong(2));
    	    	a.setEnd(c.getLong(3));
    	    	a.setTimeCreated(c.getLong(4));
    	    	
    	    	// adding to action list
    	    	actions.add(a);
    		} while (c.moveToNext());
    	}
    	
    	// returns without category, subcategories
    	return actions;
    	
    }
    
    
    
    // CRUD - U
    /**
     * This will update the name, start, and end of an Action. Leaves the created_at timestamp alone
     */
    public int updateAction(Action action) {
    	
    	
    	ContentValues values = new ContentValues();
    	values.put(COLUMN_NAME, action.getName());
    	values.put(COLUMN_START, action.getStart().getTimeInMillis());
    	values.put(COLUMN_END, action.getEnd().getTimeInMillis());
    	
    	// updating row
    	int rowsAffected = db.update(TABLE_ACTIONS, values, COLUMN_ID + " = ?", new String[] {String.valueOf(action.getId()) });
    	
    	return rowsAffected;
    }
    
    
    
    // CRUD - D
    // need to clean up other tables after removing the action...
    /**
     * Deletes an action
     */
    public int deleteAction(Action action) {
    	
    	int action_id = action.getId();
        int rows = db.delete(TABLE_ACTIONS, COLUMN_ID + " = ?", new String[] { String.valueOf(action_id) });
        return rows;
    }
    /**
     * Deletes an action
     */
    public int deleteAction(int action_id) {
    	
    	
        int rows = db.delete(TABLE_ACTIONS, COLUMN_ID + " = ?", new String[] { String.valueOf(action_id) });
        return rows;
    }
    
    
    
    //  Category table
    
    // 	CRUD - C
    /**
     * Creates a category, requires that the category has a level
     */
    public long createCategory(String category, int catLevel) {
    	
    	
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
     
        long category_id = 0;
        
        String catTable = getCategoryTable(catLevel);
        
        category_id = db.insert(catTable, null, values);
     
        return category_id;
    }
    
    
    
    // CRUD - R
    /**
     * Fetches a category, requires category to have a level
     * */
    public Category getCategory(Category category) {
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_ID + " = " + category.getId();
        
        Log.e(TAG, selectQuery);
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        
        Category cat = new Category();
        cat.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
        cat.setCatName(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
        
        return cat;
    }
    
    /**
     * Fetches all categories, subcategories, or subsubcategories
     **/
    // There's currently nothing preventing the return from being an ArrayList<String>
    public ArrayList<Category> getAllCats(int catLevel) {
        List<Category> cats = new ArrayList<Category>();
        
        String catTable = getCategoryTable(catLevel);
        
        String selectQuery = "SELECT  * FROM " + catTable;
     
        Log.e(TAG, selectQuery);
     
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                cat.setCatName(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
     
                // adding to tags list
                cats.add(cat);
            } while (c.moveToNext());
        }
        return (ArrayList<Category>) cats;
    }
    
    /**
     * Checks if a category exists
     */
    public boolean categoryExists(String category, int catLevel) {
    	
    	
    	String catTable = getCategoryTable(catLevel);
    	
    	String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY + " FROM " + catTable + " WHERE " + COLUMN_CATEGORY + " = " + category;
    	
    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	if (c.getCount() != 0)
    		return true;
    	
    	return false;
    }
    
    
    // CRUD - U
    /**
     * Updates a category, subcategory, or subsubcategory
     */
    public int updateCat(Category category) {
        
     
        String catTable = getCategoryTable(category.getLevel());
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category.getCatName());
        
        
        // updating row
        int rowsAffected = db.update(catTable, values, COLUMN_ID + " = ?", new String[] { String.valueOf(category.getId()) });
        return rowsAffected;
    }
    
    
    // CRUD - D
    /**
     * Deletes a category, subcategory, or subsubcategory
     */
    public int deleteCat(Category category) {
        
     
        String catTable = getCategoryTable(category.getLevel());
        
        // delete row
        int rowsAffected = db.delete(catTable, COLUMN_ID + " = ?", new String[] { String.valueOf(category.getId()) });
        
        return rowsAffected;
    }
    
    
    
    // action_category table
    
    // CRUD - C
    
    // Relate actions and categories
    /**
    * Creates action-category relationship  -  call this multiple times to relate an action to multiple categories
    * 
    * @param action, category, catTable
    * @return the row ID of the newly inserted row, or -1 if an error occurred 
    */
   public long createActionCategory(long action_id, long category_id, int catLevel) {
       

       String catTable = getCategoryTable(catLevel);
       
       ContentValues values = new ContentValues();
       values.put(COLUMN_ACTION_ID, action_id);
       values.put(COLUMN_CATEGORY_ID, category_id);
       values.put(COLUMN_CATEGORY_TABLE, catTable);
       values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());

       long id = db.insert(TABLE_ACTION_CATEGORIES, null, values);

       return id;
   }
   
   /**
    * "SELECT id, categories FROM action_categories WHERE action_id = 4 AND category_table = categories"
    * @param action
    * @return Arraylist of the categories of an action
    */
   public Category getCategoryOfAction (Action action, int catLevel) {
	   
	   
	   String catTable = getCategoryTable(catLevel);
	   
	   String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY + " FROM " + TABLE_ACTION_CATEGORIES 
			   + " WHERE " + COLUMN_ACTION_ID + " = " + action.getId() + " AND " + COLUMN_CATEGORY_TABLE + " = " + catTable;
	   
	   Cursor c = db.rawQuery(selectQuery, null);
	   
	   Category category = new Category();
	   
	   if (c.moveToFirst()) {
			   category.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
			   category.setCatName(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
	   	}
	   return category;
   }
   
   /**
    * pass in a category level (e.g. 0, 1, 2, etc.) return the table name corresponding to that level
    * @param catLevel
    * @return
    */
   public String getCategoryTable(int catLevel) {
	   
	   if (catLevel == CATEGORY_TABLE_LEVEL)
		   return TABLE_CATEGORIES;
	   else if (catLevel == SUB_1_CATEGORY_TABLE_LEVEL)
		   return TABLE_SUB_1_CATEGORIES;
	   else if (catLevel == SUB_2_CATEGORY_TABLE_LEVEL)
		   return TABLE_SUB_2_CATEGORIES;
	   
	   // default
	   return "";
   }
}