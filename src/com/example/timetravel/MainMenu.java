package com.example.timetravel;

// This is my TODO list.
 
// Redesign database scheme - make sure this is easiest for retracting total time and such.
// View categories and edit the list of them
// Analytics!


import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainMenu extends Activity {
	
	public final String IsEDIT = "com.example.timetravel.IsEDIT";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		Log.i("TimeTravel", "MainMenu.onCreate()");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		return true;
	}

	public void onClickNewAct(View v) {
		Intent intent = new Intent(this, NewAction.class);
		intent.putExtra(IsEDIT, false);
		startActivity(intent);
	}
	
	public void onClickDisplayAction(View v) {
		Intent intent = new Intent(this, DisplayActions.class);
		startActivity(intent);
	}
	
	public void onClickStats(View v) {
		Intent intent = new Intent(this, Statistics.class);
		startActivity(intent);
	}
	
	public void onClickUpdate(View v) {
		DatabaseHelper datasource = new DatabaseHelper(this);
		
		datasource.setup();
		
		datasource.close();
		Toast.makeText(this, "Upgraded database", Toast.LENGTH_SHORT).show();
	}
}
