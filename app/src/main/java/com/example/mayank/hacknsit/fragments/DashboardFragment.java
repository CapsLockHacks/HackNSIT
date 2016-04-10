package com.example.mayank.hacknsit.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mayank on 09-04-2016.
 */
public class DashboardFragment extends android.app.Fragment {

    public static DashboardFragment newInstance() {
        return new DashboardFragment();
    }
    int calories = 0;
    TextView profileNameTv ;
    ImageView profilePhotoIv ;
    TextView caloriesTv, name ;
    RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;
    SpinKitView progressBar;
    List<FeedItem> feedItemList = new ArrayList<>();
    FeedItem feedItem ;
    FloatingActionButton fab, recipe;
    String TAG = this.getClass().getSimpleName();
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
        recipe = (FloatingActionButton) view.findViewById(R.id.recipe);
        recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO-DO:
                //Add code to change to recipe fragment
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new RecipesFragment(), "NewFragmentTag");
                ft.commit();

            }
        });
        foodItemAdapter = new FoodItemAdapter(feedItemList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(foodItemAdapter);
        fillTile();
        prepareFeedData();
        return view;
    }

    public File saveBitmapToFile(File file) {
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
            final int REQUIRED_SIZE = 75;
            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
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
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
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
                        final String c, d, f;
                        try {
                            result = jsonObject.getJSONObject("result");
                            final String ca = result.getString("nf_calories");
                            final String de = result.getString("item_name");
                            final String e = result.getString("brand_name");
                            c = result.getString("nf_calories");
                            d = result.getString("item_name");
                            f = result.getString("brand_name");
                            DialogPlus dialog = DialogPlus.newDialog(getActivity())
                                    .setContentHolder(new ViewHolder(R.layout.dialog_content))
                                    .setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(final DialogPlus dialog, View view) {
                                            switch (view.getId()) {
                                                case R.id.save: {
                                                    try {
                                                        JSONObject j = new JSONObject();
                                                        j.put("brand_name", f);
                                                        j.put("item_name", d);
                                                        j.put("calories", c);
                                                        Calendar cal = Calendar.getInstance();
                                                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                                        String formattedDate = sdf.format(cal.getTime());
                                                        j.put("date", formattedDate);
                                                        ParseUser.getCurrentUser().add("feed", j.toString());
                                                        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                                            @Override
                                                            public void done(ParseException e) {
                                                                Log.v(this.getClass().getSimpleName(), "Save Complete");
                                                                prepareFeedData();
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;
                                                }
                                                case R.id.cancel:
                                                    dialog.dismiss();
                                                    break;
                                            }
                                        }
                                    })
                                    .create();
                            ((ImageView) dialog.getHolderView().findViewById(R.id.image)).setImageURI(Uri.parse(file.getPath()));
                            ((TextView) dialog.getHolderView().findViewById(R.id.caloriesTv)).setText(ca);
                            ((TextView) dialog.getHolderView().findViewById(R.id.name)).setText(de);
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            c = "Error";
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
        String username = ParseUser.getCurrentUser().getUsername();
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    ArrayList<String> feedList = (ArrayList<String>) user.get("feed");
                    int size = feedList.size();
                    feedItemList.clear();
                    Log.d(TAG, "Size of feed list is " + size);
                    for (int i = 0; i < size; i++) {
                        String json = feedList.get(i);
                        try {
                            JSONObject obj = new JSONObject(json);
                            Log.d(TAG, "JSON object in string : " + obj.toString());
                            String s = (String) obj.get("brand_name") + " - " + obj.get("item_name");
                            String c = (String) obj.get("calories");
                            String d = (String) obj.get("date");
                            calories += Integer.parseInt(c);
                            Log.d(TAG, "String s = " + s);
                            Log.d(TAG, "String c = " + c);
                            Log.d(TAG, "String d = " + d);
                            feedItem = new FeedItem(s, c, d);
                            feedItemList.add(feedItem);
                            caloriesTv.setText(Integer.toString(calories));
                        } catch (Throwable t) {
                            Log.d(TAG, "Throwable : " + t);
                        }
                    }
                    foodItemAdapter = new FoodItemAdapter(feedItemList);
                    recyclerView.setAdapter(foodItemAdapter);
                }
            }
        });
    }

    public void fillTile() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        String name = null;
        if (currentUser != null) {
            name = currentUser.getUsername().toString();
            profileNameTv.setText(name);
        }
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();
        String s = String.valueOf(name.charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(s, color);
        profilePhotoIv.setImageDrawable(drawable);
    }
}
