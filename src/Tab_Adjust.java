package ahp.thesis.code;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

/**
 * Tab_Adjust class for adjusting the weights in the 
 * AHP method process.
 * 
 * @author xaris08
 *
 */
public class Tab_Adjust extends Activity {

	private static final int CHANGE_NAME = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	private Database_Title dbHelper;
	private Cursor groupcursor, childcursor;
	private Button back;
	public ViewFlipper vf;
	private ListView list0, list1, list2, list3, list4;
	public int level = 0;
	private String item_checked;
	private TextView path;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_adjust);
		// Call a new instance of the database.
		dbHelper = new Database_Title(this);
		dbHelper.open();

		back = (Button) findViewById(R.id.back);
		back.setEnabled(false);
		vf = (ViewFlipper) findViewById(R.id.ViewFlipper01);
		list0 = (ListView) findViewById(R.id.list0);
		list1 = (ListView) findViewById(R.id.list1);
		list2 = (ListView) findViewById(R.id.list2);
		list3 = (ListView) findViewById(R.id.list3);
		list4 = (ListView) findViewById(R.id.list4);
		// Fill Data the very first time.
		fillPremiereData();
		// Showing the path.
		path = (TextView) findViewById(R.id.path);
		final String[] path_name = new String[3];
		path.setText("/");

		// Action performed by clicking the back button.
		back.setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				vf.showPrevious();
				String x = "";
				for (int i = 0; i < level - 1; i++) {
					x = x + "/" + path_name[i];
					path.setText(x);
				}
				--level;
				if (level == 0) {
					back.setEnabled(false);
					path.setText("/");
				}
			}
		});

		// onClick an Item in the first list
		list0.setOnItemClickListener(new OnItemClickListener() {
		
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				String item = arg0.getItemAtPosition(position).toString();
				path_name[0] = item;
				path.setText("/");
				item_checked = item;
				if (fillData(1, item)) {
					back.setEnabled(true);
					path.setText("/" + path_name[0]);
					level = 1;
					vf.showNext();
				}
			}
		});
		// onClick an Item in the second list
		list1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				String item = arg0.getItemAtPosition(position).toString();
				item_checked = item;
				if (fillData(2, item)) {
					path_name[1] = item;
					path.setText("/" + path_name[0] + "/" + path_name[1]);
					level = 2;
					vf.showNext();
				}
			}
		});
		// onClick an Item in the third list
		list2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				String item = arg0.getItemAtPosition(position).toString();
				item_checked = item;
				if (fillData(3, item)) {
					path_name[2] = item;
					path.setText("/" + path_name[0] + "/" + path_name[1] + "/"
							+ path_name[2]);
					level = 3;
					vf.showNext();
				}
			}
		});
		// onClick an Item in the fourth list
		list3.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				String item = arg0.getItemAtPosition(position).toString();
				item_checked = item;
				if (fillData(4, item)) {
					path_name[3] = item;
					path.setText("/" + path_name[0] + "/" + path_name[1] + "/"
							+ path_name[2] + "/" + path_name[3]);
					level = 4;
					vf.showNext();
				}
			}
		});
		// onClick an Item in the fifth list
		list4.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				Toast.makeText(Tab_Adjust.this,
						R.string.toast_no_further_level, Toast.LENGTH_SHORT)
						.show();
				back.setEnabled(true);
			}
		});

		// Long-pressed click for each list, in order to fill the weights.
		list0.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 0, R.string.change_name);
				menu.add(0, DELETE_ID, 0, R.string.delete_criteria);
			}

		});
		list1.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenu.ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 0, R.string.change_name);
				menu.add(0, DELETE_ID, 0, R.string.delete_criteria);
			}
		});
		list2.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenu.ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 0, R.string.change_name);
				menu.add(0, DELETE_ID, 0, R.string.delete_criteria);
			}
		});
		list3.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenu.ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 0, R.string.change_name);
				menu.add(0, DELETE_ID, 0, R.string.delete_criteria);
			}
		});
		list4.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenu.ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 0, R.string.change_name);
				menu.add(0, DELETE_ID, 0, R.string.delete_criteria);
			}
		});
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

	public void fillPremiereData() {
		String[] groups = getGroupColumn();
		while (level != 0) {
			vf.showPrevious();
			level--;
		}
		back.setEnabled(false);

		list0.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
				android.R.layout.simple_list_item_1, groups));
	}

	public boolean fillData(int level, String text) {
		String[] children = getChildColumn(text);
		switch (level) {
		case 0:
			String[] groups = getGroupColumn();
			list0.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
					android.R.layout.simple_list_item_1, groups));

		case 1:
			list1.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
					android.R.layout.simple_list_item_1, children));

		case 2:
			list2.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
					android.R.layout.simple_list_item_1, children));

		case 3:
			list3.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
					android.R.layout.simple_list_item_1, children));

		case 4:
			list4.setAdapter(new ArrayAdapter<String>(Tab_Adjust.this,
					android.R.layout.simple_list_item_1, children));

		}
		if (children.length == 0) {
			level--;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		String text = ((TextView) info.targetView).getText().toString();
		switch (item.getItemId()) {
		case DELETE_ID:
			onDeleteDialog(text);
			break;
		case CHANGE_NAME:
			onCreateDialog(text);
			break;
		default:
			return super.onContextItemSelected(item);
		}
		return true;
	}

	// Dialog for changing the name of a criteria.
	protected AlertDialog onCreateDialog(final String name) {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);
		alt_builder.setTitle(R.string.change_name).setCancelable(true)
		.setMessage(R.string.name).setIcon(R.drawable.alert);

		// Decision Name.
		final EditText edit_name = new EditText(getBaseContext());
		alt_builder.setView(edit_name);
		edit_name.setText(name);

		alt_builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {

						String new_name = edit_name.getText().toString();
						if (new_name.equals(null) || new_name.equals("")) {
							Toast toast = Toast.makeText(
									getApplicationContext(),
									R.string.toast_empty_name,
									Toast.LENGTH_SHORT);
							toast.show();
						} else {
							// Update the Row in the database if it is not the
							// same.
							if (!exists(new_name)) {
								dbHelper.updateCriteriaRow(new_name, name,
										Tab_Summary.DECISION);
								fillData(level, item_checked);
							} else {
								Toast.makeText(getApplicationContext(),
										R.string.toast_same_name_exists,
										Toast.LENGTH_SHORT).show();
								onCreateDialog(name);
							}

						}
					}
				}).setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {
				
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alt_builder.create();
		alert.show();
		return alert;
	}

	// Dialog for deleting a Criteria.
	protected AlertDialog onDeleteDialog(final String name) {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);
		alt_builder
				.setTitle(R.string.confirm_delete)
				.setCancelable(true)
				.setIcon(R.drawable.alert)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
						
							public void onClick(DialogInterface dialog,
									int which) {
								dbHelper.deleteCriteriaRow(name,
										Tab_Summary.DECISION);
								Toast.makeText(
										Tab_Adjust.this,
										name +" "+ getString(R.string.toast_deleted_correctly),
										Toast.LENGTH_SHORT).show();
								path.setText("/");
								fillPremiereData();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
					
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
							}
						});
		AlertDialog alert = alt_builder.create();
		alert.show();
		return alert;
	}

	// Return table for all groups.
	private String[] getGroupColumn() {

		groupcursor = dbHelper.GetGroups(Tab_Summary.DECISION);
		startManagingCursor(groupcursor);

		int count1 = groupcursor.getCount();
		String[] group = new String[count1];

		int i = 0;
		if (groupcursor.moveToFirst()) {
			do {
				group[i] = groupcursor.getString(groupcursor
						.getColumnIndex(Database_Title.CriteriaName));
				i++;
			} while (groupcursor.moveToNext());
		}
		return group;
	}

	// Return table for all children.
	private String[] getChildColumn(String group) {

		childcursor = dbHelper.GetChilds(group, Tab_Summary.DECISION);
		startManagingCursor(childcursor);

		int count2 = childcursor.getCount();
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

	@Override
	protected void onResume() {
		super.onResume();
		fillPremiereData();
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

	// Check if same criteria already exists in the database.
	public boolean exists(String name) {
		Cursor c = dbHelper.checkExistenceCriteria(name);
		startManagingCursor(c);
		String x = null;
		if (c.moveToFirst()) {
			x = c.getString(c.getColumnIndex(Database_Title.CriteriaName));
		}
		if (x == null) {
			return false;
		} else {
			return true;
		}
	}

}
