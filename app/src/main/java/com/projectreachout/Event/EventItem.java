package com.projectreachout.Event;

import com.google.gson.Gson;
import com.projectreachout.User.User;

public class EventItem {
    private String event_id;
    private String event_title;
    private User event_leader;
    private User assigned_by;
    private String created_on;
    private String event_date;
    private String description;
    private User[] organizers;
    private String[] selected_teams;
    private String investment_amount;
    private String investment_return;

    public EventItem() { }

    public static EventItem fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, EventItem.class);
    }

    public User getEvent_leader() {
        return event_leader;
    }
    public User getAssigned_by() {
        return assigned_by;
    }
    public String getCreated_on() {
        return created_on;
    }
    public String getEvent_date() {
        return event_date;
    }
    public User[] getOrganizers() {
        return organizers;
    }
    public String[] getSelected_teams() { return selected_teams; }
    public String getInvestment_amount() {
        return investment_amount;
    }
    public String getInvestment_return() {
        return investment_return;
    }
    public String getEvent_id() {
        return event_id;
    }
    public String getDescription() {
        return description;
    }
    public String getEvent_title() {
        return event_title;
    }
}
