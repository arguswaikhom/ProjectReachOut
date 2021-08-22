package com.projectreachout.Event.AddEvent.BottomSheets;

import static com.projectreachout.GeneralStatic.OPTION;
import static com.projectreachout.GeneralStatic.OPTION_ORGANIZERS;
import static com.projectreachout.GeneralStatic.OPTION_TEAM;
import static com.projectreachout.GeneralStatic.ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.TEAM_LIST;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.projectreachout.R;
import com.projectreachout.User.User;

import java.util.ArrayList;
import java.util.Objects;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetListView mListView;
    private TextView mTextView;

    public static final String TITLE_SELECTED_ORGANISERS = "Selected Organizers";
    public static final String TITLE_REACTED_PEOPLE = "Reacted people";
    public static final String TITLE_SELECTED_TEAM = "Selected Teams";
    public static final String TITLE = "title";

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ane_bs_bottom_sheet_layout, container, false);

        mListView = rootView.findViewById(R.id.bslv_abbsl_list_view);
        mTextView = rootView.findViewById(R.id.tv_abbsl_text_view);

        Bundle bundle = getArguments();
        int option = bundle != null ? bundle.getInt(OPTION) : 0;
        switch (option){
            case OPTION_TEAM:{
                ArrayList<String> mTeamList = bundle.getStringArrayList(TEAM_LIST);
                mTextView.setText(bundle.getString(TITLE));

                if (mTeamList != null){
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, mTeamList);
                    mListView.setAdapter(arrayAdapter);
                }
                break;
            }
            case OPTION_ORGANIZERS:{
                ArrayList<User> userArrayList = bundle.getParcelableArrayList(ORGANIZER_LIST);
                mTextView.setText(bundle.getString(TITLE));

                OrganizersListAdapter organizersListAdapter = new OrganizersListAdapter(getContext(), R.layout.u_user_row_item, userArrayList);
                mListView.setAdapter(organizersListAdapter);
                break;
            }
        }
        return rootView;
    }
}
