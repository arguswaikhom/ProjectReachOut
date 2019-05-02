package com.projectreachout.SelectPeople;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.projectreachout.GeneralStatic;
import com.projectreachout.R;
import com.projectreachout.User.UserDetails;
import com.projectreachout.User.UserDetailsAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.FIXED_ID_100;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.getRandomString;

public class SelectPeopleActivity extends AppCompatActivity {

    ListView mListView;
    UserDetailsAdapter mUserDetailsAdapter;
    List<UserDetails> mUserDetailsList;
    FloatingActionButton mSubmitFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_activity_select_people);
        Toolbar toolbar = findViewById(R.id.sp_toolbar);
        setSupportActionBar(toolbar);

        mSubmitFAB = findViewById(R.id.fab_lvl_fab);
        mSubmitFAB.show();
        mSubmitFAB.setImageResource(R.drawable.ic_send_white_24dp);

        mListView = findViewById(R.id.lv_lvl_list_view);

        mUserDetailsList = new ArrayList<>();
        mUserDetailsAdapter = new UserDetailsAdapter(this, R.layout.u_user_row_item, mUserDetailsList);

        mListView.setAdapter(mUserDetailsAdapter);

        Intent intent = getIntent();
        ArrayList<UserDetails> userDetailsArrayList = intent.getParcelableArrayListExtra(EXISTING_ORGANIZERS);

        /* TODO: Delete this SparseBooleanArray part and use setSelectedToExistingOrganisers(userDetailsArrayList)
        *   after item selection works based on user id
        */
        ArrayList<Integer> integerArrayList = intent.getIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY);
        for (int i=0; i<integerArrayList.size(); i++) {
            mUserDetailsAdapter.mSelectedItems.put(integerArrayList.get(i), true);
        }

        mListView.setOnItemClickListener(onListItemClicked);

        addDummyData();

        mSubmitFAB.setOnClickListener(v -> returnSelectedOrganizers(v, intent));
    }

    private void returnSelectedOrganizers(View view, Intent intent) {
        ArrayList<Integer> arrayList = mUserDetailsAdapter.getSelectedItem();

        ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();
        for(int i=0; i<arrayList.size(); i++){
            /*for(int j=0; j<mUserDetailsAdapter.getCount(); j++){
                UserDetails userDetails = mUserDetailsAdapter.getItem(j);
                if(Objects.requireNonNull(userDetails).getId().equals(String.valueOf(arrayList.get(i)))){
                    userDetailsArrayList.add(userDetails);
                    break;
                }
            }*/

            /* TODO: Replace the below two lines of code with the above commented code after item selection works based on user id
             */
            UserDetails userDetails = mUserDetailsAdapter.getItem(arrayList.get(i));
            userDetailsArrayList.add(userDetails);
        }

        Toast.makeText(view.getContext(), arrayList.size() + " Organizers Selected", Toast.LENGTH_SHORT).show();

        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for(int i=0; i<mUserDetailsAdapter.mSelectedItems.size(); i++){
            integerArrayList.add(mUserDetailsAdapter.mSelectedItems.keyAt(i));
        }
        intent.putIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY, integerArrayList);

        intent.putParcelableArrayListExtra(SELECTED_ORGANIZERS, userDetailsArrayList);
        setResult(RESULT_OK, intent);
        finish();
    }

    private AdapterView.OnItemClickListener onListItemClicked = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /* TODO: Replace the below code i.e. "Item selection based on position" with "Item selection based on user id"
             *  after the server is running i.e. after you make sure all ids aren't same
             *  because getRandomInt() doesn't take responsible for generating unique int
             */

            // Item selection based on user id
            /*int index = Integer.valueOf(mUserDetailsAdapter.getItem(position).getId());

            if(mUserDetailsAdapter.mSelectedItems.get(index)){
                mUserDetailsAdapter.mSelectedItems.delete(index);
                view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }else {
                mUserDetailsAdapter.mSelectedItems.put(index, true);
                view.setBackgroundColor(getResources().getColor(R.color.color_item_selected));
            }*/

            // Item selection based on position
            if (mUserDetailsAdapter.mSelectedItems.get(position)){
                mUserDetailsAdapter.mSelectedItems.delete(position);
                view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }else {
                mUserDetailsAdapter.mSelectedItems.put(position, true);
                view.setBackgroundColor(getResources().getColor(R.color.color_item_selected));
            }
        }
    };

    private void setSelectedToExistingOrganisers(ArrayList<UserDetails> userDetailsArrayList, int[] ints) {
        if(userDetailsArrayList != null && userDetailsArrayList.size() != 0){
            for(int i=0; i<userDetailsArrayList.size(); i++){
                int userId = Integer.valueOf(userDetailsArrayList.get(i).getId());
                mUserDetailsAdapter.mSelectedItems.put(userId, true);
            }
        }
    }

    private void addDummyData() {
        for(int i=0; i<40; i++){
            int id = FIXED_ID_100[i];
            String username = getRandomString(10, 15);
            String teamName = "Regular Volunteers";
            String profileThumbnailUrl = "https://api.androidhive.info/json/images/tom_hardy.jpg";

            UserDetails userDetails = new UserDetails(Integer.toString(id), username, teamName, profileThumbnailUrl);
            mUserDetailsAdapter.add(userDetails);
        }
        mUserDetailsAdapter.notifyDataSetChanged();
    }
}
