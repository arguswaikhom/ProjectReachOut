package com.projectreachout.Event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.projectreachout.R;
import com.projectreachout.SingleEventDetailsAndModification.SingleEventDetailsActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projectreachout.GeneralStatic.*;


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

        TextView eventTitleTextView = convertView.findViewById(R.id.tv_eei_event_title);
        TextView teamNameTextView = convertView.findViewById(R.id.tv_eei_team_name);
        TextView dateTextView = convertView.findViewById(R.id.tv_eei_date);
        ImageButton  overflowImageButton = convertView.findViewById(R.id.ibtn_eei_overflow_button);
        mTableLayout = convertView.findViewById(R.id.tl_eei_contribute_people);

        mTableLayout.removeAllViews();

        overflowImageButton.setOnClickListener(this::showPopupMenu);

        assert currentEventItem != null;
        String eventTitle = currentEventItem.getEvent_title();
        String date = currentEventItem.getDate();
        String teamName = currentEventItem.getTeam_name();
        List<ContributePeople> contributePeopleList = currentEventItem.getContribute_people_list();

        eventTitleTextView.setText(eventTitle);
        teamNameTextView.setText(teamName);
        dateTextView.setText(getTimeAgo(date));

        for (int i = 0; i < contributePeopleList.size(); i++) {
            addContributor(contributePeopleList.get(i));
        }
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

        if (profilePictureUrl != null) {
            Glide.with(getContext()).load(profilePictureUrl).into(profilePictureImageView);
        }

        userNameTextView.setText(userName);

        if (contributePeopleLinearLayout.getParent() != null) {
            ((ViewGroup) contributePeopleLinearLayout.getParent()).removeView(contributePeopleLinearLayout);
        }
        mTableLayout.addView(contributePeopleLinearLayout);
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.menu_eepm_modify: {
                    Class main = SingleEventDetailsActivity.class;
                    Intent intent = new Intent(view.getContext(), main);
                    view.getContext().startActivity(intent);
                    break;
                }
            }
            return true;
        });

        popup.inflate(R.menu.evn_eei_popup_menu);
        popup.show();
    }
}