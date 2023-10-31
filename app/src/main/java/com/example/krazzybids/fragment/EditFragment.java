package com.example.krazzybids.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.krazzybids.ApiList;
import com.example.krazzybids.LoadingDialogBar;

import android.Manifest;

import com.example.krazzybids.R;
import com.example.krazzybids.SessionManager;
import com.example.krazzybids.VolleyMultipartRequest;
import com.example.krazzybids.activity.LoginPage;
import com.example.krazzybids.databinding.ActivityEditProfileBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditFragment extends Fragment {

    ActivityEditProfileBinding profileBinding;
    LoadingDialogBar loadingDialogBar;
    SessionManager sessionManager;
    String userId, token, selectedDocument, convertedPath = "";
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private static int REQUEST_GALLERY_CADE = 200;
    ActivityResultLauncher<Intent> activityResultLauncher;
    private static int CAMERA_PERMISSION_CODE = 100;
    private static int STORAGE_PERMISSION_CODE = 1001;
    public static final int PICK_IMAGE = 1;
    Uri orgUri, uriFromPath;
    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        profileBinding = ActivityEditProfileBinding.inflate(inflater, container, false);

        loadingDialogBar = new LoadingDialogBar(getActivity());
        sessionManager = new SessionManager(getContext());
        userId = sessionManager.getUSERID();
        token = sessionManager.getFcmToken();
        viewProfile(userId, token);

        profileBinding.imagEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment fragment = new ProfileFragment();
                Bundle args = new Bundle();
                args.putString("YourKey", "SchoolUniform");
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentFrame, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        profileBinding.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (profileBinding.btnEditProfile.getText().toString().trim().equals("Edit")) {

                    profileBinding.btnEditProfile.setText("Update");

                    profileBinding.editName.setEnabled(true);
                    profileBinding.editMailId.setEnabled(true);
                    profileBinding.editPhoneNo.setEnabled(true);

                } else {

                    if (profileBinding.editName.getText().toString().trim().equals("")) {

                        profileBinding.editName.requestFocus();
                        profileBinding.editName.setError("Enter Your Name");

                    } else if (profileBinding.editMailId.getText().toString().trim().equals("")) {

                        profileBinding.editMailId.requestFocus();
                        profileBinding.editMailId.setError("Enter Your EmailId");

                    } else if (!isValidEmail(profileBinding.editMailId.getText().toString().trim())) {

                        profileBinding.editMailId.requestFocus();
                        profileBinding.editMailId.setError("Enter Your Valid EmailId");

                    } else if (profileBinding.editPhoneNo.getText().toString().trim().equals("")) {

                        profileBinding.editMailId.requestFocus();
                        profileBinding.editMailId.setError("Enter Your MobileNo");

                    } else if (profileBinding.editPhoneNo.getText().toString().trim().length() != 10) {

                        profileBinding.editMailId.requestFocus();
                        profileBinding.editMailId.setError("Enter Your Valid MobileNo");

                    }else if (convertedPath.equals("")) {

                        profileBinding.editMailId.requestFocus();
                        profileBinding.editMailId.setError("Select Your Image Profile");

                    } else {

                        uploadBitmap(userId,bitmap,
                                profileBinding.editName.getText().toString().trim(),
                                profileBinding.editMailId.getText().toString().trim(),
                                profileBinding.editPhoneNo.getText().toString().trim());

                    }
                }

            }
        });

        profileBinding.cardProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profileBinding.cardProgressbar.setVisibility(View.GONE);
                profileBinding.btnEditProfile.setVisibility(View.VISIBLE);
            }
        });

        profileBinding.photoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDialog();
            }
        });

//        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
//                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
//                    // Callback is invoked after the user selects a media item or closes the
//                    // photo picker.
//                    if (uri != null) {
//
//                        Log.d("PhotoPicker", "Selected URI: " + uri);
//                        Toast.makeText(getActivity(), ""+uri, Toast.LENGTH_SHORT).show();
//
//                    } else {
//
//                        Log.d("PhotoPicker", "No media selected");
//                        Toast.makeText(getActivity(), "No media selected", Toast.LENGTH_SHORT).show();
//
//                    }
//                });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {
                            // Here, no request code
                            Intent data = result.getData();
                            if (data != null) {

                                orgUri = data.getData();
                                // File filepath = new File(String.valueOf(uri_data));

                                profileBinding.profileImage.setImageURI(orgUri);

                                convertedPath = getRealPathFromURI(orgUri);
                                uriFromPath = Uri.fromFile(new File(convertedPath));

                                if (convertedPath != null){

                                    try {

                                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), orgUri);
                                        //uploadBitmap(bitmap);
                                        profileBinding.profileImage.setImageBitmap(bitmap);

                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }

                                }

                                Log.d("userimage", "yourImagePath " + convertedPath);
                                Log.d("userimage", "yourImagePath1 " + uriFromPath);
                                Log.d("userimage", "yourImagePath2 " + bitmap);
                            }


                        } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                            Toast.makeText(getActivity(), "Image selected CANCELED", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getActivity(), "Image Is Not Selected", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        return profileBinding.getRoot();
    }

    public boolean isValidEmail(final String email) {

        Pattern pattern;
        Matcher matcher;

        //final String PASSWORD_PATTERN = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$";

        pattern = Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(email);

        return matcher.matches();

        //return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    public void viewProfile(String userid, String token) {

        loadingDialogBar.ShowDialog("View Profile Wait.....");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.viewprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                loadingDialogBar.HideDialog();

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    String status = jsonObject.getString("status");
                    String data = jsonObject.getString("data");
                    JSONArray jsonArray = new JSONArray(data);
                    JSONObject jsonObject_data = jsonArray.getJSONObject(0);

                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                    if (status.equals("200")) {

                        String id = jsonObject_data.getString("id");
                        String name = jsonObject_data.getString("name");
                        String email = jsonObject_data.getString("email");
                        String mobile_number = jsonObject_data.getString("mobile_number");
                        String image = jsonObject_data.getString("image");

                        profileBinding.editName.setText(name);
                        profileBinding.editMailId.setText(email);
                        profileBinding.editPhoneNo.setText(mobile_number);

                        profileBinding.editName.setEnabled(false);
                        profileBinding.editMailId.setEnabled(false);
                        profileBinding.editPhoneNo.setEnabled(false);

                        if (image.equals("null")){}else{
                            String url = "https://www.polosoftech.com/krazybids/public/user_image/"+image;
                            Picasso.with(getActivity()).load(url).into(profileBinding.profileImage);
                        }



                    }else{

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                loadingDialogBar.HideDialog();
                Toast.makeText(getActivity(), "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("userid", userid);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", token);
                return headers;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 0, DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    public void startDialog() {

        final CharSequence[] items = {"Choose Images", "Select Camera", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo or PDF!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Choose Images")) {

                    checkPermission(Manifest.permission.READ_MEDIA_IMAGES, REQUEST_GALLERY_CADE);

                    selectedDocument = "selectImage";

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S_V2) {

                        dialog.dismiss();

                        if (ContextCompat.checkSelfPermission(
                                getActivity(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
                            //startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                            //pickMedia.launch(new PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly));
                            // Launch the photo picker and let the user choose only images.
                            // pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());


                        } else {
                            ActivityCompat.requestPermissions(
                                    getActivity(),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_CADE
                            );
                        }
                    } else {

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));

//                        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType = (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
//                        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
//                                .setMediaType(mediaType)
//                                .build();
//                        pickMedia.launch(request);

                        //pickMedia.launch(new PickVisualMediaRequest.Builder().
                        //setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());


                    }

                } else if (items[item].equals("Choose From Gallery")) {

                    dialog.dismiss();

                    checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);

                    selectedDocument = "selectCamera";
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activityResultLauncher.launch(cameraIntent);

                } else if (items[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {

        if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

        } else {
            Toast.makeText(getActivity(), "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Camera Permission Granted", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(), "Camera Permission Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == REQUEST_GALLERY_CADE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Storage Permission Granted", Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(getActivity(), "Storage Permission Denied", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                getActivity(),
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
    public void editProfile(String id, String file, String name, String email, String mobile_number){

        profileBinding.btnEditProfile.setVisibility(View.GONE);
        profileBinding.cardProgressbar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ApiList.editprofile, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                profileBinding.cardProgressbar.setVisibility(View.GONE);
                profileBinding.btnEditProfile.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");


                    if (status.equals("200")){

                        String data = jsonObject.getString("data");
                        JSONObject jsonObject_data = new JSONObject(data);
                        String id = jsonObject_data.getString("id");
                        String name = jsonObject_data.getString("name");
                        String email = jsonObject_data.getString("email");
                        String mobile_number = jsonObject_data.getString("mobile_number");
                        String image = jsonObject_data.getString("image");

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        viewProfile(id, sessionManager.getFcmToken());


                    }else{

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                profileBinding.cardProgressbar.setVisibility(View.GONE);
                profileBinding.btnEditProfile.setVisibility(View.VISIBLE);

                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
            }
        }){

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("file",file);
                params.put("name",name);
                params.put("email",email);
                params.put("mobile_number",mobile_number);

                Log.d("profiledetails",params.toString());


                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);

    }

    private void uploadBitmap(String id, Bitmap bitmap, String name, String email, String mobile_number) {

        profileBinding.btnEditProfile.setVisibility(View.GONE);
        profileBinding.cardProgressbar.setVisibility(View.VISIBLE);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ApiList.editprofile,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        profileBinding.cardProgressbar.setVisibility(View.GONE);
                        profileBinding.btnEditProfile.setVisibility(View.VISIBLE);

                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                           // JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String message = jsonObject.getString("message");


                            if (status.equals("200")){

                                String data = jsonObject.getString("data");
                                JSONObject jsonObject_data = new JSONObject(data);
                                String id = jsonObject_data.getString("id");
                                String name = jsonObject_data.getString("name");
                                String email = jsonObject_data.getString("email");
                                String mobile_number = jsonObject_data.getString("mobile_number");
                                String image = jsonObject_data.getString("image");

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                                viewProfile(id, sessionManager.getFcmToken());


                            }else{

                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        profileBinding.cardProgressbar.setVisibility(View.GONE);
                        profileBinding.btnEditProfile.setVisibility(View.VISIBLE);

                        Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("name",name);
                params.put("email",email);
                params.put("mobile_number",mobile_number);

                Log.d("profiledetails",params.toString());


                return params;
            }
        };

        //adding the request to volley
      //  Volley.newRequestQueue(getActivity()).add(volleyMultipartRequest);

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(3000,0,DefaultRetryPolicy.DEFAULT_TIMEOUT_MS));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.getCache().clear();
        requestQueue.add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

}
