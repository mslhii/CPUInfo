package com.kritikalerror.cpuinfo.adapter;

import com.kritikalerror.cpuinfo.CPUFragment;
import com.kritikalerror.cpuinfo.OthersFragment;
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
			return new CPUFragment();
		case 1:
			return new MiscFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
