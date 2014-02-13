package edu.upenn.cis350.graphics;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class GameActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Obtain scoreboard TextView reference and send to GameView
		TextView tv = (TextView) findViewById(R.id.scoreboard);
        GameView gv = (GameView) findViewById(R.id.gameView);
        gv.giveTV(tv);
	}
	
	// This executes when game ends
	public void onFinishButtonClick(GameView view) {
		// Send back to MainActivity
		Intent i = new Intent();
		setResult(RESULT_OK, i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; 
		// this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

}
