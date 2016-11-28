package com.dryver.Models;

/**
 * Rating object for auto-iteration, used for driver ratings
 *
 * @see android.widget.RatingBar
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
