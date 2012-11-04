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
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class to create and view the desired alternatives that will
 * be used throughout the AHP method process.
 * @author xaris08
 *
 */
public class Tab_Alternatives extends Activity {

	private Database_Title dbHelper;
	private AlertDialog alertDialog;
	private Cursor allCursors;
	private Button btn_add_alternative;
	private TextView text_count;
	private ListView listView;

	private static final int CHANGE_NAME = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_alternatives);
		dbHelper = new Database_Title(this);
		dbHelper.open();

		listView = (ListView) findViewById(R.id.altern_list);
		listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.setHeaderTitle(R.string.choose_task);
				menu.add(0, CHANGE_NAME, 1, R.string.change_name);
				menu.add(0, DELETE_ID, 1, R.string.delete_alternative);
			}
		});
		fillData();

		text_count = (TextView) findViewById(R.id.text_sum);
		countAlternatives();

		btn_add_alternative = (Button) findViewById(R.id.add_altern);
		btn_add_alternative.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				onCreateDialog();
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

	private void fillData() {

		allCursors = dbHelper.GetAllAlternativeRows(Tab_Summary.DECISION);
		startManagingCursor(allCursors);

		// Create an array to specify the fields we want to display in the list
		// (only name)
		String[] from2 = new String[] { Database_Title.AlternativeName };
		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to2 = new int[] { R.id.altern_text1 };
		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this,
				R.layout.tab_altern_row, allCursors, from2, to2);
		

		int rows = sca.getCount();
		int height = 70 * rows;

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = height;
		listView.setLayoutParams(params);
		listView.requestLayout();

		listView.setAdapter(sca);
	}

	/** Alternative Item pressed for long */
	/**@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.choose_task);
		menu.add(0, CHANGE_NAME, 1, R.string.change_name);
		menu.add(0, DELETE_ID, 1, R.string.delete_alternative);
	}**/

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String text = ((TextView) info.targetView).getText().toString();
		switch (item.getItemId()) {
		case DELETE_ID:
			onDeleteDialog(info.id);
			return true;
		case CHANGE_NAME:
			onUpdateDialog(text);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/** Dialog Box for adding an alternative */
	protected AlertDialog onCreateDialog() {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);

		alt_builder.setTitle(R.string.add_altern).setCancelable(true)
				.setIcon(R.drawable.alert)
				.setMessage(R.string.altern_dialog_message);

		// EditText for the Alternative name.
		final EditText edit_name = new EditText(getBaseContext());
		alt_builder.setView(edit_name);

		alt_builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						String textvalue = edit_name.getText().toString();
						if (!exists(textvalue)) {
							dbHelper.createRowAlternative(textvalue,
									Tab_Summary.DECISION);
							fillData();
							countAlternatives();
						} else {
							Toast.makeText(getApplicationContext(),
									R.string.toast_same_name_exists,
									Toast.LENGTH_SHORT).show();
							onCreateDialog();
						}
					}
				}).setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog = alt_builder.create();
		alertDialog.show();
		return alertDialog;
	}

	/** Pop-up dialog when pressed for long */
	protected AlertDialog onDeleteDialog(final long rowId) {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);
		alt_builder
				.setMessage(R.string.confirm_delete)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dbHelper.deleteAlternativeRow(rowId,
										Tab_Summary.DECISION);
								fillData();
								countAlternatives();
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

	// Dialog for changing the name of an alternative.
	protected AlertDialog onUpdateDialog(final String name) {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);

		alt_builder.setTitle(R.string.change_name).setCancelable(true)
				.setMessage(R.string.name).setIcon(R.drawable.alert);

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
							// Update the Row in the database.
							if (!exists(new_name)) {
								dbHelper.updateAlternativeRow(new_name, name,
										Tab_Summary.DECISION);
								fillData();
							} else {
								Toast.makeText(getApplicationContext(),
										R.string.toast_same_name_exists,
										Toast.LENGTH_SHORT).show();
								onUpdateDialog(name);
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

	/**
	 * Count all rows from alternative table and set the number in String.
	 **/
	private void countAlternatives() {
		Cursor c = dbHelper.GetAllAlternativeRows(Tab_Summary.DECISION);

		int i = c.getCount();
		String count = String.valueOf(i);

		if (count == null) {
			count = "0";
		}
		text_count.setText(count);
	}

	// Check if same name already exists in the deatabase.
	public boolean exists(String name) {
		Cursor c = dbHelper.checkExistenceAlternatives(name);
		startManagingCursor(c);
		String x = null;
		if (c.moveToFirst()) {
			x = c.getString(c.getColumnIndex(Database_Title.AlternativeName));
		}
		if (x == null) {
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
	/**
	 * public static class Utility { public static void
	 * setListViewHeightBasedOnChildren(ListView listView) { ListAdapter
	 * listAdapter = listView.getAdapter(); if (listAdapter == null) { return; }
	 * int totalHeight = 0; int desiredWidth =
	 * MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.AT_MOST);
	 * for (int i = 0; i < listAdapter.getCount(); i++) { View listItem =
	 * listAdapter.getView(i, null, listView); listItem.measure(desiredWidth,
	 * MeasureSpec.UNSPECIFIED); totalHeight += listItem.getMeasuredHeight(); }
	 * 
	 * ViewGroup.LayoutParams params = listView.getLayoutParams(); params.height
	 * = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() -
	 * 1)); listView.setLayoutParams(params); listView.requestLayout(); } }
	 **/
}