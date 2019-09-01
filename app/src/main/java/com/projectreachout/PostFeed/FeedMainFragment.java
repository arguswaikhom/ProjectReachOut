package com.projectreachout.PostFeed;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.MainActivity;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
 * {@link FeedMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedMainFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = FeedMainFragment.class.getSimpleName();

    public FeedMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedMainFragment newInstance(String param1, String param2) {
        FeedMainFragment fragment = new FeedMainFragment();
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

    @Override
    public void onResume() {
        super.onResume();
    }

    public final String LOG_TAG_FMF = FeedMainFragment.class.getSimpleName();

    public static FeedListAdapter mFeedListAdapter;

    //private FeedAdapter mFeedAdapter;

    public static List<FeedItem> mFeedItemList;
    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);

        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(getString(R.string.title_home)));
        }

        ListView listView = rootView.findViewById(R.id.lv_lvl_list_view);
        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        mFeedItemList = new ArrayList<>();

        mFeedListAdapter = new FeedListAdapter(getActivity(), mFeedItemList);
        listView.setAdapter(mFeedListAdapter);

        /*mFeedAdapter = new FeedAdapter(getContext() , R.layout.pf_feed_item, mFeedItemListMyArticles);
        listView.setAdapter(mFeedAdapter);*/

        loadData(REFRESH);

        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));

        return rootView;
    }

    private void loadData(int action) {
        /*Uri.Builder builder = new Uri.Builder();
        // TODO: use .authority(getString(R.string.localhost)) after having a domain name
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("get_articles")
                .appendPath("");*/

        String url = getDomainUrl() + "/get_articles/";

        switch (action) {
            case REFRESH: {
                loadBackgroundAsyncTask(url);
            }
            case LOAD_MORE: {
                // TODO: get timeStamp of the last post in the feed list
                /*String lastPostTimeStamp = "1556604826";

                builder.appendQueryParameter("before", lastPostTimeStamp);
                loadBackgroundAsyncTask(builder.build().toString());*/
            }
        }
    }

    private void loadBackgroundAsyncTask(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String output) {
                if (output != null) {
                    if (mErrorMessageLayout.getVisibility() == View.VISIBLE) {
                        mErrorMessageLayout.setVisibility(View.GONE);
                    }
                    Log.v(TAG, output);
                    parseJsonFeed(JSONParsingArrayFromString(output));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void displayErrorMessage() {
        if (mFeedListAdapter.isEmpty()) {
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

    private void parseJsonFeed(JSONArray response) {
        mFeedItemList.clear();
        for (int i = response.length()-1; i >= 0; i--) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);

            /*Uri.Builder builder = new Uri.Builder();
            builder.scheme(getString(R.string.http))
                    .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no));

            String url = builder.build().toString();*/

            String url = getDomainUrl();

            int id = JSONParsingIntFromObject(feedObj, "article_id");
            String teamName = JSONParsingStringFromObject(feedObj, "team_name");
            String userName = JSONParsingStringFromObject(feedObj, "username");
            String timeStamp = JSONParsingStringFromObject(feedObj, "time_stamp");
            String profilePictureUrl = JSONParsingStringFromObject(feedObj, "profile_picture_url");
            String imageUrl = JSONParsingStringFromObject(feedObj, "image");
            String description = JSONParsingStringFromObject(feedObj, "desc");

            Log.v(TAG, imageUrl);

            FeedItem item = new FeedItem();

            item.setId(id);
            item.setTeam_name(teamName);
            item.setUsername(userName);
            item.setTime_stamp(timeStamp);

            item.setProfile_picture_url(url + profilePictureUrl);
            item.setImage_url(url + imageUrl);

            item.setDescription(description);

            String string = item.toString();
            Log.v(LOG_TAG_FMF, string);

            mFeedItemList.add(item);

            //mFeedAdapter.add(item);
        }
        mFeedListAdapter.notifyDataSetChanged();
        //mFeedAdapter.notifyDataSetChanged();
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
}