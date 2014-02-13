package edu.upenn.cis350.graphics;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends View {

	protected Paint p;
	protected List<Float> points;
	protected Bitmap image;
	protected float image_x;
	protected float image_y;
	protected float vel_x;
	protected float vel_y;
	protected int score;
	protected int miss;
	protected boolean hit_flag;
	protected Context c;
	protected TextView scoreboard;
	protected boolean gameOver;
	
	public GameView(Context context) {
		super(context);
		init();
		c = context;
	}

	public GameView(Context context, AttributeSet as) {
		super(context, as);
		init();
		c = context;
	}

	private void init() {
		/* one-time initialization stuff */
		// Set background image
		setBackgroundResource(R.drawable.space);
		p = new Paint();
		p.setStrokeWidth(6);
		
		points = new ArrayList<Float>();
		// Set pony image 
		image = BitmapFactory.decodeResource(getResources(), 
				R.drawable.unicorn);
		image = Bitmap.createScaledBitmap(image, 150, 150, false);
		// Set initial position to x = -150 
		// and y = random between 200 and 400
		image_x = -150;
		image_y = (float)(Math.random() * 200) + 200;
		// Set constant x velocity of 10
		// and random y velocity from -10 to +10
		vel_x = 10;
		vel_y = (float)(Math.random() * 2 * vel_x) - vel_x;
		// Set score and misses to 0
		score = 0;
		miss = 0;
		// Set gameOver flag to false
		gameOver = false;
		// hit_flag is true only when an intersection occurs
		hit_flag = false;
		// Start the background task to move the pony after 20 ms
		new BackgroundTask().execute(20);
	}
	
	// Obtain reference to score TextView from GameActivity
	public void giveTV(TextView tv) {
		scoreboard = tv;
	}

	public void onDraw(Canvas c) {
		/* called each time this View is drawn */
		// If game over, don't draw anything
		if (gameOver)
			return;
		// Set pen color to red
		p.setColor(Color.RED);
		// Draw the image at current x and y positions
		c.drawBitmap(image, image_x, image_y, p);
		// Draw a line if there are points recorded
		if (!points.isEmpty()) {
			int size = points.size();
			float[] pts = new float[size];
			int k = 0;
			for (float n : points) {
				pts[k++] = n;
			}
			// Draw multiple lines between consecutive points
			for (int i = 0; i < size; i += 2) {
				if (i + 3 >= size)
					break;
				c.drawLine(pts[i], pts[i+1], pts[i+2], pts[i+3], p);
			}
		}
	}
	
	// Function to deal with touch actions
	public boolean onTouchEvent(MotionEvent event) {
		// If action down, start recording points
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x1 = event.getX();
			float y1 = event.getY();
			points.add(x1);
			points.add(y1);
			return true;
		}
		// If action move, continue to record points and draw line
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			float x1 = (int)event.getX();
			float y1 = (int)event.getY();
			points.add(x1);
			points.add(y1);
			invalidate();
			return true;
		}
		// If action up, check if intersection occurred
		// Also clear points and clear line
		if (event.getAction() == MotionEvent.ACTION_UP) {
			hit_flag = isIntersecting();
			points = new ArrayList<Float>();
			invalidate();
			// If an intersection occurred, record a hit and restart pony
			if (hit_flag) {
				hit_flag = false;
				image_x = -150;
				image_y = (float)(Math.random() * 200) + 200;
				vel_x += 4;
				vel_y = (float)(Math.random() * 2 * vel_x) - vel_x;
				// Increase score and reset miss streak
				score ++;
				miss = 0;
				// Update score board text to current score
				scoreboard.setText(Integer.toString(score));
				// If score = 5, game over, Toast, return to GameActivity
				if (score == 5) {
					gameOver = true;
					Toast.makeText(c, "You won!", 
    						Toast.LENGTH_LONG).show();
					GameActivity ga = (GameActivity) c;
					ga.onFinishButtonClick(this);
				}
			}
			return true;
		}
		return false;
	}
	
	// Checks if any of the points recorded intersect with the pony
	private boolean isIntersecting() {
		int size = points.size();
		float[] pts = new float[size];
		int k = 0;
		for (float n : points) {
			pts[k++] = n;
		}
		for (int i = 0; i < size; i += 2) {
			if (i + 1 >= size)
				break;
			if (pts[i] >= image_x && pts[i] <= (image_x + 150) &&
					pts[i+1] >= image_y && pts[i+1] < (image_y + 150))
				return true;
		}
		return false;
	}
	
	// Background task to move pony
	class BackgroundTask extends AsyncTask<Integer, Void, Integer> {

		protected Integer doInBackground(Integer... inputs) {
			try {
				// Sleep for as long as inputs[0]
				Thread.sleep(inputs[0]);
			} 
			catch (Exception e) {
				
			}
			// return the time waited
			return inputs[0];
		}
		
		protected void onPostExecute(Integer result) {
			// Move the image in the x and y directions
			image_x += vel_x;
			image_y += vel_y;
			// Draw the new image
			invalidate();
			// If image is out of the screen, reset and record a miss
			if (image_x >= getWidth() || image_y >= getHeight()
					|| image_y <= -150) {
				hit_flag = false;
				image_x = -150;
				image_y = (float)(Math.random() * 200) + 200;
				vel_x += 4;
				vel_y = (float)(Math.random() * 2 * vel_x) - vel_x;
				miss++;
				// If third miss, game over
				if (miss == 3) {
					gameOver = true;
					threeMiss();
				}
			}
			// If the game isn't over, keep shifting the pony
			if (!gameOver)
				new BackgroundTask().execute(20);
				
		}
	}
	
	// When three misses occur, Toast and return to GameActivity
	private void threeMiss() {
		Toast.makeText(c, "You lost!", 
				Toast.LENGTH_LONG).show();
		GameActivity ga = (GameActivity) c;
		ga.onFinishButtonClick(this);
	}
}