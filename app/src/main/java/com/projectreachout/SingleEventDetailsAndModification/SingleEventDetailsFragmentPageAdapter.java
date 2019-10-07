package com.projectreachout.SingleEventDetailsAndModification;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SingleEventDetailsFragmentPageAdapter extends FragmentPagerAdapter {

    private final int PAGE_COUNT = 2;
    private final Context mContext;
    private String[] mTabTitles = new String[]{"Details", "Investment"};

    public SingleEventDetailsFragmentPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new EventDetailsFragment();
        }else {
            return new InvestmentFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Details";
            //return mContext.getString(R.string.veg);
        } else {
            return "Investment";
            //return mContext.getString(R.string.non_veg);
        }
    }
}
