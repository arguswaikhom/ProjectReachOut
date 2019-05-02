package com.projectreachout.Event;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EventItem {
    private String event_title;
    private String date;
    private String team_name;
    private String description;
    private String assignBy;
    private String investmentInReturn;
    private String investmentAmount;
    private List<ContributePeople> contribute_people_list = new ArrayList<>();

    public EventItem() {
    }

    public EventItem(String event_title, String date, String team_name, String description, String assignBy, String investmentInReturn, String investmentAmount) {
        this.event_title = event_title;
        this.date = date;
        this.team_name = team_name;
        this.description = description;
        this.assignBy = assignBy;
        this.investmentInReturn = investmentInReturn;
        this.investmentAmount = investmentAmount;
    }

    public EventItem(String event_title, String date, String team_name, String description, List<ContributePeople> contribute_people_list) {
        this.event_title = event_title;
        this.date = date;
        this.team_name = team_name;
        this.description = description;
        this.contribute_people_list = contribute_people_list;
    }

    public EventItem(String event_title, String date, String team_name) {
        this.event_title = event_title;
        this.date = date;
        this.team_name = team_name;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTeam_name(String team_name) {
        this.team_name = team_name;
    }

    public void setContribute_people_list(List<ContributePeople> contribute_people_list) {
        this.contribute_people_list = contribute_people_list;
    }

    public void addItemContributePeopleList(ContributePeople contributePeople) {
        contribute_people_list.add(contributePeople);
    }

    public String getEvent_title() {
        return event_title;
    }

    public String getDate() {
        return date;
    }

    public String getTeam_name() {
        return team_name;
    }

    public List<ContributePeople> getContribute_people_list() {
        return contribute_people_list;
    }
}
