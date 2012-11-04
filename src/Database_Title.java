package ahp.thesis.code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database_Title {

	private static final String TAG = "Database_Title";
	private static final String DATABASE_NAME = "AHP_DB";
	private static final int DATABASE_VERSION = 1;
	// Decision Table.
	public static final String TABLE_DECISION = "Decisions";
	public static final String DecisionId = "_id";
	public static final String DecisionName = "_name";
	public static final String DecisionDate = "_date";
	// Alternative Table.
	public static final String TABLE_ALTERNATIVE = "Alternatives";
	public static final String AlternativeId = "_id";
	public static final String AlternativeName = "_name";
	public static final String AlternativeDecision = "_alt_decision";
	// Criteria Table.
	public static final String TABLE_CRITERIA = "Criteria";
	public static final String CriteriaId = "_id";
	public static final String CriteriaName = "_name";
	public static final String CriteriaGroupName = "_groupname";
	public static final String CriteriaWeight = "_weight";
	public static final String CriteriaDecision = "_crit_decision";
	// Decision-Criteria Tables
	public static final String TABLE_WEIGHT = "Weight";
	public static final String WeightId = "_id";
	public static final String WeightName = "_alt_name";
	public static final String WeightCriteriaName = "_crit_name";
	public static final String WeightWeight = "_weight";
	public static final String WeightDecision = "_weight_decision";

	private static final String CREATE_TABLE_DECISIONS = "create table if not exists Decisions (_id integer primary key autoincrement, "
			+ "_name string not null, _date string not null);";
	private static final String CREATE_TABLE_ALTERNATIVES = "create table if not exists Alternatives (_id integer primary key autoincrement, "
			+ "_name string not null, _alt_decision string not null);";
	private static final String CREATE_TABLE_CRITERIA = "create table if not exists Criteria (_id integer primary key autoincrement,"
			+ " _name string not null , _groupname string default null, _crit_decision string not null, _weight float default null);";
	private static final String CREATE_TABLE_WEIGHT = "create table if not exists Weight (_id integer primary key autoincrement, "
			+ "_alt_name string not null, _crit_name string not null, _weight float default null, _weight_decision string not null);";

	private SQLiteDatabase db;
	private Context mctx;
	private DatabaseHelper mDbHelper;

	/** The DatabaseHelper class. */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_DECISIONS);
			db.execSQL(CREATE_TABLE_ALTERNATIVES);
			db.execSQL(CREATE_TABLE_CRITERIA);
			db.execSQL(CREATE_TABLE_WEIGHT);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param textvalue
	 **/
	public Database_Title(Context ctx) {
		this.mctx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw an exception to signal
	 * the failure
	 **/
	public Database_Title open() throws SQLException {
		mDbHelper = new DatabaseHelper(mctx);
		db = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	// Fill the WEIGHT Table.
	public void fillCriteriaTables(String name, String crit_name, float weight,
			String decision) {
		db.beginTransaction();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(WeightName, name);
			initialValues.put(WeightCriteriaName, crit_name);
			initialValues.put(WeightWeight, weight);
			initialValues.put(WeightDecision, decision);

			long x = db.insert(TABLE_WEIGHT, null, initialValues);
			if (x == -1) {
				ContentValues args2 = new ContentValues();
				args2.put(WeightWeight, weight);
				db.update(TABLE_WEIGHT, args2, WeightName + "=?" + " AND "
						+ WeightCriteriaName + "=?" + " AND " + WeightDecision
						+ "=?", new String[] { name, crit_name, decision });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * Create a new row for the Decisions Table. If it is successfully created
	 * return the new rowId for that row, otherwise return -1 \ to indicate
	 * failure.
	 **/
	// Decision Table
	public long createRow(String name, String date) {
		db.beginTransaction();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(DecisionName, name);
			initialValues.put(DecisionDate, date);

			db.setTransactionSuccessful();
			return db.insert(TABLE_DECISION, null, initialValues);
		} finally {
			db.endTransaction();
		}
	}

	public Cursor checkExistence(String name) {
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_DECISION
					+ " WHERE " + DecisionName + " =? ", new String[] { name });
			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	public Cursor checkExistenceAlternatives(String name) {
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ALTERNATIVE
					+ " WHERE " + AlternativeName + " =? ",
					new String[] { name });

			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	public Cursor checkExistenceCriteria(String name) {
		db.beginTransaction();
		try {
			Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CRITERIA
					+ " WHERE " + CriteriaName + " =? ", new String[] { name });
			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	// Alternative Table
	public void createRowAlternative(String name, String decision) {
		db.beginTransaction();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(AlternativeName, name);
			initialValues.put(AlternativeDecision, decision);

			db.insert(TABLE_ALTERNATIVE, null, initialValues);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	// Criteria Table
	public void createRowCriteria(String name, String group, String decision) {
		db.beginTransaction();
		try {
			ContentValues initialValues = new ContentValues();
			initialValues.put(CriteriaName, name);
			initialValues.put(CriteriaGroupName, group);
			initialValues.put(CriteriaDecision, decision);

			db.insert(TABLE_CRITERIA, null, initialValues);

			db.delete(TABLE_WEIGHT, WeightCriteriaName + "=?" + " AND "
					+ WeightDecision + "=?", new String[] { group, decision });

			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Delete specific row using the desired rowId.
	 **/
	// Decision Table
	public void deleteRow(String decision) {
		db.beginTransaction();
		try {
			db.delete(TABLE_DECISION, DecisionName + "=? ",
					new String[] { decision });
			db.delete(TABLE_CRITERIA, CriteriaDecision + "=? ",
					new String[] { decision });
			db.delete(TABLE_ALTERNATIVE, AlternativeDecision + "=? ",
					new String[] { decision });
			db.delete(TABLE_WEIGHT, WeightDecision + "=? ",
					new String[] { decision });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	// Alternative Table
	public long deleteAlternativeRow(long rowId, String decision) {
		db.beginTransaction();
		try {
			long l = db.delete(TABLE_ALTERNATIVE, AlternativeId + " = " + rowId
					+ " AND " + AlternativeDecision + "=?",
					new String[] { decision });
			db.setTransactionSuccessful();
			return l;
		} finally {
			db.endTransaction();
		}
	}

	// Criteria Table
	public void deleteCriteriaRow(String name, String decision) {
		db.beginTransaction();
		try {
			db.delete(TABLE_CRITERIA, CriteriaName + "=?" + " AND "
					+ CriteriaDecision + "=?", new String[] { name, decision });
			db.delete(TABLE_CRITERIA, CriteriaGroupName + "=?" + " AND "
					+ CriteriaDecision + "=?", new String[] { name, decision });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	/**
	 * Update the row using all the values. Return true if row is successfully
	 * updated.
	 */
	// Decision Table
	public void updateRow(String new_name, String old_name) {
		db.beginTransaction();
		try {
			ContentValues args = new ContentValues();
			args.put(DecisionName, new_name);
			db.update(TABLE_DECISION, args, DecisionName + "=?",
					new String[] { old_name });
			ContentValues args2 = new ContentValues();
			args2.put(AlternativeDecision, new_name);
			db.update(TABLE_ALTERNATIVE, args2, AlternativeDecision + "=?",
					new String[] { old_name });
			ContentValues args3 = new ContentValues();
			args3.put(CriteriaDecision, new_name);
			db.update(TABLE_CRITERIA, args3, CriteriaDecision + "=?",
					new String[] { old_name });
			ContentValues args4 = new ContentValues();
			args4.put(WeightDecision, new_name);
			db.update(TABLE_WEIGHT, args4, WeightDecision + "=?",
					new String[] { old_name });
			ContentValues args5 = new ContentValues();
			args5.put(CriteriaName, new_name);
			db.update(TABLE_CRITERIA, args5, CriteriaName + "=?",
					new String[] { old_name });
			ContentValues args6 = new ContentValues();
			args6.put(CriteriaGroupName, new_name);
			db.update(TABLE_CRITERIA, args6, CriteriaGroupName + "=?",
					new String[] { old_name });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	// Alternative Table
	public void updateAlternativeRow(String new_name, String old_name,
			String decision) {
		db.beginTransaction();
		try {
			ContentValues args = new ContentValues();
			args.put(AlternativeName, new_name);
			db.update(TABLE_ALTERNATIVE, args, AlternativeName + "=?" + " AND "
					+ AlternativeDecision + "=?", new String[] { old_name,
					decision });
			ContentValues args2 = new ContentValues();
			args2.put(WeightName, new_name);
			db.update(TABLE_WEIGHT, args2, WeightName + "=?" + " AND "
					+ WeightDecision + "=?",
					new String[] { old_name, decision });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}

	}

	// Criteria Table
	public void updateCriteriaRow(String new_name, String old_name,
			String decision) {
		db.beginTransaction();
		try {
			ContentValues args = new ContentValues();
			args.put(CriteriaName, new_name);
			db.update(TABLE_CRITERIA, args, CriteriaName + "=?" + " AND "
					+ CriteriaDecision + "=?", new String[] { old_name,
					decision });
			ContentValues args2 = new ContentValues();
			args2.put(CriteriaGroupName, new_name);
			db.update(TABLE_CRITERIA, args2, CriteriaGroupName + "=?" + " AND "
					+ CriteriaDecision + "=?", new String[] { old_name,
					decision });
			ContentValues args3 = new ContentValues();
			args3.put(WeightCriteriaName, new_name);
			db.update(TABLE_WEIGHT, args3, WeightCriteriaName + "=?" + " AND "
					+ WeightDecision + "=?",
					new String[] { old_name, decision });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	// For Table Criteria
	public void updateWeight(Float weight, String decision, String name) {
		db.beginTransaction();
		try {
			ContentValues args = new ContentValues();
			args.put(CriteriaWeight, weight);
			db.update(TABLE_CRITERIA, args, CriteriaDecision + "=?" + " AND "
					+ CriteriaName + "=?", new String[] { decision, name });
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	public Cursor GetWeight(String name) {

		Cursor c = db.query(true, TABLE_CRITERIA,
				new String[] { CriteriaWeight }, CriteriaName + " = " + "'"
						+ name + "'", null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	public Cursor GetOtherWeight(String alt_name, String crit_name,
			String decision) {
		return db.query(TABLE_WEIGHT, new String[] { WeightWeight }, WeightName
				+ "=?" + " AND " + WeightCriteriaName + "=?" + " AND "
				+ WeightDecision + "=?", new String[] { alt_name, crit_name,
				decision }, null, null, null, null);
	}

	/** Return a Cursor over the list of all rows in the database */
	// Decision Table
	public Cursor GetAllRows() {
		db.beginTransaction();
		try {
			Cursor c = db.query(TABLE_DECISION, new String[] { DecisionId,
					DecisionName }, null, null, null, null, null);
			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	// Alternative Table
	public Cursor GetAllAlternativeRows(String decision) {
		db.beginTransaction();
		try {
			Cursor c = db.query(TABLE_ALTERNATIVE, new String[] {
					AlternativeId, AlternativeName }, AlternativeDecision
					+ "=?", new String[] { decision }, null, null, null);
			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	// Criteria Table
	public Cursor GetAllCriteriaRows(String decision) {
		db.beginTransaction();
		try {
			Cursor c = db.query(TABLE_CRITERIA, new String[] { CriteriaId,
					CriteriaName, CriteriaGroupName, CriteriaDecision,
					CriteriaWeight }, CriteriaDecision + "=?",
					new String[] { decision }, null, null, null);
			db.setTransactionSuccessful();
			return c;
		} finally {
			db.endTransaction();
		}
	}

	/** get groups */
	public Cursor GetGroups(String decision) {
		Cursor c = db.query(TABLE_CRITERIA, new String[] { CriteriaId,
				CriteriaName, CriteriaGroupName }, CriteriaGroupName + "=?"
				+ " AND " + CriteriaDecision + "=?", new String[] { "None...",
				decision }, null, null, null);
		return c;
	}

	/** get specific children for specific group */
	public Cursor GetChilds(String group, String decision) {
		Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CRITERIA + " WHERE "
				+ CriteriaGroupName + " =? " + " AND " + CriteriaDecision
				+ " =? ", new String[] { group, decision });
		return c;
	}

	/** get the father for a specific criteria_name */
	public Cursor GetFather(String crit_name, String decision) {
		return db.rawQuery("SELECT * FROM " + TABLE_CRITERIA + " WHERE "
				+ CriteriaName + " =? " + " AND " + CriteriaDecision + " =? ",
				new String[] { crit_name, decision });

	}

	/** get the criteria with no children */
	public Cursor GetLastParent(String decision) {
		Cursor c = db.rawQuery(
				"SELECT DISTINCT " + WeightCriteriaName + " FROM "
						+ TABLE_WEIGHT + " WHERE " + WeightDecision + " =? ",
				new String[] { decision });
		if (c != null) {
			return c;
		} else {
			return null;
		}
	}

	public boolean askForWeights() {
		Cursor c1, c2;
		c1 = db.rawQuery("select * from " + TABLE_CRITERIA + " where "
				+ CriteriaWeight + " =? ", new String[] { "null" });
		c2 = db.rawQuery("select * from " + TABLE_WEIGHT + " where "
				+ WeightWeight + " =? ", new String[] { "null" });
		
		if (c1 == null && c2==null) {
			return true;
		} else
			return false;
	}

	/**
	 * Return a Cursor positioned at the row that matches the given rowId
	 */
	// Decision Table
	public Cursor fetchRow(long rowId) throws SQLException {

		Cursor mCursor = db.query(true, TABLE_DECISION, new String[] {
				DecisionId, DecisionName, DecisionDate }, "_id=" + rowId, null,
				null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();

		}
		return mCursor;
	}

	// Criteria Table
	public Cursor fetchCriteriaRow(long id, String decision) {
		Cursor mCursor = db.query(true, TABLE_CRITERIA, new String[] {
				CriteriaId, CriteriaName, CriteriaGroupName }, CriteriaId + "="
				+ id + " AND " + CriteriaDecision + "=" + decision, null, null,
				null, null, null);

		return mCursor;
	}
}