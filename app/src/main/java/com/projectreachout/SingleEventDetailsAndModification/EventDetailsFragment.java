package com.projectreachout.SingleEventDetailsAndModification;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.Event.ContributePeople;
import com.projectreachout.R;
import com.projectreachout.SelectPeople.SelectPeopleActivity;
import com.projectreachout.User.UserDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.GET_ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.JSONParsingArrayFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.getDate;
import static com.projectreachout.GeneralStatic.getDomainUrl;
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

    private final String TAG = "aaaaa"/*EventDetailsFragment.class.getSimpleName()*/;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<UserDetails> mSelectedUsers = new ArrayList<>();
    private ArrayList<String> mExistingUsernames = new ArrayList<>();

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        mAddOrganizerbutton.setOnClickListener(this::selectOrganizersToAdd);

        //addDummyOrganizer();

        fetchEventDetails();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void fetchEventDetails() {
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("get_event_details")
                .appendPath("");

        String url = builder.build().toString();*/

        String url = getDomainUrl() + "/get_event_details/";

        String eventId = String.valueOf(AppController.getInstance().getGlobalEventId());

        Map<String, String> param = new HashMap<>();
        param.put("event_id", "" + eventId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    parseJsonFeed(JSONParsingObjectFromString(response));
                }
            }
        }, error -> {

        }) {
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

    private void selectOrganizersToAdd(View view) {
        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            integerArrayList.add(sparseBooleanArray.keyAt(i));
        }

        Log.v("zzzzz", "--" + integerArrayList.toString());

        Intent intent = new Intent(view.getContext(), SelectPeopleActivity.class);
        intent.putParcelableArrayListExtra(EXISTING_ORGANIZERS, mSelectedUsers);
        intent.putIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY, integerArrayList);

        startActivityForResult(intent, GET_ORGANIZER_LIST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_ORGANIZER_LIST) {
                mSelectedUsers = new ArrayList<>();
                mSelectedUsers.addAll(data != null ? data.getParcelableArrayListExtra(SELECTED_ORGANIZERS) : new ArrayList<>());

                addNewOrganizers();

                Log.d(TAG, mSelectedUsers.toString());
            }
        }
    }

    private void addNewOrganizers() {
        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("add_users_to_event")
                .appendPath("");

        String url = builder.build().toString();*/

        String url = getDomainUrl() + "/add_users_to_event/";

        Log.v(TAG, "add new users ----- " + url);

        String eventId = String.valueOf(AppController.getInstance().getGlobalEventId());
        ArrayList<String> newOrganizers = new ArrayList<>();

        for (int i = 0; i < mSelectedUsers.size(); i++) {
            boolean repeated = false;
            for (int j = 0; j < mExistingUsernames.size(); j++) {
                if (mSelectedUsers.get(i).getUser_name().equals(mExistingUsernames.get(j))) {
                    repeated = true;
                    break;
                } else {
                    repeated = false;
                }
            }
            if (!repeated) {
                newOrganizers.add(mSelectedUsers.get(i).getUser_name());
            }
        }

        Map<String, String> param = new HashMap<>();
        param.put("event_id", "" + eventId);
        param.put("organizers_to_add", newOrganizers.toString());


        Log.d(TAG, "get ::: " + eventId + " orga:: " + newOrganizers.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String output) {
                if (output != null) {
                    Log.d(TAG, "organizers added ::: " + output);
                    //parseJsonFeed(JSONParsingObjectFromString(output));
                    fetchEventDetails();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, error.toString());
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

    private void parseJsonFeed(JSONObject eventItemJSONObj) {

        JSONArray organizers = JSONParsingArrayFromObject(eventItemJSONObj, "organizers");

        // Extract individual organizer from the organizers and put it to the TableLayout
        mOrganizerTablelayout.removeAllViews();
        for (int index = 0; index < organizers.length(); index++) {
            JSONObject organiserItem = JSONParsingObjectFromArray(organizers, index);

            String username = JSONParsingStringFromObject(organiserItem, "username");
            String profilePictureURL = JSONParsingStringFromObject(organiserItem, "profile_picture_url");

            mExistingUsernames.add(username);

            ContributePeople contributePeople = new ContributePeople(username, getDomainUrl() + profilePictureURL);
            addOrganizer(contributePeople);
        }

        String title = JSONParsingStringFromObject(eventItemJSONObj, "event_title");
        String date = JSONParsingStringFromObject(eventItemJSONObj, "date");
        String assignBy = JSONParsingStringFromObject(eventItemJSONObj, "assigned_by");
        String team = JSONParsingStringFromObject(eventItemJSONObj, "team_name");
        String description = JSONParsingStringFromObject(eventItemJSONObj, "description");

        if (mListener != null){
            mListener.onFragmentInteraction(Uri.parse(title));
        }

        mEventTitleTV.setText(title);
        mDateTV.setText(getDate(date));
        mAssignByTV.setText(assignBy);
        mDescriptionTV.setText(description);
        mOrganizerCountTextView.setText("Organizers: " + organizers.length());

        try {
            JSONArray teams = new JSONArray(team);
            mTeamTV.setText("");
            for (int i = 0; i < teams.length(); i++) {
                mTeamTV.append(teams.getString(i));
                if (i != teams.length() - 1) {
                    mTeamTV.append(", ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            mTeamTV.setVisibility(View.INVISIBLE);
        }

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

        //if (profilePictureUrl != null) {
            Glide.with(Objects.requireNonNull(getContext()))
                    .load(profilePictureUrl)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).circleCrop())
                    .into(profilePictureImageView);
        //}

        userNameTextView.setText(userName);
        removeImageButton.setVisibility(View.VISIBLE);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }

        removeImageButton.setOnClickListener(v -> removeOrganizer(userName));

        mOrganizerTablelayout.addView(contributePeopleLinearLayout);
    }

    private void removeOrganizer(String userName) {
        String url = getDomainUrl() + "/remove_users_from_event/";
        String event_id = String.valueOf(AppController.getInstance().getGlobalEventId());

        ArrayList<String> organizers = new ArrayList<>();
        organizers.add(userName);

        Map<String, String> param = new HashMap<>();
        param.put("event_id", event_id);
        param.put("organizers_to_remove", organizers.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.equals("200")) {
                        fetchEventDetails();
                    }
                    Log.v("aaaaa", response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("aaaaa", error.toString());
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
