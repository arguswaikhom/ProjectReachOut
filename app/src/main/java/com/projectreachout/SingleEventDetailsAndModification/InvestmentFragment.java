package com.projectreachout.SingleEventDetailsAndModification;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.projectreachout.AppController;
import com.projectreachout.R;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvestmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvestmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvestmentFragment extends Fragment {

    private final String TAG = InvestmentFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InvestmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvestmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvestmentFragment newInstance(String param1, String param2) {
        InvestmentFragment fragment = new InvestmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

    private EditText mAmountInReturnET;

    private Button mAddInvestmentItemButton;
    private Button mSubmitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.sedam_fragment_investment, container, false);

        mInvestmentTableLayout = rootView.findViewById(R.id.tl_sfi_investment);
        mTotalAmountTextView = rootView.findViewById(R.id.tv_sfi_total_amount);
        mAmountInReturnET = rootView.findViewById(R.id.et_sfi_in_return_amount);
        mAddInvestmentItemButton = rootView.findViewById(R.id.btn_sfi_add_investment_item);
        mSubmitButton = rootView.findViewById(R.id.btn_sfi_submit);

        mAddInvestmentItemButton.setOnClickListener(v -> addNewInvestmentItem(new InvestmentItem()));
        mSubmitButton.setOnClickListener(v -> submitInvestment());

        fetchEventInvestmentDetails();

        return rootView;
    }

    private void fetchEventInvestmentDetails() {
        String url = getDomainUrl() + "/get_event_investment/";
        String event_id = String.valueOf(AppController.getInstance().getGlobalEventId());

        Map<String, String> param = new HashMap<>();
        param.put("event_id", event_id);

        Log.v("aaaaa", url);
        Log.v("aaaaa", event_id);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    Log.v("aaaaa", response);
                    parseJsonFeed(JSONParsingObjectFromString(response));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("aaaaa", error.toString());
            }
        }) {
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

    private void parseJsonFeed(JSONObject outputObj) {
        String investmentAmount = JSONParsingStringFromObject(outputObj, "amount_invested");
        String investmentOnReturn = JSONParsingStringFromObject(outputObj, "amount_recieved");

        JSONArray investmentList = JSONParsingArrayFromObject(outputObj, "investment_list");

        mInvestmentTableLayout.removeAllViews();

        for (int i = 0; i < investmentList.length(); i++) {
            JSONObject investmentItem = JSONParsingObjectFromArray(investmentList, i);

            String investmentOn = JSONParsingStringFromObject(investmentItem, "investment_on");
            String amount = JSONParsingStringFromObject(investmentItem, "amount");

            addNewInvestmentItem(new InvestmentItem(investmentOn, amount));
        }

        mTotalAmountTextView.setText(investmentAmount);
        mAmountInReturnET.setText(investmentOnReturn);
    }

    private void submitInvestment() {
        ArrayList<String> reasons = new ArrayList<>();
        ArrayList<String> amountInvested = new ArrayList<>();

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

            reasons.add(investmentOn);
            amountInvested.add(amount);
        }

        if (!uploadReady) return;

        String url = getDomainUrl() + "/add_investment/";
        String event_id = String.valueOf(AppController.getInstance().getGlobalEventId());

        Map<String, String> map = new HashMap<>();
        map.put("event_id", event_id);
        map.put("investment_on_return", mAmountInReturnET.getText().toString().trim());
        map.put("investment_on", reasons.toString());
        map.put("amount", amountInvested.toString());

        long total = 0;
        for (String x : amountInvested) {
            total += Long.parseLong(x);
        }
        mTotalAmountTextView.setText(String.valueOf(total));

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    if (response.trim().equals("200")) {
                        fetchEventInvestmentDetails();
                        Log.v("aaaaa", "response :: " + response);

                    }
                    Log.v("aaaaa", "response :: " + response);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
