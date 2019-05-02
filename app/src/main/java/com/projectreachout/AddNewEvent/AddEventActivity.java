package com.projectreachout.AddNewEvent;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.projectreachout.AddNewEvent.BottomSheets.BottomSheetFragment;
import com.projectreachout.NetworkUtils.AsyncResponsePost;
import com.projectreachout.NetworkUtils.BackgroundAsyncPost;
import com.projectreachout.R;
import com.projectreachout.SelectPeople.SelectPeopleActivity;

import com.projectreachout.AddNewEvent.DateAndTimePicker.*;
import com.projectreachout.User.UserDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.projectreachout.GeneralStatic.DATE_RESPONSE;
import static com.projectreachout.GeneralStatic.EXISTING_ORGANIZERS;
import static com.projectreachout.GeneralStatic.GET_ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.OPTION;
import static com.projectreachout.GeneralStatic.OPTION_ORGANIZERS;
import static com.projectreachout.GeneralStatic.OPTION_TEAM;
import static com.projectreachout.GeneralStatic.ORGANIZER_LIST;
import static com.projectreachout.GeneralStatic.SELECTED_ORGANIZERS;
import static com.projectreachout.GeneralStatic.SPARSE_BOOLEAN_ARRAY;
import static com.projectreachout.GeneralStatic.TEAM_LIST;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class AddEventActivity extends AppCompatActivity {

    private TextView mShowDateTV;
    private TextView mShowTeamTV;
    private TextView mShowOrganizersTV;

    private Button mPickDateBtn;
    private Button mPickTeamBtn;
    private Button mSelectPeopleBtn;

    private EditText mTitleET;
    private EditText mDescriptionET;

    private Button mSubmitBtn;

    private String mDate;
    private ArrayList<String> mSelectedTeam = new ArrayList<>();
    private ArrayList<UserDetails> mSelectedUsers = new ArrayList<>();

    // TODO: Delete this SparseBooleanArray after item selection works with base on user id
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ane_activity_add_event);

        mShowDateTV = findViewById(R.id.tv_aaae_show_date);
        mShowTeamTV = findViewById(R.id.tv_aaae_show_team);
        mShowOrganizersTV = findViewById(R.id.tv_aaae_show_selected_people);

        // Underlining text for clickable
        mShowTeamTV.setPaintFlags(mShowTeamTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mShowOrganizersTV.setPaintFlags(mShowOrganizersTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mPickDateBtn = findViewById(R.id.btn_aaae_pick_date);
        mPickTeamBtn = findViewById(R.id.btn_aaae_pick_team);
        mSelectPeopleBtn = findViewById(R.id.btn_aaae_select_people);

        mTitleET = findViewById(R.id.et_aaae_title);
        mDescriptionET = findViewById(R.id.et_aaae_description);

        mSubmitBtn = findViewById(R.id.btn_aaae_submit);

        displayDateToday();

        mShowTeamTV.setOnClickListener(this::showSelectedTeams);
        mShowOrganizersTV.setOnClickListener(this::showSelectedOrganizers);

        mPickDateBtn.setOnClickListener(this::pickEventDate);
        mPickTeamBtn.setOnClickListener(this::showTeamPicker);
        mSelectPeopleBtn.setOnClickListener(this::navigateSelectPeopleActivity);

        mSubmitBtn.setOnClickListener(this::submitEvent);
    }

    private void navigateSelectPeopleActivity(View view) {
        // TODO: Delete this SparseBooleanArray part after item selection works with base on user id
        ArrayList<Integer> integerArrayList = new ArrayList<>();
        for (int i = 0; i < sparseBooleanArray.size(); i++) {
            integerArrayList.add(sparseBooleanArray.keyAt(i));
        }

        Intent intent = new Intent(view.getContext(), SelectPeopleActivity.class);
        intent.putParcelableArrayListExtra(EXISTING_ORGANIZERS, mSelectedUsers);
        intent.putIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY, integerArrayList);

        startActivityForResult(intent, GET_ORGANIZER_LIST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GET_ORGANIZER_LIST) {
                mSelectedUsers = new ArrayList<>();
                mSelectedUsers.addAll(data != null ? data.getParcelableArrayListExtra(SELECTED_ORGANIZERS) : new ArrayList<>());

                // TODO: Delete this SparseBooleanArray part after item selection works with base on user id
                ArrayList<Integer> integerArrayList = data != null ? data.getIntegerArrayListExtra(SPARSE_BOOLEAN_ARRAY) : new ArrayList<>();
                sparseBooleanArray = new SparseBooleanArray();
                for (int i = 0; i < integerArrayList.size(); i++) {
                    sparseBooleanArray.put(integerArrayList.get(i), true);
                }

                if (mSelectedUsers.size() == 0) {
                    mShowOrganizersTV.setText("None");
                } else {
                    mShowOrganizersTV.setText("Organizers: " + mSelectedUsers.size());
                }
            }
        }
    }

    private void showSelectedOrganizers(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(OPTION, OPTION_ORGANIZERS);
        bundle.putParcelableArrayList(ORGANIZER_LIST, mSelectedUsers);

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setArguments(bundle);

        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void showSelectedTeams(View view) {
        Bundle bundle = new Bundle();
        bundle.putInt(OPTION, OPTION_TEAM);
        bundle.putStringArrayList(TEAM_LIST, mSelectedTeam);

        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setArguments(bundle);

        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    private void pickEventDate(View view) {
        DialogFragment newFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(DATE_RESPONSE, new ResponseDate() {
            @Override
            public void setDate(int year, int month, int day) {
                mShowDateTV.setText(day + "/" + month + "/" + year);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {

            }
        });
        newFragment.setArguments(bundle);
        newFragment.show(getSupportFragmentManager(), "date_picker");
    }

    private Map<String, String> getMappedData() {

        // TODO: Improve to Handle Null Exceptions, errors and empty EditTexts
        String title = mTitleET.getText().toString().trim();
        String description = mDescriptionET.getText().toString().trim();

        // TODO: Implement getUser_name() and replace with the current user
        String username = "Tony Stark";

        List<String> organizersId = new ArrayList<>();
        for (int i = 0; i < mSelectedUsers.size(); i++) {
            organizersId.add(mSelectedUsers.get(i).getId());
        }

        Map<String, String> param = new HashMap<>();
        param.put("assign_by", username);
        param.put("date", mDate);
        param.put("title", title);
        param.put("description", description);
        param.put("selected_team", mSelectedTeam.toString());
        param.put("organizers", organizersId.toString());

        return param;
    }

    private void submitEvent(View view) {

        if (mSelectedTeam.size() == 0) {
            showTeamPicker(new View(getApplicationContext()));
            return;
        }

        if (mSelectedUsers.size() == 0) {
            navigateSelectPeopleActivity(new View(getApplicationContext()));
            return;
        }

        String description = mTitleET.getText().toString().trim();
        if (description.equals("")){
            showKeyBoard(mTitleET);
            return;
        }

        Map<String, String> param = getMappedData();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("add_new_event");

        String url = builder.build().toString();

        /*
         * TODO: Implement the empty methods
         * */

        BackgroundAsyncPost backgroundAsyncPost = new BackgroundAsyncPost(param, new AsyncResponsePost() {
            @Override
            public void onResponse(String output) {}

            @Override
            public void onErrorResponse(VolleyError error) {
                displayErrorMessage(view);
            }

            @Override
            public void onProgressUpdate(int value) {}

            @Override
            public void onPreExecute() {}
        });

        backgroundAsyncPost.execute(url);
    }

    private void displayErrorMessage(View view) {
        String errorMessage = "Couldn't update information to server...";
        Snackbar.make(view, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitEvent(v);
            }
        }).show();
    }


    ArrayList<String> selectedTeam = new ArrayList<>();
    private void showTeamPicker(View view) {
        String title = "Choose Team";
        String[] teamName = new String[]{"Regular Volunteers", "Fund Raising", "Teaching", "School Event", "Environmental Awareness"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        boolean[] checkedItem = new boolean[teamName.length];
        if(mSelectedTeam.size() != 0){
            selectedTeam = new ArrayList<>();
            for(int i=0; i<mSelectedTeam.size(); i++){
                for(int j=0; j<teamName.length; j++){
                    if(mSelectedTeam.get(i).equals(teamName[j])){
                        selectedTeam.add(teamName[j]);
                        checkedItem[j] = true;
                        break;
                    }
                }
            }
        }

        builder.setTitle(title).setMultiChoiceItems(teamName, checkedItem, (dialog, which, isChecked) -> {
            if (isChecked) {
                selectedTeam.add(teamName[which]);
            } else {
                selectedTeam.remove(teamName[which]);
            }
        }).setPositiveButton("OK", (dialog, which) -> {
            /*StringBuilder s = new StringBuilder();
            for (int i = 0; i < selectedTeam.size(); i++) {
                s.append(selectedTeam.get(i));
            }*/
            mSelectedTeam = new ArrayList<>();
            mSelectedTeam = selectedTeam;

            if (mSelectedTeam.size() == 0) {
                mShowTeamTV.setText("None");
            } else {
                mShowTeamTV.setText("Team: " + mSelectedTeam.size());
            }
        }).setNegativeButton("Cancel", (dialog, which) -> {

        });

        builder.create().show();
    }

    private void displayDateToday() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        mShowDateTV.setText(day + "/" + month + "/" + year);
    }

   /* private void showTeamPickerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Team");

        String[] teamName = {"None", "Regular Volunteers", "Fund Raising", "Teaching", "School Event", "Environmental Awareness"};
        builder.setItems(teamName, (dialog, which) -> {
            switch (which) {
                case 0: {
                    mShowTeamTV.setText(teamName[0]);
                    break;
                }
                case 1: {
                    mShowTeamTV.setText(teamName[1]);
                    break;
                }
                case 2: {
                    mShowTeamTV.setText(teamName[2]);
                    break;
                }
                case 3: {
                    mShowTeamTV.setText(teamName[3]);
                    break;
                }
                case 4: {
                    mShowTeamTV.setText(teamName[4]);
                    break;
                }
                case 5: {
                    mShowTeamTV.setText(teamName[5]);
                    break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }*/
}
