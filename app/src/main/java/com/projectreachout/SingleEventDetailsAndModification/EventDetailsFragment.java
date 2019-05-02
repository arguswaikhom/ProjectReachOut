package com.projectreachout.SingleEventDetailsAndModification;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.projectreachout.Event.ContributePeople;
import com.projectreachout.Event.EventItem;
import com.projectreachout.NetworkUtils.AsyncResponseGet;
import com.projectreachout.NetworkUtils.BackgroundAsyncGet;
import com.projectreachout.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getRandomInt;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EventDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EventDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventDetailsFragment extends Fragment {

    private final String LOG_TAG = EventDetailsFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EventDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EventDetailsFragment newInstance(String param1, String param2) {
        EventDetailsFragment fragment = new EventDetailsFragment();
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

    private TextView mEventTitleTV;
    private TextView mDateTV;
    private TextView mAssignByTV;
    private TextView mTeamTV;
    private TextView mDescriptionTV;
    private TextView mOrganizerCountTextView;

    private TableLayout mOrganizerTablelayout;

    private Button mAddOrganizerbutton;
    private Button mSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.sedam_fragment_event_details, container, false);

        mEventTitleTV = rootView.findViewById(R.id.tv_sfed_event_title);
        mDateTV = rootView.findViewById(R.id.tv_sfed_date);
        mAssignByTV = rootView.findViewById(R.id.tv_sfed_assign_by);
        mTeamTV = rootView.findViewById(R.id.tv_sfed_team);
        mDescriptionTV = rootView.findViewById(R.id.tv_sfed_description);
        mOrganizerCountTextView = rootView.findViewById(R.id.tv_sfed_organizer_count);

        mOrganizerTablelayout = rootView.findViewById(R.id.tl_sfed_organizer);
        mAddOrganizerbutton = rootView.findViewById(R.id.btn_sfed_add_organizer);

        //addDummyOrganizer();

        // TODO: Implement getEventId()
        String eventId = String.valueOf(getRandomInt(1000, 10000));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .authority(getString(R.string.localhost))
                .appendPath("event")
                .appendQueryParameter("event_id", eventId);

        String url = builder.build().toString();

        /*
         * TODO: Implement the empty methods
         * */

         BackgroundAsyncGet backgroundAsyncGet = new BackgroundAsyncGet(new AsyncResponseGet() {
             @Override
             public void onResponse(JSONArray output) {
                 if(output != null){
                     parseJsonFeed(output);
                 }
             }

             @Override
             public void onErrorResponse(VolleyError error) {

             }

             @Override
             public void onProgressUpdate(int value) {

             }

             @Override
             public void onPreExecute() {

             }
         });
         backgroundAsyncGet.execute(url);

         return rootView;
    }

    private void parseJsonFeed(JSONArray jsonArray) {
        JSONObject eventItemJSONObj = JSONParsingObjectFromArray(jsonArray, 0);

        JSONArray organizers = JSONParsingArrayFromObject(eventItemJSONObj, "organizers");

        // Extract individual organizer from the organizers and put it to the TableLayout
        for (int index = 0; index < organizers.length(); index++) {
            JSONObject organiserItem = JSONParsingObjectFromArray(organizers, index);

            String username = JSONParsingStringFromObject(organiserItem, "username");
            String profilePictureURL = JSONParsingStringFromObject(organiserItem, "profile_picture_url");

            ContributePeople contributePeople = new ContributePeople(username, profilePictureURL);
            addOrganizer(contributePeople);
        }

        String title = JSONParsingStringFromObject(eventItemJSONObj, "event_title");
        String date = JSONParsingStringFromObject(eventItemJSONObj, "date");
        String assignBy = JSONParsingStringFromObject(eventItemJSONObj, "assign_by");
        String team = JSONParsingStringFromObject(eventItemJSONObj, "team_name");
        String description = JSONParsingStringFromObject(eventItemJSONObj, "description");

        mEventTitleTV.setText(title);
        mDateTV.setText(date);
        mAssignByTV.setText(assignBy);
        mTeamTV.setText(team);
        mDescriptionTV.setText(description);
        mOrganizerCountTextView.setText("Organizers: " + organizers.length());
    }

    private void addOrganizer(ContributePeople contributePeople) {
        final int INDEX_PROFILE_PICTURE_TEXT_VIEW = 0;
        final int INDEX_USER_NAME_TEXT_VIEW = 1;
        final int INDEX_REMOVE_IMAGE_BUTTON = 3;
        final int INDEX_IS_GOING_CHECKBOX = 4;

        // inflating layout
        LinearLayout rootViewLinearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.evn_contribute_people_item, null);

        LinearLayout contributePeopleLinearLayout = (LinearLayout) rootViewLinearLayout.getChildAt(0);

        LinearLayout linearLayout = (LinearLayout) contributePeopleLinearLayout.getChildAt(1);

        CircleImageView profilePictureImageView = (CircleImageView) linearLayout.getChildAt(INDEX_PROFILE_PICTURE_TEXT_VIEW);
        TextView userNameTextView = (TextView) linearLayout.getChildAt(INDEX_USER_NAME_TEXT_VIEW);
        ImageButton removeImageButton = (ImageButton) linearLayout.getChildAt(INDEX_REMOVE_IMAGE_BUTTON);

        String profilePictureUrl = contributePeople.getProfile_picture_url();
        String userName = contributePeople.getUser_name();

        if (profilePictureUrl != null) {
            Glide.with(Objects.requireNonNull(getContext())).load(profilePictureUrl).into(profilePictureImageView);
        }

        userNameTextView.setText(userName);
        removeImageButton.setVisibility(View.VISIBLE);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }
        mOrganizerTablelayout.addView(contributePeopleLinearLayout);
    }

    private void addDummyOrganizer() {
        // Generation dummy user content for organizer
        List<ContributePeople> contributePeopleList = new ArrayList<>();
        int size = getRandomInt(4, 15);
        for (int j = 0; j < size; j++) {
            ContributePeople contributePeople = new ContributePeople("UserName_" + (j + 1),
                    "https://api.androidhive.info/json/images/johnny.jpg");
            contributePeopleList.add(contributePeople);
        }

        mOrganizerCountTextView.setText("Organizer: " + size);

        // adding generated dummy content to the table layout
        for (int i = 0; i < contributePeopleList.size(); i++) {
            ContributePeople contributePeople = contributePeopleList.get(i);
            addOrganizer(contributePeople);
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
}
