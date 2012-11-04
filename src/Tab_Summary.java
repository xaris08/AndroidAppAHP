package ahp.thesis.code;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The last step is being accomplished by providing the result (output) of the AHP method.
 * @author xaris08
 *
 */
public class Tab_Summary extends Activity {

	private Database_Title dbHelper;
	private TextView dec_name, altern_count, crit_count;
	private TextView date;
	private Cursor mCursor;
	public static String DECISION;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_summary);


		Bundle b = getIntent().getExtras();
		Long mrowId = b.getLong("rowId");
		Long mrowId2 = b.getLong("DecisionId");
		
		dbHelper = new Database_Title(this);
		dbHelper.open();
		
		if (mrowId2 != 0) {
			mCursor = dbHelper.fetchRow(mrowId2);
			startManagingCursor(mCursor);
		} else {
			mCursor = dbHelper.fetchRow(mrowId);
			startManagingCursor(mCursor);
		}
		dec_name = (TextView) findViewById(R.id.decision_name_1);
		DECISION = mCursor.getString(mCursor.getColumnIndexOrThrow(Database_Title.DecisionName));
		dec_name.setText(DECISION);

		date = (TextView) findViewById(R.id.date_1);
		date.setText(mCursor.getString(mCursor
				.getColumnIndexOrThrow(Database_Title.DecisionDate)));

		 altern_count = (TextView) findViewById(R.id.altern_1);
		 crit_count = (TextView) findViewById(R.id.crit_1);
		 altern_count.setText(countAlternatives());
		 crit_count.setText(countCriteria());

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
	 public void onResume(){
	 super.onResume();
	 	altern_count.setText(countAlternatives());
		crit_count.setText(countCriteria());
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

	private String countAlternatives() {
		Cursor c = dbHelper.GetAllAlternativeRows(DECISION);

		int i = c.getCount();
		String count = String.valueOf(i);

		if (count == null) {
			count = "0";
		}
		return count;
	}
	
	private String countCriteria() {
		Cursor c = dbHelper.GetAllCriteriaRows(DECISION);
		int i = c.getCount();
		String count = String.valueOf(i);

		if (count == null) {
			count = "0";
		}
		return count;
	}
}// class closed
