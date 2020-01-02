package com.projectreachout.AddNewEvent;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.projectreachout.AddNewEvent.BottomSheets.BottomSheetFragment;
import com.projectreachout.AddNewEvent.DateAndTimePicker.DatePickerFragment;
import com.projectreachout.AddNewEvent.DateAndTimePicker.ResponseDate;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.SelectPeople.SelectPeopleActivity;
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
import static com.projectreachout.GeneralStatic.getDomainUrl;
import static com.projectreachout.GeneralStatic.getDummyUrl;
import static com.projectreachout.GeneralStatic.getMonthForInt;
import static com.projectreachout.GeneralStatic.showKeyBoard;

public class AddEventActivity extends AppCompatActivity {

    private static final String TAG = AddEventActivity.class.getSimpleName();

    private TextView mShowDateTV;
    private TextView mShowTeamTV;
    private TextView mShowOrganizersTV;
    private TextView mShowEventLeaderTV;

    private Button mPickDateBtn;
    private Button mPickTeamBtn;
    private Button mSelectPeopleBtn;
    private Button mSelectEventLeaderBtn;

    private EditText mTitleET;
    private EditText mDescriptionET;

    private Button mSubmitBtn;

    private String mDate;
    private String mEventLeader;
    private int mEventLeaderIndex = -1;
    private ArrayList<String> mSelectedTeam = new ArrayList<>();
    private ArrayList<UserDetails> mSelectedUsers = new ArrayList<>();

    private ProgressDialog mDialog;

    // TODO: Delete this SparseBooleanArray after item selection works with base on user id
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ane_activity_add_event);

        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getString(R.string.title_add_event));
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mShowDateTV = findViewById(R.id.tv_aaae_show_date);
        mShowTeamTV = findViewById(R.id.tv_aaae_show_team);
        mShowOrganizersTV = findViewById(R.id.tv_aaae_show_selected_people);
        mShowEventLeaderTV = findViewById(R.id.tv_aaae_show_event_leader);

        // Underlining text for clickable
        mShowTeamTV.setPaintFlags(mShowTeamTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        mShowOrganizersTV.setPaintFlags(mShowOrganizersTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        mPickDateBtn = findViewById(R.id.btn_aaae_pick_date);
        mPickTeamBtn = findViewById(R.id.btn_aaae_pick_team);
        mSelectPeopleBtn = findViewById(R.id.btn_aaae_select_people);
        mSelectEventLeaderBtn = findViewById(R.id.btn_aaae_select_event_leader);

        mTitleET = findViewById(R.id.et_aaae_title);
        mDescriptionET = findViewById(R.id.et_aaae_description);

        mSubmitBtn = findViewById(R.id.btn_aaae_submit);

        mDialog = new ProgressDialog(this);

        displayDateToday();

        mShowTeamTV.setOnClickListener(this::showSelectedTeams);
        mShowOrganizersTV.setOnClickListener(this::showSelectedOrganizers);

        mPickDateBtn.setOnClickListener(this::pickEventDate);
        mPickTeamBtn.setOnClickListener(this::showTeamPicker);
        mSelectPeopleBtn.setOnClickListener(this::navigateSelectPeopleActivity);
        mSelectEventLeaderBtn.setOnClickListener(this::selectEventLeader);

        mSubmitBtn.setOnClickListener(this::submitEvent);
    }

    String eventLeader = null;
    int eventLeaderIndex = -1;

    private void selectEventLeader(View view) {
        if (mSelectedUsers.size() == 0) {
            String errorMessage = "Select Organizers First";
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_INDEFINITE).setAction("Select", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigateSelectPeopleActivity(new View(getApplicationContext()));
                }

            }).show();
            return;
        }

        String title = "Choose Event Leader";
        String[] teamName = new String[mSelectedUsers.size()];

        for (int i=0; i<mSelectedUsers.size(); i++) {
            UserDetails userDetails = mSelectedUsers.get(i);
            teamName[i] = userDetails.getUser_name();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setSingleChoiceItems(teamName, mEventLeaderIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eventLeader = teamName[which];
                eventLeaderIndex = which;
            }
        }).setPositiveButton("OK", (dialog, which) -> {
            mEventLeader = eventLeader;
            mEventLeaderIndex = eventLeaderIndex;
            mShowEventLeaderTV.setText(mEventLeader);
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
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

                boolean flag = false;
                for (int i=0; i<mSelectedUsers.size(); i++) {
                    if (mSelectedUsers.get(i).getUser_name().equals(mEventLeader)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    mShowEventLeaderTV.setText("None");
                    mEventLeader = null;
                    mEventLeaderIndex = -1;
                }

                Log.d(TAG, mSelectedUsers.toString());
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
                int realMonth = month + 1;
                mDate = year + "-" + realMonth + "-" + day;
                mShowDateTV.setText(day + " " + getMonthForInt(month) + " " + year);
                Log.v(TAG, mDate);
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
        String username = AppController.getInstance().getLoginUserUsername();

        List<String> organizersId = new ArrayList<>();
        for (int i = 0; i < mSelectedUsers.size(); i++) {
            // organizersId.add(mSelectedUsers.get(i).getId());
            organizersId.add(mSelectedUsers.get(i).getUser_name());
        }

        Map<String, String> param = new HashMap<>();
        param.put("assigned_by", username);
        param.put("event_date", mDate);
        param.put("event_title", title);
        param.put("description", description);
        param.put("selected_teams", mSelectedTeam.toString());
        param.put("organizers", organizersId.toString());
        param.put("event_leader", mEventLeader);

        /*String play = username + "\n\n" +
                mDate + "\n\n" +
                title + "\n\n" +
                description + "\n\n" +
                mSelectedTeam.toString() + "\n\n" +
                organizersId.toString() + "\n\n\n";

        Log.v(TAG, play);*/

        Log.v(TAG, "MAP: \n\n\n" + param.toString() + "\n\n\n");

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

        if (mEventLeader == null || mEventLeader.trim().equals("")) {
            selectEventLeader(new View(getApplicationContext()));
            return;
        }

        String description = mTitleET.getText().toString().trim();
        if (description.equals("")) {
            showKeyBoard(mTitleET);
            return;
        }

        Map<String, String> param = getMappedData();

        /*Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .encodedAuthority(getString(R.string.localhost) + ":" + getString(R.string.port_no))
                .appendPath("add_event")
                .appendPath("");

        String url = builder.build().toString();*/

        String url = getDummyUrl() + "/add_event/";

        Log.v(TAG, "URL: " + url);

        mDialog.setMessage("Loading. Please wait...   ");
        mDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String output) {
                Log.v(TAG, output);
                if (output.trim().equals("200")) {
                    mDialog.dismiss();
                    Toast.makeText(AddEventActivity.this, "Event Added", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    mDialog.dismiss();
                    displayErrorMessage(view);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, error.toString());
                mDialog.dismiss();
                displayErrorMessage(view);
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
        String[] teamName = new String[]{"Regular Volunteers", "Fund Raising", "Event", "Environmental Awareness"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        boolean[] checkedItem = new boolean[teamName.length];
        if (mSelectedTeam.size() != 0) {
            selectedTeam = new ArrayList<>();
            for (int i = 0; i < mSelectedTeam.size(); i++) {
                for (int j = 0; j < teamName.length; j++) {
                    if (mSelectedTeam.get(i).equals(teamName[j])) {
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
        int month = c.get(Calendar.MONTH) + 1;      // month starts from 0
        int day = c.get(Calendar.DAY_OF_MONTH);

        mDate = year + "-" + month + "-" + day;
        mShowDateTV.setText(day + " " + getMonthForInt(month-1) + " " + year);
    }
}