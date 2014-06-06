package com.example.timetravel;

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
 * tables: actions, categories, subcategories, etc, and action_categories
 * 		actions 	- contains actions
 * 		categories 	- contains categories
 * 		subcategories 	- contains subcategories
 *		action_categories	- contains action ids, category ids, and which category level the category id is from (category, subcategory, subsubcategory, etc.)
 *
 *
 *
 * - need to clean up other tables after removing the action...
 * 
 * source: http://www.androidhive.info/2013/09/android-sqlite-database-with-multiple-tables/
 */

// TODO db.close() for all methods

/*
 * methods:
 * DatabaseHelper()
 * close()
 * onCreate()
 * onUpgrade()
 * onDowngrade()
 * 
 * proxyQuery()
 * createNextSubTable()
 * getTotalLevels()
 * countLevels()
 * 
 * createAction()
 * getAction()
 * getAllActions()
 * updateAction()
 * deleteAction()
 * 
 * createCategory()
 * getCategory()
 * getSubCategories()
 * getCatsFromLevel()
 * categoryExists()
 * updateCategory()
 * deleteCategory()
 * 
 * createActionCategory()
 * getCategoryOfAction()
 * getActionsOfCategory()
 * 
 * getCategoryTable()
 * setup()
 */

public class DatabaseHelper extends SQLiteOpenHelper {
	
	// Logcat tag
    private static final String TAG = "DatabaseHelper";
 
    // Database Version
    private static final int DATABASE_VERSION = 6;
 
    // Database Name
    private static final String DATABASE_NAME = "actionManager";
    
    // Table Names
    static final String TABLE_ACTIONS = "actions";
    static final String TABLE_CATEGORIES = "categories";
    static final String TABLE_ACTION_CATEGORIES = "action_categories";
 
    //  Category table levels - integer representations of tables
    static final int CATEGORY_TABLE_LEVEL = 0;
    static int totalLevels = 1; 	// initiate counter
    
    // SUB CATEGORY column name
    static final String COLUMN_PARENT_CATEGORY_ID = "parent_id";
    
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
    static final String COLUMN_CATEGORY_LEVEL = "category_level";	// shows what table the category belongs to (cat, subcat, subsubcat)
    
	
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
            + COLUMN_PARENT_CATEGORY_ID + " INTEGER, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";

    // ACTION_CATEGORIES table create statement
    private static final String CREATE_TABLE_ACTION_CATEGORIES = "CREATE TABLE "
    		+ TABLE_ACTION_CATEGORIES + "("
    		+ COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_ACTION_ID + " INTEGER, "
    		+ COLUMN_CATEGORY_ID + " INTEGER, "
    		+ COLUMN_CATEGORY_LEVEL + " INTEGER, "
    		+ COLUMN_CREATED_AT + " DATETIME" + ")";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		totalLevels = getTotalLevels();
	}
	
	@Override
	public synchronized void close() {
		super.close();
	}

	@Override
    public void onCreate(SQLiteDatabase db) {
		Log.d(TAG, "DatabaseHelper.onCreate()");
	    
		// creating initial tables
		db.execSQL(CREATE_TABLE_ACTION);
		db.execSQL(CREATE_TABLE_CATEGORY);
		db.execSQL(CREATE_TABLE_ACTION_CATEGORIES);
    }
 	  
	// Called when Database version is different than current version.
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(TAG, "onUpgrade()");
		
		// Use string literals instead of variables >> variables will change as database updates
	    for (int i = oldVersion; i < newVersion; i++) {
	    	switch(i){
            	case 5:
            		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            		db.execSQL("DROP TABLE IF EXISTS actions");
            		db.execSQL("DROP TABLE IF EXISTS categories");
            		db.execSQL("DROP TABLE IF EXISTS action_categories");
            		break;
			    case 6:
		    		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
		    		db.execSQL("DROP TABLE IF EXISTS actions");
		    		db.execSQL("DROP TABLE IF EXISTS categories");
		    		db.execSQL("DROP TABLE IF EXISTS action_categories");
		    		break;
			    case 7:
		    		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
		    		db.execSQL("DROP TABLE IF EXISTS actions");
		    		db.execSQL("DROP TABLE IF EXISTS categories");
		    		db.execSQL("DROP TABLE IF EXISTS action_categories");
		    		break;
	    	}
	    }
	    onCreate(db);
	}
	
	// haven't tested this
	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		Log.d(TAG, "onDown()");
		
		// Use string literals instead of variables >> variables will change as database updates
	    for (int i = oldVersion; i > newVersion; i--) {
	    	switch(i){
            	case 4:
            		Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            		db.execSQL("DROP TABLE IF EXISTS actions");
            		db.execSQL("DROP TABLE IF EXISTS categories");
            		db.execSQL("DROP TABLE IF EXISTS actions_categories");
            		break;
	    	}
	    }
	    onCreate(db);
		
		super.onDowngrade(db, oldVersion, newVersion);
	}

	// should this be around? defeats the purpose, doesn't it?
	public Cursor proxyQuery(String table, String[] columns, String whereClause, String[] selectionArgs, 
			String groupBy, String having, String orderBy) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(table, columns, whereClause, selectionArgs, groupBy, having, orderBy);
		return cursor;
	}
	
	
	
	
	// Database
	
	// CRUD - C
	
	
	// NEED TO FIGURE OUT HOW TO MAKE TABLE NAME AND LEVEL# PUBLIC, STATIC, AND FINAL 
	/**
	 * Creates the table for the next layer of subcategories
	 */
	public void createNextSubTable() {
		SQLiteDatabase db = this.getWritableDatabase();
		totalLevels++;
		
		Log.d(TAG, "totalLevels = " + totalLevels);
		
		// e.g. sub_2_categories
		String tableName = "sub_" + (totalLevels - 1) + "_categories";
		
		Log.d(TAG, "tableName = " + tableName);
		
		String createStatement = "CREATE TABLE "
    		+ tableName + "(" 
    		+ COLUMN_ID + " INTEGER PRIMARY KEY, " 
    		+ COLUMN_CATEGORY + " TEXT, "
            + COLUMN_PARENT_CATEGORY_ID  + " INTEGER, "
            + COLUMN_CREATED_AT + " DATETIME" + ")";
		
		// create table
		db.execSQL(createStatement);
		
		// increment total levels counter
	}
	
	
	// CRUD - R
	
	/**
	 * @return the number of category levels (only top category = 1, two subcategories = 3, etc.)
	 */
	public int getTotalLevels() {
		// 2 non-category tables: action, action_categories
		return countTables() - 2 ;
	}
	
	/**
	 * @return the number of tables created manually (create statement)
	 */
	public int countTables() {
		Log.i(TAG, "DatabaseHelper.countTables()");
		SQLiteDatabase db = getReadableDatabase();
		
		int count = 0;
	    String selectQuery = "SELECT * FROM sqlite_master WHERE type = 'table' AND " +
	    		"name != 'android_metadata' AND name != 'sqlite_sequence'";
	    
	    Cursor cursor = db.rawQuery(selectQuery, null);
	    
	    count = cursor.getCount();
	    getReadableDatabase().close();
	    
	    cursor.close();
	    db.close();
	    return count;
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
		Log.i(TAG, "DatabaseHelper.createAction()");
		SQLiteDatabase db = this.getWritableDatabase();
		
	    ContentValues values = new ContentValues();
	    values.put(COLUMN_NAME, action.getName());
	    values.put(COLUMN_START, action.getStart().getTimeInMillis());
	    values.put(COLUMN_END, action.getEnd().getTimeInMillis());
	    values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
	    
	    // insert row
	    long action_id = db.insert(TABLE_ACTIONS, null, values);
	    
	    db.close();
	    return action_id;
	}
	
	
    
    // CRUD - R
	/**
     * This will fetch a single action
     */
    public Action getAction(int action_id) {
    	Log.i(TAG, "DatabaseHelper.getAction()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
    	// SELECT * FROM actions WHERE id = 1
    	String selectQuery = "SELECT * FROM " + TABLE_ACTIONS + " WHERE " + COLUMN_ID + " = " + action_id; 
    	Log.e(TAG, selectQuery);

    	Cursor cA = db.rawQuery(selectQuery, null);
    	
    	Action a = new Action();
    	if (cA.moveToFirst()) {
			
    		a.setId(cA.getInt(cA.getColumnIndex(COLUMN_ID)));
			a.setName(cA.getString(cA.getColumnIndex(COLUMN_NAME)));
			a.setStart(cA.getLong(cA.getColumnIndex(COLUMN_START)));
			a.setEnd(cA.getLong(cA.getColumnIndex(COLUMN_END)));
	    	a.setTimeCreated(cA.getLong(cA.getColumnIndex(COLUMN_CREATED_AT)));
    	}
    	
    	// find category
    	String findCategoryLevel = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY_LEVEL + ", " + COLUMN_CATEGORY_ID  
    			+ " FROM " + TABLE_ACTION_CATEGORIES + " WHERE " + COLUMN_ACTION_ID + " = " + a.getId(); 
    	Log.e(TAG, findCategoryLevel);
    	
    	Cursor cCL = db.rawQuery(findCategoryLevel, null);
    	
    	if (cCL.moveToFirst()) {
    		int catLevel = cCL.getInt(cCL.getColumnIndex(COLUMN_CATEGORY_LEVEL));
    		int catID = cCL.getInt(cCL.getColumnIndex(COLUMN_CATEGORY_ID));
    		
    		String catTable = getCategoryTable(catLevel);
    		
    		String findCategory = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY + " FROM " 
        			+ catTable + " WHERE " + COLUMN_CATEGORY_ID + " = " + catID;
        	Log.e(TAG, findCategory);
        	
        	Cursor cC = db.rawQuery(findCategory, null);
        	
        	if (cC.moveToFirst()) {
        		a.setCategory(cA.getString(cA.getColumnIndex(COLUMN_CATEGORY)), catLevel);
        		
        		cA.close();
        		cCL.close();
        		cC.close();
        		return a;
        	}
    	}
    	else {
    		// returns with no category
    		cA.close();
    		cCL.close();
    		return a; 
    	}
 
    	cA.close();
		cCL.close();
    	db.close(); 
    	
    	// default
    	return null;
    }
    
    /**
     * This will fetch all actions, returns without category, subcategories
     */
    public List<Action> getAllActions() {
    	Log.i(TAG, "DatabaseHelper.getAllActions()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
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
    	
    	c.close();
    	return actions;
    	
    }
    
    
    
    // CRUD - U
    /**
     * This will update the name, start, and end of an Action. Leaves the created_at timestamp alone
     */
    public int updateAction(Action action) {
    	Log.i(TAG, "DatabaseHelper.updateAction()");
		SQLiteDatabase db = this.getWritableDatabase();
    	
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
    	Log.i(TAG, "DatabaseHelper.deleteAction()");
		SQLiteDatabase db = this.getWritableDatabase();
    	
    	int action_id = action.getId();
        int rows = db.delete(TABLE_ACTIONS, COLUMN_ID + " = ?", new String[] { String.valueOf(action_id) });
        return rows;
    }
    /**
     * Deletes an action
     */
    public int deleteAction(int action_id) {
    	Log.i(TAG, "DatabaseHelper.deleteAction()");
		SQLiteDatabase db = this.getWritableDatabase();
    	
        int rows = db.delete(TABLE_ACTIONS, COLUMN_ID + " = ?", new String[] { String.valueOf(action_id) });
        return rows;
    }
    
    
    
    //  Category table
    
    // 	CRUD - C
    /**
     * Creates a category, requires that the category has a level
     * @return id of new row, or -1 if error occurred
     */
    // row ids are not zero based, unless explicitly declared as less than 1.
    public long createCategory(String category, int catLevel, int parentId) {
    	Log.i(TAG, "DatabaseHelper.createCategory()");
		SQLiteDatabase db = this.getWritableDatabase();
    	
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_PARENT_CATEGORY_ID, parentId);
        values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());
        
        String catTable = getCategoryTable(catLevel);
        
        long category_id = db.insert(catTable, null, values);
     
        return category_id;
    }
    
    
    
    // CRUD - R
    /**
     * Fetches a category, requires category to have a level
     * */
    public Category getCategory(Category category) {
    	Log.i(TAG, "DatabaseHelper.getCategory()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
    	String catTable = getCategoryTable(category.getLevel());
    	
        String selectQuery = "SELECT * FROM " + catTable + " WHERE " + COLUMN_ID + " = " + category.getId();
        
        Log.e(TAG, selectQuery);
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        Category cat = new Category();
        cat.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
        cat.setCatName(c.getString((c.getColumnIndex(COLUMN_CATEGORY))));
        
        c.close();
        return cat;
    }
    
    /**
     * Fetches a category
     * @params name - category name
     * @params catLevel - level of desired category
     */
    public Category getCategory(String name, int catLevel) {
    	Log.i(TAG, "DatabaseHelper.getCategory()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
    	String catTable = getCategoryTable(catLevel);
    	
    	String selectQuery = "SELECT * FROM " + catTable + " WHERE " + COLUMN_CATEGORY + " = '" + name + "'";
        
        Log.e(TAG, selectQuery);
        
        Cursor c = db.rawQuery(selectQuery, null);
        
        Category cat = new Category();
        if (c.moveToFirst()) {
        	c.moveToFirst();
	        
	        cat.setId(c.getInt(c.getColumnIndex(COLUMN_ID)));
	        cat.setCatName(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
	        cat.setLevel(catLevel);
	        cat.setTimeCreated(c.getLong(c.getColumnIndex(COLUMN_CREATED_AT)));
        }
        c.close();
        return cat;
    }
    
    /**
     * Fetches all direct subcategories of a category.
     * Usually getCategory() is called before this
     * 
     * @params category - the parent category
     */
    public ArrayList<Category> getSubCategories (String parentName, int parentLevel) {
    	Log.i(TAG, "DatabaseHelper.getSubCategories()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
    	List<Category> subcategories = new ArrayList<Category>();
    	Category category = getCategory(parentName, parentLevel);
    	
    	String catTable = getCategoryTable(category.getLevel() + 1); 	// we want the subcategories
    	
    	String selectQuery = "SELECT * FROM " + catTable + " WHERE " + COLUMN_PARENT_CATEGORY_ID + " = " + category.getId();
    	
    	Log.e(TAG, selectQuery);
        
        Cursor c = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
                cat.setCatName(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
                cat.setLevel(category.getLevel() + 1);
                cat.setTimeCreated(c.getLong(c.getColumnIndex(COLUMN_CREATED_AT)));
     
                // adding to tags list
                subcategories.add(cat);
            } while (c.moveToNext());
        }
    	
        c.close(); 
    	return (ArrayList<Category>) subcategories;
    }
    
    /**
     * Fetches all categories in the table corresponding to the catLevel
     **/
    // There's currently nothing preventing the return from being an ArrayList<String>
    public ArrayList<Category> getCatsFromLevel (int catLevel) {
    	SQLiteDatabase db = this.getReadableDatabase();
        Log.i(TAG, "DatabaseHelper.getCatsFromLevel()");
    	
    	List<Category> cats = new ArrayList<Category>();
        
        String catTable = getCategoryTable(catLevel);
        
        String selectQuery = "SELECT * FROM " + catTable;
     
        Log.e(TAG, selectQuery);
     
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
        
        c.close();
        db.close();
        return (ArrayList<Category>) cats;
    }
    
    /**
     * Checks if a category exists
     */
    public boolean categoryExists(String category, int catLevel) {
    	Log.i(TAG, "DatabaseHelper.categoryExists()");
		SQLiteDatabase db = this.getReadableDatabase();
    	
    	String catTable = getCategoryTable(catLevel);
    	
    	// SELECT _id, category FROM categories WHERE category = 'running';
    	String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY + " FROM " + catTable 
    			+ " WHERE " + COLUMN_CATEGORY + " = '" + category + "'";
    	
    	Cursor c = db.rawQuery(selectQuery, null);
    	
    	if (c.getCount() != 0)
    		return true;
    	
    	c.close();
    	return false;
    }
    
    
    // CRUD - U
    /**
     * Updates a category, subcategory, or subsubcategory
     */
    public int updateCat(Category category) {
    	Log.i(TAG, "DatabaseHelper.updateCategory()");
		SQLiteDatabase db = this.getWritableDatabase();
     
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
    public int deleteCategory(Category category) {
    	Log.i(TAG, "DatabaseHelper.deleteCategory()");
		SQLiteDatabase db = this.getWritableDatabase();
     
        String catTable = getCategoryTable(category.getLevel());
        
        // delete row
        int rowsAffected = db.delete(catTable, COLUMN_ID + " = ?", new String[] { String.valueOf(category.getId()) });
        
        return rowsAffected;
    }
    
    
    
    // action_category table
    
    // CRUD - C
    
    // Tested. works properly.
    /**
    * Creates action-category relationship  -  call this multiple times to relate an action to multiple categories
    * 
    * @param action, category, catTable
    * @return the row ID of the newly inserted row, or -1 if an error occurred 
    */
   public long createActionCategory(long action_id, long category_id, int catLevel) {
	   Log.i(TAG, "DatabaseHelper.createActionCategory()");
	   SQLiteDatabase db = this.getWritableDatabase();
	   
       ContentValues values = new ContentValues();
       values.put(COLUMN_ACTION_ID, action_id);
       values.put(COLUMN_CATEGORY_ID, category_id);
       values.put(COLUMN_CATEGORY_LEVEL, catLevel);
       values.put(COLUMN_CREATED_AT, Calendar.getInstance().getTimeInMillis());

       long id = db.insert(TABLE_ACTION_CATEGORIES, null, values);
       
       
    // TODO test stuff
       String query = "SELECT * FROM " + TABLE_ACTION_CATEGORIES;
       Cursor cursor = db.rawQuery(query, null);
       Log.d(TAG, "table_action_categories");
       cursor.moveToFirst();
       for (int i = 0; i < cursor.getCount(); i ++ ) {
    	   Log.d(TAG, "id: " + cursor.getInt(0) + " a_id = " + cursor.getInt(1) + " c_id = " + cursor.getInt(2) + " c_lev = " + cursor.getInt(3) + " crtd = " + cursor.getInt(4));
    	   cursor.moveToNext();
       }
       cursor.close();
       
       db.close();
       return id;
   }
   
   /**
    * Finds the Category of an Action
    * @param action
    * @return Arraylist of the categories of an action
    */
   public Category getCategoryOfAction (Action action, int catLevel) {
	   Log.i(TAG, "DatabaseHelper.getCategoryOfAction()");
	   SQLiteDatabase db = this.getReadableDatabase();
	  
	   // "SELECT id, categories FROM action_categories WHERE action_id = 4 AND category_table = categories"
	   String selectQuery = "SELECT " + COLUMN_ID + ", " + COLUMN_CATEGORY + " FROM " + TABLE_CATEGORIES + ", " 
			   + TABLE_ACTION_CATEGORIES + " WHERE " + COLUMN_ACTION_ID + " = " + action.getId() 
			   + " AND " + COLUMN_CATEGORY_LEVEL + " = " + catLevel;
	   
	   Cursor c = db.rawQuery(selectQuery, null);
	   
	   Category category = new Category();
	   
	   if (c.moveToFirst()) {
			   category.setId(c.getInt((c.getColumnIndex(COLUMN_ID))));
			   category.setCatName(c.getString(c.getColumnIndex(COLUMN_CATEGORY)));
	   	}
	   
	   c.close();
	   return category;
   }
   
   public ArrayList<Action> getActionsOfCategory(Category category) {
	   Log.i(TAG, "DatabaseHelper.getActionsOfCategory()");
	   SQLiteDatabase db = this.getReadableDatabase();
	   
	   String tableName = getCategoryTable(category.getLevel()); 
	   
	   // SELECT act.id, act.name, act.start, act.end, act.created_at 
	   // FROM actions act, action_categories ac, categories cat
	   // WHERE act.id = ac.act_id AND cat.id = ac.cat_id AND cat.category = 'running'
	   String selectQuery = "SELECT act." + COLUMN_ID + ", act." + COLUMN_NAME + ", act." + COLUMN_START + ", act."
			   				+ COLUMN_END + ", act." + COLUMN_CREATED_AT
			   		+ " FROM " + TABLE_ACTIONS + " act, " + TABLE_ACTION_CATEGORIES + " ac, " + tableName + " cat"
			   			+ " WHERE act." + COLUMN_ID + " = ac." + COLUMN_ACTION_ID 
			   				+ " AND cat." + COLUMN_ID + " = ac." + COLUMN_CATEGORY_ID 
			   				+ " AND cat." + COLUMN_CATEGORY + " = '" + category.getCatName() + "'";
			   
	   Log.e(TAG, selectQuery);
	   
	   Cursor c = db.rawQuery(selectQuery, null);
	   ArrayList<Action> actions = new ArrayList<Action>();
	    
	   //looping through all rows and adding to list
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
	   	
	   	// returns only basic action (see action class for definition)
	   	c.close();
	   	
	   	
	   	// TODO test stuff. doesn't work.
	   	String sql = "SELECT * FROM " + getCategoryTable(6) + " cat WHERE cat." + COLUMN_CATEGORY + " = '" + category.getCatName() + "'";
	   	Cursor cc = db.rawQuery(sql, null);
	   	cc.moveToFirst();
	   	Log.d(TAG, "sql: " + sql);
	   	for (int i = 0; i < cc.getCount(); i++) {
	   		Log.d(TAG, "cat.category = " + cc.getString(cc.getColumnIndex(COLUMN_CATEGORY)));
	   		cc.moveToNext();
	   	}
	   	cc.close();
	   	
	   	
	   	return actions;
   }
   
   /**
    * pass in a category level (e.g. 0, 1, 2, etc.) return the table name corresponding to that level
    * @param catLevel
    * @return
    */
   public String getCategoryTable(int catLevel) {
	   Log.i(TAG, "DatabaseHelper.getCategoryTable()");

	   if (catLevel == CATEGORY_TABLE_LEVEL)
		   return TABLE_CATEGORIES;
	   else if (catLevel < totalLevels) {
		   // e.g. sub_1_categoryies
		   String tableName = "sub_" + catLevel + "_categories";
		   return tableName;
	   }
	   
	   // TODO - need a better default plan
	   return null;
   }
   
   
   // just inserts test data
   // create three sub tables
   public void setup() {
	   Log.i(TAG, "DatabaseHelper.setup()");
	   SQLiteDatabase db = this.getWritableDatabase();
		
	   Log.d(TAG, "setup()");
	   
	   db.execSQL("DROP TABLE IF EXISTS actions");
	   db.execSQL("DROP TABLE IF EXISTS action_categories");
	   db.execSQL("DROP TABLE IF EXISTS categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_1_categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_2_categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_3_categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_4_categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_5_categories");
	   db.execSQL("DROP TABLE IF EXISTS sub_6_categories");
	   DatabaseHelper.totalLevels = 1;
	   
	   db.execSQL(CREATE_TABLE_ACTION);
	   db.execSQL(CREATE_TABLE_ACTION_CATEGORIES);
	   db.execSQL(CREATE_TABLE_CATEGORY);
	   
	   createNextSubTable();
	   createNextSubTable();
	   createNextSubTable();
	   createNextSubTable();
	   createNextSubTable();
	   createNextSubTable();
	   
	   long id1 = createCategory("Lord of the Rings", 0, -1);
	   
	   createCategory("Events", 1, 1);
	   createCategory("Places", 1, 1);
	   createCategory("Creatures", 1, 1);
	   	   
	   createCategory("The great battle", 2, 1);
	   createCategory("Ent moot", 2, 1);
	   long id2 = createCategory("Attach on Isengard", 2, 1);
	   createCategory("Reforge", 2, 1);
	   createCategory("Rally the dead", 2, 1);
	   createCategory("Mount doom", 2, 2);
	   long id2a = createCategory("Rohan", 2, 2);
	   createCategory("Oliphants", 2, 3);
	   createCategory("Balrog", 2, 3);
	   createCategory("Elves", 2, 3);
	   
	   createCategory("Treebeard", 3, 2);
	   createCategory("Merry", 3, 2);
	   createCategory("Pippen", 3, 2);
	   createCategory("Beech", 3, 2);
	   createCategory("Oak", 3, 2);
	   createCategory("Chestnut", 3, 2);
	   createCategory("Ash", 3, 2);
	   long id3 = createCategory("Mithril", 3, 9);
	   createCategory("Saruman crushed", 3, 7);
	   createCategory("Tortured", 3, 10);
	   
	   long id4 = createCategory("Perfected", 4, 10);
	   
	   long id5 = createCategory("Orcs", 5, 1);
	   
	   long id6 = createCategory("bat wack shiznit", 6, 1);
	   createCategory("it's time to start", 6, 1);
	   createCategory("ready go", 6, 1);
	   createCategory("kill the cow", 6, 1);
	   createCategory("the ultimate dance party", 6, 1);
	   createCategory("who needs the 3rd dimension when you have texting?", 6, 1);
	   createCategory("cones and rods", 6, 1);
	   
	   
	   Calendar c = Calendar.getInstance();
	   Calendar cc = Calendar.getInstance();
	   cc.set(Calendar.HOUR_OF_DAY, 7);
	   
	   Action a = new Action("Aragorn vs Sauron", "Lord of the Rings", 0, cc, c);
	   Action aa = new Action("Break Sauron's staff", "Attach on Isengard", 2, cc, c);
	   Action aaA = new Action("Release the river", "Attach on Isengard", 2, cc, c);
	   Action a3 = new Action("Breathe the free air", "Rohan", 2, cc, c);
	   Action a4 = new Action("Blue Man Group", "bat wack shiznit", 6, cc, c);
	   
       long action_id1 = createAction(a);
       long action_id2 = createAction(aa);
       long action_id2a = createAction(aaA);
       long action_id3 = createAction(a3);
       long action_id4 = createAction(a4);
       
       createActionCategory(action_id1, id1, DatabaseHelper.CATEGORY_TABLE_LEVEL);
       createActionCategory(action_id2, id2, 2);
       createActionCategory(action_id2a, id2, 2);
       createActionCategory(action_id3, id2a, 2);
       createActionCategory(action_id4, id6, 6);
       
       db.close();
   }
}