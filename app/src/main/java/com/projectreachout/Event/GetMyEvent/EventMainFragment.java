package com.projectreachout.Event.GetMyEvent;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromString;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;
import static com.projectreachout.GeneralStatic.getDomainUrl;

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

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.projectreachout.AppController;
import com.projectreachout.Event.EventItem;
import com.projectreachout.R;
import com.projectreachout.Utilities.CallbackUtilities.OnInteractionWithItem;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class EventMainFragment extends Fragment implements OnHttpResponse, OnInteractionWithItem {

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

        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_my_events)));
        }

        mListView = rootView.findViewById(R.id.lv_lvl_list_view);
        mFAB = rootView.findViewById(R.id.fab_lvl_fab);

        /*mFAB.show();
        mFAB.setImageResource(R.drawable.ic_add_white_24dp);*/

        mEventItemList = new ArrayList<>();
        mEventListAdapter = new EventListAdapter(getContext(), R.layout.evn_event_item, mEventItemList, this);
        mListView.setAdapter(mEventListAdapter);

        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        /*mFAB.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddEventActivity.class);
            startActivity(intent);
        });*/

        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(getActivity())) {
            loadData(REFRESH);
        }
        // BackgoundServerChecker.backgroundCheck(BackgoundServerChecker.ACTION_MY_EVENT_ONLY);
    }

    private void loadData(int action) {
        mListener.onUpdateProgressVisibility(View.VISIBLE);
        String url = getDomainUrl() + "/get_my_event/";

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
        param.put("user_id", AppController.getInstance().getFirebaseAuth().getUid());
        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, 0, null, param, this);
        httpVolleyRequest.execute();
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
        for (int i = jsonArray.length() - 1; i >= 0; i--) {
            JSONObject eventItemJSONObj = JSONParsingObjectFromArray(jsonArray, i);
            EventItem eventItem = EventItem.fromJson(eventItemJSONObj.toString());
            mEventListAdapter.add(eventItem);
        }
        mEventListAdapter.notifyDataSetChanged();
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

    @Override
    public void onHttpResponse(String response, int request) {
        Log.d(TAG, response);
        mListener.onUpdateProgressVisibility(View.INVISIBLE);
        if (request == 0) {
            parseJsonFeed(JSONParsingArrayFromString(response));
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.d(TAG, error.toString());
        mListener.onUpdateProgressVisibility(View.INVISIBLE);
    }

    @Override
    public void onInteractionWithItem(int request, String message) {
        switch (request) {
            case EventListAdapter.RC_FAIL_TO_DELETE:
            case EventListAdapter.RC_DELETED_EVENT: {
                mListener.onUpdateProgressVisibility(View.INVISIBLE);
                MessageUtils.showShortToast(getContext(), message);
                loadData(REFRESH);
                break;
            }
            case EventListAdapter.RC_DELETE_EVENT: {
                mListener.onUpdateProgressVisibility(View.VISIBLE);
                break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onUpdateProgressVisibility(int visibility);
        void onFragmentInteraction(Uri uri);
    }
}
