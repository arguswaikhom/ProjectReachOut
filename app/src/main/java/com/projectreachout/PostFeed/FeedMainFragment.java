package com.projectreachout.PostFeed;

import android.content.Context;
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

import com.android.volley.VolleyError;
import com.projectreachout.NetworkUtils.AsyncResponseGet;
import com.projectreachout.NetworkUtils.BackgroundAsyncGet;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.LOAD_MORE;
import static com.projectreachout.GeneralStatic.REFRESH;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedMainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedMainFragment extends Fragment {

    public final String DUMMY_FEED_DATA_JSON = "{\n" +
            "    \"feed\": [\n" +
            "        {\n" +
            "            \"id\": 1,\n" +
            "            \"team_name\": \"Regular Volunteers\",\n" +
            "            \"username\": \"Valerye Minihan\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/nat.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/cosmos.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 2,\n" +
            "            \"team_name\": \"School Event\",\n" +
            "            \"username\": \"Prescott De Lascy\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/time.png\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/time_best.jpg\",\n" +
            "            \"description\": \"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 3,\n" +
            "            \"team_name\": \"President\",\n" +
            "            \"username\": \"Demott Falconer-Taylo\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/lincoln.jpg\",\n" +
            "            \"image_url\": null,\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 4,\n" +
            "            \"team_name\": \"Teaching\",\n" +
            "            \"username\": \"Franklin Halle\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/discovery.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/discovery_mos.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 5,\n" +
            "            \"team_name\": \"Fund Raising\",\n" +
            "            \"username\": \"Cherilynn Bampkin\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/ravi_tamada.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/nav_drawer.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 6,\n" +
            "            \"team_name\": \"School Event\",\n" +
            "            \"username\": \"Nicolette Jerzak\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/ktm.png\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/ktm_1290.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 7,\n" +
            "            \"team_name\": \"School Event\",\n" +
            "            \"username\": \"Lockwood Sheplande\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/harley.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/harley_bike.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 8,\n" +
            "            \"team_name\": \"Fund Raising\",\n" +
            "            \"username\": \"Devland Cleatherow\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/rock_girl.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/rock.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 9,\n" +
            "            \"team_name\": \"Teaching\",\n" +
            "            \"username\": \"Claiborne Dubois\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/gandhi.jpg\",\n" +
            "            \"image_url\": null,\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 10,\n" +
            "            \"team_name\": \"Fund Raising\",\n" +
            "            \"username\": \"Shell Croysdale\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/life.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/life_photo.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 11,\n" +
            "            \"team_name\": \"Event Manager\",\n" +
            "            \"username\": \"Thornton Vivers\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/shakira.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/shakira_la_la.png\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 12,\n" +
            "            \"team_name\": \"Regular Volunteers\",\n" +
            "            \"username\": \"A. R. rahman\",\n" +
            "            \"time_stamp\": \"1403375851930\",\n" +
            "            \"profile_picture_url\": \"https://api.androidhive.info/feed/img/ar.jpg\",\n" +
            "            \"image_url\": \"https://api.androidhive.info/feed/img/ar_bw.jpg\",\n" +
            "            \"description\": \"\\\"Science is a beautiful and emotional human endeavor,\\\" says Brannon Braga, executive producer and director. \\\"And Cosmos is all about making science an experience.\\\"\"\n" +
            "        }\n" +
            "\n" +
            "    ]\n" +
            "}\n";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    public final String LOG_TAG_FMF = FeedMainFragment.class.getSimpleName();

    private FeedListAdapter mFeedListAdapter;

    //private FeedAdapter mFeedAdapter;

    private List<FeedItem> mFeedItemList;
    private LinearLayout mErrorMessageLayout;
    private Button mRetryBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.list_view_layout, container, false);

        ListView listView = rootView.findViewById(R.id.lv_lvl_list_view);
        mErrorMessageLayout = rootView.findViewById(R.id.ll_lvl_error_message_layout);
        mRetryBtn = rootView.findViewById(R.id.btn_nel_retry);

        mFeedItemList = new ArrayList<>();

        mFeedListAdapter = new FeedListAdapter(getActivity(), mFeedItemList);
        listView.setAdapter(mFeedListAdapter);

        /*mFeedAdapter = new FeedAdapter(getContext() , R.layout.pf_feed_item, mFeedItemList);
        listView.setAdapter(mFeedAdapter);*/

        loadData(REFRESH);

        mRetryBtn.setOnClickListener(v -> loadData(REFRESH));

        return rootView;
    }

    private void loadData(int action) {
        Uri.Builder builder = new Uri.Builder();
        // TODO: use .authority(getString(R.string.localhost)) after having a domain name
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("articles");

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
                if (output != null) {
                    if(mErrorMessageLayout.getVisibility() == View.VISIBLE){
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
        if(mFeedListAdapter.isEmpty()){
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

    private void parseJsonFeed(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject feedObj = JSONParsingObjectFromArray(response, i);

            String teamName = JSONParsingStringFromObject(feedObj, "team_name");
            String userName = JSONParsingStringFromObject(feedObj, "username");
            String timeStamp = JSONParsingStringFromObject(feedObj, "time_stamp");
            String profilePictureUrl = JSONParsingStringFromObject(feedObj, "profile_picture_url");
            String imageUrl = JSONParsingStringFromObject(feedObj, "image_url");
            String description = JSONParsingStringFromObject(feedObj, "description");

            FeedItem item = new FeedItem();

            item.setTeam_name(teamName);
            item.setUsername(userName);
            item.setTime_stamp(timeStamp);
            item.setProfile_picture_url(profilePictureUrl);
            item.setImage_url(imageUrl);
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
