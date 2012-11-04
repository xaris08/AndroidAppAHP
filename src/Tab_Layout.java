package ahp.thesis.code;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * A class to control the layout of each previous tabs (steps).
 * @author xaris08
 *
 */
public class Tab_Layout extends TabActivity {

	Intent intent; // Reusable Intent for each tab
	Bundle b = new Bundle();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);

		Resources res = getResources(); // Resource object to get drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Reusable TabSpec for each tab

		// Pass the value of the rowId from start activity.
		Bundle b = getIntent().getExtras();
		Long mrowId = b.getLong("rowId");
		// Pass the value of the rowId from Decisions_List activity.
		Long mrowId2 = b.getLong("DecisionId");

		// Create an Intent to launch an Activity for the tab (to be reused) and
		// Initialize a TabSpec for each tab and add it to the TabHost
		intent = new Intent(this, Tab_Summary.class);

		Bundle bundle = new Bundle();
		bundle.putLong("rowId", mrowId);
		bundle.putLong("DecisionId", mrowId2);
		intent.putExtras(bundle);

		spec = tabHost
				.newTabSpec("summary")
				.setIndicator(getString(R.string.tab_summary),
						res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tab_Alternatives.class);
		spec = tabHost
				.newTabSpec("alternatives")
				.setIndicator(getString(R.string.tab_alternatives),
						res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tab_Criteria.class);
		spec = tabHost
				.newTabSpec("criteria")
				.setIndicator(getString(R.string.tab_criteria),
						res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tab_Adjust.class);
		spec = tabHost
				.newTabSpec("adjust")
				.setIndicator(getString(R.string.tab_adjust), res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tab_Weights.class);
		spec = tabHost
				.newTabSpec("weights")
				.setIndicator(getString(R.string.tab_weight),
						res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Tab_Decision.class);
		spec = tabHost
				.newTabSpec("decision")
				.setIndicator(getString(R.string.tab_decision),
						res.getDrawable(R.drawable.tab_enabled))
				.setContent(intent);

		tabHost.addTab(spec);
		tabHost.setCurrentTab(6);
	}
}
