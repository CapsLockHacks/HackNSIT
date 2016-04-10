package com.example.mayank.hacknsit.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.mayank.hacknsit.R;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.HashMap;

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
    public JSONParser jsonParser;

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

                JSONParser jsonParser = new JSONParser();

                // Building Parameters ( you can pass as many parameters as you want)
                HashMap<String,String> params = new HashMap<String, String>();

                // Getting JSON Object
                JSONObject json = jsonParser.makeHttpRequest(url, "POST", params);

                Log.d(TAG, "JSON object is : " + json.toString());

            }
        });

        return view;

    }



}
