package ahp.thesis.code;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The class that presents the weights of the alternatives in a tab.
 * @author xaris08
 *
 */
public class Tab_Weights extends Activity {

	private static final String ALTERN1 = "altern1";
	private static final String ALTERN2 = "altern2";
	private static final String SEEK = "seek";

	private Database_Title dbHelper;
	private Spinner spinner;
	//private TextView title;
	private ListView list;
	private int sum = 0;
	private String[] adjust_rows;
	private Float[][] store;
	private Button save_button;
	private String criteria_selected;
	private ArrayList<HashMap<String, Object>> mylist;
	private boolean haveChilds;

	@Override
	public void onCreate(Bundle SavedInstance) {
		super.onCreate(SavedInstance);
		setContentView(R.layout.tab_weights);
		dbHelper = new Database_Title(this);
		dbHelper.open();

		spinner = (Spinner) findViewById(R.id.weights_spinner);
		fillSpinner();
		
		list = (ListView) findViewById(R.id.weight_list);
		save_button = (Button) findViewById(R.id.save_button);

		save_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(getApplicationContext(),
						R.string.toast_weights_saved, Toast.LENGTH_SHORT).show();

				float athrisma_stilis;
				Float[] A = new Float[sum];
				Float[] Weights = new Float[sum];
				for (int j = 0; j < sum; j++) {
					athrisma_stilis = 0;
					for (int i = 0; i < sum; i++) {
						athrisma_stilis += store[i][j];
					}
					A[j] = athrisma_stilis;
				}

				float x;
				for (int i = 0; i < sum; i++) {
					x = 0;
					for (int j = 0; j < sum; j++) {
						x += store[i][j] / A[j];
					}
					Weights[i] = x / sum;

					if (haveChilds == true) {
						dbHelper.updateWeight(Weights[i], Tab_Summary.DECISION,
								adjust_rows[i]);
					} else if (haveChilds == false) {
						dbHelper.fillCriteriaTables(adjust_rows[i],
								criteria_selected, Weights[i], Tab_Summary.DECISION);
					}
				}
			}
		});
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

	@Override
	protected void onResume() {
		fillSpinner();
		super.onResume();
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
	public void fillSpinner() {
		// Fill spinner with the group names.
		String[] allrows = getAllRows();

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, allrows);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	}

	// When you click an item in the spinner.
	public class MyOnItemSelectedListener implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view, int pos,
				long id) {
			String s = parent.getItemAtPosition(pos).toString();
			//title.setText(s);
			criteria_selected = s;

			mylist = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> map;

			if (haveChild(criteria_selected)) {
				haveChilds = true;
				adjust_rows = getChildren(s);
				store = new Float[sum][sum];
				for (int i = 0; i < sum; i++) {
					for (int j = sum - 1; j > i; j--) {
						map = new HashMap<String, Object>();
						map.put(ALTERN1, adjust_rows[i]);
						map.put(SEEK, "1");
						map.put(ALTERN2, adjust_rows[j]);
						mylist.add(map);
					}
				}
			} else if (!haveChild(criteria_selected)) {
				haveChilds = false;
				adjust_rows = getAllAlternatives();
				store = new Float[sum][sum];
				for (int i = 0; i < sum; i++) {
					for (int j = sum - 1; j > i; j--) {
						map = new HashMap<String, Object>();
						map.put(ALTERN1, adjust_rows[i]);
						map.put(SEEK, "1");
						map.put(ALTERN2, adjust_rows[j]);
						mylist.add(map);
					}
				}
			}
			// Make the whole screen scrollable
			int rows = (sum*sum - sum)/2;
			int height = 95 * rows;

			ViewGroup.LayoutParams params = list.getLayoutParams();
			params.height = height;
			list.setLayoutParams(params);
			list.requestLayout();
			
			list.setAdapter(new myListAdapter(mylist, Tab_Weights.this));
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// do nothing.
		}

		private class myListAdapter extends BaseAdapter {

			private ArrayList<HashMap<String, Object>> myList;
			private LayoutInflater mInflater;

			public myListAdapter(ArrayList<HashMap<String, Object>> list,
					Context context) {

				myList = list;
				mInflater = LayoutInflater.from(context);
			}

			public int getCount() {
				return myList.size();
			}

			public Object getItem(int position) {
				return myList.get(position);
			}

			public long getItemId(int position) {
				return position;
			}

			public View getView(int position, View convertView, ViewGroup parent) {
				// A ViewHolder keeps references to children views to avoid
				// unneccessary calls to findViewById() on each row.
				final ViewHolder holder;

				// When convertView is not null, we can reuse it directly, there
				// is no need to reinflate it. We only inflate a new View when
				// the convertView supplied by ListView is null
				if (convertView == null) {
					convertView = mInflater.inflate(R.layout.tab_weights_row,
							null);
					// Creates a ViewHolder and store references to the two
					// children views we want to bind data to.
					holder = new ViewHolder();
					holder.alt1 = (TextView) convertView
							.findViewById(R.id.subaltern1);
					holder.alt2 = (TextView) convertView
							.findViewById(R.id.subaltern2);
					holder.seekbar = (SeekBar) convertView
							.findViewById(R.id.seekbar);
					holder.seekbarvalue = (TextView) convertView
							.findViewById(R.id.seekbarvalue);
					convertView.setTag(holder);

				} else {
					// Get the ViewHolder back to get fast access to the
					// TextView and the ImageView.
					holder = (ViewHolder) convertView.getTag();
				}
				// Bind the data with the holder.
				holder.alt1.setText((String) myList.get(position).get(ALTERN1));
				holder.alt2.setText((String) myList.get(position).get(ALTERN2));
				holder.seekbarvalue.setText((String) myList.get(position).get(
						SEEK));

				holder.seekbar = (SeekBar) convertView
						.findViewById(R.id.seekbar);
				holder.seekbarvalue = (TextView) convertView
						.findViewById(R.id.seekbarvalue);
				holder.seekbarvalue.setText("0");
				
				holder.seekbar
						.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								holder.seekbarvalue.setText(String
										.valueOf(progress -9));
								float x = Float.valueOf(progress-9);
								for (int i = 0; i < sum; i++) {
									for (int j = sum - 1; j >= 0; j--) {
										store[i][j] = (float) 1;
										if (holder.alt1.getText() == adjust_rows[i]
												&& holder.alt2.getText() == adjust_rows[j]) {
											if (x < 0) {
												store[i][j] = Math.abs(1 / x);
											} else {
												store[i][j] = x;
											}
										} else if (holder.alt1.getText() == adjust_rows[j]
												&& holder.alt2.getText() == adjust_rows[i]) {
											if (x < 0) {
												store[i][j] = Math.abs(x);
											} else {
												store[i][j] = 1 / x;
											}
										} else if (i == j) {
											store[i][j] = (float) 1;
										}
									}
								}
							}

							public void onStartTrackingTouch(SeekBar seekBar) {
							}

							public void onStopTrackingTouch(SeekBar seekBar) {
							}
						});
				return convertView;
			}

			class ViewHolder {
				TextView alt1;
				TextView alt2;
				TextView seekbarvalue;
				SeekBar seekbar;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(R.string.explain);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);

		alt_builder
				.setTitle(R.string.tab_weight)
				.setCancelable(true)
				.setIcon(R.drawable.alert)
				.setMessage(R.string.explain_text)
				.setNeutralButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_builder.create();
		alert.show();		
		return super.onOptionsItemSelected(item);
	}

	private String[] getAllAlternatives() {

		Cursor c = dbHelper.GetAllAlternativeRows(Tab_Summary.DECISION);
		startManagingCursor(c);

		int count1 = c.getCount();
		sum = count1;
		String[] group = new String[count1];

		int i = 0;
		if (c.moveToFirst()) {
			do {
				group[i] = c.getString(c
						.getColumnIndex(Database_Title.AlternativeName));
				i++;
			} while (c.moveToNext());
		}
		return group;
	}

	// Return table for all Criteria.
	private String[] getAllRows() {

		Cursor c = dbHelper.GetAllCriteriaRows(Tab_Summary.DECISION);
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

	private boolean haveChild(String group) {

		Cursor c = dbHelper.GetChilds(group, Tab_Summary.DECISION);
		startManagingCursor(c);
		if (c.getCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	private String[] getChildren(String group) {

		Cursor childcursor = dbHelper.GetChilds(group, Tab_Summary.DECISION);
		startManagingCursor(childcursor);

		int count2 = childcursor.getCount();
		sum = count2;
		String[] child = new String[count2];

		int i = 0;
		if (childcursor.moveToFirst()) {
			do {
				child[i] = childcursor.getString(childcursor
						.getColumnIndex(Database_Title.CriteriaName));
				i++;
			} while (childcursor.moveToNext());
		}
		return child;
	}
}