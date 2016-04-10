package com.example.mayank.hacknsit.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.desmond.squarecamera.CameraActivity;
import com.example.mayank.hacknsit.DividerItemDecoration;
import com.example.mayank.hacknsit.MultipartPost;
import com.example.mayank.hacknsit.PostParameter;
import com.example.mayank.hacknsit.R;
import com.example.mayank.hacknsit.adapter.FoodItemAdapter;
import com.example.mayank.hacknsit.model.FeedItem;
import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayank on 09-04-2016.
 */
public class DashboardFragment extends android.app.Fragment {

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }

    TextView profileNameTv ;
    ImageView profilePhotoIv ;
    TextView caloriesTv, name ;
    RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;
    SpinKitView progressBar;
    List<FeedItem> feedItemList = new ArrayList<>();
    FeedItem feedItem ;
    FloatingActionButton fab;
    private static final int REQUEST_CAMERA = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        profileNameTv = (TextView) view.findViewById(R.id.profile_name);
        profilePhotoIv = (ImageView) view.findViewById(R.id.profile_photo);
        caloriesTv = (TextView) view.findViewById(R.id.calories);
        name = (TextView) view.findViewById(R.id.name);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        progressBar = (SpinKitView) view.findViewById(R.id.loading_spinner);
        progressBar.setIndeterminateDrawable(new DoubleBounce());
        progressBar.setVisibility(View.INVISIBLE);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String permission = Manifest.permission.CAMERA;
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                        // Show permission rationale
                    } else {
                        // Handle the result in Activity#onRequestPermissionResult(int, String[], int[])
//                        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, REQUEST_CAMERA_PERMISSION);
                    }
                } else {
                    Intent startCustomCameraIntent = new Intent(getActivity(), CameraActivity.class);
                    startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
                }
            }
        });
        foodItemAdapter = new FoodItemAdapter(feedItemList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(foodItemAdapter);
        fillTile();
        prepareFeedData() ;
        return view ;
    }
    public File saveBitmapToFile(File file){
        try {
            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image
            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();
            // The new size we want to scale to
            final int REQUIRED_SIZE=75;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);
            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);
            return file;
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_CAMERA) {
            Uri photoUri = data.getData();
            List<PostParameter> params = new ArrayList<>();
            final File file = saveBitmapToFile(new File(photoUri.getPath()));
            params.add(new PostParameter<>("file", file));
            final MultipartPost post = new MultipartPost(params);
            try {
                new AsyncTask<Void, Void, JSONObject>() {
                    @Override
                    protected void onPreExecute() {
                        progressBar.setVisibility(View.VISIBLE);
                        fab.setEnabled(false);
                    }
                    @Override
                    protected JSONObject doInBackground(Void... params) {
                        String s = null;
                        try {
                            s = post.send("http://hacknsit.herokuapp.com/upload");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.v(this.getClass().getSimpleName(), s);
                        try {
                            return new JSONObject(s);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(JSONObject jsonObject) {
                        JSONObject result;
                        String c, d;
                        try {
                            result = jsonObject.getJSONObject("result");
                            c = result.getString("nf_calories");
                            d = result.getString("item_name");
                            DialogPlus dialog = DialogPlus.newDialog(getActivity())
                                    .setContentHolder(new ViewHolder(R.layout.dialog_content))
                                    .setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(DialogPlus dialog, View view) {
                                            switch (view.getId()) {
                                                case R.id.save:
                                                    Toast.makeText(getActivity(), "Save Button", Toast.LENGTH_LONG).show();
                                                    break;
                                                case R.id.cancel:
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    })
                                    .create();
                            ((ImageView) dialog.getHolderView().findViewById(R.id.image)).setImageURI(Uri.parse(file.getPath()));
                            ((TextView) dialog.getHolderView().findViewById(R.id.caloriesTv)).setText(c);
                            ((TextView) dialog.getHolderView().findViewById(R.id.name)).setText(d);
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            c = "Error";
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        fab.setEnabled(true);
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void prepareFeedData() {
        feedItem = new FeedItem("Chips", "200", "13/4/16" );
        feedItemList.add(feedItem);
        feedItem = new FeedItem("Biscuits", "300", "13/4/16" );
        feedItemList.add(feedItem);
        feedItem = new FeedItem("Pasta", "350", "13/4/16" );
        feedItemList.add(feedItem);
        foodItemAdapter.notifyDataSetChanged();
    }

    public void fillTile(){
        final ParseUser currentUser = ParseUser.getCurrentUser();
        String name = null;
        if (currentUser != null) {
            name = currentUser.getUsername().toString();
            profileNameTv.setText(name);
        }
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();
        String s = String.valueOf(name.charAt(0)) ;
        TextDrawable drawable = TextDrawable.builder().buildRound(s ,color );
        profilePhotoIv.setImageDrawable(drawable);
    }
}
