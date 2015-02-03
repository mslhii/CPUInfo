package com.kritikalerror.cpuinfo;

import java.lang.reflect.Method;

import com.kritikalerror.cpuinfo.adapter.TabsAdapter;

import com.example.cpuinfo.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsAdapter mAdapter;
	private ActionBar actionBar;
	
	public String mMaxProcesses = "Disabled";
	public String mRefreshFreq = "1";
	public boolean mShowThreads = false;
	public String mSortColumns = "None";
	
	// Tab titles
	private String[] tabs = { "CPU", "Processes" };
	
	private final int RESULT = 1;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		forceTabs();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@SuppressLint("NewApi")
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
	
	@Override
    public void onConfigurationChanged(final Configuration config) {
        super.onConfigurationChanged(config);
        forceTabs();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Settings");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Intent myIntent = new Intent(this, SettingsActivity.class);
			startActivityForResult(myIntent, RESULT);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) 
        {
        case RESULT:
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        	
        	mMaxProcesses = sharedPrefs.getString("maxProcesses", "Disabled");
        	mRefreshFreq = sharedPrefs.getString("refreshFrequency", "1");
        	mShowThreads = sharedPrefs.getBoolean("threads", false);
        	mSortColumns = sharedPrefs.getString("columns", "None");
        	
        	String testString = "true";
        	if(!mShowThreads)
        	{
        		testString = "false";
        	}
        	
        	Toast.makeText(this, "Settings is: " + mMaxProcesses + " " +
        			mRefreshFreq + " " + testString + " " + mSortColumns, Toast.LENGTH_SHORT).show();
            break;
        }
    }

    public void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, true);
        }
        catch(final Exception e) {
        	Log.e("MAIN", "tabs");
        }
    }
    /*
    private void loadAds()
	{
		// Create and setup the AdMob view
		mAdView = new AdView(this);
		FrameLayout layout = (FrameLayout) findViewById(R.id.pager);

		mAdView.setAdSize(AdSize.SMART_BANNER);
		mAdView.setAdUnitId("ca-app-pub-6309606968767978/4023310042");
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		
		// Get the height for offset calculations
		AdSize adSize = mAdView.getAdSize();
		//mAdHeight = adSize.getHeight();
		mAdHeight = adSize.getHeightInPixels(getApplicationContext());
		
		// Add the AdMob view
		FrameLayout.LayoutParams adParams = 
				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 
						FrameLayout.LayoutParams.WRAP_CONTENT);

		layout.addView(mAdView, adParams);

		mAdView.loadAd(adRequestBuilder.build());
	}
	*/
}
