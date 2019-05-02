package com.projectreachout.Event.Expenditures;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.projectreachout.Event.EventItem;
import com.projectreachout.NetworkUtils.AsyncResponseGet;
import com.projectreachout.NetworkUtils.BackgroundAsyncGet;
import com.projectreachout.R;
import com.projectreachout.SingleEventDetailsAndModification.SingleEventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.view.View.GONE;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpendituresMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpendituresMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpendituresMainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExpendituresMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpendituresMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpendituresMainFragment newInstance(String param1, String param2) {
        ExpendituresMainFragment fragment = new ExpendituresMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ListView mListView;
    private ExpendituresEventListAdapter mEventListAdapter;
    private List<EventItem> mEventItemList;

    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);

        mListView = rootView.findViewById(R.id.lv_lvl_list_view);

        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        mEventItemList = new ArrayList<>();
        mEventListAdapter = new ExpendituresEventListAdapter(getContext(), R.layout.evn_exp_event_item, mEventItemList);
        mListView.setAdapter(mEventListAdapter);

        mListView.setOnItemClickListener(this::onItemClick);

        //addDummyEvent();

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
                // TODO: get timeStamp of the last event in the list
                String lastEventTimeStamp = "1556604826";

                builder.appendQueryParameter("before", lastEventTimeStamp);
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
                        mErrorMessageLayout.setVisibility(View.GONE);
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

            String title = JSONParsingStringFromObject(eventItemJSONObj, "event_title");
            String date = JSONParsingStringFromObject(eventItemJSONObj, "date");
            String team = JSONParsingStringFromObject(eventItemJSONObj, "team_name");
            String assignBy = JSONParsingStringFromObject(eventItemJSONObj, "assign_by");
            String investedAmount = JSONParsingStringFromObject(eventItemJSONObj, "investment_amount");
            String investmentInReturn = JSONParsingStringFromObject(eventItemJSONObj, "investment_in_return");
            String description = JSONParsingStringFromObject(eventItemJSONObj, "description");

            EventItem eventItem = new EventItem(title, date, team, description, assignBy, investmentInReturn, investedAmount);

            mEventListAdapter.add(eventItem);
        }
        mEventListAdapter.notifyDataSetChanged();
    }

    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Objects.requireNonNull(getActivity()).startActivity(new Intent(getContext(), SingleEventDetailsActivity.class));
    }

    /*private void addDummyEvent() {
        for (int i = 0; i < 20; i++) {
            mEventListAdapter.add(new EventItem());
        }
        mEventListAdapter.notifyDataSetChanged();
    }*/

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
}
