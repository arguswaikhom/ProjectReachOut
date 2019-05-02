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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.projectreachout.NetworkUtils.AsyncResponseGet;
import com.projectreachout.NetworkUtils.AsyncResponsePost;
import com.projectreachout.NetworkUtils.BackgroundAsyncGet;
import com.projectreachout.NetworkUtils.BackgroundAsyncPost;
import com.projectreachout.R;

import net.gotev.uploadservice.MultipartUploadRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.projectreachout.GeneralStatic.JSONParsingObjectFromArray;
import static com.projectreachout.GeneralStatic.JSONParsingStringFromObject;
import static com.projectreachout.GeneralStatic.getRandomInt;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvestmentFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvestmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvestmentFragment extends Fragment {

    private final String LOG_TAG = InvestmentFragment.class.getSimpleName();

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
    private TextView mInReturnTextView;
    private Button mAddInvestmentItemButton;
    private Button mSubmitButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.sedam_fragment_investment, container, false);

        mInvestmentTableLayout = rootView.findViewById(R.id.tl_sfi_investment);
        mTotalAmountTextView = rootView.findViewById(R.id.tv_sfi_total_amount);
        mInReturnTextView = rootView.findViewById(R.id.et_sfi_in_return_amount);
        mAddInvestmentItemButton = rootView.findViewById(R.id.btn_sfi_add_investment_item);
        mSubmitButton = rootView.findViewById(R.id.btn_sfi_submit);

        mAddInvestmentItemButton.setOnClickListener(v -> addNewInvestmentItem(new InvestmentItem()));
        mSubmitButton.setOnClickListener(v -> submitInvestment());

        //addDummyInvestmentData();

        // TODO: Implement getEventId()
        String eventId = String.valueOf(getRandomInt(1000, 10000));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .authority(getString(R.string.localhost))
                .appendPath("event")
                .appendPath("investment_details")
                .appendQueryParameter("event_id", eventId);

        String url = builder.build().toString();

        BackgroundAsyncGet backgroundAsyncGet = new BackgroundAsyncGet(new AsyncResponseGet() {
            @Override
            public void onResponse(JSONArray output) {
                if(output != null){
                    parseJsonFeed(output);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onProgressUpdate(int value) {

            }

            @Override
            public void onPreExecute() {

            }
        });
        backgroundAsyncGet.execute(url);

        return rootView;
    }

    private void parseJsonFeed(JSONArray output) {
        for (int i = 0; i < output.length(); i++) {
            JSONObject investmentItem = JSONParsingObjectFromArray(output, i);

            String investmentOn = JSONParsingStringFromObject(investmentItem, "investment_on");
            String amount = JSONParsingStringFromObject(investmentItem, "amount");

            addNewInvestmentItem(new InvestmentItem(investmentOn, Double.parseDouble(amount)));
        }
    }

    private void submitInvestment() {
        Map<String, String> map = new HashMap<>();

        Log.v("aaa", mInvestmentTableLayout.getChildCount() + "");
        for (int i = 0, j = mInvestmentTableLayout.getChildCount(); i < j; i++) {

            LinearLayout view = (LinearLayout) mInvestmentTableLayout.getChildAt(i);

            EditText investmentOnET = (EditText) view.getChildAt(0);
            EditText amountET = (EditText) view.getChildAt(1);

            String investmentOn = investmentOnET.getText().toString().trim();
            String amount = amountET.getText().toString().trim();

            /*InvestmentItem investmentItem = new InvestmentItem(investmentOn.getText().toString(),
                    Double.parseDouble(amount.getText().toString()));*/

            map.put(investmentOn, amount);
        }

        // TODO: Implement getEventId()
        String eventId = String.valueOf(getRandomInt(1000, 10000));

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .authority(getString(R.string.localhost))
                .appendPath("event")
                .appendPath("submit_investment")
                .appendQueryParameter("event_id", eventId);
        String url = builder.build().toString();

        BackgroundAsyncPost backgroundAsyncPost = new BackgroundAsyncPost(map, new AsyncResponsePost() {
            @Override
            public void onResponse(String output) {

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }

            @Override
            public void onProgressUpdate(int value) {

            }

            @Override
            public void onPreExecute() {

            }
        });
        backgroundAsyncPost.execute(url);
    }

    private void addNewInvestmentItem(InvestmentItem singleInvestmentItem) {
        final int INDEX_INVESTMENT_ON = 0;
        final int INDEX_AMOUNT = 1;

        FrameLayout rootViewLinearLayout = (FrameLayout) LayoutInflater.from(getContext()).inflate(R.layout.sedam_investment_item, null);

        LinearLayout rootLayoutView = (LinearLayout) rootViewLinearLayout.getChildAt(0);

        EditText investmentOnEditText = (EditText) rootLayoutView.getChildAt(INDEX_INVESTMENT_ON);
        EditText amountEditText = (EditText) rootLayoutView.getChildAt(INDEX_AMOUNT);

        String investmentOn = singleInvestmentItem.getInvestment_on();
        double amount = singleInvestmentItem.getAmount();

        investmentOnEditText.setText(investmentOn);
        amountEditText.setText("" + amount);

        if (rootLayoutView.getParent() != null) {
            ((ViewGroup) rootLayoutView.getParent()).removeView(rootLayoutView);
        }
        mInvestmentTableLayout.addView(rootLayoutView);
    }

    /*private void addDummyInvestmentData() {

        List<InvestmentItem> investmentItemList = new ArrayList<>();
        int size = getRandomInt(4, 7);
        for (int j = 0; j < size; j++) {
            InvestmentItem singleInvestmentItem = new InvestmentItem("SomeReason", 500.00);
            investmentItemList.add(singleInvestmentItem);
        }

        for (int i = 0; i < investmentItemList.size(); i++) {
            addNewInvestmentItem(investmentItemList.get(i));
        }
    }*/

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
