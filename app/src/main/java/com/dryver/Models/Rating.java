package com.dryver.Models;

/**
 * Created by Adam on 11/26/2016.
 */

public class Rating {
    private int count;
    private float average;

    public float getAverage() {
        return average;
    }

    public void addRating(float rating){
        if(count == 0){
            average = rating;
            count++;
        } else{
            average = ((average*count + rating) / count++);
        }
    }
}
