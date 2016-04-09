package com.example.mayank.hacknsit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mayank.hacknsit.R;
import com.example.mayank.hacknsit.model.FeedItem;

import java.util.List;

/**
 * Created by Mayank on 09-04-2016.
 */
public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.MyViewHolder> {

    private List<FeedItem> feedList ;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName, calories, foodDate ;

        public MyViewHolder(View view){
            super(view);
            foodName = (TextView) view.findViewById(R.id.foodName);
            calories = (TextView) view.findViewById(R.id.calories);
            foodDate = (TextView) view.findViewById(R.id.foodDate);
        }
    }

    public FoodItemAdapter(List<FeedItem> feedItemList) {
        this.feedList = feedItemList ;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FeedItem feedItem = feedList.get(position);
        holder.foodName.setText(feedItem.getFoodName());
        holder.calories.setText(feedItem.getCalories());
        holder.foodDate.setText(feedItem.getFoodDate());
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

}
