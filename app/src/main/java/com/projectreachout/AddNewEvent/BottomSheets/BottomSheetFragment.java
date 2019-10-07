package com.projectreachout.AddNewEvent.BottomSheets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.projectreachout.R;
import com.projectreachout.User.UserDetails;

import java.util.ArrayList;
import java.util.Objects;

import static com.projectreachout.GeneralStatic.OPTION;
import static com.projectreachout.GeneralStatic.OPTION_ORGANIZERS;
import static com.projectreachout.GeneralStatic.OPTION_TEAM;
import static com.projectreachout.GeneralStatic.ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.TEAM_LIST;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private BottomSheetListView mListView;
    private TextView mTextView;

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

        int option = getArguments() != null ? getArguments().getInt(OPTION) : 0;

        switch (option){
            case OPTION_TEAM:{
                ArrayList<String> mTeamList = getArguments() != null ? getArguments().getStringArrayList(TEAM_LIST) : null;

                mTextView.setText("Selected Teams");

                if (mTeamList != null){
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_list_item_1, mTeamList);
                    mListView.setAdapter(arrayAdapter);
                }
                break;
            }
            case OPTION_ORGANIZERS:{
                ArrayList<UserDetails> userDetailsArrayList = getArguments().getParcelableArrayList(ORGANIZER_LIST);

                mTextView.setText("Selected Organizers");

                OrganizersListAdapter organizersListAdapter = new OrganizersListAdapter(getContext(), R.layout.u_user_row_item, userDetailsArrayList);
                mListView.setAdapter(organizersListAdapter);
                break;
            }
        }
        return rootView;
    }
}
