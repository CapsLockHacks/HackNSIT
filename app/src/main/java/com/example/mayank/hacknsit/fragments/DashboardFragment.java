package com.example.mayank.hacknsit.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.mayank.hacknsit.R;
import com.example.mayank.hacknsit.adapter.FoodItemAdapter;
import com.example.mayank.hacknsit.model.FeedItem;
import com.melnykov.fab.FloatingActionButton;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mayank on 09-04-2016.
 */
public class DashboardFragment extends android.app.Fragment {

    TextView profileNameTv ;
    ImageView profilePhotoIv ;
    TextView caloriesTv ;
    RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;

    List<FeedItem> feedItemList = new ArrayList<>();
    FeedItem feedItem ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
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
        recyclerView.setAdapter(foodItemAdapter);


        fillTile();

        prepareFeedData() ;

        return view ;
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
            name = currentUser.get("Full_Name").toString();
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
