package com.kritikalerror.cpuinfo.adapter;

import com.kritikalerror.cpuinfo.CPUFragment;
import com.kritikalerror.cpuinfo.TopFragment;
import com.kritikalerror.cpuinfo.MiscFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAdapter extends FragmentPagerAdapter {

	public TabsAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Top Rated fragment activity
			return new MiscFragment();
		case 1:
			// Games fragment activity
			return new CPUFragment();
		case 2:
			// Movies fragment activity
			return new TopFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
