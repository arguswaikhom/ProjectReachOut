package com.projectreachout.Event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.SingleEventDetailsAndModification.SingleEventDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.Event.EventMainFragment.mEventItemList;
import static com.projectreachout.Event.EventMainFragment.mEventListAdapter;
import static com.projectreachout.GeneralStatic.getDate;
import static com.projectreachout.GeneralStatic.getDateTime;
import static com.projectreachout.GeneralStatic.getDomainUrl;


public class EventListAdapter extends ArrayAdapter<EventItem> {

    public static final String LOG_TAG = EventListAdapter.class.getSimpleName();

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

        EventItem currentEventItem = getItem(position);

        TextView eventTitleTextView = convertView.findViewById(R.id.tv_eeei_event_title);
        TextView teamNameTextView = convertView.findViewById(R.id.tv_eeei_team_name);
        TextView dateTextView = convertView.findViewById(R.id.tv_eeei_date);
        TextView descriptionTextView = convertView.findViewById(R.id.tv_eei_description);
        ImageButton  overflowImageButton = convertView.findViewById(R.id.ibtn_eeei_overflow_button);
        mTableLayout = convertView.findViewById(R.id.tl_eei_contribute_people);

        mTableLayout.removeAllViews();

        String eventTitle = currentEventItem.getEvent_title();
        String date = currentEventItem.getDate();
        String teamName = currentEventItem.getTeam_name();
        String description = currentEventItem.getDescription();
        String eventLeader = currentEventItem.getEventLeader();

        List<ContributePeople> contributePeopleList = currentEventItem.getContribute_people_list();

        eventTitleTextView.setText(eventTitle);

        try {
            JSONArray teams = new JSONArray(teamName);
            teamNameTextView.setText("");
            for (int i=0; i<teams.length(); i++) {
                teamNameTextView.append(teams.getString(i));
                if (i != teams.length()-1) {
                    teamNameTextView.append(", ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            teamNameTextView.setVisibility(View.INVISIBLE);
        }

        //teamNameTextView.setText(teamName);
        descriptionTextView.setText(description);


        //dateTextView.setText(getTimeAgo(date));
        dateTextView.setText(getDate(date));

        for (int i = 0; i < contributePeopleList.size(); i++) {
            addContributor(contributePeopleList.get(i));
        }

        if (AppController.getInstance().getLoginUserAccountType().equals("superuser") || AppController.getInstance().getLoginUserUsername().equals(eventLeader)) {
            overflowImageButton.setVisibility(View.VISIBLE);
        } else {
            overflowImageButton.setVisibility(View.GONE);
        }

        overflowImageButton.setOnClickListener(v -> showPopupMenu(v, currentEventItem.getEvent_id(), position));

        return convertView;
    }

    private void addContributor(ContributePeople contributePeople){

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

        String profilePictureUrl = contributePeople.getProfile_picture_url();
        String userName = contributePeople.getUser_name();

        //if (profilePictureUrl != null) {
            RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_person_black_124dp)
                    .error(R.drawable.ic_person_black_124dp).circleCrop();

            Glide.with(getContext()).load(getDomainUrl() + profilePictureUrl).apply(requestOptions).into(profilePictureImageView);
       // }

        userNameTextView.setText(userName);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }
        mTableLayout.addView(contributePeopleLinearLayout);
    }

    private void showPopupMenu(View view, int event_id, int position) {
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

    private void deleteEvent(int event_id, int position) {
        String url = getDomainUrl() + "/delete_event/";

        Log.d(LOG_TAG, url);
        Log.d(LOG_TAG, String.valueOf(event_id));

        Map<String, String> param = new HashMap<>();
        param.put("event_id", String.valueOf(event_id));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.trim().equals("200")) {
                        mEventItemList.remove(position);
                        mEventListAdapter.notifyDataSetChanged();
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
}