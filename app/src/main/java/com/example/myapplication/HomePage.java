package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomePage extends AppCompatActivity {
    private Button buttonSavedResults;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.buttonEqualBreakdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the EqualBreakdownActivity
                Intent intent = new Intent(HomePage.this, EqualBreakdown.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonCustomBreakdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the EqualBreakdownActivity
                Intent intent = new Intent(HomePage.this, CustomBreakdown.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.buttonCombinationBreakdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the EqualBreakdownActivity
                Intent intent = new Intent(HomePage.this, EqualBreakdown.class);
                startActivity(intent);
            }
        });


        buttonSavedResults = findViewById(R.id.buttonSavedResults);

        // Set a click listener for the button
        buttonSavedResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the SavedResultsActivity when the button is clicked
                Intent intent = new Intent(HomePage.this, SavedResultsActivity.class);
                startActivity(intent);
            }
        });
    }
}