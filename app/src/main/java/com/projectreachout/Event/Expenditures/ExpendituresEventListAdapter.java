package com.projectreachout.Event.Expenditures;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.projectreachout.Event.EventItem;
import com.projectreachout.R;

import java.util.List;

public class ExpendituresEventListAdapter extends ArrayAdapter<EventItem> {

    private final String LOG_TAG = ExpendituresEventListAdapter.class.getSimpleName();

    public ExpendituresEventListAdapter(Context context, int resource, List<EventItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.evn_exp_event_item, parent, false);
        }

        return convertView;
    }
}
