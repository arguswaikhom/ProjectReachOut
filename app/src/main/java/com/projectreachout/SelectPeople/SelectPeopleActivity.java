package com.projectreachout.SelectPeople;

import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.getDomainUrl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.User.User;
import com.projectreachout.User.UserDetailsAdapter;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SelectPeopleActivity extends AppCompatActivity implements OnHttpResponse {

    private static final String TAG = SelectPeopleActivity.class.getSimpleName();

    ListView mListView;
    UserDetailsAdapter mUserDetailsAdapter;
    List<User> mUserList;
    FloatingActionButton mSubmitFAB;
    private ProgressBar mLoadingPbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sp_activity_select_people);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSubmitFAB = findViewById(R.id.fab_lvl_fab);
        mSubmitFAB.show();
        mSubmitFAB.setImageResource(R.drawable.ic_send_white_24dp);

        mListView = findViewById(R.id.lv_lvl_list_view);
        mLoadingPbar = findViewById(R.id.pbar_sasp_loading);

        mUserList = new ArrayList<>();
        mUserDetailsAdapter = new UserDetailsAdapter(this, R.layout.u_user_row_item, mUserList);

        mListView.setAdapter(mUserDetailsAdapter);

        Intent intent = getIntent();
        ArrayList<User> userArrayList = intent.getParcelableArrayListExtra(EXISTING_ORGANIZERS);


        /* TODO: Delete this SparseBooleanArray part and use setSelectedToExistingOrganisers(userArrayList)
         *   after item selection works based on user id
         */

        ArrayList<Integer> integerArrayList = intent.getIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY);
        for (int i = 0; i < integerArrayList.size(); i++) {
            mUserDetailsAdapter.mSelectedItems.put(integerArrayList.get(i), true);
        }

        mListView.setOnItemClickListener(onListItemClicked);
        mSubmitFAB.setOnClickListener(v -> returnSelectedOrganizers(v, intent));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(this)) {
            mLoadingPbar.setVisibility(View.VISIBLE);
            fetchUserData();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void fetchUserData() {
        String url = getDomainUrl() + "/get_users/";
        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.GET, url, null, 0, this);
        httpVolleyRequest.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        if (request == 0) {
            parseJsonFeed(JSONParsingArrayFromString(response));
            Log.d(TAG, response);
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        mLoadingPbar.setVisibility(View.GONE);
        Log.d(TAG, error.toString());
        MessageUtils.showShortToast(this, "Something went wrong!!");
    }

    class ShortByName implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {
            return user1.getDisplay_name().compareToIgnoreCase(user2.getDisplay_name());
        }
    }

    private void parseJsonFeed(JSONArray responseArray) {
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject userJSON = JSONParsingObjectFromArray(responseArray, i);
            User user = User.fromJson(userJSON.toString());
            if (user != null && !user.getUser_type().equals(User.AC_GUEST)) {
                userList.add(user);
            }
        }

        Collections.sort(userList, new ShortByName());

        mUserDetailsAdapter.addAll(userList);
        mUserDetailsAdapter.notifyDataSetChanged();
    }

    private void returnSelectedOrganizers(View view, Intent intent) {
        ArrayList<Integer> arrayList = mUserDetailsAdapter.getSelectedItem();

        ArrayList<User> userArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            /*for(int j=0; j<mUserDetailsAdapter.getCount(); j++){
                User user = mUserDetailsAdapter.getItem(j);
                if(Objects.requireNonNull(user).getUser_id().equals(String.valueOf(arrayList.get(i)))){
                    userArrayList.add(user);
                    break;
                }
            }*/

            /* TODO: Replace the below two lines of code with the above commented code after item selection works based on user id
             */
            Log.v(TAG, "index ----- " + arrayList.get(i) + " :::: " + arrayList.size());
            User user = mUserDetailsAdapter.getItem(arrayList.get(i));
            userArrayList.add(user);
        }

        Toast.makeText(view.getContext(), arrayList.size() + " Organizers Selected", Toast.LENGTH_SHORT).show();

        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < mUserDetailsAdapter.mSelectedItems.size(); i++) {
            integerArrayList.add(mUserDetailsAdapter.mSelectedItems.keyAt(i));
        }
        intent.putIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY, integerArrayList);

        intent.putParcelableArrayListExtra(SELECTED_ORGANIZERS, userArrayList);
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
            /*int index = Integer.valueOf(mUserDetailsAdapter.getItem(position).getUser_id());

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

    private void setSelectedToExistingOrganisers(ArrayList<User> userArrayList, int[] ints) {
        if (userArrayList != null && userArrayList.size() != 0) {
            for (int i = 0; i < userArrayList.size(); i++) {
                int userId = Integer.valueOf(userArrayList.get(i).getUser_id());
                mUserDetailsAdapter.mSelectedItems.put(userId, true);
            }
        }
    }
}
