package ahp.thesis.code;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The main class from which the application starts 
 * and the first welcome screen is appeared.
 * 
 * @author xaris08
 *
 */
public class Main extends Activity implements View.OnTouchListener {

	private Database_Title dbHelper;
    
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		// Link to the dsslab site.
		TextView dsslab = (TextView) findViewById(R.id.dsslab);
		dsslab.setOnTouchListener(this);
		
		// Open menu automatically.
		new Handler().postDelayed(new Runnable() {
            
			public void run() {
                openOptionsMenu();
            }
        }, 600);

		// Language Button and changing language code.
		Button btn_en = (Button) findViewById(R.id.button_en);
        Button btn_gr = (Button) findViewById(R.id.button_gr);
        
        btn_en.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				
				 Locale locale = new Locale("en");
			     Locale.setDefault(locale);
			     Configuration config = new Configuration();
			     config.locale = locale;
			     getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
			     onConfigurationChanged(config);
			}
		});
        
        btn_gr.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				
				 Locale locale = new Locale("gr");
				 Locale.setDefault(locale);
			     Configuration config = new Configuration();
			     config.locale = locale;
			     getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
			     onConfigurationChanged(config); 
			}
		});
	}
	
	
	  public boolean onTouch(View v, MotionEvent event) { 
		Intent intent_website = new Intent(Intent.ACTION_VIEW);
		intent_website.setData(Uri.parse("http://dsslab.cs.unipi.gr"));
		startActivity(intent_website);
	    return true; 
	  } 
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	      // Refresh your activity with the new language.
	      super.onConfigurationChanged(newConfig);
	      //setContentView(R.layout.main);
	      finish();
	      Intent myIntent = new Intent(Main.this,Main.class);
	      Main.this.startActivity(myIntent);
	}
	 
	// Option Menu.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu1, menu);
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		try {
			this.finalize();
			finish();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Regarding the option pressed from 'menu' button,
	// it will do a specific task.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection.
		switch (item.getItemId()) {
		// 'Create a New Decision' button is pressed
		case R.id.create_decision:
			onCreateDialog();
			return true;
		// Edit an existing Decision	
		case R.id.edit_decision:
			Intent i = new Intent(Main.this, Decisions_List.class);
			startActivity(i);
			return true;
		// Informations 'About Me' button is pressed.
		case R.id.about_me:
			// Dialog Box with OK button to close.
			AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);

			alt_builder
					.setTitle(R.string.about_me)
					.setCancelable(true)
					.setIcon(R.drawable.alert)
					.setMessage(R.string.about_me_text)
					.setNeutralButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			alert = alt_builder.create();
			alert.show();
			return true;
		// 'Help' Button is pressed.
		case R.id.help:
			AlertDialog.Builder alt_builder2 = new AlertDialog.Builder(this);

			alt_builder2
					.setTitle(R.string.help)
					.setCancelable(true)
					.setIcon(R.drawable.alert)
					.setMessage(R.string.help_text)
					.setNeutralButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			alert = alt_builder2.create();
			alert.show();
			return true;
		// 'About_AHP' button is pressed.
		case R.id.about_app:
			// Dialog Box with 'OK' button to close.
			AlertDialog.Builder alt_builder3 = new AlertDialog.Builder(this);

			alt_builder3
					.setTitle(R.string.about_app_title)
					.setCancelable(true)
					.setIcon(R.drawable.alert)
					.setMessage(R.string.about_app_text)
					.setNeutralButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			alert = alt_builder3.create();
			alert.show();
			return true;		
		// Quit whole application.
		case R.id.quit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}// End of switch.
	}// End of option_item_selected.
	
	AlertDialog alert;
	
	// When you create a new decision, by pressing the right button
	protected AlertDialog onCreateDialog() {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);
			
			alt_builder.setTitle(R.string.create_dialog_title)
					.setCancelable(true)
					.setIcon(R.drawable.alert)
					.setMessage(R.string.create_dialog_message);

			// Start the database
			dbHelper = new Database_Title(Main.this);
			dbHelper.open();
			
			// Decision Name.
			final EditText edit_name = new EditText(getBaseContext());
			alt_builder.setView(edit_name);

			alt_builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {

							final String textvalue = edit_name.getText().toString();
							// set the format to sql date time
							Calendar cal = Calendar.getInstance();
							int year = cal.get(Calendar.YEAR);
							int month = cal.get(Calendar.MONTH);
							int day = cal.get(Calendar.DAY_OF_MONTH);
							String date = "" + day + "-" + month + "-" + year + "";
							
							if (textvalue.equals(null) || textvalue.equals("")) {
								Toast.makeText(getApplicationContext(), R.string.toast_empty_name, Toast.LENGTH_SHORT).show();
								onCreateDialog();
							} 
							else {
								// Insert row.								
								if (!exists(textvalue)){
								dbHelper.createRowCriteria(textvalue, "None...", textvalue);
								long mrowId = dbHelper.createRow(textvalue.trim(), date);
								
								Intent myIntent = new Intent(Main.this, Tab_Layout.class);
								
								Bundle bundle = new Bundle();
								bundle.putLong("rowId", mrowId);
								myIntent.putExtras(bundle);
								startActivity(myIntent);	
								} else {
									Toast.makeText(getApplicationContext(), R.string.toast_same_name_exists, Toast.LENGTH_SHORT).show();
									onCreateDialog();
								}
							}
						}
					}).setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			alert = alt_builder.create();
			alert.show();
			return alert;
	}
	
	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	    try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	// Check if same decision already exists in the database.
	public boolean exists(String name){
		Cursor c = dbHelper.checkExistence(name);
		startManagingCursor(c);
		String x = null;
		if (c.moveToFirst()) {
			x = c.getString(c.getColumnIndex(Database_Title.DecisionName));
		}
		if (x == null){
			return false;
		} else {
			return true;
		}
	}
	 
}
