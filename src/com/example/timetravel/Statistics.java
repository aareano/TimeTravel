package com.example.timetravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class Statistics extends FragmentActivity {

	private String TAG = "Statistics";
	private DatabaseHelper datasource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_statistics);
		
		datasource = new DatabaseHelper(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.statistics_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d(TAG, "Clicked a ActionBar button");
		boolean saved = false;
		switch (item.getItemId()) {
            case android.R.id.home:
            	// Navigate up
            	// DOESN'T WORK ON DEVICE
                //NavUtils.navigateUpFromSameTask(this);
                Log.d(TAG, "Navigating up from NewAction");
                break;
		}
		Intent intent = new Intent(this, MainMenu.class);
		startActivity(intent);
        return super.onOptionsItemSelected(item);
	}

	
	
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	
}
