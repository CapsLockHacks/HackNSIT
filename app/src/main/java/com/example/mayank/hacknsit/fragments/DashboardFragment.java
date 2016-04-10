package com.example.mayank.hacknsit.fragments;

import android.os.Bundle;
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
import com.example.mayank.hacknsit.DividerItemDecoration;
import com.example.mayank.hacknsit.R;
import com.example.mayank.hacknsit.adapter.FoodItemAdapter;
import com.example.mayank.hacknsit.model.FeedItem;
import com.melnykov.fab.FloatingActionButton;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

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
    TextView caloriesTv ;
    RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;

    List<FeedItem> feedItemList = new ArrayList<>();
    FeedItem feedItem ;

    String username;
    public static final String TAG = DashboardFragment.class.getSimpleName();

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
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity().getApplicationContext(), "FAB clicked." , Toast.LENGTH_SHORT).show();
            }
        });

        foodItemAdapter = new FoodItemAdapter(feedItemList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(foodItemAdapter);

        username = ParseUser.getCurrentUser().getUsername().toString();

        fillTile();

        prepareFeedData() ;

        return view ;
    }

    public void prepareFeedData() {
        ParseQuery query = ParseUser.getQuery();
        query.whereEqualTo("username",username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    ParseUser p = user;
                    ArrayList<String> feedList = (ArrayList<String>) user.get("feed");
                    int size = feedList.size();
                    Log.d(TAG, "Size of feed list is " + size);

                    for (int i = 0; i < size; i++) {
                        String json = feedList.get(i);
                        try {
                            JSONObject obj = new JSONObject(json);
                            Log.d(TAG, "JSON object in string : " + obj.toString());
                            String s = (String) obj.get("brand_name") + " - " + obj.get("item_name");
                            String c = (String) obj.get("calories");
                            String d = (String) obj.get("date");
                            Log.d(TAG, "String s = " + s);
                            Log.d(TAG, "String c = " + c);
                            Log.d(TAG, "String d = " + d);
                            feedItem = new FeedItem(s, c, d);
                            feedItemList.add(feedItem);
                            //feedItemList.notify();
                            foodItemAdapter.notifyDataSetChanged();
                        } catch (Throwable t) {
                            Log.d(TAG, "Throwable : " + t);
                        }
                        foodItemAdapter.notifyDataSetChanged();
                    }

                    foodItemAdapter.notifyDataSetChanged();
                }
            }
        });

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
