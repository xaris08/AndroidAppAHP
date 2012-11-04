package ahp.thesis.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * The calculation of the method being produced in that class.
 *  
 * @author xaris08
 *
 */
public class Tab_Decision extends Activity {
	/** Called when the activity is first created. */
	List<Pie_Item> PieData = new ArrayList<Pie_Item>(0);
	private ListView list;
	private Database_Title dbHelper;
	private String[] alternatives;

	private static final String LIST_PIE1 = "pie_list_color";
	private static final String LIST_PIE2 = "pie_list_text1";
	private static final String LIST_PIE3 = "pie_list_text2";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_decision);
	}

	@Override
	protected void onResume() {
		super.onResume();
			generateData();
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

	public void generateData() {

		dbHelper = new Database_Title(this);
		dbHelper.open();
		// ------------------------------------------------------------------------------------------
		// Used vars declaration
		// ------------------------------------------------------------------------------------------
		list = (ListView) findViewById(R.id.pie_list);
		Pie_Item Item;
		Random mNumGen = new Random();
		// ------------------------------------------------------------------------------------------
		// Generating data
		// ------------------------------------------------------------------------------------------
		alternatives = getAllAlternatives();
		Float[] percentage = new Float[alternatives.length];

		/*************** Weight Calculation *****************/
		String father = null;
		String[] lastParent = getLastParent();

		for (int i = 0; i < alternatives.length; i++) {

			percentage[i] = (float) 0;
			for (int j = 0; j < lastParent.length; j++) {
				float x = getOtherWeight(alternatives[i], lastParent[j]);
				x *= getWeight(lastParent[j]);
				father = getFather(lastParent[j]);

				while (!father.equals(Tab_Summary.DECISION)) {
					x *= getWeight(father);
					father = getFather(father);
				}
				x = Math.round(x * 100);
				percentage[i] += x;
			}
		}
		/**************** End Of Calculation ******************/
		// Arrays.sort(percentage, Collections.reverseOrder());

		int MaxPieItems = alternatives.length;
		int MaxCount = 0;
		float ItemCount = 0;
		ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		for (int i = 0; i < MaxPieItems; i++) {
			ItemCount = percentage[i];
			Item = new Pie_Item();
			Item.Count = ItemCount;
			Item.Label = alternatives[i];
			Item.Color = 0xff000000 + 256 * 256 * mNumGen.nextInt(256) + 256
					* mNumGen.nextInt(256) + mNumGen.nextInt(256);
			// Add to the listview.
			map = new HashMap<String, Object>();
			map.put(LIST_PIE1, Item.Color);
			map.put(LIST_PIE2, Item.Label);
			map.put(LIST_PIE3, percentage[i]);
			mylist.add(map);

			PieData.add(Item);
			MaxCount += ItemCount;
		}
		// Make the full screen scrollable.
		int rows = MaxPieItems;
		int height = 86 * rows;

		ViewGroup.LayoutParams params = list.getLayoutParams();
		params.height = height;
		list.setLayoutParams(params);
		list.requestLayout();
		
		list.setAdapter(new myListAdapter(mylist, Tab_Decision.this));
		// Size => Pie size
		// ------------------------------------------------------------------------------------------
		int Size = 200;
		// ------------------------------------------------------------------------------------------
		// BgColor => The background Pie Color
		// ------------------------------------------------------------------------------------------
		int BgColor = 0xffa1a1a1;
		// ------------------------------------------------------------------------------------------
		// mBackgroundImage => Temporary image will be drawn with the content of
		// pie view
		// ------------------------------------------------------------------------------------------
		Bitmap mBackgroundImage = Bitmap.createBitmap(Size, Size,
				Bitmap.Config.RGB_565);
		// ------------------------------------------------------------------------------------------
		// Generating Pie view
		// ------------------------------------------------------------------------------------------
		Pie_Chart_View PieChartView = new Pie_Chart_View(this);
		PieChartView.setLayoutParams(new LayoutParams(Size, Size));
		PieChartView.setGeometry(Size, Size, 2, 2, 2, 2);
		PieChartView.setSkinParams(BgColor);
		PieChartView.setData(PieData, MaxCount);
		PieChartView.invalidate();
		// ------------------------------------------------------------------------------------------
		// Draw Pie Vien on Bitmap canvas
		// ------------------------------------------------------------------------------------------
		PieChartView.draw(new Canvas(mBackgroundImage));
		PieChartView = null;
		// ------------------------------------------------------------------------------------------
		// Create a new ImageView to add to main layout
		// ------------------------------------------------------------------------------------------
		ImageView mImageView = new ImageView(this);
		mImageView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		mImageView.setBackgroundColor(BgColor);
		mImageView.setImageBitmap(mBackgroundImage);
		// ------------------------------------------------------------------------------------------
		// Finaly add Image View to target view !!!
		// ------------------------------------------------------------------------------------------
		LinearLayout TargetPieView = (LinearLayout) findViewById(R.id.pie_container);
		TargetPieView.removeAllViews();
		TargetPieView.addView(mImageView);
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
			// is no need to reinflate it. We only inflate a new View when the
			// convertView supplied by ListView is null.
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.tab_decision_row, null);
				// Creates a ViewHolder and store references to the two
				// children views we want to bind data to.
				holder = new ViewHolder();

				holder.pie_alternative = (TextView) convertView
						.findViewById(R.id.pie_alternative);
				convertView.setTag(holder);

			} else {
				// Get the ViewHolder back to get fast access to the
				// TextView and the ImageView.
				holder = (ViewHolder) convertView.getTag();
			}
			// Bind the data with the holder.
			holder.pie_alternative.setTextColor((Integer) myList.get(position)
					.get(LIST_PIE1));
			holder.pie_alternative.setText((String) myList.get(position).get(
					LIST_PIE2)
					+ " - " + myList.get(position).get(LIST_PIE3) + "%");
			return convertView;
		}

		class ViewHolder {
			TextView pie_alternative;
		}
	}

	// Return a table with all the alternatives.
	private String[] getAllAlternatives() {

		Cursor c = dbHelper.GetAllAlternativeRows(Tab_Summary.DECISION);
		startManagingCursor(c);

		int count1 = c.getCount();
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
	
	// Get the weight value from the Criteria Table
	private float getWeight(String name) {
		Cursor c = dbHelper.GetWeight(name);
		startManagingCursor(c);
		if (c.moveToFirst()) {
			return c.getFloat(c.getColumnIndex(Database_Title.CriteriaWeight));
		}
		return (float) 0.00;
	}

	// Get the weight value from the Weight Table
	private float getOtherWeight(String alt_name, String crit_name) {
		Cursor c = dbHelper.GetOtherWeight(alt_name, crit_name,
				Tab_Summary.DECISION);
		startManagingCursor(c);
		if (c.moveToFirst()) {
			return c.getFloat(c.getColumnIndex(Database_Title.WeightWeight));
		}
		return (float) 0.00;
	}

	private String getFather(String crit_name) {
		Cursor c = dbHelper.GetFather(crit_name, Tab_Summary.DECISION);
		startManagingCursor(c);
		if (c.moveToFirst()) {
			return c.getString(c
					.getColumnIndex(Database_Title.CriteriaGroupName));
		}
		return null;
	}

	private String[] getLastParent() {
		Cursor c = dbHelper.GetLastParent(Tab_Summary.DECISION);
		startManagingCursor(c);

		int count = c.getCount();
		String[] lastParent = new String[count];

		int i = 0;
		if (c.moveToFirst()) {
			do {
				lastParent[i] = c.getString(c
						.getColumnIndex(Database_Title.WeightCriteriaName));
				i++;
			} while (c.moveToNext());
		}
		return lastParent;
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