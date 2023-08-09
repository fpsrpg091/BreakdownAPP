package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EqualBreakdown extends AppCompatActivity {

    private EditText editTextTotalBill;
    private EditText editTextNumPeople;
    private Button buttonCalculate;
    private TextView textViewResult;
    private Button buttonReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equal_breakdown);

        editTextTotalBill = findViewById(R.id.editTextTotalBill);
        editTextNumPeople = findViewById(R.id.editTextNumPeople);
        buttonCalculate = findViewById(R.id.buttonNext);
        textViewResult = findViewById(R.id.textViewResult);
        buttonReset = findViewById(R.id.buttonReset);

        buttonReset = findViewById(R.id.buttonReset);

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call a method to reset input fields and result
                resetInputsAndResult();
            }
        });


        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateEqualBreakdown();
            }
        });
    }


    private void calculateEqualBreakdown() {

        String totalBillText = editTextTotalBill.getText().toString().trim();
        String numPeopleText = editTextNumPeople.getText().toString().trim();

        if (totalBillText.isEmpty() || numPeopleText.isEmpty()) {
            // Display a toast message to inform the user
            showToast("Please enter all input values");
            return; // Exit the method
        }

        // Get total bill amount and number of people from EditText fields
        double totalBill = Double.parseDouble(totalBillText);
        int numPeople = Integer.parseInt(numPeopleText);

        // Calculate the equal breakdown amount
        double equalAmount = totalBill / numPeople;

        // Create and show a pop-up dialog with the result
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Equal Break-Down Result");

        // Format the amount with two decimal places
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedAmount = decimalFormat.format(equalAmount);

        String message = "Each person should pay: RM" + formattedAmount;
        builder.setMessage(message);

        // Add buttons to the dialog
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Share the result using a sharing intent
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
            }
        });

        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Close the dialog
            }
        });

        builder.setNeutralButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveResultToStorage(totalBill, numPeople, message);
                showToast("Result saved successfully");

                // Navigate to SavedResultsActivity
                Intent intent = new Intent(EqualBreakdown.this, SavedResultsActivity.class);
                startActivity(intent);
            }
        });


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetInputsAndResult() {
        // Clear input fields
        editTextTotalBill.setText("");
        editTextNumPeople.setText("");

        // Clear result text
        textViewResult.setText("");
    }

    private void saveResultToStorage(double totalBill, int numPeople, String result) {
        // Get the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        // Combine the result with date and time, total bill, and number of people
        String dataToSave = "Date and Time: " + currentDateAndTime +
                "\nTotal bill: RM" + totalBill +
                "\nNumber of people: " + numPeople +
                "\n" + result;

        // Save the combined data to SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("SavedResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0); // Get the current count
        count++;
        editor.putString("result_" + count, dataToSave);
        editor.putInt("count", count);
        editor.apply();
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}