package com.projectreachout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.projectreachout.AddNewEvent.AddEventActivity;
import com.projectreachout.AddNewPost.AddNewPostFragment;
import com.projectreachout.Event.EventMainFragment;
import com.projectreachout.Event.Expenditures.ExpendituresMainFragment;
import com.projectreachout.Login.LoginActivity;
import com.projectreachout.PostFeed.FeedMainFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FeedMainFragment.OnFragmentInteractionListener,
        AddNewPostFragment.OnFragmentInteractionListener, EventMainFragment.OnFragmentInteractionListener,
        ExpendituresMainFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.sp_toolbar);
        setSupportActionBar(toolbar);

        inflateFeedMainFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private boolean hasLastCall = true;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(hasLastCall){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_abm_include, new FeedMainFragment())
                    .commit();
            hasLastCall = false;
        } else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home : {
                inflateFeedMainFragment();
                break;
            }
            case R.id.nav_add_post : {
                hasLastCall = true;
                inflateAddNewPostFragment();
                break;
            }
            case R.id.nav_events : {
                hasLastCall = true;
                inflateEventMainFragment();
                break;
            }
            case R.id.nav_expenditures : {
                hasLastCall = true;
                inflateExpendituresMainFragment();
                break;
            }
            case R.id.nav_add_event:{
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
            }
            case R.id.nav_share : {
                break;
            }
            case R.id.nav_send : {
                startActivity(new Intent(this, LoginActivity.class));
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private void inflateFeedMainFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new FeedMainFragment())
                .commit();
    }
    private void inflateAddNewPostFragment(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new AddNewPostFragment())
                .commit();
    }
    private void inflateEventMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new EventMainFragment())
                .commit();
    }
    private void inflateExpendituresMainFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_abm_include, new ExpendituresMainFragment())
                .commit();
    }
}
