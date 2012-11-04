package ahp.thesis.code;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A class that all the previously created decisions are being listed.
 * @author xaris08
 *
 */
public class Decisions_List extends ListActivity {

	private Database_Title dbHelper;
	private Cursor allCursors;

	private static final int CHANGE_NAME = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.decision_list);
		
		fillData();
		registerForContextMenu(getListView());
	}

	private void fillData() {
		dbHelper = new Database_Title(this);
		dbHelper.open();
		
		allCursors = dbHelper.GetAllRows();
		startManagingCursor(allCursors);

		// Create an array to specify the fields we want to display in the list
		// (only title)
		String[] from = new String[] { Database_Title.DecisionName };
		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.text1 };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter titles = new SimpleCursorAdapter(this,
				R.layout.decision_row, allCursors, from, to);
		setListAdapter(titles);
	}

	// Menu appeared when decision pressed for long.
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.choose_task);
		menu.add(0, CHANGE_NAME, 0, R.string.change_name);
		menu.add(0, DELETE_ID, 0, R.string.delete_decision);
	}

	// When the context menu is selected to delete a decision.
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String text = ((TextView) info.targetView).getText().toString();
		switch (item.getItemId()) {
		case DELETE_ID:
			onDeleteDialog(DELETE_ID, info.id);
			return true;
		case CHANGE_NAME:
			onCreateDialog(text);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	// Click to edit a specific Decision.
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, Tab_Layout.class);
		Bundle bundle = new Bundle();
		bundle.putLong("DecisionId", id);
		i.putExtras(bundle);
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	// Dialog window for deleting a decision.
	protected AlertDialog onDeleteDialog(int id, final long rowId) {
		AlertDialog.Builder alt_builder = new AlertDialog.Builder(this);
		alt_builder
				.setMessage(R.string.confirm_delete)
				.setCancelable(false)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {

							Cursor c = dbHelper.fetchRow(rowId);
							String decision = c.getString(c
									.getColumnIndexOrThrow(Database_Title.DecisionName));

							public void onClick(DialogInterface dialog,
									int which) {
								dbHelper.deleteRow(decision);
								fillData();
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

	// Dialog for changing the name of a decision.
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

						if (new_name != null) {
							// Update the Row in the database.
							if (!exists(new_name)) {
								dbHelper.updateRow(new_name, name);
								// and update the decision_list view.
								fillData();
							} else {
								Toast.makeText(getApplicationContext(),
										R.string.toast_same_name_exists,
										Toast.LENGTH_SHORT).show();
								onCreateDialog(name);
							}
						} else {
							Toast t = Toast
									.makeText(getApplicationContext(),
											R.string.toast_empty_name,
											Toast.LENGTH_SHORT);
							t.show();
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

	@Override    
	protected void onDestroy() {        
	    super.onDestroy();
	    dbHelper.close();
	}
	
	// Check if same decision already exists in the database.
	public boolean exists(String name) {
		Cursor c = dbHelper.checkExistence(name);
		startManagingCursor(c);
		String x = null;
		if (c.moveToFirst()) {
			x = c.getString(c.getColumnIndex(Database_Title.DecisionName));
		}
		if (x == null) {
			return false;
		} else {
			return true;
		}
	}

}