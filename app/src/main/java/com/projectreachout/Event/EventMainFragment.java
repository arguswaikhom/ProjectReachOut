package com.projectreachout.Event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.projectreachout.AddNewEvent.AddEventActivity;
import com.projectreachout.NetworkUtils.AsyncResponseGet;
import com.projectreachout.NetworkUtils.BackgroundAsyncGet;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.projectreachout.GeneralStatic.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventMainFragment extends Fragment {

    ListView mListView;
    EventListAdapter mEventListAdapter;
    List<EventItem> mEventItemList;

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

        mListView = rootView.findViewById(R.id.lv_lvl_list_view);
        mFAB = rootView.findViewById(R.id.fab_lvl_fab);

        mFAB.show();
        mFAB.setImageResource(R.drawable.ic_add_white_24dp);

        mEventItemList = new ArrayList<>();
        mEventListAdapter = new EventListAdapter(getContext(), R.layout.evn_event_item, mEventItemList);
        mListView.setAdapter(mEventListAdapter);

        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        mFAB.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEventActivity.class);
            startActivity(intent);
        });

        //setDummyData();

        loadData(REFRESH);
        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));

        return rootView;
    }
    private void loadData(int action) {
        Uri.Builder builder = new Uri.Builder();
        // TODO: use .authority(getString(R.string.localhost)) after having a domain name
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("events");

        switch (action){
            case REFRESH: {
                loadBackgroundAsyncTask(builder.build().toString());
            }
            case LOAD_MORE: {
                // TODO: get timeStamp of the last post in the feed list
                String lastPostTimeStamp = "1556604826";

                builder.appendQueryParameter("before", lastPostTimeStamp);
                loadBackgroundAsyncTask(builder.build().toString());
            }
        }
    }

    private void loadBackgroundAsyncTask(String url){
        /*
         * TODO: Implement the empty methods
         * */

        BackgroundAsyncGet backgroundAsyncGet = new BackgroundAsyncGet(new AsyncResponseGet() {
            @Override
            public void onResponse(JSONArray output) {
                if(output != null){
                    if (mErrorMessageLayout.getVisibility() == View.VISIBLE) {
                        mErrorMessageLayout.setVisibility(GONE);
                    }
                    parseJsonFeed(output);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage();
            }

            @Override
            public void onProgressUpdate(int value) {

            }

            @Override
            public void onPreExecute() {

            }
        });

        backgroundAsyncGet.execute(url);
    }

    private void displayErrorMessage() {
        if(mEventListAdapter.isEmpty()){
            mErrorMessageLayout.setVisibility(View.VISIBLE);
        }else {
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
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject eventItemJSONObj = JSONParsingObjectFromArray(jsonArray, i);

            JSONArray organizers = JSONParsingArrayFromObject(eventItemJSONObj, "organizers");

            List<ContributePeople> contributePeopleList = new ArrayList<>();

            for (int index = 0; index < organizers.length(); index++) {
                JSONObject organiserItem = JSONParsingObjectFromArray(organizers, index);

                String username = JSONParsingStringFromObject(organiserItem, "username");
                String profilePictureURL = JSONParsingStringFromObject(organiserItem, "profile_picture_url");

                ContributePeople contributePeople = new ContributePeople(username, profilePictureURL);
                contributePeopleList.add(contributePeople);
            }

            String title = JSONParsingStringFromObject(eventItemJSONObj, "event_title");
            String date = JSONParsingStringFromObject(eventItemJSONObj, "date");
            String team = JSONParsingStringFromObject(eventItemJSONObj, "team_name");
            String description = JSONParsingStringFromObject(eventItemJSONObj, "description");


            EventItem eventItem = new EventItem(title, date, team, description, contributePeopleList);

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
