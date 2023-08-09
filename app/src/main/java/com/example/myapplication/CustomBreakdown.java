package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CustomBreakdown extends AppCompatActivity {
    private RadioGroup radioGroupOptions;
    private RadioButton radioButtonPercentage;
    private RadioButton radioButtonRatio;
    private RadioButton radioButtonAmount;
    private EditText editTextTotalBill;
    private EditText editTextNumPeople;
    private Button buttonNext;
    private Button buttonReset;
    private TextView textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_breakdown);

        // Initialize views
        radioGroupOptions = findViewById(R.id.radioGroupOptions);
        radioButtonPercentage = findViewById(R.id.radioButtonPercentage);
        radioButtonRatio = findViewById(R.id.radioButtonRatio);
        radioButtonAmount = findViewById(R.id.radioButtonAmount);
        editTextTotalBill = findViewById(R.id.editTextTotalBill);
        editTextNumPeople = findViewById(R.id.editTextNumPeople);
        buttonNext = findViewById(R.id.buttonNext);
        buttonReset = findViewById(R.id.buttonReset);
        textViewResult = findViewById(R.id.textViewResult);

        // Set click listener for Next button
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNextButtonClick();
            }
        });

        // Set click listener for Reset button
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetInputsAndResult();
            }
        });
    }

    private void handleNextButtonClick() {
        String totalBill = editTextTotalBill.getText().toString().trim();
        String numPeople = editTextNumPeople.getText().toString().trim();

        // Check if any of the input fields are empty
        if (totalBill.isEmpty() || numPeople.isEmpty()) {
            // Display a toast message to inform the user
            Toast.makeText(this, "Please enter all input values", Toast.LENGTH_SHORT).show();
            return; // Exit the method
        }

        // Determine the selected option (percentage, ratio, or amount)
        int selectedRadioButtonId = radioGroupOptions.getCheckedRadioButtonId();
        if (selectedRadioButtonId == R.id.radioButtonPercentage) {
            // Handle percentage input scenario
            // Implement the logic for calculating and displaying result
            Intent intent = new Intent(CustomBreakdown.this, CustomBreakdown2.class);
            intent.putExtra("totalAmount", totalBill);
            intent.putExtra("numPeople", numPeople);
            startActivity(intent);
        } else if (selectedRadioButtonId == R.id.radioButtonRatio) {
            // Handle ratio input scenario
            // Implement the logic for calculating and displaying result
            Intent intent = new Intent(CustomBreakdown.this, CustomBreakdown3.class);
            intent.putExtra("totalAmount", totalBill);
            intent.putExtra("numPeople", numPeople);
            startActivity(intent);
        }
        else if (selectedRadioButtonId == R.id.radioButtonAmount) {
            // Handle ratio input scenario
            // Implement the logic for calculating and displaying result
            Intent intent = new Intent(CustomBreakdown.this, CustomBreakdown4.class);
            intent.putExtra("totalAmount", totalBill);
            intent.putExtra("numPeople", numPeople);
            startActivity(intent);
        }

    }

    private void resetInputsAndResult() {
        // Clear input fields
        editTextTotalBill.setText("");
        editTextNumPeople.setText("");

        // Clear result text
        textViewResult.setText("");
    }
}