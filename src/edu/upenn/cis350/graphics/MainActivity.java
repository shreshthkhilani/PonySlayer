package edu.upenn.cis350.graphics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {

	public static final int ButtonClickActivity_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Start at Main Menu
        setContentView(R.layout.main_menu);
    }
    
    public void onLaunchButtonClick(View v) {
    	// When "Play!" is clicked, start a GameActivity
    	Intent i = new Intent(this, GameActivity.class);
        startActivityForResult(i, ButtonClickActivity_ID);
    }
    
    // Function executed when GameView returns a result
    protected void onActivityResult(int requestCode, int resultCode, 
    		Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	setContentView(R.layout.main_menu);
    }
    
    // Executed when "Quit" is clicked
    public void onEndButtonClick(View v) {
    	// MainActivity finished
    	Intent i = new Intent();
    	setResult(RESULT_OK, i);
    	finish();
    }
    
}