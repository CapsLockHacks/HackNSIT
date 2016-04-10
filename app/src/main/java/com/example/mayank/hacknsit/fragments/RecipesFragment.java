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
import android.widget.TextView;

import com.example.mayank.hacknsit.R;
import com.parse.ParseUser;

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
public class RecipesFragment extends Fragment{

    ListView lv ;
    Button recipeButton;
    EditText caloriesEt;
    String calories;
    String username ;
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
                new AsyncTask<Void, Void, JSONObject:angry:) {
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
                            ((TextView) dialog.getHolderView().findViewById(R.id.caloriesTv)).setText(c);
                            ((TextView) dialog.getHolderView().findViewById(R.id.name)).setText(d);
                            dialog.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
//                            c = "Error";
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        fab.setEnabled(true);
                    }
                }.execute();
                Choose Files

                URL u;
                HttpURLConnection urlConnection = null;
                try {
                    u = new URL(url);

                    urlConnection = (HttpURLConnection) u
                            .openConnection();

                    InputStream in = urlConnection.getInputStream();

                    InputStreamReader isw = new InputStreamReader(in);

                    JSONObject js = convertToJSONObject(in);

                    JSONObject j = js.getJSONObject("result");

                    Log.d(TAG, j.toString());

                    String food_name = j.getString("title");
                    String cal = j.getString("calories");
                    String imgSrc = j.getString("image");

                    Log.d(TAG, "Food Name is : " + food_name);
                    Log.d(TAG, "Calories : " + calories);
                    Log.d(TAG, "Image source is : " + imgSrc);

                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(food_name);
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, arrayList);
                    lv.setAdapter(arrayAdapter);

                    int data = isw.read();
                    while (data != -1) {
                        char current = (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        });

        return view;

    }

    public JSONObject convertToJSONObject(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bR = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";

        StringBuilder responseStrBuilder = new StringBuilder();
        while((line =  bR.readLine()) != null){

            responseStrBuilder.append(line);
        }
        inputStream.close();

        JSONObject result= new JSONObject(responseStrBuilder.toString());

        return result ;
    }

}
