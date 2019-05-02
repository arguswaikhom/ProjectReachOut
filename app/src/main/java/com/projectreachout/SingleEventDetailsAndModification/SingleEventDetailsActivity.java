package com.projectreachout.SingleEventDetailsAndModification;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.projectreachout.R;

public class SingleEventDetailsActivity extends AppCompatActivity implements EventDetailsFragment.OnFragmentInteractionListener, InvestmentFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sedam_activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        ViewPager viewPager = findViewById(R.id.vp_sam_view_pager);

        // Create an adapter that knows which fragment should be shown on each page
        SingleEventDetailsFragmentPageAdapter adapter = new SingleEventDetailsFragmentPageAdapter(getSupportFragmentManager(),this);

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tl_sam_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
