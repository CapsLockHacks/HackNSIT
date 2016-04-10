package com.example.mayank.hacknsit.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.desmond.squarecamera.CameraActivity;
import com.example.mayank.hacknsit.MultipartPost;
import com.example.mayank.hacknsit.PostParameter;
import com.example.mayank.hacknsit.R;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.imgscalr.Scalr;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TestFragment extends Fragment {
    Button capture, dashboard;
    TextView calories;
    private static final int REQUEST_CAMERA = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.test_fragment, container, false);
        capture = (Button) view.findViewById(R.id.capture);
        dashboard = (Button) view.findViewById(R.id.dashboard);
        calories = (TextView) view.findViewById(R.id.calories);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    /*List<PostParameter> params = new ArrayList<>();
                    File file = new File( getActivity().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "/image.jpg" );
                    InputStream is = getActivity().getApplicationContext().getResources().openRawResource(R.raw.coke);
                    try {
                        OutputStream os = new FileOutputStream(file);
                        byte buf[]=new byte[1024];
                        int len;
                        while((len = is.read(buf))>0)
                            os.write(buf,0,len);
                        os.close();
                        is.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    params.add(new PostParameter<>("file", new File(file.getPath())));
                    final MultipartPost post = new MultipartPost(params);
                    final ProgressBar progressBar = new ProgressBar(getActivity().getApplicationContext());
                    try {
                        new AsyncTask<Void, Void, JSONObject>() {
                            @Override
                            protected void onPreExecute() {
                                progressBar.setIndeterminateDrawable(new DoubleBounce());
                                progressBar.setVisibility(View.VISIBLE);
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
                                JSONObject result = null;
                                String c;
                                try {
                                    result = jsonObject.getJSONObject("result");
                                    c = result.getString("nf_calories");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    c = "Error";
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                                Log.v(this.getClass().getSimpleName(), result.toString());
                                calories.setText(c);
                            }
                        }.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }
        });
        dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, DashboardFragment.newInstance()).commit();
            }
        });
        return view;
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

            // here i override the original image file
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
            File file = saveBitmapToFile(new File(photoUri.getPath()));
            params.add(new PostParameter<>("file", file));
            final MultipartPost post = new MultipartPost(params);
            final ProgressBar progressBar = new ProgressBar(getActivity().getApplicationContext());
            try {
                new AsyncTask<Void, Void, JSONObject>() {
                    @Override
                    protected void onPreExecute() {
                        progressBar.setIndeterminateDrawable(new DoubleBounce());
                        progressBar.setVisibility(View.VISIBLE);
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
                        String c;
                        try {
                            result = jsonObject.getJSONObject("result");
                            c = result.getString("nf_calories");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            c = "Error";
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        calories.setText(c);
                    }
                }.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
