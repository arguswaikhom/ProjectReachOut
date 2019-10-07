package com.projectreachout.Event;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EventItem {
    private int event_id;
    private String event_title;
    private String date;
    private String team_name;
    private String description;
    private String assignBy;
    private String investmentInReturn;
    private String investmentAmount;
    private int organizerCount;
    private String eventLeader;
    private List<ContributePeople> contribute_people_list = new ArrayList<>();

    public EventItem() {
    }

    public EventItem(String event_title, String date, String team_name, String description, String assignBy, int organizerCount, String investmentInReturn, String investmentAmount) {
        this.event_title = event_title;
        this.date = date;
        this.team_name = team_name;
        this.description = description;
        this.assignBy = assignBy;
        this.organizerCount = organizerCount;
        this.investmentInReturn = investmentInReturn;
        this.investmentAmount = investmentAmount;
    }

    public String getEventLeader() {
        return eventLeader;
    }

    public void setEventLeader(String eventLeader) {
        this.eventLeader = eventLeader;
    }

    public void setOrganizerCount(int count) {
        this.organizerCount = count;
    }

    public int getOrganizerCount() {
        return organizerCount;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAssignBy(String assignBy) {
        this.assignBy = assignBy;
    }

    public void setInvestmentInReturn(String investmentInReturn) {
        this.investmentInReturn = investmentInReturn;
    }

    public void setInvestmentAmount(String investmentAmount) {
        this.investmentAmount = investmentAmount;
    }

    public int getEvent_id() {
        return event_id;
    }

    public String getDescription() {
        return description;
    }

    public String getAssignBy() {
        return assignBy;
    }

    public String getInvestmentInReturn() {
        return investmentInReturn;
    }

    public String getInvestmentAmount() {
        return investmentAmount;
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

    @NonNull
    @Override
    public String toString() {
        return getEvent_title() + "\n" +
                getTeam_name() + "\n" +
                getDate() + "\n" +
                getAssignBy() + "\n" +
                getOrganizerCount() + "\n" +
                getInvestmentAmount() + "\n" +
                getInvestmentInReturn() + "\n" +
                getDescription() + "\n";
    }
}
