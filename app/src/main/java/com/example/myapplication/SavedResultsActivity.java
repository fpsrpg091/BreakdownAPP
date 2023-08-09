package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SavedResultsActivity extends AppCompatActivity {

    private LinearLayout linearSavedResults;
    private SharedPreferences sharedPreferences;
    private List<View> savedResultViews; // New list to store views

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_results);

        linearSavedResults = findViewById(R.id.linearSavedResults);
        sharedPreferences = getSharedPreferences("SavedResults", MODE_PRIVATE);

        savedResultViews = new ArrayList<>(); // Initialize the list

        displaySavedResults();
    }

    private void displaySavedResults() {
        int count = sharedPreferences.getInt("count", 0);

        for (int i = 1; i <= count; i++) {
            final String result = sharedPreferences.getString("result_" + i, "");

            // Create a TextView to display the result
            TextView textViewResult = new TextView(this);
            textViewResult.setText(result);

            // Create buttons for deleting and sharing
            Button buttonDelete = new Button(this);
            buttonDelete.setText("Delete");
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteSavedResult(result);
                }
            });

            Button buttonShare = new Button(this);
            buttonShare.setText("Share");
            buttonShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Share the saved result using an intent
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, result); // The result you want to share

                    // Show the share options
                    startActivity(Intent.createChooser(shareIntent, "Share via"));
                }
            });


            // Create a LinearLayout to contain the buttons
            LinearLayout layoutButtons = new LinearLayout(this);
            layoutButtons.addView(buttonDelete);
            layoutButtons.addView(buttonShare);

            // Create a LinearLayout to contain the result and buttons
            LinearLayout layoutResultAndButtons = new LinearLayout(this);
            layoutResultAndButtons.setOrientation(LinearLayout.VERTICAL);
            layoutResultAndButtons.addView(textViewResult);
            layoutResultAndButtons.addView(layoutButtons);

            // Add the combined layout to the linearSavedResults LinearLayout
            linearSavedResults.addView(layoutResultAndButtons);

            // Store the view in the list
            savedResultViews.add(layoutResultAndButtons);
        }
    }


    private void deleteSavedResult(String result) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0);

        for (int i = 1; i <= count; i++) {
            String savedResult = sharedPreferences.getString("result_" + i, "");
            if (savedResult.equals(result)) {
                editor.remove("result_" + i);
                editor.apply();

                int childViewIndexToDelete = i - 1;
                View viewToDelete = savedResultViews.get(childViewIndexToDelete);

                // Remove the view from the linear layout and the list
                linearSavedResults.removeView(viewToDelete);
                savedResultViews.remove(childViewIndexToDelete);

                count--; // Update the count
                editor.putInt("count", count);
                editor.apply();

                return;
            }
        }
    }
}

