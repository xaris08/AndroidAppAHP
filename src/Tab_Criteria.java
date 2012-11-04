package ahp.thesis.code;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A class to create and show the criteria needed.
 * @author xaris08
 *
 */
public class Tab_Criteria extends Activity{

	private String PARENT = "None...";
	private Button ok, clear;
	private TextView text_count;
	private Database_Title dbHelper;
	private Cursor c;
	private Spinner spin;
	private CheckBox checkbox;
	private EditText edit_name;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_criteria);
		dbHelper = new Database_Title(this);
		dbHelper.open();
		// Read each element needed from the layout.
		text_count = (TextView) findViewById(R.id.text_sum_crit);
		countCriteria();
		edit_name = (EditText) findViewById(R.id.add_criteria_edittext);
		checkbox = (CheckBox) findViewById(R.id.criteria_checkbox);
		spin = (Spinner) findViewById(R.id.spinner);
		spin.setEnabled(false);
		ok = (Button) findViewById(R.id.add_criteria_ok);
		clear = (Button) findViewById(R.id.add_criteria_clear);
		
		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				fillSpinner(spin);
			}
		});
		
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String textvalue = edit_name.getText().toString();
				if (textvalue.equals(null) || textvalue.equals("")) {
					Toast.makeText(getApplicationContext(), R.string.toast_empty_name, Toast.LENGTH_SHORT).show();
				}			
				else {
					if (!exists(textvalue)){
						if (PARENT == "None..."){
							PARENT = Tab_Summary.DECISION;
						}
						dbHelper.createRowCriteria(textvalue, PARENT ,Tab_Summary.DECISION);
						countCriteria();
						Toast.makeText(Tab_Criteria.this, R.string.toast_crit_created, Toast.LENGTH_SHORT).show();
						// Default values in everything.
						edit_name.setText("");
						checkbox.setChecked(false);
						spin.setPrompt("");
						} else {
							Toast.makeText(getApplicationContext(), R.string.toast_same_name_exists, Toast.LENGTH_SHORT).show();
						}					
				}
			}
		});
		clear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				edit_name.setText("");
				checkbox.setChecked(false);
				spin.setPrompt("");
			}
		});
	}

	 @Override
	 public void onResume(){
	 super.onResume();
	 	countCriteria();
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
	 
	/*** Create and fill the spinner (drop-down menu). **/
	public void fillSpinner(Spinner spinner) {
		if (checkbox.isChecked()) {
			// Make spinner available.
			spin.setEnabled(true);
			// Fill spinner with the group names.
			String[] allrows = getAllRows();

			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, allrows);
			spinnerArrayAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spinner.setAdapter(spinnerArrayAdapter);
			spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
		} else if (!checkbox.isChecked()) {
			String[] groups = { "None..." };
			spin.setEnabled(false);
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, groups);
			spinner.setAdapter(spinnerArrayAdapter);
		}
	}

	// When you click an item in the spinner.
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String s = parent.getItemAtPosition(pos).toString();
			PARENT = s;
		}
		public void onNothingSelected(AdapterView<?> parent) {
			PARENT = "None...";
		}
	}

	/**
	 * Count all rows from Criteria table and set the number in String.
	 **/
	private void countCriteria() {
		Cursor c = dbHelper.GetAllCriteriaRows(Tab_Summary.DECISION);
		int i = c.getCount();
		String count = String.valueOf(i);

		if (count == null) {
			count = "0";
		}
		text_count.setText(count);
	}
	
	// Return table for all Criteria.
	private String[] getAllRows() {

		c = dbHelper.GetAllCriteriaRows(Tab_Summary.DECISION);
		startManagingCursor(c);

		int count1 = c.getCount();
		String[] group = new String[count1];

		int i = 0;
		if (c.moveToFirst()) {
			do {
				group[i] = c.getString(c
						.getColumnIndex(Database_Title.CriteriaName));
				i++;
			} while (c.moveToNext());
		}
		return group;
	}
	
	// Check if same decision already exists in the database.
	public boolean exists(String name){
		Cursor c = dbHelper.checkExistenceCriteria(name);
		startManagingCursor(c);
		String x = null;
		if (c.moveToFirst()) {
			x = c.getString(c.getColumnIndex(Database_Title.CriteriaName));
		}
		if (x == null){
			return false;
		} else {
			return true;
		}
	}

	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	    dbHelper.close();
	    try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}