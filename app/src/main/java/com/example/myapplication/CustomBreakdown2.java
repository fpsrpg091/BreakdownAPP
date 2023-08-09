package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomBreakdown2 extends AppCompatActivity {

    private LinearLayout layoutIndividualAmounts;
    private TextView textViewTotalAmount;
    private TextView textViewNumPeople;
    private Button buttonCalculateCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_breakdown2);

        layoutIndividualAmounts = findViewById(R.id.layoutIndividualAmounts);
        textViewTotalAmount = findViewById(R.id.textViewTotalAmount);
        textViewNumPeople = findViewById(R.id.textViewNumPeople);
        buttonCalculateCustom = findViewById(R.id.buttonCalculateCustom);
        buttonCalculateCustom = findViewById(R.id.buttonCalculateCustom);
        Button buttonReset = findViewById(R.id.buttonReset);
        // Retrieve data from intent
        Intent intent = getIntent();
        String totalAmount = intent.getStringExtra("totalAmount");
        String numPeople = intent.getStringExtra("numPeople");

        // Set text views
        textViewTotalAmount.setText("Total Amount: RM " + totalAmount);
        textViewNumPeople.setText("Number of People: " + numPeople);

        // Create input fields based on the number of people
        int numPeopleInt = Integer.parseInt(numPeople);
        createInputFields(numPeopleInt);

        buttonCalculateCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateAndDisplayResult(numPeopleInt);
            }
        });

        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetInputFields(); // Call the new method to reset input fields
            }
        });
    }

    private void createInputFields(int numFields) {
        for (int i = 0; i < numFields; i++) {
            EditText editText = new EditText(this);
            editText.setHint("Person " + (i + 1) + " Percentage");
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            layoutIndividualAmounts.addView(editText);
        }
    }

    private void calculateAndDisplayResult(int numPeople) {
        int totalPercentage = 0;
        List<Integer> percentages = new ArrayList<>();

        // Calculate the total percentage and collect individual percentages
        for (int i = 0; i < numPeople; i++) {
            EditText editText = (EditText) layoutIndividualAmounts.getChildAt(i);
            String inputText = editText.getText().toString().trim();
            if (!inputText.isEmpty()) {
                int percentage = Integer.parseInt(inputText);
                totalPercentage += percentage;
                percentages.add(percentage);
            }
        }

        if (totalPercentage == 100) {
            // Calculate individual amounts based on percentages and total bill
            Intent intent = getIntent();
            String totalAmount = intent.getStringExtra("totalAmount");

            double totalBill = Double.parseDouble(totalAmount);
            List<Double> individualAmounts = calculateIndividualAmounts(percentages, totalBill);

            // Show dialog for sharing and saving the result
            showShareAndSaveDialog(individualAmounts, percentages); // Pass the percentages list here
        } else if (totalPercentage < 100) {
            // Display a warning message about incomplete percentage
            String warningMessage = "Total percentage is " + totalPercentage + "%. You need " +
                    (100 - totalPercentage) + "% more.";
            Toast.makeText(this, warningMessage, Toast.LENGTH_SHORT).show();
        } else {
            // Display an error message about excessive percentage
            Toast.makeText(this, "Total percentage cannot exceed 100%", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Double> calculateIndividualAmounts(List<Integer> percentages, double totalBill) {
        List<Double> individualAmounts = new ArrayList<>();
        for (int percentage : percentages) {
            double individualAmount = (percentage / 100.0) * totalBill;
            individualAmounts.add(individualAmount);
        }
        return individualAmounts;
    }

    private void showShareAndSaveDialog(List<Double> individualAmounts, List<Integer> percentages) {
        String totalAmountText = textViewTotalAmount.getText().toString();
        String numericTotalAmount = totalAmountText.substring(totalAmountText.indexOf("RM") + 3).trim();
        double totalBill = Double.parseDouble(numericTotalAmount);

        String numPeopleText = textViewNumPeople.getText().toString();
        String numericNumPeople = numPeopleText.substring(numPeopleText.indexOf(":") + 1).trim();
        int numPeople = Integer.parseInt(numericNumPeople);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result");

        StringBuilder resultTextBuilder = new StringBuilder();
        for (int i = 0; i < individualAmounts.size(); i++) {
            resultTextBuilder.append("Person ").append(i + 1).append(": RM ")
                    .append(String.format("%.2f", individualAmounts.get(i)))
                    .append(" (").append(percentages.get(i)).append("%)").append("\n");
        }
        final String resultText = resultTextBuilder.toString();

        builder.setMessage(resultText);
        builder.setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Create an intent to share the result
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, resultText);

                // Start the share activity
                startActivity(Intent.createChooser(shareIntent, "Share Result"));
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
                saveResultToStorage(totalBill, numPeople, resultText);  // Modify this line
                Toast.makeText(CustomBreakdown2.this, "Result saved", Toast.LENGTH_SHORT).show();

                // Navigate to SavedResultsActivity
                Intent intent = new Intent(CustomBreakdown2.this, SavedResultsActivity.class);
                startActivity(intent);
            }
        });

        builder.create().show();
    }



    private void saveResultToStorage(double totalBill, int numPeople, String result) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentDateAndTime = dateFormat.format(new Date());

        String dataToSave = "Date and Time: " + currentDateAndTime +
                "\nTotal bill: RM" + totalBill +
                "\nNumber of people: " + numPeople +
                "\n" + result;

        SharedPreferences sharedPreferences = getSharedPreferences("SavedResults", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int count = sharedPreferences.getInt("count", 0);
        count++;
        editor.putString("result_" + count, dataToSave);
        editor.putInt("count", count);
        editor.apply();
    }
    private void resetInputFields() {
        for (int i = 0; i < layoutIndividualAmounts.getChildCount(); i++) {
            EditText editText = (EditText) layoutIndividualAmounts.getChildAt(i);
            editText.setText(""); // Clear the text
        }
    }

}
