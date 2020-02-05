package com.projectreachout.Event.EventDetailsAndModification;

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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.Event.EventItem;
import com.projectreachout.R;
import com.projectreachout.SelectPeople.SelectPeopleActivity;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;
import com.projectreachout.Utilities.TimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.GET_ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class EventDetailsFragment extends Fragment implements OnHttpResponse, MessageUtils.OnSnackBarActionListener {

    private final String TAG = EventDetailsFragment.class.getSimpleName();
    private final int RC_GET_EVENT_DETAILS = 1;
    private final int RC_DELETE_ORGANIZER = 2;
    private final int RC_ADD_ORGANIZER = 3;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ArrayList<User> mSelectedUsers = new ArrayList<>();
    private ArrayList<String> mExistingUsernames = new ArrayList<>();

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    public EventDetailsFragment() {
        // Required empty public constructor
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
    private ProgressBar mLoadingPbar;

    private Button mAddOrganizerbutton;
    private Button mSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sedam_fragment_event_details, container, false);

        mEventTitleTV = rootView.findViewById(R.id.tv_sfed_event_title);
        mDateTV = rootView.findViewById(R.id.tv_sfed_date);
        mAssignByTV = rootView.findViewById(R.id.tv_sfed_assign_by);
        mTeamTV = rootView.findViewById(R.id.tv_sfed_team);
        mDescriptionTV = rootView.findViewById(R.id.tv_sfed_description);
        mOrganizerCountTextView = rootView.findViewById(R.id.tv_sfed_organizer_count);

        mOrganizerTablelayout = rootView.findViewById(R.id.tl_sfed_organizer);
        mAddOrganizerbutton = rootView.findViewById(R.id.btn_sfed_add_organizer);
        mLoadingPbar = rootView.findViewById(R.id.pbar_sfed_loading);

        mAddOrganizerbutton.setOnClickListener(this::selectOrganizersToAdd);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(getActivity())) {
            fetchEventDetails();
        }
    }

    private void fetchEventDetails() {
        mLoadingPbar.setVisibility(View.VISIBLE);

        String url = getDomainUrl() + "/get_event_details/";
        Map<String, String> param = new HashMap<>();
        param.put("event_id", AppController.getInstance().getGlobalEventId());
        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_GET_EVENT_DETAILS, null, param, this);
        httpVolleyRequest.execute();
    }

    private void selectOrganizersToAdd(View view) {
        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id

        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            integerArrayList.add(sparseBooleanArray.keyAt(i));
        }

        Log.v(TAG, "--" + integerArrayList.toString());

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
        mLoadingPbar.setVisibility(View.VISIBLE);

        String url = getDomainUrl() + "/add_organizer_to_event/";
        String eventId = AppController.getInstance().getGlobalEventId();
        ArrayList<String> newOrganizers = new ArrayList<>();

        for (int i = 0; i < mSelectedUsers.size(); i++) {
            boolean repeated = false;
            for (int j = 0; j < mExistingUsernames.size(); j++) {
                if (mSelectedUsers.get(i).getUser_id().equals(mExistingUsernames.get(j))) {
                    repeated = true;
                    break;
                } else {
                    repeated = false;
                }
            }
            if (!repeated) {
                newOrganizers.add(mSelectedUsers.get(i).getUser_id());
            }
        }

        if (newOrganizers.isEmpty()) {
            MessageUtils.showActionIndefiniteSnackBar(getActivity().findViewById(android.R.id.content), "Failed to add!!", "RETRY", RC_ADD_ORGANIZER, this);
            return;
        }

        Map<String, String> param = new HashMap<>();
        param.put("event_id", "" + eventId);
        param.put("organizers_to_add", newOrganizers.toString());

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_ADD_ORGANIZER, null, param, this);
        httpVolleyRequest.execute();
    }

    private void parseJsonFeed(String response) {
        EventItem eventItem = EventItem.fromJson(response);
        User[] organizers = eventItem.getOrganizers();
        mOrganizerTablelayout.removeAllViews();
        for (User user : organizers) {
            mExistingUsernames.add(user.getUser_id());
            addOrganizer(user);
        }

        if (mListener != null) {
            mListener.onFragmentInteraction(Uri.parse(eventItem.getEvent_title()));
        }

        mEventTitleTV.setText(eventItem.getEvent_title());
        mDateTV.setText(TimeUtil.getTimeAgaFromSecond(Long.parseLong(eventItem.getEvent_date())));
        mAssignByTV.setText(eventItem.getAssigned_by().getDisplay_name());
        mDescriptionTV.setText(eventItem.getDescription());
        mOrganizerCountTextView.setText("Organizers: " + organizers.length);

        String[] teams = eventItem.getSelected_teams();
        mTeamTV.setText("");
        for (int i = 0; i < teams.length; i++) {
            mTeamTV.append(teams[i]);
            if (i != teams.length - 1) {
                mTeamTV.append(", ");
            }
        }
    }

    private void addOrganizer(User user) {
        // inflating layout
        ViewGroup rootViewLinearLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.evn_contribute_people_item, null);
        ViewGroup contributePeopleLinearLayout = rootViewLinearLayout.findViewById(R.id.ll_ecpi_contribute_ppl);

        CircleImageView profilePictureImageView = contributePeopleLinearLayout.findViewById(R.id.civ_ecpi_profile);
        TextView userNameTextView = contributePeopleLinearLayout.findViewById(R.id.tv_ecpi_display_name);
        ImageButton removeImageButton = contributePeopleLinearLayout.findViewById(R.id.ib_ecpi_remove_user);

        String profilePictureUrl = user.getProfile_image_url();
        Glide.with(Objects.requireNonNull(getContext()))
                .load(profilePictureUrl)
                .apply(new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).circleCrop())
                .into(profilePictureImageView);
        userNameTextView.setText(user.getDisplay_name());
        removeImageButton.setVisibility(View.VISIBLE);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }
        removeImageButton.setOnClickListener(v -> removeOrganizer(user.getUser_id()));
        mOrganizerTablelayout.addView(contributePeopleLinearLayout);
    }

    private void removeOrganizer(String userId) {
        mLoadingPbar.setVisibility(View.VISIBLE);

        String url = getDomainUrl() + "/remove_organizer_from_event/";
        String event_id = AppController.getInstance().getGlobalEventId();

        Map<String, String> param = new HashMap<>();
        param.put("event_id", event_id);
        param.put("user_id", userId);

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_DELETE_ORGANIZER, null, param, this);
        httpVolleyRequest.execute();
    }

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
        Log.v(TAG, response);
        mLoadingPbar.setVisibility(View.GONE);
        switch (request) {
            case RC_GET_EVENT_DETAILS:
                parseJsonFeed(response);
                break;
            case RC_ADD_ORGANIZER:
                fetchEventDetails();
                break;
            case RC_DELETE_ORGANIZER:
                if (response.equals("200")) {
                    fetchEventDetails();
                }
                break;
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        Log.v(TAG, error.toString());
        mLoadingPbar.setVisibility(View.GONE);
        Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActionBarClicked(View view, int requestCode) {
        if (requestCode == RC_ADD_ORGANIZER) {
            selectOrganizersToAdd(new View(getActivity()));
        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}