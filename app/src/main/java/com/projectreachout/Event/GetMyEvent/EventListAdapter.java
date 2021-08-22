package com.projectreachout.Event.GetMyEvent;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;

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
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.projectreachout.AppController;
import com.projectreachout.Event.EventDetailsAndModification.SingleEventDetailsActivity;
import com.projectreachout.Event.EventItem;
import com.projectreachout.R;
import com.projectreachout.User.User;
import com.projectreachout.Utilities.CallbackUtilities.OnInteractionWithItem;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;
import com.projectreachout.Utilities.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class EventListAdapter extends ArrayAdapter<EventItem> implements OnHttpResponse {

    public static final String TAG = EventListAdapter.class.getSimpleName();
    public static final int RC_DELETE_EVENT = 1;
    public static final int RC_DELETED_EVENT = 100;
    public static final int RC_FAIL_TO_DELETE = 200;

    private OnInteractionWithItem mOnInteractionWithItem;

    public EventListAdapter(Context context, int resource, List<EventItem> objects, OnInteractionWithItem onInteractionWithItem) {
        super(context, resource, objects);
        this.mOnInteractionWithItem = onInteractionWithItem;
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
        descriptionTextView.setText(eventItem.getDescription());
        dateTextView.setText(TimeUtil.getTimeAgaFromSecond(Long.parseLong(eventItem.getEvent_date())));

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
        ViewGroup rootViewLinearLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.evn_contribute_people_item, null);

        /* The Redundant LinearLayout from the evn_contribute_people_item. */
        ViewGroup contributePeopleLinearLayout = rootViewLinearLayout.findViewById(R.id.ll_ecpi_contribute_ppl);
        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }

        CircleImageView profilePictureImageView = contributePeopleLinearLayout.findViewById(R.id.civ_ecpi_profile);
        TextView userNameTextView = contributePeopleLinearLayout.findViewById(R.id.tv_ecpi_display_name);

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
        String url = getDomainUrl() + "/delete_event/";

        Map<String, String> param = new HashMap<>();
        param.put("event_id", String.valueOf(event_id));

        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, RC_DELETE_EVENT, null, param, this);
        httpVolleyRequest.execute();
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        if (request == RC_DELETE_EVENT) {
            if (response != null) {
                if (JSONParsingStringFromObject(JSONParsingObjectFromString(response), "status").trim().equals("200")) {
                    mOnInteractionWithItem.onInteractionWithItem(RC_DELETED_EVENT, "Item deleted.");
                }
            }
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {
        if (request == RC_DELETE_EVENT) {
            mOnInteractionWithItem.onInteractionWithItem(RC_FAIL_TO_DELETE, "Delete failed.");
        }
    }
}