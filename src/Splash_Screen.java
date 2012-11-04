package ahp.thesis.code;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * The splash screen with DSS logo when the application starts. 
 * @author xaris08
 *
 */
public class Splash_Screen extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 2500;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.splash_screen);

		/*
		 * New Handler to start the Menu-Activity
		 * and close this Splash-Screen after some seconds.
		 */	
		new Handler().postDelayed(new Runnable() {

			public void run() {
				/* Create an Intent that will start the Menu-Activity. */
				Intent mainIntent = new Intent(Splash_Screen.this, Main.class);
				Splash_Screen.this.startActivity(mainIntent);
				Splash_Screen.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);

	}
}
