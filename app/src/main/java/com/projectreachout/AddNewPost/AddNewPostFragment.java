package com.projectreachout.AddNewPost;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.projectreachout.NetworkUtils.AsyncResponsePost;
import com.projectreachout.NetworkUtils.BackgroundAsyncPost;
import com.projectreachout.R;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.STORAGE_STATS_SERVICE;
import static com.projectreachout.GeneralStatic.getRandomInt;

import android.widget.EditText;
import android.widget.Toast;


import net.gotev.uploadservice.MultipartUploadRequest;

import java.util.UUID;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewPostFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewPostFragment extends Fragment {


    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddNewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewPostFragment newInstance(String param1, String param2) {
        AddNewPostFragment fragment = new AddNewPostFragment();
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

    public static final int REQUEST_IMAGE = 100;

    public static final String LOG_TAG = AddNewPostFragment.class.getSimpleName();

    private ImageView mImageView;
    private EditText mDescriptionEditText;
    private Button mUploadImage;
    private Button mSubmitPost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_new_post, container, false);

        requestStoragePermission();

        mImageView = rootView.findViewById(R.id.iv_fanp_uploaded_image);
        mUploadImage = rootView.findViewById(R.id.btn_fanp_upload_picture);
        mSubmitPost = rootView.findViewById(R.id.btn_fanp_upload_post);
        mDescriptionEditText = rootView.findViewById(R.id.et_fanp_description);

        mUploadImage.setOnClickListener(this::uploadPost);

        mSubmitPost.setOnClickListener(v -> uploadMultipart());

        return rootView;
    }

    private void uploadPost(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(getActivity()).getContentResolver(), filePath);
                mImageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * This is the method responsible for image upload
     * We need the full image path and the name for the image in this method
     * */
    public void uploadMultipart() {

        String path = getPath(filePath);
        String uploadId = UUID.randomUUID().toString();

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(getString(R.string.http))
                .authority(getString(R.string.localhost))
                .appendPath("add_article");

        String url = builder.build().toString();

        //TODO: Implement getUserId()
        int userId = getRandomInt(1000, 99999);
        String description = mDescriptionEditText.getText().toString().trim();

        /*
         *   This MultipartUploadRequest won't upload anything without a file
         *   So, implement other technique to be able to upload articles without image
         *   */

        if ((path == null || path.equals("")) && description.equals("")) {
            Toast.makeText(getContext(), "Cannot upload empty content", Toast.LENGTH_SHORT).show();
        } else if (path == null || path.equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", String.valueOf(userId));
            map.put("description", description);

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
        } else {
            try {
                new MultipartUploadRequest(Objects.requireNonNull(getContext()), uploadId, url)
                        .addParameter("user_id", String.valueOf(userId))
                        .addParameter("description", description)
                        .addFileToUpload(path, "image") //Adding file
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(getContext(), exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = Objects.requireNonNull(getActivity()).getContentResolver().query(uri, null, null, null, null);
        Objects.requireNonNull(cursor).moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        Objects.requireNonNull(cursor).moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
}
