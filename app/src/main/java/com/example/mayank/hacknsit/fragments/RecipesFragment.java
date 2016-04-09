package com.example.mayank.hacknsit.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mayank.hacknsit.R;

/**
 * Created by Mayank on 09-04-2016.
 */
public class RecipesFragment extends Fragment{

    ListView lv ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        lv = (ListView) view.findViewById(R.id.list);


        return view;

    }
}
