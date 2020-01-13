package com.projectreachout.Event.GetEvent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.Event.EventDetailsAndModification.SingleEventDetailsActivity;
import com.projectreachout.Event.GetMyEvent.EventItem;
import com.projectreachout.R;
import com.projectreachout.Utilities.MessageUtilities.MessageUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.Event.GetEvent.ExpendituresMainFragment.mEventItemList;
import static com.projectreachout.Event.GetEvent.ExpendituresMainFragment.mEventListAdapter;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDate;
import static com.projectreachout.GeneralStatic.getDummyUrl;

public class ExpendituresEventListAdapter extends ArrayAdapter<EventItem> {

    private final String TAG = ExpendituresEventListAdapter.class.getSimpleName();

    public ExpendituresEventListAdapter(Context context, int resource, List<EventItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.evn_exp_event_item, parent, false);
        }

        EventItem eventItem = getItem(position);

        TextView titleTV = convertView.findViewById(R.id.tv_eeei_event_title);
        ImageButton optionEB = convertView.findViewById(R.id.ibtn_eeei_overflow_button);
        TextView teamNameTV = convertView.findViewById(R.id.tv_eeei_team_name);
        TextView dateTV = convertView.findViewById(R.id.tv_eeei_date);
        TextView assignedByTV = convertView.findViewById(R.id.tv_eeei_assigned_by);
        TextView organizerCountTV = convertView.findViewById(R.id.tv_eeei_organizers_count);
        TextView investedAmountTV = convertView.findViewById(R.id.tv_eeei_invested_amount);
        TextView investmentInReturnAmountTV = convertView.findViewById(R.id.tv_eeei_in_return_amount);
        TextView descriptionTV = convertView.findViewById(R.id.tv_eeei_description);

        try {
            JSONArray teams = new JSONArray(eventItem.getTeam_name());
            teamNameTV.setText("");
            for (int i=0; i<teams.length(); i++) {
                teamNameTV.append(teams.getString(i));
                if (i != teams.length()-1) {
                    teamNameTV.append(", ");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            teamNameTV.setVisibility(View.INVISIBLE);
        }

        String eventLeader = eventItem.getEventLeader();

        if (AppController.getInstance().getLoginUserAccountType().equals("superuser") || AppController.getInstance().getLoginUserUsername().equals(eventLeader)) {
            optionEB.setVisibility(View.VISIBLE);
        } else {
            optionEB.setVisibility(View.GONE);
        }

        optionEB.setOnClickListener(v -> showPopupMenu(v, eventItem.getEvent_id(), position));

        titleTV.setText(eventItem.getEvent_title());
        //teamNameTV.setText(eventItem.getTeam_name());
        dateTV.setText(getDate(eventItem.getDate()));
        assignedByTV.setText(Html.fromHtml("<font color='#000000'>Assign by: </font>" + "<i>" + eventItem.getAssignBy() + "</i>"));
        organizerCountTV.setText(Html.fromHtml("<font color='#000000'>Organizer: </font>" + "<i>" + eventItem.getOrganizerCount() + "</i>"));
        investedAmountTV.setText(Html.fromHtml("<font color='#000000'>Invested: </font>" + "<i>" + eventItem.getInvestmentAmount() + "</i>"));
        investmentInReturnAmountTV.setText(Html.fromHtml("<font color='#000000'>In Return: </font>" + "<i>" + eventItem.getInvestmentInReturn() + "</i>"));
        descriptionTV.setText(eventItem.getDescription());

        return convertView;
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

        /*Log.d(LOG_TAG, url);
        Log.d(LOG_TAG, String.valueOf(event_id));
*/
        Map<String, String> param = new HashMap<>();
        param.put("event_id", String.valueOf(event_id));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (JSONParsingStringFromObject(JSONParsingObjectFromString(response), "status").trim().equals("200")) {
                        MessageUtils.showShortToast(getContext(), "Event deleted");
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
