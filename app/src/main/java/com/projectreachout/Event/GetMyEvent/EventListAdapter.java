package com.projectreachout.Event.GetMyEvent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.Event.EventDetailsAndModification.SingleEventDetailsActivity;
import com.projectreachout.Event.EventItem;
import com.projectreachout.R;
import com.projectreachout.User.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.Event.GetMyEvent.EventMainFragment.mEventItemList;
import static com.projectreachout.Event.GetMyEvent.EventMainFragment.mEventListAdapter;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDummyUrl;


public class EventListAdapter extends ArrayAdapter<EventItem> {

    public static final String TAG = EventListAdapter.class.getSimpleName();

    public EventListAdapter(Context context, int resource, List<EventItem> objects) {
        super(context, resource, objects);
    }

    private TableLayout mTableLayout;

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.evn_event_item, parent, false);
        }

        EventItem eventItem = getItem(position);
        TextView eventTitleTextView = convertView.findViewById(R.id.tv_eeei_event_title);
        TextView teamNameTextView = convertView.findViewById(R.id.tv_eeei_team_name);
        TextView dateTextView = convertView.findViewById(R.id.tv_eeei_date);
        TextView descriptionTextView = convertView.findViewById(R.id.tv_eei_description);
        ImageButton overflowImageButton = convertView.findViewById(R.id.ibtn_eeei_overflow_button);
        mTableLayout = convertView.findViewById(R.id.tl_eei_contribute_people);

        mTableLayout.removeAllViews();
        eventTitleTextView.setText(eventItem.getEvent_title());

        String[] teams = eventItem.getSelected_teams();
        teamNameTextView.setText("");
        for (int i = 0; i < teams.length; i++) {
            teamNameTextView.append(teams[i]);
            if (i != teams.length - 1) {
                teamNameTextView.append(", ");
            }
        }
        //teamNameTextView.setText(teamName);
        descriptionTextView.setText(eventItem.getDescription());
        //dateTextView.setText(getTimeAgo(date));
        dateTextView.setText(eventItem.getEvent_date());

        User[] organizers = eventItem.getOrganizers();
        for (int i = 0; i < organizers.length; i++) {
            addContributor(organizers[i]);
        }

        if (AppController.getInstance().getLoginUserAccountType().equals("superuser") || AppController.getInstance().getFirebaseAuth().getUid().equals(eventItem.getEvent_leader())) {
            overflowImageButton.setVisibility(View.VISIBLE);
        } else {
            overflowImageButton.setVisibility(View.GONE);
        }
        overflowImageButton.setOnClickListener(v -> showPopupMenu(v, eventItem.getEvent_id(), position));
        return convertView;
    }

    private void addContributor(User user) {
        /* Synchronise indices with changes in the evn_contribute_people_item.xml layouts
         * i.e. change the below index ids according to the changes in the xml layout of the contributePeopleLinearLayout
         */

        final int INDEX_PROFILE_PICTURE_TEXT_VIEW = 0;
        final int INDEX_USER_NAME_TEXT_VIEW = 1;
        final int INDEX_REMOVE_IMAGE_BUTTON = 3;
        final int INDEX_IS_GOING_CHECKBOX = 4;

        LinearLayout rootViewLinearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.evn_contribute_people_item, null);

        /* The Redundant LinearLayout from the evn_contribute_people_item. */
        LinearLayout contributePeopleLinearLayout = (LinearLayout) rootViewLinearLayout.getChildAt(0);
        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }

        LinearLayout linearLayout = (LinearLayout) contributePeopleLinearLayout.getChildAt(1);

        CircleImageView profilePictureImageView = (CircleImageView) linearLayout.getChildAt(INDEX_PROFILE_PICTURE_TEXT_VIEW);
        TextView userNameTextView = (TextView) linearLayout.getChildAt(INDEX_USER_NAME_TEXT_VIEW);

        String profilePictureUrl = user.getProfile_image_url();
        String userName = user.getDisplay_name();

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp).error(R.drawable.ic_person_black_124dp).circleCrop();
        Glide.with(getContext()).load(profilePictureUrl).apply(requestOptions).into(profilePictureImageView);

        userNameTextView.setText(userName);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }
        mTableLayout.addView(contributePeopleLinearLayout);
    }

    private void showPopupMenu(View view, String event_id, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.inflate(R.menu.evn_eei_popup_menu);

        Menu overFlowMenu = popup.getMenu();

        if (AppController.getInstance().getLoginUserAccountType().equals("superuser")) {
            overFlowMenu.findItem(R.id.menu_eepm_modify).setVisible(true);
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(true);
        } else {
            overFlowMenu.findItem(R.id.menu_eepm_modify).setVisible(true);
            overFlowMenu.findItem(R.id.menu_eepm_delete).setVisible(false);
        }

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_eepm_modify: {
                    AppController.getInstance().setGlobalEventId(event_id);

                    Class main = SingleEventDetailsActivity.class;
                    Intent intent = new Intent(view.getContext(), main);
                    view.getContext().startActivity(intent);
                    break;
                }
                case R.id.menu_eepm_delete: {
                    deleteEvent(event_id, position);
                }
            }
            return true;
        });

        popup.inflate(R.menu.evn_eei_popup_menu);
        popup.show();
    }

    private void deleteEvent(String event_id, int position) {
        String url = getDummyUrl() + "/delete_event/";

        Map<String, String> param = new HashMap<>();
        param.put("event_id", String.valueOf(event_id));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (JSONParsingStringFromObject(JSONParsingObjectFromString(response), "status").trim().equals("200")) {
                    mEventItemList.remove(position);
                    mEventListAdapter.notifyDataSetChanged();
                }
                Log.v(TAG, response);
            }
        }, error -> Log.v(TAG, error.toString())) {
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
}