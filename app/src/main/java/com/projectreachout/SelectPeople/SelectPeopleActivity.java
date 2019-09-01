package com.projectreachout.SelectPeople;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.User.UserDetails;
import com.projectreachout.User.UserDetailsAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.FIXED_ID_100;
import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingIntFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getRandomString;

public class SelectPeopleActivity extends AppCompatActivity {

    private static final String TAG = SelectPeopleActivity.class.getSimpleName();

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitFAB = findViewById(R.id.fab_lvl_fab);
        mSubmitFAB.show();
        mSubmitFAB.setImageResource(R.drawable.ic_send_white_24dp);

        mListView = findViewById(R.id.lv_lvl_list_view);

        mUserDetailsList = new ArrayList<>();
        mUserDetailsAdapter = new UserDetailsAdapter(this, R.layout.u_user_row_item, mUserDetailsList);

        mListView.setAdapter(mUserDetailsAdapter);

        /*TextView textView = new TextView(this);
        textView.setText("First selected person will be the event leader");

        mListView.addHeaderView(textView);*/

        Intent intent = getIntent();
        ArrayList<UserDetails> userDetailsArrayList = intent.getParcelableArrayListExtra(EXISTING_ORGANIZERS);

        fetchUserData();

        /* TODO: Delete this SparseBooleanArray part and use setSelectedToExistingOrganisers(userDetailsArrayList)
         *   after item selection works based on user id
         */
        ArrayList<Integer> integerArrayList = intent.getIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY);
        for (int i = 0; i < integerArrayList.size(); i++) {
            mUserDetailsAdapter.mSelectedItems.put(integerArrayList.get(i), true);
        }

        Log.v("zzzzz", integerArrayList.toString());

        mListView.setOnItemClickListener(onListItemClicked);

        // addDummyData();

        mSubmitFAB.setOnClickListener(v -> returnSelectedOrganizers(v, intent));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void fetchUserData() {
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("all_users")
                .appendPath("");

        String url = builder.build().toString();*/

        String url = getDomainUrl() + "/all_users/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, output -> {
            if (output != null) {
                parseJsonFeed(JSONParsingArrayFromString(output));
                Log.d(TAG, output);
            }
        }, error -> Log.d(TAG, error.toString())){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    class ShortByName implements Comparator<UserDetails> {

        @Override
        public int compare(UserDetails user1, UserDetails user2) {
            return user1.getUser_name().compareToIgnoreCase(user2.getUser_name());
        }
    }

    private void parseJsonFeed(JSONArray responseArray) {

        List<UserDetails> userList = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject user = JSONParsingObjectFromArray(responseArray, i);

            int user_id = JSONParsingIntFromObject(user, "id");
            String username = JSONParsingStringFromObject(user, "username");
            String profile_picture_url = JSONParsingStringFromObject(user, "image");

            UserDetails userDetails = new UserDetails();
            userDetails.setId("" + user_id);
            userDetails.setUser_name(username);
            userDetails.setProfile_picture_url(getDomainUrl() + profile_picture_url);

            userList.add(userDetails);

            // mUserDetailsAdapter.add(userDetails);
        }

        Collections.sort(userList, new ShortByName());

        mUserDetailsAdapter.addAll(userList);
        mUserDetailsAdapter.notifyDataSetChanged();
    }

    private void returnSelectedOrganizers(View view, Intent intent) {
        ArrayList<Integer> arrayList = mUserDetailsAdapter.getSelectedItem();

        ArrayList<UserDetails> userDetailsArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            /*for(int j=0; j<mUserDetailsAdapter.getCount(); j++){
                UserDetails userDetails = mUserDetailsAdapter.getItem(j);
                if(Objects.requireNonNull(userDetails).getId().equals(String.valueOf(arrayList.get(i)))){
                    userDetailsArrayList.add(userDetails);
                    break;
                }
            }*/

            /* TODO: Replace the below two lines of code with the above commented code after item selection works based on user id
             */
            Log.v(TAG, "index ----- " + arrayList.get(i) + " :::: " + arrayList.size());
            UserDetails userDetails = mUserDetailsAdapter.getItem(arrayList.get(i));
            userDetailsArrayList.add(userDetails);
        }

        Toast.makeText(view.getContext(), arrayList.size() + " Organizers Selected", Toast.LENGTH_SHORT).show();

        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < mUserDetailsAdapter.mSelectedItems.size(); i++) {
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
            if (mUserDetailsAdapter.mSelectedItems.get(position)) {
                mUserDetailsAdapter.mSelectedItems.delete(position);
                //Toast.makeText(SelectPeopleActivity.this, "position : " + position, Toast.LENGTH_SHORT).show();
                view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            } else {
                mUserDetailsAdapter.mSelectedItems.put(position, true);
                //Toast.makeText(SelectPeopleActivity.this, "position : " + position, Toast.LENGTH_SHORT).show();
                view.setBackgroundColor(getResources().getColor(R.color.color_item_selected));
            }
        }
    };

    private void setSelectedToExistingOrganisers(ArrayList<UserDetails> userDetailsArrayList, int[] ints) {
        if (userDetailsArrayList != null && userDetailsArrayList.size() != 0) {
            for (int i = 0; i < userDetailsArrayList.size(); i++) {
                int userId = Integer.valueOf(userDetailsArrayList.get(i).getId());
                mUserDetailsAdapter.mSelectedItems.put(userId, true);
            }
        }
    }

    private void addDummyData() {
        for (int i = 0; i < 40; i++) {
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
