package com.projectreachout.Event.EventDetailsAndModification;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.projectreachout.AppController;
import com.projectreachout.R;
import com.projectreachout.Utilities.NetworkUtils.HttpVolleyRequest;
import com.projectreachout.Utilities.NetworkUtils.OnHttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.projectreachout.GeneralStatic.JSONParsingArrayFromObject;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingObjectFromString;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getDomainUrl;

public class InvestmentFragment extends Fragment implements OnHttpResponse {

    private final String TAG = InvestmentFragment.class.getSimpleName();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InvestmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private TableLayout mInvestmentTableLayout;
    private TextView mTotalAmountTextView;
    private TextView mDifferentAmountTV;

    private EditText mAmountInReturnET;

    private Button mAddInvestmentItemButton;
    private Button mSubmitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sedam_fragment_investment, container, false);

        mInvestmentTableLayout = rootView.findViewById(R.id.tl_sfi_investment);
        mTotalAmountTextView = rootView.findViewById(R.id.tv_sfi_total_amount);
        mDifferentAmountTV = rootView.findViewById(R.id.tv_sfi_difference_amount);
        mAmountInReturnET = rootView.findViewById(R.id.et_sfi_in_return_amount);
        mAddInvestmentItemButton = rootView.findViewById(R.id.btn_sfi_add_investment_item);
        mSubmitButton = rootView.findViewById(R.id.btn_sfi_submit);

        mAddInvestmentItemButton.setOnClickListener(v -> addNewInvestmentItem(new InvestmentItem()));
        mSubmitButton.setOnClickListener(v -> {
            try {
                submitInvestment();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppController.getInstance().performIfAuthenticated(getActivity())) {
            fetchEventInvestmentDetails();
        }
    }

    private void fetchEventInvestmentDetails() {
        String url = getDomainUrl() + "/get_event_investment/";
        Map<String, String> param = new HashMap<>();
        param.put("event_id", AppController.getInstance().getGlobalEventId());
        HttpVolleyRequest httpVolleyRequest = new HttpVolleyRequest(Request.Method.POST, url, null, 0, null, param, this);
        httpVolleyRequest.execute();
    }

    private void parseJsonFeed(String response) {
        JSONObject responseObj = JSONParsingObjectFromString(response);
        String investmentAmount = JSONParsingStringFromObject(responseObj, "investment_amount");
        String investmentOnReturn = JSONParsingStringFromObject(responseObj, "investment_return");

        JSONArray investmentList = JSONParsingArrayFromObject(responseObj, "investment_details");
        mInvestmentTableLayout.removeAllViews();
        for (int i = 0; i < investmentList.length(); i++) {
            JSONObject investmentItem = JSONParsingObjectFromArray(investmentList, i);
            addNewInvestmentItem(InvestmentItem.fromJson(investmentItem.toString()));
        }

        mTotalAmountTextView.setText(investmentAmount);
        mAmountInReturnET.setText(investmentOnReturn);
        long diffAmount = Long.parseLong(investmentOnReturn) - Long.parseLong(investmentAmount);
        mDifferentAmountTV.setText(diffAmount + "");
    }

    private void submitInvestment() throws JsonProcessingException {
        ArrayList<String> amountInvested = new ArrayList<>();
        ArrayList<InvestmentItem> investmentItems = new ArrayList<>();

        boolean uploadReady = false;

        for (int i = 0, j = mInvestmentTableLayout.getChildCount(); i < j; i++) {
            LinearLayout view = (LinearLayout) mInvestmentTableLayout.getChildAt(i);

            EditText investmentOnET = (EditText) view.getChildAt(0);
            EditText amountET = (EditText) view.getChildAt(1);

            String investmentOn = investmentOnET.getText().toString().trim();
            String amount = amountET.getText().toString().trim();

            if (investmentOn.equals("") && amount.equals("")) {
                continue;
            }

            if (amount.contains(".")) {
                amountET.setError("Don't use decimal amount!!");
                return;
            }

            uploadReady = true;

            amountInvested.add(amount);
            investmentItems.add(new InvestmentItem(investmentOn, amount));
        }

        if (!uploadReady) return;

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String investment_details = ow.writeValueAsString(investmentItems);
        Log.v(TAG, investment_details);

        String url = getDomainUrl() + "/add_investment_details/";
        String event_id = AppController.getInstance().getGlobalEventId();

        long total = 0;
        for (String x : amountInvested) {
            total += Long.parseLong(x);
        }
        mTotalAmountTextView.setText(String.valueOf(total));

        Map<String, String> map = new HashMap<>();
        map.put("event_id", event_id);
        map.put("investment_return", mAmountInReturnET.getText().toString().trim());
        map.put("investment_amount", String.valueOf(total));
        map.put("investment_details", investment_details);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            if (response != null) {
                if (response.trim().equals("200")) {
                    fetchEventInvestmentDetails();
                    Log.v("aaaaa", "response :: " + response);
                }
                Log.v("aaaaa", "response :: " + response);
            }
        }, error -> {

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return AppController.getInstance().getLoginCredentialHeader();
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void addNewInvestmentItem(InvestmentItem singleInvestmentItem) {
        final int INDEX_INVESTMENT_ON = 0;
        final int INDEX_AMOUNT = 1;

        FrameLayout rootViewLinearLayout = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.sedam_investment_item, null);

        LinearLayout rootLayoutView = (LinearLayout) rootViewLinearLayout.getChildAt(0);

        EditText investmentOnEditText = (EditText) rootLayoutView.getChildAt(INDEX_INVESTMENT_ON);
        EditText amountEditText = (EditText) rootLayoutView.getChildAt(INDEX_AMOUNT);

        String investmentOn = singleInvestmentItem.getInvestment_on();
        String amount = singleInvestmentItem.getAmount();

        amountEditText.setText("" + amount);
        investmentOnEditText.setText(investmentOn);

        if (rootLayoutView.getParent() != null) {
            ((ViewGroup) rootLayoutView.getParent()).removeView(rootLayoutView);
        }
        mInvestmentTableLayout.addView(rootLayoutView);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onHttpResponse(String response, int request) {
        Log.v(TAG, response);
        if (request == 0) {
            parseJsonFeed(response);
        }
    }

    @Override
    public void onHttpErrorResponse(VolleyError error, int request) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
