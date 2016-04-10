package com.example.mayank.hacknsit.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mayank.hacknsit.R;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mayank on 09-04-2016.
 */
public class RecipesFragment extends Fragment {

    ListView lv;
    Button recipeButton;
    EditText caloriesEt;
    String calories;
    String username;
    String keywordCalories = "calories";
    String url = "http://hacknsit.herokuapp.com/recipes/calories/";
    public static final String TAG = RecipesFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        lv = (ListView) view.findViewById(R.id.list);
        recipeButton = (Button) view.findViewById(R.id.find_recipes_button);
        caloriesEt = (EditText) view.findViewById(R.id.calories_for_recipe);

        recipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calories = caloriesEt.getText().toString();
                username = ParseUser.getCurrentUser().getUsername();
                url = url + calories;
                Log.d(TAG, "Max calories are : " + calories);
                Log.d(TAG, "URL is : " + url);
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {

                    }

                    @Override
                    protected Void doInBackground(Void... params) {
                        URL u;
                        HttpURLConnection urlConnection = null;
                        try {
                            u = new URL(url);

                            urlConnection = (HttpURLConnection) u
                                    .openConnection();

                            InputStream in = urlConnection.getInputStream();

                            InputStreamReader isw = new InputStreamReader(in);

                            JSONObject js = convertToJSONObject(in);

                            JSONObject j1 = js;
                            //JSONObject j = js.getJSONObject("result");
                            ArrayList<String> arrayList = null;
                            Log.d(TAG, j1.toString());
                            JSONArray ja = j1.getJSONArray("result");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject j = ja.getJSONObject(i);
                                String food_name = j.getString("title");
                                String cal = j.getString("calories");
                                String imgSrc = j.getString("image");
                                Log.d(TAG, "Food Name is : " + food_name);
                                Log.d(TAG, "Calories : " + calories);
                                Log.d(TAG, "Image source is : " + imgSrc);
                                arrayList = new ArrayList<String>();
                                arrayList.add(food_name);
                            }
                            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayList);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    lv.setAdapter(arrayAdapter);
                                }
                            });
/*
                            int data = isw.read();
                            while (data != -1) {
                                char current = (char) data;
                                data = isw.read();
                                System.out.print(current);
                            }*/
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }

                        return null;
                    }
                }.execute();

            }
        });

        return view;
    }

    public JSONObject convertToJSONObject(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";

        StringBuilder responseStrBuilder = new StringBuilder();
        while ((line = bR.readLine()) != null) {

            responseStrBuilder.append(line);
        }
        inputStream.close();

        JSONObject result = new JSONObject(responseStrBuilder.toString());

        return result;
    }

}
