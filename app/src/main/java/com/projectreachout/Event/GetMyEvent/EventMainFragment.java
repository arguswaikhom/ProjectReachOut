package com.projectreachout.Event.GetMyEvent;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.Utilities.BackgroundSyncUtilities.BackgoundServerChecker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingIntFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;
import static com.projectreachout.GeneralStatic.getDomainUrl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventMainFragment extends Fragment{

    private static final String TAG = EventMainFragment.class.getSimpleName();

    ListView mListView;
    public static EventListAdapter mEventListAdapter;
    public static List<EventItem> mEventItemList;

    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;

    FloatingActionButton mFAB;

    private OnFragmentInteractionListener mListener;

    public EventMainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);

        if (mListener != null){
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_my_events)));
        }

        mListView = rootView.findViewById(R.id.lv_lvl_list_view);
        mFAB = rootView.findViewById(R.id.fab_lvl_fab);

        /*mFAB.show();
        mFAB.setImageResource(R.drawable.ic_add_white_24dp);*/

        mEventItemList = new ArrayList<>();
        mEventListAdapter = new EventListAdapter(getContext(), R.layout.evn_event_item, mEventItemList);
        mListView.setAdapter(mEventListAdapter);

        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        /*mFAB.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEventActivity.class);
            startActivity(intent);
        });*/

        //setDummyData();

        loadData(REFRESH);

        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        BackgoundServerChecker.backgroundCheck(BackgoundServerChecker.ACTION_MY_EVENT_ONLY);
    }

    private void loadData(int action) {
        /*Uri.Builder builder = new Uri.Builder();
        // TODO: use .authority(getString(R.string.localhost)) after having a domain name
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("get_my_events")
                .appendPath("");*/

        String url = getDomainUrl() + "/get_my_events/";


        switch (action) {
            case REFRESH: {
                loadBackgroundAsyncTask(url);
            }
            case LOAD_MORE: {
                // TODO: get timeStamp of the last post in the feed list
                String lastPostTimeStamp = "1556604826";

                /*builder.appendQueryParameter("before", lastPostTimeStamp);
                loadBackgroundAsyncTask(builder.build().toString());*/
            }
        }
    }

    private void loadBackgroundAsyncTask(String url) {

        Map<String, String> param = new HashMap<>();
        param.put("user_name", AppController.getInstance().getLoginUserUsername());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String output) {
                if (output != null) {
                    Log.d(TAG, output);
                    JSONArray response = JSONParsingArrayFromString(output);
                    parseJsonFeed(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return param;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void displayErrorMessage() {
        if (mEventListAdapter.isEmpty()) {
            mErrorMessageLayout.setVisibility(View.VISIBLE);
        } else {
            String errorMessage = "Couldn't update information from server...";
            Snackbar.make(Objects.requireNonNull(getView()), errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(REFRESH);
                }
            }).show();
        }
    }

    private void parseJsonFeed(JSONArray jsonArray) {
        mEventItemList.clear();
        for (int i = jsonArray.length()-1; i >= 0; i--) {
            JSONObject eventItemJSONObj = JSONParsingObjectFromArray(jsonArray, i);

            JSONArray organizers = JSONParsingArrayFromObject(eventItemJSONObj, "organizers");

            List<ContributePeople> contributePeopleList = new ArrayList<>();

            for (int index = 0; index < organizers.length(); index++) {
                JSONObject organiserItem = JSONParsingObjectFromArray(organizers, index);

                int user_id = JSONParsingIntFromObject(organiserItem, "user_id");
                String username = JSONParsingStringFromObject(organiserItem, "username");
                String profilePictureURL = JSONParsingStringFromObject(organiserItem, "profile_picture_url");

                ContributePeople contributePeople = new ContributePeople(user_id, username, profilePictureURL);
                contributePeopleList.add(contributePeople);
            }

            String event_id = JSONParsingStringFromObject(eventItemJSONObj, "event_id");
            String title = JSONParsingStringFromObject(eventItemJSONObj, "event_title");
            String date = JSONParsingStringFromObject(eventItemJSONObj, "date");
            String team = JSONParsingStringFromObject(eventItemJSONObj, "team_name");
            String assigned_by = JSONParsingStringFromObject(eventItemJSONObj, "assigned_by");
            String description = JSONParsingStringFromObject(eventItemJSONObj, "description");
            String investment_amount = JSONParsingStringFromObject(eventItemJSONObj, "investment_amount");
            String investment_in_return = JSONParsingStringFromObject(eventItemJSONObj, "investment_in_return");
            String eventLeader = JSONParsingStringFromObject(eventItemJSONObj, "event_leader");

            //EventItem eventItem = new EventItem(title, date, team, description, contributePeopleList);

            EventItem eventItem = new EventItem();
            eventItem.setEvent_id(event_id);
            eventItem.setEvent_title(title);
            eventItem.setDate(date);
            eventItem.setTeam_name(team);
            eventItem.setAssignBy(assigned_by);
            eventItem.setDescription(description);
            eventItem.setContribute_people_list(contributePeopleList);
            eventItem.setInvestmentAmount(investment_amount);
            eventItem.setInvestmentInReturn(investment_in_return);
            eventItem.setEventLeader(eventLeader);

            mEventListAdapter.add(eventItem);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /*private void setDummyData() {
        List<EventItem> eventItemList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<ContributePeople> contributePeopleList = new ArrayList<>();
            int size = getRandomInt(4, 8);
            Log.v("nnn", "size : " + size);
            for (int j = 0; j < size; j++) {
                ContributePeople contributePeople = new ContributePeople("UserName_" + (j + 1),
                        "https://api.androidhive.info/json/images/johnny.jpg");
                contributePeopleList.add(contributePeople);
            }
            EventItem eventItem = new EventItem("EventTitle_" + (i + 1),
                    "1403375851930", "Regular Volunteers", contributePeopleList);
            eventItemList.add(eventItem);
        }
        mEventListAdapter.addAll(eventItemList);
        mEventListAdapter.notifyDataSetChanged();
    }*/
}
