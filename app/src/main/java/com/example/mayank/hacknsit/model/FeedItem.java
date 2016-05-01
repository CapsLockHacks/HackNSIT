package com.example.mayank.hacknsit.model;

/**
 * Created by Mayank on 09-04-2016.
 */
public class FeedItem {

    private String foodName , calories, foodDate;

    public FeedItem(){}

    public FeedItem(String foodName, String calories, String foodDate){
        this.foodName = foodName;
        this.calories = calories;
        this.foodDate = foodDate;
    }

    public String getFoodName() { return  foodName ; }

    public void setFoodName(String foodName) { this.foodName = foodName ; }

    public  String getCalories() { return calories ; }

    public void setCalories(String calories) { this.calories = calories ; }

    public String getFoodDate() { return foodDate; }

    public void setFoodDate(String foodDate) { this.foodDate = foodDate ;}

}
