package com.projectreachout;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class GeneralStatic {

    public static final int REFRESH = 1;
    public static final int LOAD_MORE = 2;

    public static final String USER_ID_KEY = "user_id";
    public static final String USERNAME_KEY = "username";
    public static final String PASSWORD_KEY = "password";
    public static final String EMAIL_KEY = "email";
    public static final String PHONE_NO_KEY = "phone_no";


    public static final String DATE_RESPONSE = "date_response";
    public static final String TIME_RESPONSE = "time_response";

    public static final String OPTION = "option";
    public static final int OPTION_TEAM = 1;
    public static final int OPTION_ORGANIZERS = 2;
    public static final String TEAM_LIST = "team_list";
    public static final String ORGANIZER_LIST = "organizer_list";

    public static final int GET_ORGANIZER_LIST = 1;
    public static final String EXISTING_ORGANIZERS = "existing_organizers";
    public static final String SELECTED_ORGANIZERS = "selected_organizers";
    public static final String SPARSE_BOOLEAN_ARRAY = "sparse_boolean_array";

    public static final String FRAGMENT_HOME = "home";
    public static final String FRAGMENT_ADD_POST = "add_post";
    public static final String FRAGMENT_EVENTS = "events";
    public static final String FRAGMENT_EXPENDITURES = "expenditures";

    public static int gEventId = -1;

    public static final int[] FIXED_ID_100 = getRandomInt(1000, 100000, 100);

    // Converting timestamp into x ago format
    public static String getTimeAgo(String time) {
        CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                Long.parseLong(time),
                System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        return timeAgo.toString();
    }

    public static int getRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    public static int[] getRandomInt(int min, int max, int size) {
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = ThreadLocalRandom.current().nextInt(min, max + 1);
        }
        return arr;
    }

    public static String getRandomString(int min, int max) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        //int randomLength = generator.nextInt(max);
        int randomLength = getRandomInt(min, max);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    public static String JSONParsingStringFromObject(JSONObject jsonObject, String paramName) {
        try {
            return jsonObject.getString(paramName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static JSONArray JSONParsingArrayFromString(String string) {
        try {
            return new JSONArray(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static JSONObject JSONParsingObjectFromString(String string) {
        try {
            return new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static int JSONParsingIntFromObject(JSONObject jsonObject, String paramName) {
        try {
            return jsonObject.getInt(paramName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static JSONObject JSONParsingObjectFromArray(JSONArray jsonArray, int index) {
        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static JSONArray JSONParsingArrayFromObject(JSONObject jsonObject, String paramName) {
        try {
            return jsonObject.getJSONArray(paramName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONArray();
    }

    public static void showKeyBoard(EditText editText) {
        Context context = editText.getContext();
        editText.post(() -> {
            editText.requestFocusFromTouch();
            InputMethodManager lManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            lManager.showSoftInput(editText, 0);
        });
    }

    public static void hideKeyBoard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isValidEmail(String email) {
        //return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
                "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                        "\\@" +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                        "(" +
                        "\\." +
                        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                        ")+"
        );
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        // return android.util.Patterns.PHONE.matcher(phone).matches();
        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            check = phone.length() >= 6 && phone.length() <= 13;
        } else {
            check = false;
        }
        return check;
    }

    public static String getDomainUrl() {
        /*Resources resources = Resources.getSystem();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(resources.getString(R.string.http))
                .encodedAuthority(resources.getString(R.string.localhost) + ":" + resources.getString(R.string.port_no));

        return builder.build().toString();*/
        //return "http://10.24.48.78:8000";
        return "http://reachout.pythonanywhere.com";
    }

    public static String getDate(String input) {
        input = input.replaceAll("T", " ");
        input = input.replaceAll("Z", " ");

        String response;

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input);

            String day = new SimpleDateFormat("dd").format(date);
            String month = new SimpleDateFormat("MM").format(date);
            String year = new SimpleDateFormat("yyyy").format(date);

            response = day + " " + getMonthForInt(Integer.valueOf(month) - 1) + " " + year;
            return  response;
        } catch (ParseException e) {
            e.printStackTrace();
            return input;
        }
    }

    public static String getDateTime(String input) {
        input = input.replaceAll("T", " ");
        input = input.replaceAll("Z", " ");

        String response;

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(input);

            String min = new SimpleDateFormat("mm").format(date);
            String hour = new SimpleDateFormat("HH").format(date);
            String day = new SimpleDateFormat("dd").format(date);
            String month = new SimpleDateFormat("MM").format(date);
            String year = new SimpleDateFormat("yyyy").format(date);

            response = getTwelveHours(Integer.valueOf(hour)) + ":" +
                    min + " " + getTimeIndicator(Integer.valueOf(hour)) + " - " +
                    day + " " + getMonthForInt(Integer.valueOf(month) - 1) + " " + year;
            return  response;
        } catch (ParseException e) {
            e.printStackTrace();
            return input;
        }
    }

    public static String getMonthForInt(int num) {
        String month = "" + num;
        String[] months = new DateFormatSymbols().getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }

    public static int getTwelveHours(int input) {
        if (input > 12) {
            return input - 12;
        } else if (input == 0) {
            return 12;
        }
        return input ;
    }

    public static String getVolleyErrorMessage(String string) {
        return string.replace("com.android.volley.", "");
    }

    public static String getTimeIndicator(int input) {
        if (input > 23 || input < 0) {
            return "";
        }
        if (input >= 12) {
            return "PM";
        } else {
            return "AM";
        }
    }
}
